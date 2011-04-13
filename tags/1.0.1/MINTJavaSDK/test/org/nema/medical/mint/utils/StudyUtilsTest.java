/*
 *   Copyright 2010 MINT Working Group
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.nema.medical.mint.utils;

import org.junit.*;
import org.nema.medical.mint.datadictionary.*;
import org.nema.medical.mint.metadata.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * @author Uli Bubenheimer
 */
public class StudyUtilsTest {
    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testMergeStudy() {
        //TODO
    }

    @Test
    public void testApplyExcludes() {
        //TODO
    }

    @Test
    public void testShiftStudyBids() {
        //TODO
    }

    @Test
    public void testRemoveStudyExcludes() {
        //TODO
    }

    @Test
    public void testIsExclude() {
        //TODO
    }

    @Test
    public void testDenormalizeStudy() {
        //TODO must add tests
    }


    //TODO must add more tests for normalizeStudy() method

    /**
     * Test that a DICOM attribute that's the same in two instances will be normalized.
     * @throws java.io.IOException in case of I/O problem
     */
    @Test
    public void testNormalizeSameAttributes() throws IOException {
        final String xml =
                "<study xmlns='http://medical.nema.org/mint' studyInstanceUID='1' type='DICOM'>" +
                        "  <attributes/>" +
                        "  <seriesList>" +
                        "    <series seriesInstanceUID='2'>" +
                        "      <attributes/>" +
                        "      <normalizedInstanceAttributes/>" +
                        "      <instances>" +
                        "        <instance sopInstanceUID='3' transferSyntaxUID='1.2.840.10008.1.2'>" +
                        "          <attributes>" +
                        "            <attr tag='00281200' vr='US' val='10'/>" +
                        "          </attributes>" +
                        "        </instance>" +
                        "        <instance sopInstanceUID='4' transferSyntaxUID='1.2.840.10008.1.2'>" +
                        "          <attributes>" +
                        "            <attr tag='00281200' vr='US' val='10'/>" +
                        "          </attributes>" +
                        "        </instance>" +
                        "      </instances>" +
                        "    </series>" +
                        "  </seriesList>" +
                        "</study>";
        final StudyMetadata study = StudyIO.parseFromXML(new ByteArrayInputStream(xml.getBytes()));
        StudyUtils.normalizeStudy(study);
        final Series series = study.seriesIterator().next();
        final Attribute normalizedAttr = series.normalizedInstanceAttributeIterator().next();
        final Attribute correctAttr = new Attribute();
        correctAttr.setTag(0x00281200);
        correctAttr.setVr("US");
        correctAttr.setVal("10");
        assertEquals(correctAttr, normalizedAttr);
        assertNull(series.getInstance("3").getAttribute(0x00281200));
        assertNull(series.getInstance("4").getAttribute(0x00281200));
    }

    /**
     * Test that a DICOM attribute with two different non-binary VRs for the same tag in two instances
     * will not be normalized.
     * @throws java.io.IOException in case of I/O problem
     */
    @Test
    public void testNormalizeChangingNonBinaryVR() throws IOException {
        final String xml =
                "<study xmlns='http://medical.nema.org/mint' studyInstanceUID='1' type='DICOM'>" +
                        "  <attributes/>" +
                        "  <seriesList>" +
                        "    <series seriesInstanceUID='2'>" +
                        "      <attributes/>" +
                        "      <normalizedInstanceAttributes/>" +
                        "      <instances>" +
                        "        <instance sopInstanceUID='3' transferSyntaxUID='1.2.840.10008.1.2'>" +
                        "          <attributes>" +
                        "            <attr tag='00281200' vr='US' val='0'/>" +
                        "          </attributes>" +
                        "        </instance>" +
                        "        <instance sopInstanceUID='4' transferSyntaxUID='1.2.840.10008.1.2'>" +
                        "          <attributes>" +
                        "            <attr tag='00281200' vr='SS' val='0'/>" +
                        "          </attributes>" +
                        "        </instance>" +
                        "      </instances>" +
                        "    </series>" +
                        "  </seriesList>" +
                        "</study>";
        final StudyMetadata study = StudyIO.parseFromXML(new ByteArrayInputStream(xml.getBytes()));
        StudyUtils.normalizeStudy(study);
        final Series series = study.seriesIterator().next();
        assertFalse(series.normalizedInstanceAttributeIterator().hasNext());
        assertNotNull(series.getInstance("3").getAttribute(0x00281200));
        assertNotNull(series.getInstance("4").getAttribute(0x00281200));
    }

