package org.nema.medical.mint.metadata;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.junit.Test;

public class OptionalMetadataAttributeTest {
	
	@Test 
	public void testStudyMetaTypeAttributesPresent() throws IOException {
		final String xml = 
			"<study xmlns='http://medical.nema.org/mint' studyInstanceUID='1.2.3.4.5' type='DICOM'>" +
            "  <attributes>" +
            "    <attr tag='00080020' vr='DA' val='20101123' bid='1' bsize='64' framecount='2'/>" +
            "    <attr tag='00080051' vr='SQ'>" +
            "      <item>" +
            "        <attributes>" +
            "          <attr tag='00081150' vr='UI' val='1.2.840.10008.5.1.4.1.1.2'/>" +
            "        </attributes>" +
            "      </item>" +
            "    </attr>" +
            "  </attributes>" +
            "</study>";
		StudyIO.parseFromXML(new ByteArrayInputStream(xml.getBytes()));
	}
	
	@Test
	public void testStudyMetaTypeAttributesAbsent() throws IOException {
		final String xml = 
			"<study xmlns='http://medical.nema.org/mint' studyInstanceUID='1.2.3.4.5' type='DICOM'>" +
            "</study>";
        StudyIO.parseFromXML(new ByteArrayInputStream(xml.getBytes()));
	}
	
	@Test
	public void testStudyMetaTypeSeriesListPresent() throws IOException {
        final String xml =
            "<study xmlns='http://medical.nema.org/mint' studyInstanceUID='1.2.3.4.5' type='DICOM'>" +
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
        StudyIO.parseFromXML(new ByteArrayInputStream(xml.getBytes()));
	}
	
	@Test
	public void testStudyMetaTypeSeriesListAbsent() throws IOException {
        final String xml =
            "<study xmlns='http://medical.nema.org/mint' studyInstanceUID='1.2.3.4.5' type='DICOM'>" +
            "</study>";
        StudyIO.parseFromXML(new ByteArrayInputStream(xml.getBytes()));
	}
	
	@Test
	public void testAttributeItemPresent() throws IOException {
		 final String xml =
	            "<study xmlns='http://medical.nema.org/mint' studyInstanceUID='1.2.3.4.5' type='DICOM'>" +
	            "  <attributes>" +
	            "    <attr tag='00080051' vr='SQ'>" +
	            "      <item>" +
	            "        <attributes>" +
	            "          <attr tag='00081150' vr='UI' val='1.2.840.10008.5.1.4.1.1.2'/>" +
	            "        </attributes>" +
	            "      </item>" +
	            "    </attr>" +
	            "  </attributes>" +
	            "</study>";
	        StudyIO.parseFromXML(new ByteArrayInputStream(xml.getBytes()));
	}
	
	@Test
	public void testAttributeItemAbsent() throws IOException {
		 final String xml =
	            "<study xmlns='http://medical.nema.org/mint' studyInstanceUID='1.2.3.4.5' type='DICOM'>" +
	            "  <attributes>" +
	            "    <attr tag='00080051' vr='SQ'>" +
	            "    </attr>" +
	            "  </attributes>" +
	            "</study>";
	        StudyIO.parseFromXML(new ByteArrayInputStream(xml.getBytes()));
	}
	
	@Test
	public void testAttributeBidPresent() throws IOException {
        final String xml =
            "<study xmlns='http://medical.nema.org/mint' studyInstanceUID='1.2.3.4.5' type='DICOM'>" +
            "  <attributes>" +
            "    <attr tag='00080020' bid='1'/>" +
            "  </attributes>" +
            "</study>";
        StudyIO.parseFromXML(new ByteArrayInputStream(xml.getBytes()));
	}
	
	@Test
	public void testAttributeBidAbsent() throws IOException {
        final String xml =
            "<study xmlns='http://medical.nema.org/mint' studyInstanceUID='1.2.3.4.5' type='DICOM'>" +
            "  <attributes>" +
            "    <attr tag='00080020'/>" +
            "  </attributes>" +
            "</study>";
        StudyIO.parseFromXML(new ByteArrayInputStream(xml.getBytes()));
	}
	
	@Test
	public void testAttributeBsizePresent() throws IOException {
        final String xml =
            "<study xmlns='http://medical.nema.org/mint' studyInstanceUID='1.2.3.4.5' type='DICOM'>" +
            "  <attributes>" +
            "    <attr tag='00080020' bsize='64'/>" +
            "  </attributes>" +
            "</study>";
        StudyIO.parseFromXML(new ByteArrayInputStream(xml.getBytes()));
	}
	
	@Test
	public void testAttributeBsizeAbsent() throws IOException {
        final String xml =
            "<study xmlns='http://medical.nema.org/mint' studyInstanceUID='1.2.3.4.5' type='DICOM'>" +
            "  <attributes>" +
            "    <attr tag='00080020'/>" +
            "  </attributes>" +
            "</study>";
        StudyIO.parseFromXML(new ByteArrayInputStream(xml.getBytes()));
	}
	
	@Test
	public void testAttributeFramecountPresent() throws IOException {
        final String xml =
            "<study xmlns='http://medical.nema.org/mint' studyInstanceUID='1.2.3.4.5' type='DICOM'>" +
            "  <attributes>" +
            "    <attr tag='00080020' framecount='2'/>" +
            "  </attributes>" +
            "</study>";
        StudyIO.parseFromXML(new ByteArrayInputStream(xml.getBytes()));
	}
	
