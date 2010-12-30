package org.nema.medical.mint.utils;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.nema.medical.mint.metadata.*;

import static org.junit.Assert.assertEquals;

/**
 * @author Uli Bubenheimer
 */
public class StudyTraversalsTest {
    private StudyMetadata study;

    @Before
    public void setUp() throws Exception {
        study = new StudyMetadata();
        study.setStudyInstanceUID("1.2.3.4.5");
        study.setType("DICOM");
        {
            final Attribute attr = new Attribute();
            attr.setTag(0x00080020);
            attr.setVr("DA");
            attr.setVal("20101123");
            study.putAttribute(attr);
        }
        {
            final Attribute attr = new Attribute();
            attr.setTag(0x00080051);
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
            study.putAttribute(attr);
        }
        {
            Series series = new Series();
            series.setSeriesInstanceUID("9.8.7.6");
            {
                final Attribute attr = new Attribute();
                attr.setTag(0x00080021);
                attr.setVr("DA");
                attr.setVal("20101123");
                series.putAttribute(attr);
            }
            {
                final Attribute attr = new Attribute();
                attr.setTag(0x00081072);
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
                series.putAttribute(attr);
            }
            {
                final Attribute attr = new Attribute();
                attr.setTag(0x00080008);
                attr.setVr("CS");
                attr.setVal("ORIGINAL\\PRIMARY\\AXIAL");
                series.putNormalizedInstanceAttribute(attr);
            }
            {
                final Instance instance = new Instance();
                instance.setSOPInstanceUID("5.4.3.2");
                instance.setTransferSyntaxUID("1.2.840.10008.1.2.1");
                {
                    final Attribute attr = new Attribute();
                    attr.setTag(0x00080013);
                    attr.setVr("TM");
                    attr.setVal("082635");
                    instance.putAttribute(attr);
                }
                {
                    final Attribute attr = new Attribute();
                    attr.setBid(0);
                    attr.setBinarySize(3000);
                    attr.setTag(0x7fe00010);
                    attr.setVr("OW");
                    instance.putAttribute(attr);
                }
                series.putInstance(instance);
            }
            {
                final Instance instance = new Instance();
                instance.setSOPInstanceUID("1.0.9.8");
                instance.setTransferSyntaxUID("1.2.840.10008.1.2.1");
                {
                    final Attribute attr = new Attribute();
                    attr.setTag(0x00080013);
                    attr.setVr("TM");
                    attr.setVal("082636");
                    instance.putAttribute(attr);
                }
                {
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
                    instance.putAttribute(attr);
                }
                series.putInstance(instance);
            }
            study.putSeries(series);
        }
    }

    @After
    public void tearDown() throws Exception {
        study = null;
    }

    @Test
    public void testAllAttributeTraverser() throws Exception {
        final int[] visitCounter = {0};
        final class CountAction implements StudyTraversals.AttributeAction {
            @Override
            public void doAction(final Attribute attribute) throws StudyTraversals.TraversalException {
                ++visitCounter[0];
            }
        }
        final CountAction countAction = new CountAction();
        StudyTraversals.allAttributeTraverser(study, countAction);
        assertEquals(12, visitCounter[0]);
    }

    @Test
    public void testFlatStudyAttributeTraverser() throws Exception {
        final int[] visitCounter = {0};
        final class CountAction implements StudyTraversals.AttributeAction {
            @Override
            public void doAction(final Attribute attribute) throws StudyTraversals.TraversalException {
                ++visitCounter[0];
            }
        }
        final CountAction countAction = new CountAction();
        StudyTraversals.flatStudyAttributeTraverser(study, countAction);
        assertEquals(2, visitCounter[0]);
    }

    @Test
    public void testFlatSeriesAttributeTraverser() throws Exception {
        final int[] visitCounter = {0};
        final class CountAction implements StudyTraversals.AttributeAction {
            @Override
            public void doAction(final Attribute attribute) throws StudyTraversals.TraversalException {
                ++visitCounter[0];
            }
        }
        final CountAction countAction = new CountAction();
        StudyTraversals.flatSeriesAttributeTraverser(study, countAction);
        assertEquals(2, visitCounter[0]);
    }

    @Test
    public void testFlatAttributeStoreTraverser() throws Exception {
        final int[] visitCounter = {0};
        final class CountAction implements StudyTraversals.AttributeAction {
            @Override
            public void doAction(final Attribute attribute) throws StudyTraversals.TraversalException {
                ++visitCounter[0];
            }
        }
        final CountAction countAction = new CountAction();
        StudyTraversals.flatAttributeStoreTraverser(study, countAction);
        assertEquals(2, visitCounter[0]);
    }

    @Test
    public void testHierarchicalAttributeStoreTraverser() throws Exception {
        final int[] visitCounter = {0};
        final class CountAction implements StudyTraversals.AttributeAction {
            @Override
            public void doAction(final Attribute attribute) throws StudyTraversals.TraversalException {
                ++visitCounter[0];
            }
        }
        final CountAction countAction = new CountAction();
        StudyTraversals.hierarchicalAttributeStoreTraverser(study, countAction);
        assertEquals(3, visitCounter[0]);
    }

    @Test
    public void testHierarchicalAttributeTraverser() throws Exception {
        {
            final int[] visitCounter = {0};
            final class CountAction implements StudyTraversals.AttributeAction {
                @Override
                public void doAction(final Attribute attribute) throws StudyTraversals.TraversalException {
                    ++visitCounter[0];
                }
            }
            final CountAction countAction = new CountAction();

            final Attribute attr = new Attribute();
            attr.setTag(0x00080013);
            attr.setVr("TM");
            attr.setVal("082636");

            StudyTraversals.hierarchicalAttributeTraverser(attr, countAction);
            assertEquals(1, visitCounter[0]);
        }
        {
            final int[] visitCounter = {0};
            final class CountAction implements StudyTraversals.AttributeAction {
                @Override
                public void doAction(final Attribute attribute) throws StudyTraversals.TraversalException {
                    ++visitCounter[0];
                }
            }
            final CountAction countAction = new CountAction();

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
            assertEquals(2, visitCounter[0]);
        }
    }
}
