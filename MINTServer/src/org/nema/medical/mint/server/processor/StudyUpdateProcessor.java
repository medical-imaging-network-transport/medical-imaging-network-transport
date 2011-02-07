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

import org.apache.log4j.Logger;
import org.nema.medical.mint.datadictionary.MetadataType;
import org.nema.medical.mint.metadata.StudyIO;
import org.nema.medical.mint.metadata.StudyMetadata;
import org.nema.medical.mint.server.domain.*;
import org.nema.medical.mint.server.util.StorageUtil;
import org.nema.medical.mint.utils.StudyTraversals;
import org.nema.medical.mint.utils.StudyUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
	
	private final String oldVersion, remoteUser, remoteHost, principal;
	private final JobInfoDAO jobInfoDAO;
	private final StudyDAO studyDAO;
	private final ChangeDAO updateDAO;
    private final Map<String, MetadataType> availableTypes;

	/**
	 * extracts files from the jobFolder, merges them in the studyFolder
	 * updates the database
	 * @param jobFolder the folder containing the uploaded files - must contain metadata.xml or metadata.gpb
	 * @param studyFolder the target folder where the study to update exists
	 * @param jobInfoDAO needed to update the database
	 * @param studyDAO needed to update the database
	 */
	public StudyUpdateProcessor(final File jobFolder, final File studyFolder,
                                final Map<String, MetadataType> availableTypes, final String oldVersion,
                                final String remoteUser, final String remoteHost, final String principal,
                                final JobInfoDAO jobInfoDAO, final StudyDAO studyDAO, final ChangeDAO updateDAO) {
		this.jobFolder = jobFolder;
		this.studyFolder = studyFolder;
		this.availableTypes = availableTypes;
        this.oldVersion = oldVersion;
		this.remoteUser = remoteUser;
		this.remoteHost = remoteHost;
		this.principal = principal;
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
			try
			{
	            LOG.debug("Got lock, and starting process");

				//Not calling mkdir on this because they better already exist
				File changelogRoot = new File(studyFolder, "changelog");
				
				if(!changelogRoot.exists())
				{
					throw new FileNotFoundException("The changelog for study uuid " + studyUUID
                            + " does not exist, may need to do a create first.");
				}
				
				/*
				 * Need to load new study information
				 */
				final StudyMetadata newStudy = StudyIO.loadStudy(jobFolder);
                final String typeName = newStudy.getType();
                final MetadataType dataDictionary = availableTypes.get(typeName);
                if (dataDictionary == null) {
                    throw new RuntimeException("Invalid study type " + typeName);
                }

                if (newStudy.getVersion() != null) {
                    throw new RuntimeException(
                            "Study update data specifies a version; versions are controlled by server, not client");
                }

                try {
                    StorageUtil.validateStudy(newStudy, dataDictionary, jobFolder);
                } catch (final StudyTraversals.TraversalException e) {
                    throw new RuntimeException("Validation of the jobs study failed", e);
                }
	
                final File typeFolder = new File(studyFolder, typeName);
                final File existingBinaryFolder = new File(typeFolder, "binaryitems");
                existingBinaryFolder.mkdirs();

                StudyMetadata existingStudy;
                try {
                    /*
                     * Need to load current study information
                     */
                    existingStudy = StudyIO.loadStudy(typeFolder);
                } catch (final RuntimeException e){
                    /*
                     * Do nothing, just means there is no existing study
                     * which is fine.
                     */
                    existingStudy = null;
                }

                /*
                 * If the study versions are not the same, then this
                 * update is for a version that is not the most recent and
                 * should not be applied.
                 */
                if (existingStudy != null && (existingStudy.getVersion() == null
                        || !existingStudy.getVersion().equals(oldVersion))) {
                    throw new RuntimeException("Study update data is of a different version than the current study, " +
                            "cannot update if versions do not match. (" + existingStudy.getVersion() + " : "
                            + oldVersion + ")");
                }

				/*
				 * Need to rename the new binary files so there are no collisions
				 * with existing data files when merging. This also means updating
				 * the new study document.
				 */
				int maxExistingItemNumber = StorageUtil.getHighestNumberedBinaryItem(existingBinaryFolder);
				if(!StorageUtil.shiftItemIds(newStudy, jobFolder, maxExistingItemNumber+1))
				{
					//Shift Item Ids failed!
					throw new RuntimeException("Failed to shift binary item identifies. Cause is unknown.");
				}
				
				/*
				 * Write metadata update message to change log folder.
				 */
		        File changelogFolder = StorageUtil.getNextChangelogDir(changelogRoot);
		        
		        StudyUtils.writeStudy(newStudy, changelogFolder);
				
		        Collection<Integer> excludedBids = new HashSet<Integer>();
		        if(existingStudy != null)
		        {
					/*
					 * Need to move through the new study and look for things to exclude
					 * and exclude them from the existing study.
					 */
                    if(!StudyUtils.applyExcludes(existingStudy, newStudy, excludedBids))
					{
						//Applying Excludes failed!
						throw new RuntimeException("Failed to apply exclude tags. Cause is unknown.");
					}
		        }
		        
				/*
				 * Clean out excludes because excludes should not be left in
				 * the newStudy.
				 */
                StudyUtils.removeExcludes(newStudy);

                /*
                     * Need to merge the study documents and renormalize the result.
                     * This means first denormalize, then merge, then normalize the
                     * result
                     */
                if(!StudyUtils.denormalizeStudy(newStudy))
				{
					throw new RuntimeException("Failed to denormalize new study. Cause is unknown.");
				}
		        
		        if(existingStudy != null) {
                    if(!StudyUtils.denormalizeStudy(existingStudy))
					{
						throw new RuntimeException("Failed to denormalize existing study. Cause is unknown.");
					}

                    StudyUtils.mergeStudy(existingStudy, newStudy, excludedBids);

                    // Get next version number
                    existingStudy.setVersion(StudyUtils.getNextVersion(existingStudy.getVersion()));
		        } else {
					/*
					 * If no existing study, new study becomes the existing
					 * study. This happens when an update is done on a type that
					 * has no data yet.
					 */
		        	existingStudy = newStudy;
		        	
		        	// Set to base level version
                    existingStudy.setVersion(StudyUtils.getBaseVersion());
		        	existingStudy.setType(typeName);
		        }
		        
		        //Rename all excluded binary files to have .exclude
				StorageUtil.renameExcludedFiles(existingBinaryFolder, excludedBids);

                if (!StudyUtils.normalizeStudy(existingStudy)) {
					throw new RuntimeException("Failed to normalize final study. Cause is unknown.");
				}
				
				/*
				 * Need to copy into the Study folder the new study document and
				 * binary data files.
				 */
				StudyUtils.writeStudy(existingStudy, typeFolder);
				
				StudyUtils.moveBinaryItems(jobFolder, existingBinaryFolder);
				
				StudyUtils.deleteFolder(jobFolder);
				
                //Update study DAO only if this is DICOM data; don't update study DAO for other types (DICOM is primary)
                if (typeName.equals("DICOM")) {
                    MINTStudy studyData = new MINTStudy();
                    studyData.setID(studyUUID);
                    studyData.setStudyInstanceUID(existingStudy.getStudyInstanceUID());
                    studyData.setPatientID(existingStudy.getValueForAttribute(0x00100020));
                    studyData.setAccessionNumber(existingStudy.getValueForAttribute(0x00080050));
                    // studyData.setDateTime(study.getValueForAttribute(0x00080020));
                    studyData.setDateTime(MINTStudy.now());
                    studyData.setStudyVersion(existingStudy.getVersion());
                    studyDAO.updateStudy(studyData);
                }

                //Update change DAO for any type
				Change updateInfo = new Change();
				updateInfo.setId(UUID.randomUUID().toString());
				updateInfo.setStudyID(studyUUID);
				updateInfo.setType(typeName);
				updateInfo.setRemoteUser(remoteUser);
				updateInfo.setRemoteHost(remoteHost);
				updateInfo.setPrincipal(principal);
				updateInfo.setIndex(Integer.parseInt(changelogFolder.getName()));
                updateInfo.setOperation(org.nema.medical.mint.changelog.Change.OPERATION_UPDATE);
				updateDAO.saveChange(updateInfo);
	
				jobInfo.setStatus(JobStatus.SUCCESS);
				jobInfo.setStatusDescription("complete");
			}catch(Exception e){
				jobInfo.setStatus(JobStatus.FAILED);
				jobInfo.setStatusDescription(e.getMessage());
				LOG.error("unable to process job " + jobID, e);
			}finally{
                lock.unlock();
				LOG.debug("Released lock and stopping.");
			}
		}else{
			jobInfo.setStatus(JobStatus.FAILED);
			jobInfo.setStatusDescription("unable to process job " + jobID
                    + ", another update is current being processed on the same study.");
		}
		
		jobInfoDAO.saveOrUpdateJobInfo(jobInfo);
	}

}
