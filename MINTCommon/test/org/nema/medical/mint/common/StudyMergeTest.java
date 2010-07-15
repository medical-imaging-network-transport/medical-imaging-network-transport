package org.nema.medical.mint.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.nema.medical.mint.metadata.Study;

/**
 * @author Rex
 *
 */
public class StudyMergeTest {
	static final Logger LOG = Logger.getLogger(StudyMergeTest.class);

	@Test
	public void testNothing() throws Exception {
		// simple test to provide junit infrastructure - replace this
		// when study merge functionality exists
		assertEquals(0,0);
		assertNotNull("stupid test");
	}
	
	@Test
	public void testLoadStudy() throws IOException
	{
		File studyDicomDir = getStudyDicomDir();
		
		Study study = StudyUtil.loadStudy(studyDicomDir);
		
		assertNotNull(study);
	}
	
	@Test
	public void testGetHighestNumberedBinaryItem()
	{
		File binaryItems = new File(getStudyDicomDir(), "binaryitems");
		
		assertTrue(binaryItems.exists());
		
		int highestItem = StudyUtil.getHighestNumberedBinaryItem(binaryItems);
		
		String[] binaryItemNames = binaryItems.list();
		
		//Either there are 0 files, or only metadata and no binary, or binary and highestItem should then be > -1
		assertTrue(binaryItemNames.length == 0
				|| (binaryItemNames.length == 1 && binaryItemNames[0]
						.startsWith("metadata")) ? 
						highestItem == -1 : highestItem > -1);
		
		LOG.info("Highest Item found in '" + binaryItems.getPath() + "' is: " + highestItem);
	}
	
	/**
	 * Don't care which study, all of them SHOULD work. This should drill down
	 * to the DICOM folder level where the metadata should be.
	 * 
	 * @return
	 */
	private File getStudyDicomDir()
	{
		String mintHome = System.getenv("MINT_HOME");
		
		assertNotNull(mintHome);
		
		File studiesRoot = new File(mintHome, "studies");
		
		assertTrue(studiesRoot.exists());
		
		
		File studyDir = studiesRoot.listFiles()[0];
		
		assertTrue(studyDir.exists());
		
		File studyDicomDir = studyDir.listFiles()[0];
		
		assertTrue(studyDicomDir.exists());
		
		return studyDicomDir;
	}

}
