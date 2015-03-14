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
package org.nema.medical.mint.datadictionary;

import static org.junit.Assert.assertNotNull;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import org.junit.Test;

/**
 * @author dan.chaffee
 * 
 */
public class DataDictionaryIOTest {

	
	static final File dicomDataDictionaryInstance = createTempFile("dataDictionary-", ".xml");
	@Test
	public void testMarshal() throws Exception {
		DataDictionaryIO.writeToXML(getDataDictionaryDocument(), dicomDataDictionaryInstance);
	}
	
	@Test
	public void testUnmarshal() throws Exception {
		MetadataType dicomDocument = DataDictionaryIO.parseFromXML(dicomDataDictionaryInstance);
		assertNotNull(dicomDocument);
	}
	
	
	private MetadataType getDataDictionaryDocument() {
		
		//Set up the <metadata> tag
		MetadataType dataDictionary = new MetadataType();
		dataDictionary.setVersion(new BigDecimal(1.0));
		AttributesType attributes = new AttributesType();
		attributes.setUnknownAttributes(AttributesType.UnknownAttribute.ACCEPT);

		//Set up the <metadata><attributes> tag
		ArrayList<ElementType> elements = new ArrayList<ElementType>();
		
		//Set up the <metadata><attributes><elements> tags
		ElementType et1 = new ElementType();
		et1.setTag(0x00000000);
		et1.setKeyword("CommandGroupLength");
		et1.setVr("UL");
		et1.setVm("1");
		et1.setRetired(false);
		et1.setString("Command Group Length");
        attributes.addElement(et1);
		
		ElementType et2 = new ElementType();
		et2.setTag(0x00000001);
		et2.setKeyword("CommandLengthToEnd");
		et2.setVr("UL");
		et2.setVm("1");
		et2.setRetired(true);
		et2.setString("Length to End");
		attributes.addElement(et2);

		//Set up the <metadata><study-attributes> tag
		LevelAttributes studyAttributes = new LevelAttributes();
		
		//Set up the <metadata><study-attributes><attribute> tags
        studyAttributes.addAttributeType(0x00080020, "Study Date");
        studyAttributes.addAttributeType(0x00080030, "Study Time");

		//Set up the <metadata><series-attributes> tag
		LevelAttributes seriesAttributes = new LevelAttributes();
		
		//Set up the <metadata><series-attributes><attribute> tags
        seriesAttributes.addAttributeType(0x00080020, "Study Date");
        seriesAttributes.addAttributeType(0x00080030, "Study Time");

		dataDictionary.setAttributes(attributes);
		dataDictionary.setStudyAttributes(studyAttributes);
		dataDictionary.setSeriesAttributes(seriesAttributes);

		return dataDictionary;
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
}
