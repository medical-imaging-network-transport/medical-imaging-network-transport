package org.nema.medical.mint.utils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.nema.medical.mint.datadictionary.*;
import org.nema.medical.mint.metadata.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;

/**
 * @author Uli Bubenheimer
 */
public class StudyValidationTest {
    private StudyMetadata study;
    private MetadataType metadataType;

    @Before
    public void setUp() throws IOException {
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
                "    <series seriesInstanceUID='9.8.7.5'>" +
                "      <attributes/>" +
                "      <normalizedInstanceAttributes/>" +
                "      <instances>" +
                "        <instance sopInstanceUID='5.4.3.1' transferSyntaxUID='1.2.840.10008.1.2.1'>" +
                "          <attributes>" +
                "            <attr tag='00020010' vr='UI' val='1.2.840.10008.1.2.1'/>" +
                "          </attributes>" +
                "        </instance>" +
                "      </instances>" +
                "    </series>" +
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
                "        <attr tag='00020010' vr='UI' val='1.2.840.10008.1.2.1'/>" +
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

        final LevelAttributes studyAttrType = createAttributeTypes(Arrays.asList(0x00080020, 0x00080051));
        final LevelAttributes seriesAttrType = createAttributeTypes(Arrays.asList(0x00080021,0x00081072));

        final AttributesType attrType = createAttributesType(Arrays.asList(
                0x00020010, 0x00080008, 0x00080013, 0x00080020, 0x00080021,
                0x00080051, 0x00081072, 0x00081140, 0x00081150, 0x7fe00010));
        attrType.setUnknownAttributes(AttributesType.UnknownAttribute.REJECT);

        metadataType = new MetadataType();
        metadataType.setAttributes(attrType);
        metadataType.setStudyAttributes(studyAttrType);
        metadataType.setSeriesAttributes(seriesAttrType);
    }

    @After
    public void tearDown() {
        study = null;
        metadataType = null;
    }

    private AttributesType createAttributesType(final Collection<Integer> tags) {
        final AttributesType attrType = new AttributesType();
        for (final Integer tag: tags) {
            final ElementType elemType = new ElementType();
            elemType.setTag(tag);
            attrType.addElement(elemType);
        }
        return attrType;
    }

    private LevelAttributes createAttributeTypes(final Collection<Integer> tags) {
        final LevelAttributes attributeTypes = new LevelAttributes();
        for (final int tag: tags) {
            attributeTypes.addAttributeType(tag, null);
        }
        return attributeTypes;
    }

    /**
     * Tests the baseline case where an absence of unknown attributes will not cause study rejection.
     * @throws StudyTraversals.TraversalException when something is wrong
     */
    @Test
    public void testValidateUnknownAttributes1() throws StudyTraversals.TraversalException {
        StudyValidation.validateUnknownAttributes(study, metadataType);
    }

    /**
     * Tests that an unknown study-level attribute causes the study to be rejected.
     * @throws StudyTraversals.TraversalException when correct
     */
    @Test(expected = StudyTraversals.TraversalException.class)
    public void testValidateUnknownAttributes2() throws StudyTraversals.TraversalException {
        final Attribute attr = new Attribute();
        attr.setTag(0x00010003);
        study.putAttribute(attr);
        StudyValidation.validateUnknownAttributes(study, metadataType);
    }

    /**
     * Tests that an unknown study-level attribute will not cause the study to be rejected when
     * unknown attributes are silently accepted.
     * @throws StudyTraversals.TraversalException when correct
     */
    @Test
    public void testValidateUnknownAttributes3() throws StudyTraversals.TraversalException {
        final Attribute attr = new Attribute();
        attr.setTag(0x00010003);
        study.putAttribute(attr);
        metadataType.getAttributes().setUnknownAttributes(AttributesType.UnknownAttribute.ACCEPT);
        StudyValidation.validateUnknownAttributes(study, metadataType);
    }

    /**
     * Tests the baseline case where having an attribute at the right level (study, series, instance)
     * will not cause study rejection.
     * @throws StudyTraversals.TraversalException when something is wrong
     */
    @Test
    public void testValidateAttributeLevel1() throws StudyTraversals.TraversalException {
        StudyValidation.validateAttributeLevel(study, metadataType);
    }

    /**
     * Tests that an instance-level attribute causes the study to be rejected when it occurs at the study level.
     * @throws StudyTraversals.TraversalException when correct
     */
    @Test(expected = StudyTraversals.TraversalException.class)
    public void testValidateAttributeLevel2() throws StudyTraversals.TraversalException {
        final Attribute attr = new Attribute();
        attr.setTag(0x00010003);
        study.putAttribute(attr);
        StudyValidation.validateAttributeLevel(study, metadataType);
    }

    /**
     * Tests the baseline case where having all attributes with an initialized VR value
     * will not cause study rejection.
     * @throws StudyTraversals.TraversalException when something is wrong
     */
    @Test
    public void testValidateDICOMVRValueExistence1() throws StudyTraversals.TraversalException {
        StudyValidation.validateDICOMVRValueExistence(study);
    }

    /**
     * Tests that a study-level attribute with no VR set causes the study to be rejected.
     * @throws StudyTraversals.TraversalException when correct
     */
    @Test(expected = StudyTraversals.TraversalException.class)
    public void testValidateDICOMVRValueExistence2() throws StudyTraversals.TraversalException {
        study.attributeIterator().next().setVr(null);
        StudyValidation.validateDICOMVRValueExistence(study);
    }

