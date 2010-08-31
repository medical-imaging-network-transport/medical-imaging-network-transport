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
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.HashSet;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;
import org.nema.medical.mint.common.StudyUtil;
import org.nema.medical.mint.metadata.Study;
import org.nema.medical.mint.metadata.StudyIO;
import org.nema.medical.mint.server.domain.JobInfo;
import org.nema.medical.mint.server.domain.JobInfoDAO;
import org.nema.medical.mint.server.domain.JobStatus;
import org.nema.medical.mint.server.domain.StudyDAO;
import org.nema.medical.mint.server.domain.Change;
import org.nema.medical.mint.server.domain.ChangeDAO;

/**
 * 
 * @author Rex
 *
 */
public class StudyUpdateProcessor extends TimerTask {
	static final Logger LOG = Logger.getLogger(StudyUpdateProcessor.class);
	
	protected static final ConcurrentMap<String, Lock> studyIdLocks = new ConcurrentHashMap<String, Lock>();

	private final File jobFolder;
	private final File studyFolder;
	
	private final String type, sourceUser, sourceHost, sourceAddress;
	private final JobInfoDAO jobInfoDAO;
	private final StudyDAO studyDAO;
	private final ChangeDAO updateDAO;

	/**
	 * extracts files from the jobFolder, merges them in the studyFolder
	 * updates the database
	 * @param jobFolder the folder containing the uploaded files - must contain metadata.xml or metadata.gpb
	 * @param studyFolder the target folder where the study to update exists
	 * @param jobInfoDAO needed to update the database
	 * @param studyDAO needed to update the database
	 */
	public StudyUpdateProcessor(File jobFolder, File studyFolder, String type, String sourceUser, String sourceHost, String sourceAddress, JobInfoDAO jobInfoDAO, StudyDAO studyDAO, ChangeDAO updateDAO) {
		this.jobFolder = jobFolder;
		this.studyFolder = studyFolder;
		this.type = type;
		this.sourceUser = sourceUser;
		this.sourceHost = sourceHost;
		this.sourceAddress = sourceAddress;
		this.jobInfoDAO = jobInfoDAO;
		this.studyDAO = studyDAO;
		this.updateDAO = updateDAO;
	}

