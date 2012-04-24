package org.nema.medical.mint.utils;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.nema.medical.mint.metadata.*;

import java.io.ByteArrayInputStream;

import static org.junit.Assert.assertEquals;

/**
 * @author Uli Bubenheimer
 */
public class StudyTraversalsTest {
    private StudyMetadata study;
    private CountAction countAction;

    private final class CountAction implements StudyTraversals.AttributeAction {
        private int visitCounter = 0;

        @Override
        public void doAction(final Attribute attribute) throws StudyTraversals.TraversalException {
            ++visitCounter;
        }

        public void reset() {
            visitCounter = 0;
        }

        public int total() {
            return visitCounter;
        }
    }

    @Before
    public void setUp() throws Exception {
        countAction = new CountAction();

        final String xml =
                "<study xmlns='http://medical.nema.org/mint' studyInstanceUID='1.2.3.4.5' type='DICOM'>" +
                "  <attributes>" +
                "    <attr tag='00080020' vr='DA' val='20101123'/>" +
                "    <attr tag='00080051' vr='SQ'>" +
                "      <item>" +
                "        <attributes>" +
                "          <attr tag='00081150' vr='UI' val='1.2.840.10008.5.1.4.1.1.2'/>" +
                "        </attributes>" +
                "      </item>" +
                "    </attr>" +
                "  </attributes>" +
                "  <seriesList>" +
                "    <series seriesInstanceUID='9.8.7.6'>" +
                "      <attributes>" +
                "        <attr tag='00080021' vr='DA' val='20101123'/>" +
                "        <attr tag='00081072' vr='SQ'>" +
                "          <item>" +
                "            <attributes>" +
                "              <attr tag='00081150' vr='UI' val='1.2.840.10008.5.1.4.1.1.2'/>" +
                "            </attributes>" +
                "          </item>" +
                "        </attr>" +
                "      </attributes>" +
                "      <normalizedInstanceAttributes>" +
                "        <attr tag='00080008' vr='CS' val='ORIGINAL\\PRIMARY\\AXIAL'/>" +
                "      </normalizedInstanceAttributes>" +
                "      <instances>" +
                "        <instance sopInstanceUID='5.4.3.2' transferSyntaxUID='1.2.840.10008.1.2.1'>" +
                "          <attributes>" +
                "            <attr tag='00080013' vr='TM' val='082635'/>" +
                "            <attr tag='7fe00010' vr='OW' bid='0' bsize='3000'/>" +
                "          </attributes>" +
                "        </instance>" +
                "        <instance sopInstanceUID='1.0.9.8' transferSyntaxUID='1.2.840.10008.1.2.1'>" +
                "          <attributes>" +
                "            <attr tag='00080013' vr='TM' val='082636'/>" +
                "            <attr tag='00081140' vr='SQ'>" +
                "              <item>" +
                "                <attributes>" +
                "                  <attr tag='00081150' vr='UI' val='1.2.840.10008.5.1.4.1.1.2'/>" +
                "                </attributes>" +
                "              </item>" +
                "            </attr>" +
                "          </attributes>" +
                "        </instance>" +
                "      </instances>" +
                "    </series>" +
                "  </seriesList>" +
                "</study>";
        study = StudyIO.parseFromXML(new ByteArrayInputStream(xml.getBytes()));
    }

    @After
    public void tearDown() throws Exception {
        study = null;
    }

    @Test
    public void testAllAttributeTraverser() throws Exception {
        StudyTraversals.allAttributeTraverser(study, countAction);
        assertEquals(12, countAction.total());
    }

    @Test
    public void testFlatStudyAttributeTraverser() throws Exception {
        StudyTraversals.flatStudyAttributeTraverser(study, countAction);
        assertEquals(2, countAction.total());
    }

    @Test
    public void testFlatSeriesAttributeTraverser() throws Exception {
        StudyTraversals.flatSeriesAttributeTraverser(study, countAction);
        assertEquals(2, countAction.total());
    }

    @Test
    public void testFlatAttributeStoreTraverser() throws Exception {
        StudyTraversals.flatAttributeContainerTraverser(study, countAction);
        assertEquals(2, countAction.total());
    }

    @Test
    public void testHierarchicalAttributeStoreTraverser() throws Exception {
        StudyTraversals.hierarchicalAttributeContainerTraverser(study, countAction);
        assertEquals(3, countAction.total());
    }

    @Test
    public void testHierarchicalAttributeTraverser() throws Exception {
        {
            final Attribute attr = new Attribute();
            attr.setTag(0x00080013);
            attr.setVr("TM");
            attr.setVal("082636");

            StudyTraversals.hierarchicalAttributeTraverser(attr, countAction);
            assertEquals(1, countAction.total());
        }
        {
            countAction.reset();

            final Attribute attr = new Attribute();
            attr.setTag(0x00081140);
            attr.setVr("SQ");
            {
                final Item item = new Item();
                {
                    final Attribute itemAttr = new Attribute();
                    itemAttr.setTag(0x00081150);
                    itemAttr.setVr("UI");
                    itemAttr.setVal("1.2.840.10008.5.1.4.1.1.2");
                    item.putAttribute(itemAttr);
                }
                attr.addItem(item);
            }
            StudyTraversals.hierarchicalAttributeTraverser(attr, countAction);
            assertEquals(2, countAction.total());
        }
    }
}