    /**
     * Tests the baseline case where the transfer syntax strings in the instance tags and in the Part 10
     * metadata inside the instances match.
     * @throws StudyTraversals.TraversalException when something is wrong
     */
    @Test
    public void testValidateDICOMTransferSyntax1() throws StudyTraversals.TraversalException {
        StudyValidation.validateDICOMTransferSyntax(study);
    }

    /**
     * Tests the baseline case where the list of binary item IDs matches what's in the metadata.
     * @throws StudyTraversals.TraversalException when something is wrong
     */
    @Test
    public void testValidateBinaryItemsReferences1() throws StudyTraversals.TraversalException {
        final Collection<Integer> binaryItemIds = Arrays.asList(0);
        StudyValidation.validateBinaryItemsReferences(study, binaryItemIds);
    }

    /**
     * Tests that a mismatch between a single wrong binary ID in the passed list and the metadata
     * causes the study to be rejected.
     * @throws StudyTraversals.TraversalException when correct
     */
    @Test(expected = StudyTraversals.TraversalException.class)
    public void testValidateBinaryItemsReferences2() throws StudyTraversals.TraversalException {
        final Collection<Integer> binaryItemIds = Arrays.asList(1);
        StudyValidation.validateBinaryItemsReferences(study, binaryItemIds);
    }

    /**
     * Tests that having additional binary IDs in the passed list than in the metadata causes the study to be rejected.
     * @throws StudyTraversals.TraversalException when correct
     */
    @Test(expected = StudyTraversals.TraversalException.class)
    public void testValidateBinaryItemsReferences3() throws StudyTraversals.TraversalException {
        final Collection<Integer> binaryItemIds = Arrays.asList(0, 1);
        StudyValidation.validateBinaryItemsReferences(study, binaryItemIds);
    }

    /**
     * Tests that having more binary IDs in the passed list than in the metadata causes the study to be rejected.
     * @throws StudyTraversals.TraversalException when correct
     */
    @Test(expected = StudyTraversals.TraversalException.class)
    public void testValidateBinaryItemsReferences4() throws StudyTraversals.TraversalException {
        final Collection<Integer> binaryItemIds = Collections.emptyList();
        StudyValidation.validateBinaryItemsReferences(study, binaryItemIds);
    }

    /**
     * Tests that having a duplicate binary ID in the study causes it to be rejected.
     * @throws StudyTraversals.TraversalException when correct
     */
    @Test(expected = StudyTraversals.TraversalException.class)
    public void testValidateBinaryItemsReferences5() throws StudyTraversals.TraversalException {
        final Attribute attr = new Attribute();
        attr.setBid(0);
        attr.setFrameCount(1);
        attr.setBinarySize(3000);
        attr.setTag(0x7fe00000);
        attr.setVr("OW");
        study.putAttribute(attr);
        final Collection<Integer> binaryItemIds = Arrays.asList(0);
        StudyValidation.validateBinaryItemsReferences(study, binaryItemIds);
    }

    /**
     * Tests that having a duplicate binary ID in the study causes it to be rejected, even when it's on a multi-frame
     * attribute and is at the instance level.
     *
     * @throws StudyTraversals.TraversalException when correct
     */
    @Test(expected = StudyTraversals.TraversalException.class)
    public void testValidateBinaryItemsReferences6() throws StudyTraversals.TraversalException {
        final Attribute attr = new Attribute();
        attr.setBid(0);
        attr.setFrameCount(2);
        attr.setBinarySize(3000);
        attr.setTag(0xffffffff);
        attr.setVr("OW");
        //Put attribute at end
        Iterator<Series> seriesIter = study.seriesIterator();
        Series series = null;
        for (; seriesIter.hasNext(); series = seriesIter.next());
        Iterator<Instance> instanceIter = series.instanceIterator();
        Instance instance = null;
        for (; instanceIter.hasNext(); instance = instanceIter.next());
        instance.putAttribute(attr);
        final Collection<Integer> binaryItemIds = Arrays.asList(0, 1);
        StudyValidation.validateBinaryItemsReferences(study, binaryItemIds);
    }

    /**
     * Tests the baseline case where the list of binary item IDs matches what's in the metadata, even in the case
     * of having both a single-frame binary item and a multi-frame binary item.
     * @throws StudyTraversals.TraversalException when something is wrong
     */
    @Test
    public void testValidateBinaryItemsReferences7() throws StudyTraversals.TraversalException {
        final Attribute attr = new Attribute();
        attr.setBid(1);
        attr.setFrameCount(2);
        attr.setBinarySize(3000);
        attr.setTag(0xffffffff);
        attr.setVr("OW");
        //Put attribute at end
        Iterator<Series> seriesIter = study.seriesIterator();
        Series series = null;
        for (; seriesIter.hasNext(); series = seriesIter.next());
        Iterator<Instance> instanceIter = series.instanceIterator();
        Instance instance = null;
        for (; instanceIter.hasNext(); instance = instanceIter.next());
        instance.putAttribute(attr);
        final Collection<Integer> binaryItemIds = Arrays.asList(0, 1, 2);
        StudyValidation.validateBinaryItemsReferences(study, binaryItemIds);
    }

}