    /**
     * Test that a DICOM attribute with two different VRs, one binary, one non-binary, for the same tag in two instances
     * will not be normalized.
     * @throws java.io.IOException in case of I/O problem
     */
    @Test
    public void testNormalizeChangingBinaryNonBinaryVR() throws IOException {
        final String xml =
                "<study xmlns='http://medical.nema.org/mint' studyInstanceUID='1' type='DICOM'>" +
                        "  <attributes/>" +
                        "  <seriesList>" +
                        "    <series seriesInstanceUID='2'>" +
                        "      <attributes/>" +
                        "      <normalizedInstanceAttributes/>" +
                        "      <instances>" +
                        "        <instance sopInstanceUID='3' transferSyntaxUID='1.2.840.10008.1.2'>" +
                        "          <attributes>" +
                        "            <attr tag='00281200' vr='US' val='0'/>" +
                        "          </attributes>" +
                        "        </instance>" +
                        "        <instance sopInstanceUID='4' transferSyntaxUID='1.2.840.10008.1.2'>" +
                        "          <attributes>" +
                        "            <attr tag='00281200' vr='OW' val='MA=='/>" +
                        "          </attributes>" +
                        "        </instance>" +
                        "      </instances>" +
                        "    </series>" +
                        "  </seriesList>" +
                        "</study>";
        final StudyMetadata study = StudyIO.parseFromXML(new ByteArrayInputStream(xml.getBytes()));
        StudyUtils.normalizeStudy(study);
        final Series series = study.seriesIterator().next();
        assertFalse(series.normalizedInstanceAttributeIterator().hasNext());
        assertNotNull(series.getInstance("3").getAttribute(0x00281200));
        assertNotNull(series.getInstance("4").getAttribute(0x00281200));
    }

    @Test
    public void testEqualNonBinaryAttributes() {
        assertTrue(StudyUtils.equalNonBinaryAttributes(null, null));
        final Attribute a1 = new Attribute();
        final Attribute a2 = new Attribute();
        assertFalse(StudyUtils.equalNonBinaryAttributes(a1, null));
        assertFalse(StudyUtils.equalNonBinaryAttributes(null, a1));
        assertTrue(StudyUtils.equalNonBinaryAttributes(a1, a2));
        a1.setTag(0x00080002);
        assertFalse(StudyUtils.equalNonBinaryAttributes(a1, a2));
        a2.setTag(0x00080002);
        assertTrue(StudyUtils.equalNonBinaryAttributes(a1, a2));
    }

    @Test
    public void testIsBinaryVR() {
        assertTrue(StudyUtils.isBinaryVR("SQ"));
        assertTrue(StudyUtils.isBinaryVR("OW"));
        assertTrue(StudyUtils.isBinaryVR("OB"));
        assertTrue(StudyUtils.isBinaryVR("OF"));
        assertTrue(StudyUtils.isBinaryVR("UN"));
        assertTrue(StudyUtils.isBinaryVR("??"));

        assertFalse(StudyUtils.isBinaryVR(""));
        assertFalse(StudyUtils.isBinaryVR(null));
        assertFalse(StudyUtils.isBinaryVR("ST"));
        assertFalse(StudyUtils.isBinaryVR("UI"));
    }

    @Test
    public void testIsNonBinaryFloatVR() {
        assertTrue(StudyUtils.isNonBinaryFloatVR("FL"));
        assertTrue(StudyUtils.isNonBinaryFloatVR("FD"));

        assertFalse(StudyUtils.isNonBinaryFloatVR(""));
        assertFalse(StudyUtils.isNonBinaryFloatVR(null));
        assertFalse(StudyUtils.isNonBinaryFloatVR("ST"));
        assertFalse(StudyUtils.isNonBinaryFloatVR("OF"));
    }

    @Test
    public void testReverse() {
        {
            final byte[] a = new byte[]{1,2,3};
            StudyUtils.reverse(a);
            assertArrayEquals(new byte[]{3,2,1}, a);
        }

        {
            final byte[] a = new byte[]{1};
            StudyUtils.reverse(a);
            assertArrayEquals(new byte[]{1}, a);
        }

        {
            final byte[] a = new byte[0];
            StudyUtils.reverse(a);
            assertArrayEquals(new byte[0], a);
        }
    }

    @Test
    public void testStandardizedAttribute() {
        final Attribute attr1 = new Attribute();
        attr1.setVr("FL");
        attr1.setBytes(new byte[] {1,2});
        Attribute attr2;
        attr2 = StudyUtils.standardizedAttribute(attr1, true);
        assertThat(attr1, is(not(attr2)));
        StudyUtils.reverse(attr2.getBytes());
        assertThat(attr1, is(attr2));
        attr2 = StudyUtils.standardizedAttribute(attr1, false);
        assertThat(attr1, is(attr2));
        attr1.setVr("OF");
        attr2 = StudyUtils.standardizedAttribute(attr1, true);
        assertThat(attr1, is(attr2));
        attr1.setBytes(null);
        attr2 = StudyUtils.standardizedAttribute(attr1, true);
        assertThat(attr1, is(attr2));
        attr1.setBytes(new byte[0]);
        attr2 = StudyUtils.standardizedAttribute(attr1, true);
        assertThat(attr1, is(attr2));
        attr1.setBytes(new byte[]{1});
        attr2 = StudyUtils.standardizedAttribute(attr1, true);
        assertThat(attr1, is(attr2));
    }

    @Test
    public void testWriteStudy() {
        //TODO
    }

    @Test
    public void testGetBaseVersion() {
        //We probably shouldn't just change what version a study starts with, so having a test for this makes sense
        assertEquals("0", StudyUtils.getBaseVersion());
    }

    @Test
    public void testGetNextVersion() {
        assertEquals("1", StudyUtils.getNextVersion("0"));
        assertEquals("10", StudyUtils.getNextVersion("9"));
    }

    @Test
    public void testTagString() {
        assertEquals("0A001000", StudyUtils.tagString(167776256));
    }
}
