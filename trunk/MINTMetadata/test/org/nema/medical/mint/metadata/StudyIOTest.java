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
package org.nema.medical.mint.metadata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.Test;
import org.nema.medical.mint.metadata.Attribute;
import org.nema.medical.mint.metadata.Instance;
import org.nema.medical.mint.metadata.Item;
import org.nema.medical.mint.metadata.Series;
import org.nema.medical.mint.metadata.Study;
import org.nema.medical.mint.metadata.StudyIO;

public class StudyIOTest {

	static final File jibxFile = createTempFile("study-", ".xml");
	static final File compressedJibxFile = createTempFile("study-", ".xml.gz");
	static final File gpbFile = createTempFile("study-", ".gpb");
	static final File compressedGpbFile = createTempFile("study-", ".gpb.gz");

	@Test
	public void testJibxMarshal() throws Exception {
		StudyIO.writeToXML(getStudy(), jibxFile);
	}

	@Test
	public void testCompressedJibxMarshal() throws Exception {
		StudyIO.writeToXML(getStudy(), compressedJibxFile);
	}

	@Test
	public void testJibxUnmarshal() throws Exception {
		Study study = StudyIO.parseFromXML(jibxFile);
		assertNotNull(study);
	}

	@Test
	public void testCompressedJibxUnmarshal() throws Exception {
		Study study = StudyIO.parseFromXML(compressedJibxFile);
		assertNotNull(study);
	}

	@Test
	public void testGpbMarshal() throws Exception {
		StudyIO.writeToGPB(getStudy(), gpbFile);
	}

	@Test
	public void testCompressedGpbMarshal() throws Exception {
		StudyIO.writeToGPB(getStudy(), compressedGpbFile);
	}
	
	@Test
	public void testGpbUnmarshal() throws Exception {
		Study study = StudyIO.parseFromGPB(gpbFile);
		assertNotNull(study);
	}
	
	@Test
	public void testCompressedGpbUnmarshal() throws Exception {
		Study study = StudyIO.parseFromGPB(compressedGpbFile);
		assertNotNull(study);
	}
	
	@Test
	public void textHex2Int() {
		assertEquals("7fffffff", StudyIO.int2hex(Integer.MAX_VALUE));
		assertEquals(Integer.MAX_VALUE,StudyIO.hex2int("7fffffff"));
		
		assertEquals("80000000", StudyIO.int2hex(Integer.MIN_VALUE));
		assertEquals(Integer.MIN_VALUE,StudyIO.hex2int("80000000"));
		
		assertEquals("00000000", StudyIO.int2hex(0));
		assertEquals(0,StudyIO.hex2int("00000000"));
		
		assertEquals("00000001", StudyIO.int2hex(1));
		assertEquals(1,StudyIO.hex2int("00000001"));

		assertEquals("ffffffff", StudyIO.int2hex(-1));
		assertEquals(-1,StudyIO.hex2int("FFFFFFFF"));
		
		assertEquals("7cdea9ff", StudyIO.int2hex(2094967295));
		assertEquals(2094967295,StudyIO.hex2int("7cdea9ff"));
		
		assertEquals("0000000b", StudyIO.int2hex(11));
		assertEquals(11,StudyIO.hex2int("0000000b"));		
	}

	@Test //(expected=NumberFormatException.class)
	public void testHex2IntOverflow() {
		try {
			StudyIO.hex2int("1ffffffff");
			// necessary because maven surefire ignores expected annotation param
			fail("expected NumberFormatException.class");
		} catch (NumberFormatException e){
			// expected
		}
	}

	
	
	private static File createTempFile(String prefix, String suffix) {
		File tempFile = null;
		try {
			File dir = new File(System.getProperty("user.dir") + "/build/temp");
			dir.mkdirs();
			tempFile = File.createTempFile(prefix, suffix, dir);
			//tempFile.deleteOnExit();
		} catch (IOException e) {
			// fumble -- this will probably cause NPE in the caller
		}
		return tempFile;
	}
	

	private Study getStudy() {
		Study study = new Study();
		ArrayList<Instance> instances = new ArrayList<Instance>();

		study.setStudyInstanceUID("1");

		Attribute attr = new Attribute();
		attr.setBid(1);
		attr.setTag(99999999);
		attr.setVr("OW");
		attr.setVal("1.3.12.2.1107.5.1.4.54587.30000010042317125468700002159");
		study.putAttribute(attr);

		Attribute itemAttr1 = new Attribute();
		itemAttr1.setBid(0);
		itemAttr1.setTag("07fe0010");
		itemAttr1.setVr("OW");
		itemAttr1.setVal("1.3.12.2.1107.5.1.4.54587.30000010042317125468700002159");

		Attribute itemAttr2 = new Attribute();
		itemAttr2.setBid(1);
		itemAttr2.setTag("07fe0011");
		itemAttr2.setVr("OW");
		itemAttr2.setVal("1.3.12.2.1107.5.1.4.54587.30000010042317125468700002159");

		Attribute itemAttr3 = new Attribute();
		itemAttr3.setTag("00020003");
		itemAttr3.setVr("UI");
		itemAttr3.setVal("1.3.12.2.1107.5.1.4.54587.30000010042317125468700002159");

		Item item = new Item();
		item.putAttribute(itemAttr1);
		item.putAttribute(itemAttr2);
		item.putAttribute(itemAttr3);
		attr.addItem(item);

		Series series = new Series();

		study.putSeries(series);
		
		series.setSeriesInstanceUID("2.16.840.1.114255.393386359.389044243.31141.56");
		series.putAttribute(attr);
		series.putNormalizedInstanceAttribute(attr);

		Instance instance = new Instance();
		instance.putAttribute(itemAttr1);
		instance.putAttribute(itemAttr2);
		instance.setSOPInstanceUID("3.4.123.1023.12.2.1");
		instance.setTransferSyntaxUID("1.2.840.10008.1.2.1");
		instances.add(instance);
		instances.add(instance);

		series.putInstance(instance);

		return study;
	}
}
