package org.nema.medical.mint.server.processor;

import java.io.File;
import java.util.TimerTask;

import org.nema.medical.mint.server.domain.JobInfo;
import org.nema.medical.mint.server.domain.JobInfoDAO;
import org.nema.medical.mint.server.domain.StudyDAO;

/**
 * 
 * @author rrobin20
 *
 */
public class UpdateStudyProcessor extends TimerTask {

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
	}

}
