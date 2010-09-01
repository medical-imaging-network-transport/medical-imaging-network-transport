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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.nema.medical.mint.metadata.Study;
import org.nema.medical.mint.metadata.StudyIO;

/**
 * @author Rex
 * 
 */
public class StudyUtilTest {
	static final Logger LOG = Logger.getLogger(StudyUtilTest.class);

	/**
	 * Test the ability to load a study. Requires there to be at least one study
	 * on this machine to test correctly.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testLoadStudy() throws IOException {
		File studyDicomDir = getStudyDicomDir();

		if (studyDicomDir == null || !studyDicomDir.exists()) {
			LOG
					.error("Could not find a study folder if run test on, test failed but will not JUnit fail because will assume this failure occured because there are not study folders on the machine this was run on.");
		} else {
			Study study = StudyIO.loadStudy(studyDicomDir);

			assertNotNull(study);
		}
	}

	/**
	 * Test the ability to find the highest binary item file for any given
	 * study. Requires there to be at least one study on this machine to test
	 * correctly.
	 */
	@Test
	public void testGetHighestNumberedBinaryItem() {
		File studyDicomDir = getStudyDicomDir();

		if (studyDicomDir == null || !studyDicomDir.exists()) {
			LOG
					.error("Could not find a study folder if run test on, test failed but will not JUnit fail because will assume this failure occured because there are not study folders on the machine this was run on.");
		} else {
			File binaryItems = new File(studyDicomDir, "binaryitems");

			assertTrue(binaryItems.exists());

			int highestItem = StudyUtil
					.getHighestNumberedBinaryItem(binaryItems);

			String[] binaryItemNames = binaryItems.list();

			// Either there are 0 files, or only metadata and no binary, or
			// binary and highestItem should then be > -1
			assertTrue(binaryItemNames.length == 0
					|| (binaryItemNames.length == 1 && binaryItemNames[0]
							.startsWith("metadata")) ? highestItem == -1
					: highestItem > -1);

			LOG.info("Highest Item found in '" + binaryItems.getPath()
					+ "' is: " + highestItem);
		}
	}

	/**
	 * Tests the version methods in StudyUtil.
	 */
	@Test
	public void testVersion() {
		String version = StudyUtil.getBaseVersion();

		assertTrue(StringUtils.isNotBlank(version));

		LOG.info("1st version is: " + version);

		version = StudyUtil.getNextVersion(version);

		assertTrue(StringUtils.isNotBlank(version));

		LOG.info("2nd version is: " + version);

		version = StudyUtil.getNextVersion(version);

		assertTrue(StringUtils.isNotBlank(version));

		LOG.info("3rd version is: " + version);
	}

	/**
	 * Don't care which study, all of them SHOULD work. This should drill down
	 * to the DICOM folder level where the metadata should be. Will return null
	 * if anything goes wrong.
	 * 
	 * @return
	 */
	private File getStudyDicomDir() {
		File studyDicomDir = null;

		try {
			String mintHome = System.getenv("MINT_HOME");

			File studiesRoot = new File(mintHome, "studies");

			File studyDir = studiesRoot.listFiles()[0];

			for(File f : studyDir.listFiles())
			{
				if(f.getName().equals("DICOM"))
					studyDicomDir = f;
			}
			
			LOG.info("Study folder found: " + studyDicomDir.toString());
		} catch (Exception e) {
			// Something failed so returning null
			LOG.error("Unable to find a study folder.", e);
		}

		return studyDicomDir;
	}
}