	@Override
	public void run() {
		LOG.debug("Execution started.");
		
		String jobID = jobFolder.getName();
		String studyUUID = studyFolder.getName();
		
		JobInfo jobInfo = new JobInfo();
		jobInfo.setId(jobID);
		jobInfo.setStudyID(studyUUID);
		
		Lock lock = new ReentrantLock(), oldLock;
		
		oldLock = studyIdLocks.putIfAbsent(studyUUID, lock);
		if(oldLock != null)
		{
			LOG.debug("Lock was an existing lock.");
			lock = oldLock;
		}
		
		if(lock.tryLock())
		{
			LOG.debug("Got lock, and starting process");
			try
			{
				File typeFolder = new File(studyFolder, type);
				
				//Not calling mkdir on this because they better already exist
				File changelogRoot = new File(studyFolder, "changelog");
				
				if(!changelogRoot.exists())
				{
					throw new FileNotFoundException("The changelog for study uuid " + studyUUID + " does not exist, may need to do a create first.");
				}
				
				File existingBinaryFolder = new File(typeFolder, "binaryitems");
				existingBinaryFolder.mkdirs();
				
				Study existingStudy;
				
				try
				{
					/*
					 * Need to load current study information
					 */
					existingStudy = StudyIO.loadStudy(typeFolder);
				}catch(RuntimeException e){
					/*
					 * Do nothing, just means there is no existing study
					 * which is fine.
					 */
					existingStudy = null;
				}
				
				/*
				 * Need to load new study information
				 */
				Study newStudy = StudyIO.loadStudy(jobFolder);
				
				/*
				 * If the study versions are not the same, then this
				 * update is for a version that is not the most recent and
				 * should not be applied.
				 */
				if(existingStudy != null && (existingStudy.getVersion() == null || !existingStudy.getVersion().equals(newStudy.getVersion())))
				{
					throw new RuntimeException("Study update data is of a different version that the current study, cannot update if versions do not match. (" + existingStudy.getVersion() + " : " + newStudy.getVersion() + ")");
				}
				
				if(!StudyUtil.validateStudy(newStudy, jobFolder))
				{
					throw new RuntimeException("Validation of the jobs study failed");
				}
	
				/*
				 * Need to rename the new binary files so there are no collisions
				 * with existing data files when merging. This also means updating
				 * the new study document.
				 */
				int maxExistingItemNumber = StudyUtil.getHighestNumberedBinaryItem(existingBinaryFolder);
				if(!StudyUtil.shiftItemIds(newStudy, jobFolder, maxExistingItemNumber+1))
				{
					//Shift Item Ids failed!
					throw new RuntimeException("Failed to shift binary item identifies. Cause is unknown.");
				}
				
				/*
				 * Write metadata update message to change log folder.
				 */
		        File changelogFolder = StudyUtil.getNextChangelogDir(changelogRoot);
		        
		        StudyUtil.writeStudy(newStudy, changelogFolder);
				
		        Collection<Integer> excludedBids = new HashSet<Integer>();
		        if(existingStudy != null)
		        {
					/*
					 * Need to move through the new study and look for things to exclude
					 * and exclude them from the existing study.
					 */
					if(!StudyUtil.applyExcludes(existingStudy, newStudy, excludedBids))
					{
						//Applying Excludes failed!
						throw new RuntimeException("Failed to apply exclude tags. Cause is unknown.");
					}
		        }
		        
				/*
				 * Clean out excludes because excludes should not be left in
				 * the newStudy.
				 */
	        	StudyUtil.removeExcludes(newStudy);
				
				/*
				 * Need to merge the study documents and renormalize the result.
				 * This means first denormalize, then merge, then normalize the
				 * result
				 */
		        if(!StudyUtil.denormalizeStudy(newStudy))
				{
					throw new RuntimeException("Failed to denormalize new study. Cause is unknown.");
				}
		        
		        if(existingStudy != null)
		        {
					if(!StudyUtil.denormalizeStudy(existingStudy))
					{
						throw new RuntimeException("Failed to denormalize existing study. Cause is unknown.");
					}
					
					StudyUtil.mergeStudy(existingStudy, newStudy, excludedBids);
					
					// Get next version number
					existingStudy.setVersion(StudyUtil.getNextVersion(existingStudy.getVersion()));
		        }else{
					/*
					 * If no existing study, new study becomes the existing
					 * study. This happens when an update is done on a type that
					 * has no data yet.
					 */
		        	existingStudy = newStudy;
		        	
		        	// Set to base level version
		        	existingStudy.setVersion(StudyUtil.getBaseVersion());
		        	existingStudy.setType(type);
		        }
		        
		        //Rename all excluded binary files to have .exclude
				StudyUtil.renameExcludedFiles(existingBinaryFolder, excludedBids);
				
				if(!StudyUtil.normalizeStudy(existingStudy))
				{
					throw new RuntimeException("Failed to normalize final study. Cause is unknown.");
				}
				
				/*
				 * Need to copy into the Study folder the new study document and
				 * binary data files.
				 */
				StudyUtil.writeStudy(existingStudy, typeFolder);
				
				StudyUtil.moveBinaryItems(jobFolder, existingBinaryFolder);
				
				StudyUtil.deleteFolder(jobFolder);
				
				/*
				 * Update the Job DAO and Study DAO
				 */
				org.nema.medical.mint.server.domain.Study studyData = new org.nema.medical.mint.server.domain.Study();
				studyData.setID(studyUUID);
				studyData.setStudyInstanceUID(existingStudy.getStudyInstanceUID());
				studyData.setPatientName(existingStudy.getValueForAttribute(0x00100010));
				studyData.setPatientID(existingStudy.getValueForAttribute(0x00100020));
				studyData.setAccessionNumber(existingStudy.getValueForAttribute(0x00080050));
				studyData.setDateTime(org.nema.medical.mint.server.domain.Study
						.now());
				studyDAO.updateStudy(studyData);
				// studyData.setDateTime(study.getValueForAttribute(0x00080020));
				
				Change updateInfo = new Change();
				updateInfo.setId(UUID.randomUUID().toString());
				updateInfo.setStudyID(studyUUID);
				updateInfo.setType(type);
				updateInfo.setSourceUser(sourceUser);
				updateInfo.setSourceHost(sourceHost);
				updateInfo.setSourceAddress(sourceAddress);
				updateInfo.setIndex(Integer.parseInt(changelogFolder.getName()));
				updateDAO.saveChange(updateInfo);
	
				jobInfo.setStatus(JobStatus.SUCCESS);
				jobInfo.setStatusDescription("complete");
			}catch(Exception e){
				jobInfo.setStatus(JobStatus.FAILED);
				jobInfo.setStatusDescription(e.getMessage());
				LOG.error("unable to process job " + jobID, e);
			}finally{
				LOG.debug("Releasing lock and stopping.");
				lock.unlock();
			}
		}else{
			jobInfo.setStatus(JobStatus.FAILED);
			jobInfo.setStatusDescription("unable to process job " + jobID + ", another update is current being processed on the same study.");
		}
		
		jobInfoDAO.saveOrUpdateJobInfo(jobInfo);
	}

}
