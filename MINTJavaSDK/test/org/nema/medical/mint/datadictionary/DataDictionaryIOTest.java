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
		dataDictionary.setType("DICOM");
		dataDictionary.setVersion(1.0f);
		AttributesType attributes = new AttributesType();
		attributes.setUnknownAttributes("include/ignore/reject?");
		
		
		//Set up the <metadata><attributes> tag
		ArrayList<ElementType> elements = new ArrayList<ElementType>();
		
		//Set up the <metadata><attributes><elements> tags
		ElementType et1 = new ElementType();
		et1.setTag("00000000");
		et1.setKeyword("CommandGroupLength");
		et1.setVr("UL");
		et1.setVm("1");
		et1.setRet("");
		et1.setString("Command Group Length");
		
		ElementType et2 = new ElementType();
		et2.setTag("00000001");
		et2.setKeyword("CommandLengthToEnd");
		et2.setVr("UL");
		et2.setVm("1");
		et2.setRet("RET");
		et2.setString("Length to End");
		
		elements.add(et1);
		elements.add(et2);
		attributes.setElements(elements);
		
		
		//Set up the <metadata><study-attributes> tag
		StudyAttributesType studyAttributes = new StudyAttributesType();
		
		//Set up the <metadata><study-attributes><attribute> tags
		ArrayList<AttributeType> studyAttributesList = new ArrayList<AttributeType>();
		AttributeType at1 = new AttributeType();
		at1.setTag("00080020");
		at1.setDesc("Study Date");
	    at1.setString("");
	    
		AttributeType at2 = new AttributeType();
		at2.setTag("00080030");
		at2.setDesc("Study Time");
		at2.setString("");
		
		studyAttributesList.add(at1);
		studyAttributesList.add(at2);
		
		studyAttributes.setAttributes(studyAttributesList);
		
		
		//Set up the <metadata><series-attributes> tag
		SeriesAttributesType seriesAttributes = new SeriesAttributesType();
		
		//Set up the <metadata><series-attributes><attribute> tags
		ArrayList<AttributeType> seriesAttributesList = new ArrayList<AttributeType>();
		AttributeType at11 = new AttributeType();
		at11.setTag("00080020");
		at11.setDesc("Study Date");
		at11.setString("");
		
		AttributeType at22 = new AttributeType();
		at22.setTag("00080030");
		at22.setDesc("Study Time");
		at22.setString("");
		
		seriesAttributesList.add(at11);
		seriesAttributesList.add(at22);
		
		seriesAttributes.setAttributes(seriesAttributesList);
		
		
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
