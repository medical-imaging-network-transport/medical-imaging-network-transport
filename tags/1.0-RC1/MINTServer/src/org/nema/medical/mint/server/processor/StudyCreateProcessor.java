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
package org.nema.medical.mint.server.processor;

import java.io.File;
import java.util.TimerTask;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.nema.medical.mint.server.util.StorageUtil;
import org.nema.medical.mint.metadata.StudyMetadata;
import org.nema.medical.mint.metadata.StudyIO;
import org.nema.medical.mint.server.domain.*;
import org.nema.medical.mint.utils.StudyUtils;

public class StudyCreateProcessor extends TimerTask {

	static final Logger LOG = Logger.getLogger(StudyCreateProcessor.class);
	
	private final File jobFolder;
	private final File studyFolder;
	
	private final String type, remoteUser, remoteHost, principal;
	private final JobInfoDAO jobInfoDAO;
	private final StudyDAO studyDAO;
	private final ChangeDAO updateDAO;

	/**
	 * extracts files from the jobFolder, places them in the studyFolder updates
	 * the database
	 * 
	 * @param jobFolder
	 *            the folder containing the uploaded files - must contain
	 *            metadata.xml or metadata.gpb
	 * @param studyFolder
	 *            the target folder where the study will be created (will
	 *            contain a {type} subdir)
	 * @param type
	 *            the type of study being created
	 * @param jobInfoDAO
	 *            needed to update the database
	 * @param studyDAO
	 *            needed to update the database
	 */
	public StudyCreateProcessor(File jobFolder, File studyFolder, String type, String remoteUser, String remoteHost, String principal, JobInfoDAO jobInfoDAO, StudyDAO studyDAO, ChangeDAO updateDAO) {
		this.jobFolder = jobFolder;
		this.studyFolder = studyFolder;
		this.type = type;
		this.remoteUser = remoteUser;
		this.remoteHost = remoteHost;
		this.principal = principal;
		this.jobInfoDAO = jobInfoDAO;
		this.studyDAO = studyDAO;
		this.updateDAO = updateDAO;
	}
	
    @Override
    public void run() {
		String jobID = jobFolder.getName();
		String studyUUID = studyFolder.getName();
		
		JobInfo jobInfo = new JobInfo();
		jobInfo.setId(jobID);
		jobInfo.setStudyID(studyUUID);
		
		try {	
			File typeFolder = new File(studyFolder, type);
			typeFolder.mkdirs();
			File changelogRoot = new File(studyFolder, "changelog");
			changelogRoot.mkdirs();

			//load study into memory
			StudyMetadata study = StudyIO.loadStudy(jobFolder);
			LOG.info("job " + jobID + " loaded");
			
			if(!StorageUtil.validateStudy(study, jobFolder))
			{
				throw new RuntimeException("Validation of the new study failed");
			}
			LOG.info("job " + jobID + " validated");
			
            //Write metadata to change log
            File changelogFolder = StorageUtil.getNextChangelogDir(changelogRoot);

            StudyUtils.writeStudy(study, changelogFolder);
            LOG.info("study changelog for " + jobID + " written");

			// Set to base level version
            study.setVersion(StudyUtils.getBaseVersion());
	        study.setType(type);
			
			//write study into type folder
			StudyUtils.writeStudy(study, typeFolder);
			LOG.info("study metadata for " + jobID + " written");
	        
	        //Copy binary data into binaryitems folder
	        File binaryRoot = new File(typeFolder, "binaryitems");
			binaryRoot.mkdirs();

			LOG.info("moving binary items for " + jobID + " ");
			StudyUtils.moveBinaryItems(jobFolder, binaryRoot);
			LOG.info("moving binary items for " + jobID + " complete");
			
			//delete job folder
			StudyUtils.deleteFolder(jobFolder);

			//update database
			MINTStudy studyData = new MINTStudy();
			studyData.setID(studyUUID);
			studyData.setStudyInstanceUID(study.getStudyInstanceUID());
			studyData.setPatientID(study.getValueForAttribute(0x00100020));
			studyData.setAccessionNumber(study.getValueForAttribute(0x00080050));
			studyData.setDateTime(MINTStudy
					.now());
			studyData.setStudyVersion(study.getVersion());
			studyDAO.insertStudy(studyData);
			// studyData.setStudyDateTime(study.getValueForAttribute(0x00080020));
			
			Change updateInfo = new Change();
			updateInfo.setId(UUID.randomUUID().toString());
			updateInfo.setStudyID(studyUUID);
			updateInfo.setType(type);
			updateInfo.setRemoteUser(remoteUser);
			updateInfo.setRemoteHost(remoteHost);
			updateInfo.setPrincipal(principal);
			updateInfo.setIndex(0);
			updateDAO.saveChange(updateInfo);

			jobInfo.setStatus(JobStatus.SUCCESS);
			jobInfo.setStatusDescription("complete");
			LOG.info("job " + jobID + " complete");

		} catch (Exception e) {
			jobInfo.setStatus(JobStatus.FAILED);
			jobInfo.setStatusDescription(e.getMessage());
			LOG.error("unable to process job " + jobID, e);
		}

		jobInfoDAO.saveOrUpdateJobInfo(jobInfo);

	}
}
