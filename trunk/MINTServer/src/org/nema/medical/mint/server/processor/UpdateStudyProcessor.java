package org.nema.medical.mint.server.processor;

import java.io.File;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.nema.medical.mint.common.StudyUtil;
import org.nema.medical.mint.metadata.Study;
import org.nema.medical.mint.server.domain.JobInfo;
import org.nema.medical.mint.server.domain.JobInfoDAO;
import org.nema.medical.mint.server.domain.JobStatus;
import org.nema.medical.mint.server.domain.StudyDAO;

/**
 * 
 * @author rrobin20
 *
 */
public class UpdateStudyProcessor extends TimerTask {
	static final Logger LOG = Logger.getLogger(UpdateStudyProcessor.class);

	protected final File jobFolder;
	protected final File studyFolder;
	
	private JobInfoDAO jobInfoDAO;
	private StudyDAO studyDAO;

	/**
	 * extracts files from the jobFolder, merges them in the studyFolder
	 * updates the database
	 * @param jobFolder the folder containing the uploaded files - must contain metadata.xml or metadata.gpb
	 * @param studyFolder the target folder where the study to update exists
	 * @param jobInfoDAO needed to update the database
	 * @param studyDAO needed to update the database
	 */
	public UpdateStudyProcessor(File jobFolder, File studyFolder, JobInfoDAO jobInfoDAO, StudyDAO studyDAO) {
		this.jobFolder = jobFolder;
		this.studyFolder = studyFolder;
		this.jobInfoDAO = jobInfoDAO;
		this.studyDAO = studyDAO;
	}

	@Override
	public void run() {
		String jobID = jobFolder.getName();
		String studyUUID = studyFolder.getName();
		
		JobInfo jobInfo = new JobInfo();
		jobInfo.setId(jobID);
		jobInfo.setStudyID(studyUUID);
		File existingBinaryFolder = new File(studyFolder.getPath(), "binaryitems");
		
		try
		{
			/*
			 * Need to load current study studyinformation
			 */
			Study existingStudy = StudyUtil.loadStudy(studyFolder);
			
			/*
			 * Need to load new study information
			 */
			Study newStudy = StudyUtil.loadStudy(jobFolder);
			
			/*
			 * Need to rename the new binary files so there are no collisions
			 * with existing data files when merging. This also means updating
			 * the new study document.
			 */
			int maxExistingItemNumber = StudyUtil.getHighestNumberedBinaryItem(existingBinaryFolder);
			if(!StudyUtil.shiftItemIds(existingStudy, jobFolder, maxExistingItemNumber+1))
			{
				//Shift Item Ids failed!
				throw new RuntimeException("Failed to shift binary item identifies. Cause is unknown.");
			}
			
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
		}catch(Exception e){
			jobInfo.setStatus(JobStatus.FAILED);
			jobInfo.setStatusDescription(e.getMessage());
			LOG.error("unable to process job " + jobID, e);
		}
		
		jobInfoDAO.saveOrUpdateJobInfo(jobInfo);
	}

}