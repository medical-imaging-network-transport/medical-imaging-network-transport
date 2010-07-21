package org.nema.medical.mint.server.processor;

import java.io.File;
import java.util.TimerTask;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.nema.medical.mint.common.StudyUtil;
import org.nema.medical.mint.metadata.Study;
import org.nema.medical.mint.server.domain.JobInfo;
import org.nema.medical.mint.server.domain.JobInfoDAO;
import org.nema.medical.mint.server.domain.JobStatus;
import org.nema.medical.mint.server.domain.StudyDAO;
import org.nema.medical.mint.server.domain.Change;
import org.nema.medical.mint.server.domain.ChangeDAO;

/**
 * 
 * @author rrobin20
 *
 */
public class UpdateStudyProcessor extends TimerTask {
	static final Logger LOG = Logger.getLogger(UpdateStudyProcessor.class);

	private final File jobFolder;
	private final File studyFolder;
	
	private String type;
	private JobInfoDAO jobInfoDAO;
	private StudyDAO studyDAO;
	private ChangeDAO updateDAO;

	/**
	 * extracts files from the jobFolder, merges them in the studyFolder
	 * updates the database
	 * @param jobFolder the folder containing the uploaded files - must contain metadata.xml or metadata.gpb
	 * @param studyFolder the target folder where the study to update exists
	 * @param jobInfoDAO needed to update the database
	 * @param studyDAO needed to update the database
	 */
	public UpdateStudyProcessor(File jobFolder, File studyFolder, String type, JobInfoDAO jobInfoDAO, StudyDAO studyDAO, ChangeDAO updateDAO) {
		this.jobFolder = jobFolder;
		this.studyFolder = studyFolder;
		this.type = type;
		this.jobInfoDAO = jobInfoDAO;
		this.studyDAO = studyDAO;
		this.updateDAO = updateDAO;
	}

	@Override
	public void run() {
		String jobID = jobFolder.getName();
		String studyUUID = studyFolder.getName();
		
		//Not calling mkdirs on these because they better already exist
		File typeFolder = new File(studyFolder, type);
		File changelogRoot = new File(studyFolder, "changelog");
		
		JobInfo jobInfo = new JobInfo();
		jobInfo.setId(jobID);
		jobInfo.setStudyID(studyUUID);
		File existingBinaryFolder = new File(typeFolder, "binaryitems");
		
		try
		{
			/*
			 * Need to load current study studyinformation
			 */
			Study existingStudy = StudyUtil.loadStudy(typeFolder);

			/*
			 * Need to load new study information
			 */
			Study newStudy = StudyUtil.loadStudy(jobFolder);
			
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
			
			/*
			 * Need to move through the new study and look for things to exclude
			 * and exclude them from the existing study.
			 */
			if(!StudyUtil.applyExcludes(existingStudy, newStudy))
			{
				//Applying Excludes failed!
				throw new RuntimeException("Failed to apply exclude tags. Cause is unknown.");
			}
			
			/*
			 * Need to merge the study documents and renormalize the result.
			 * This means first denormalize, then merge, then normalize the
			 * result
			 */
			if(!StudyUtil.denormalizeStudy(existingStudy))
			{
				throw new RuntimeException("Failed to denormalize existing study. Cause is unknown.");
			}
			if(!StudyUtil.denormalizeStudy(newStudy))
			{
				throw new RuntimeException("Failed to denormalize new study. Cause is unknown.");
			}
			
			existingStudy.mergeStudy(newStudy);
			
			if(!StudyUtil.normalizeStudy(existingStudy))
			{
				throw new RuntimeException("Failed to normalize final study. Cause is unknown.");
			}
			
			/*
			 * Need to copy into the Study folder the new study document and
			 * binary data files.
			 */
			StudyUtil.writeStudy(existingStudy, typeFolder);
			StudySummaryIO.writeSummaryToXHTML(existingStudy, new File(typeFolder, "summary.html"));
			
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
			updateInfo.setDescription("Update of existing study.");
			updateInfo.setIndex(Integer.parseInt(changelogFolder.getName()));
			updateDAO.saveChange(updateInfo);

			jobInfo.setStatus(JobStatus.SUCCESS);
			jobInfo.setStatusDescription("complete");
		}catch(Exception e){
			jobInfo.setStatus(JobStatus.FAILED);
			jobInfo.setStatusDescription(e.getMessage());
			LOG.error("unable to process job " + jobID, e);
		}
		
		jobInfoDAO.saveOrUpdateJobInfo(jobInfo);
	}

}