	@Test
	public void testAttributeFramecountAbsent() throws IOException {
        final String xml =
            "<study xmlns='http://medical.nema.org/mint' studyInstanceUID='1.2.3.4.5' type='DICOM'>" +
            "  <attributes>" +
            "    <attr tag='00080020'/>" +
            "  </attributes>" +
            "</study>";
        StudyIO.parseFromXML(new ByteArrayInputStream(xml.getBytes()));
	}
	
	@Test
	public void testSeriesAttributesPresent() throws IOException {
		final String xml =
            "<study xmlns='http://medical.nema.org/mint' studyInstanceUID='1.2.3.4.5' type='DICOM'>" +
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
            "    </series>" +
            "  </seriesList>" +
            "</study>";
        StudyIO.parseFromXML(new ByteArrayInputStream(xml.getBytes()));
	}
	
	@Test
	public void testSeriesAttributesAbsent() throws IOException {
		final String xml =
            "<study xmlns='http://medical.nema.org/mint' studyInstanceUID='1.2.3.4.5' type='DICOM'>" +
            "  <seriesList>" +
            "    <series seriesInstanceUID='9.8.7.6'>" +
            "    </series>" +
            "  </seriesList>" +
            "</study>";
        StudyIO.parseFromXML(new ByteArrayInputStream(xml.getBytes()));
	}
	
	@Test
	public void testSeriesNormalizedInstanceAttributesPresent() throws IOException {
        final String xml =
            "<study xmlns='http://medical.nema.org/mint' studyInstanceUID='1.2.3.4.5' type='DICOM'>" +
            "  <seriesList>" +
            "    <series seriesInstanceUID='9.8.7.6'>" +
            "      <normalizedInstanceAttributes>" +
            "        <attr tag='00080008' vr='CS' val='ORIGINAL\\PRIMARY\\AXIAL'/>" +
            "      </normalizedInstanceAttributes>" +
            "    </series>" +
            "  </seriesList>" +
            "</study>";
        StudyIO.parseFromXML(new ByteArrayInputStream(xml.getBytes()));
	}
	
	@Test
	public void testSeriesNormalizedInstanceAttributesAbsent() throws IOException {
        final String xml =
            "<study xmlns='http://medical.nema.org/mint' studyInstanceUID='1.2.3.4.5' type='DICOM'>" +
            "  <seriesList>" +
            "    <series seriesInstanceUID='9.8.7.6'>" +
            "    </series>" +
            "  </seriesList>" +
            "</study>";
        StudyIO.parseFromXML(new ByteArrayInputStream(xml.getBytes()));
	}
	
	@Test
	public void testSeriesInstancesPresent() throws IOException {
        final String xml =
            "<study xmlns='http://medical.nema.org/mint' studyInstanceUID='1.2.3.4.5' type='DICOM'>" +
            "  <seriesList>" +
            "    <series seriesInstanceUID='9.8.7.6'>" +
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
        StudyIO.parseFromXML(new ByteArrayInputStream(xml.getBytes()));
	}
	
	@Test
	public void testSeriesInstancesAbsent() throws IOException {
        final String xml =
            "<study xmlns='http://medical.nema.org/mint' studyInstanceUID='1.2.3.4.5' type='DICOM'>" +
            "  <seriesList>" +
            "    <series seriesInstanceUID='9.8.7.6'>" +
            "    </series>" +
            "  </seriesList>" +
            "</study>";
        StudyIO.parseFromXML(new ByteArrayInputStream(xml.getBytes()));
	}
	
	@Test
	public void testInstanceAttributesPresent() throws IOException {
        final String xml =
            "<study xmlns='http://medical.nema.org/mint' studyInstanceUID='1.2.3.4.5' type='DICOM'>" +
            "  <seriesList>" +
            "    <series seriesInstanceUID='9.8.7.6'>" +
            "      <instances>" +
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
        StudyIO.parseFromXML(new ByteArrayInputStream(xml.getBytes()));
	}
	
	@Test
	public void testInstanceAttributesAbsent() throws IOException {
        final String xml =
            "<study xmlns='http://medical.nema.org/mint' studyInstanceUID='1.2.3.4.5' type='DICOM'>" +
            "  <seriesList>" +
            "    <series seriesInstanceUID='9.8.7.6'>" +
            "      <instances>" +
            "        <instance sopInstanceUID='1.0.9.8' transferSyntaxUID='1.2.840.10008.1.2.1'>" +
            "        </instance>" +
            "      </instances>" +
            "    </series>" +
            "  </seriesList>" +
            "</study>";
        StudyIO.parseFromXML(new ByteArrayInputStream(xml.getBytes()));
	}
	
	@Test
	public void testItemAttributesPresent() throws IOException {
        final String xml =
            "<study xmlns='http://medical.nema.org/mint' studyInstanceUID='1.2.3.4.5' type='DICOM'>" +
            "  <attributes>" +
            "    <attr tag='00080051' vr='SQ'>" +
            "      <item>" +
            "        <attributes>" +
            "          <attr tag='00081150' vr='UI' val='1.2.840.10008.5.1.4.1.1.2'/>" +
            "        </attributes>" +
            "      </item>" +
            "    </attr>" +
            "  </attributes>" +
            "</study>";
        StudyIO.parseFromXML(new ByteArrayInputStream(xml.getBytes()));
	}
	
	@Test
	public void testItemAttributesAbsent() throws IOException {
       final String xml =
            "<study xmlns='http://medical.nema.org/mint' studyInstanceUID='1.2.3.4.5' type='DICOM'>" +
            "  <attributes>" +
            "    <attr tag='00080051' vr='SQ'>" +
            "      <item>" +
            "      </item>" +
            "    </attr>" +
            "  </attributes>" +
            "</study>";
        StudyIO.parseFromXML(new ByteArrayInputStream(xml.getBytes()));
	}
}
