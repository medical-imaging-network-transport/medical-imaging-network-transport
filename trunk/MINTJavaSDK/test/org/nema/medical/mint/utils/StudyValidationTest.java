package org.nema.medical.mint.utils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.nema.medical.mint.datadictionary.*;
import org.nema.medical.mint.metadata.*;

import java.util.*;
import java.util.jar.Attributes;

/**
 * @author Uli Bubenheimer
 */
public class StudyValidationTest {
    private StudyMetadata study;
    private MetadataType metadataType;

    @Before
    public void setUp() {
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
            series.setSeriesInstanceUID("9.8.7.5");
            {
                final Instance instance = new Instance();
                instance.setSOPInstanceUID("5.4.3.1");
                instance.setTransferSyntaxUID("1.2.840.10008.1.2.1");
                {
                    final Attribute attr = new Attribute();
                    attr.setTag(0x00020010);
                    attr.setVr("UI");
                    attr.setVal("1.2.840.10008.1.2.1");
                    instance.putAttribute(attr);
                }
                series.putInstance(instance);
            }
            study.putSeries(series);
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
                final Attribute attr = new Attribute();
                attr.setTag(0x00020010);
                attr.setVr("UI");
                attr.setVal("1.2.840.10008.1.2.1");
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
                    attr.setFrameCount(1);
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

        final List<AttributeType> studyAttributes = new ArrayList<AttributeType>();
        {
            final AttributeType attrType = new AttributeType();
            attrType.setTag("00080020");
            studyAttributes.add(attrType);
        }
        {
            final AttributeType attrType = new AttributeType();
            attrType.setTag("00080051");
            studyAttributes.add(attrType);
        }
        final StudyAttributesType studyAttrType = new StudyAttributesType();
        studyAttrType.setAttributes(studyAttributes);

        final List<AttributeType> seriesAttributes = new ArrayList<AttributeType>();
        {
            final AttributeType attrType = new AttributeType();
            attrType.setTag("00080021");
            seriesAttributes.add(attrType);
        }
        {
            final AttributeType attrType = new AttributeType();
            attrType.setTag("00081072");
            seriesAttributes.add(attrType);
        }
        final SeriesAttributesType seriesAttrType = new SeriesAttributesType();
        seriesAttrType.setAttributes(seriesAttributes);

        final AttributesType attrType = new AttributesType();
        final List<ElementType> attrs = new ArrayList<ElementType>();
        attrType.setUnknownAttributes("reject");
        {
            final ElementType elemType = new ElementType();
            elemType.setTag("00020010");
            attrs.add(elemType);
        }
        {
            final ElementType elemType = new ElementType();
            elemType.setTag("00080008");
            attrs.add(elemType);
        }
        {
            final ElementType elemType = new ElementType();
            elemType.setTag("00080013");
            attrs.add(elemType);
        }
        {
            final ElementType elemType = new ElementType();
            elemType.setTag("00080020");
            attrs.add(elemType);
        }
        {
            final ElementType elemType = new ElementType();
            elemType.setTag("00080021");
            attrs.add(elemType);
        }
        {
            final ElementType elemType = new ElementType();
            elemType.setTag("00080051");
            attrs.add(elemType);
        }
        {
            final ElementType elemType = new ElementType();
            elemType.setTag("00081072");
            attrs.add(elemType);
        }
        {
            final ElementType elemType = new ElementType();
            elemType.setTag("00081140");
            attrs.add(elemType);
        }
        {
            final ElementType elemType = new ElementType();
            elemType.setTag("00081150");
            attrs.add(elemType);
        }
        {
            final ElementType elemType = new ElementType();
            elemType.setTag("7fe00010");
            attrs.add(elemType);
        }
        attrType.setElements(attrs);
        metadataType = new MetadataType();
        metadataType.setAttributes(attrType);
        metadataType.setStudyAttributes(studyAttrType);
        metadataType.setSeriesAttributes(seriesAttrType);
    }

    @After
    public void tearDown() {
        study = null;
    }

    @Test
    public void testValidateUnknownAttributes1() throws StudyTraversals.TraversalException {
        StudyValidation.validateUnknownAttributes(study, metadataType);
    }

    @Test(expected = StudyTraversals.TraversalException.class)
    public void testValidateUnknownAttributes2() throws StudyTraversals.TraversalException {
        final Attribute attr = new Attribute();
        attr.setTag(0x00010003);
        study.putAttribute(attr);
        StudyValidation.validateUnknownAttributes(study, metadataType);
    }

    @Test
    public void testValidateUnknownAttributes3() throws StudyTraversals.TraversalException {
        final Attribute attr = new Attribute();
        attr.setTag(0x00010003);
        study.putAttribute(attr);
        metadataType.getAttributes().setUnknownAttributes("accept");
        StudyValidation.validateUnknownAttributes(study, metadataType);
    }

    @Test
    public void testValidateAttributeLevel1() throws StudyTraversals.TraversalException{
        StudyValidation.validateAttributeLevel(study, metadataType);
    }

    @Test(expected = StudyTraversals.TraversalException.class)
    public void testValidateAttributeLevel2() throws StudyTraversals.TraversalException{
        final Attribute attr = new Attribute();
        attr.setTag(0x00010003);
        study.putAttribute(attr);
        StudyValidation.validateAttributeLevel(study, metadataType);
    }

    @Test
    public void testValidateDICOMVRValueExistence1() throws StudyTraversals.TraversalException{
        StudyValidation.validateDICOMVRValueExistence(study);
    }

    @Test(expected = StudyTraversals.TraversalException.class)
    public void testValidateDICOMVRValueExistence2() throws StudyTraversals.TraversalException{
        study.attributeIterator().next().setVr(null);
        StudyValidation.validateDICOMVRValueExistence(study);
    }

    @Test
    public void testValidateDICOMTransferSyntax1() throws StudyTraversals.TraversalException{
        StudyValidation.validateDICOMTransferSyntax(study);
    }

    @Test
    public void testValidateBinaryItemsReferences1() throws StudyTraversals.TraversalException {
        final Collection<Integer> binaryItemIds = Arrays.asList(0);
        StudyValidation.validateBinaryItemsReferences(study, binaryItemIds);
    }

    @Test(expected = StudyTraversals.TraversalException.class)
    public void testValidateBinaryItemsReferences2() throws StudyTraversals.TraversalException {
        final Collection<Integer> binaryItemIds = Arrays.asList(1);
        StudyValidation.validateBinaryItemsReferences(study, binaryItemIds);
    }

    @Test(expected = StudyTraversals.TraversalException.class)
    public void testValidateBinaryItemsReferences3() throws StudyTraversals.TraversalException {
        final Collection<Integer> binaryItemIds = Arrays.asList(0, 1);
        StudyValidation.validateBinaryItemsReferences(study, binaryItemIds);
    }

    @Test(expected = StudyTraversals.TraversalException.class)
    public void testValidateBinaryItemsReferences4() throws StudyTraversals.TraversalException {
        final Collection<Integer> binaryItemIds = Collections.emptyList();
        StudyValidation.validateBinaryItemsReferences(study, binaryItemIds);
    }

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
