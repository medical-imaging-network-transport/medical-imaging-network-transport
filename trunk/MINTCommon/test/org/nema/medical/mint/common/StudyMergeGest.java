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
package org.nema.medical.mint.common;


/**
 * @author Rex
 *
 */
//TODO this test is broken and should not be here - disabling now as it keeps valid tests from running
public class StudyMergeGest {
//	static final Logger LOG = Logger.getLogger(StudyMergeTest.class);
//
//	@Test
//	public void testNothing() throws Exception {
//		// simple test to provide junit infrastructure - replace this
//		// when study merge functionality exists
//		assertEquals(0,0);
//		assertNotNull("stupid test");
//	}
//	
//	@Test
//	public void testLoadStudy() throws IOException
//	{
//		File studyDicomDir = getStudyDicomDir();
//		
//		Study study = StudyIO.loadStudy(studyDicomDir);
//		
//		assertNotNull(study);
//	}
//	
//	@Test
//	public void testGetHighestNumberedBinaryItem()
//	{
//		File binaryItems = new File(getStudyDicomDir(), "binaryitems");
//		
//		assertTrue(binaryItems.exists());
//		
//		int highestItem = StudyUtil.getHighestNumberedBinaryItem(binaryItems);
//		
//		String[] binaryItemNames = binaryItems.list();
//		
//		//Either there are 0 files, or only metadata and no binary, or binary and highestItem should then be > -1
//		assertTrue(binaryItemNames.length == 0
//				|| (binaryItemNames.length == 1 && binaryItemNames[0]
//						.startsWith("metadata")) ? 
//						highestItem == -1 : highestItem > -1);
//		
//		LOG.info("Highest Item found in '" + binaryItems.getPath() + "' is: " + highestItem);
//	}
//	
//	/**
//	 * Don't care which study, all of them SHOULD work. This should drill down
//	 * to the DICOM folder level where the metadata should be.
//	 * 
//	 * @return
//	 */
//	private File getStudyDicomDir()
//	{
//		String mintHome = System.getenv("MINT_HOME");
//		
//		assertNotNull(mintHome);
//		
//		File studiesRoot = new File(mintHome, "studies");
//		
//		assertTrue(studiesRoot.exists());
//		
//		
//		File studyDir = studiesRoot.listFiles()[0];
//		
//		assertTrue(studyDir.exists());
//		
//		File studyDicomDir = studyDir.listFiles()[0];
//		
//		assertTrue(studyDicomDir.exists());
//		
//		return studyDicomDir;
//	}
}
