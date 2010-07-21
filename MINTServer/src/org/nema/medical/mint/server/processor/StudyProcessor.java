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

public class StudyProcessor extends TimerTask {

	static final Logger LOG = Logger.getLogger(StudyProcessor.class);
	
	private final File jobFolder;
	private final File studyFolder;
	
	private String type;
	private JobInfoDAO jobInfoDAO;
	private StudyDAO studyDAO;
	private ChangeDAO updateDAO;

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
	public StudyProcessor(File jobFolder, File studyFolder, String type, JobInfoDAO jobInfoDAO, StudyDAO studyDAO, ChangeDAO updateDAO) {
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
		
		JobInfo jobInfo = new JobInfo();
		jobInfo.setId(jobID);
		jobInfo.setStudyID(studyUUID);
		
		try {	
			File typeFolder = new File(studyFolder, type);
			typeFolder.mkdirs();
			File changelogRoot = new File(studyFolder, "changelog");
			changelogRoot.mkdirs();

			//load study into memory
			Study study = StudyUtil.loadStudy(jobFolder);
			
			if(!StudyUtil.validateStudy(study, jobFolder))
			{
				throw new RuntimeException("Validation of the new study failed");
			}
			
			//write study into type folder
			StudyUtil.writeStudy(study, typeFolder);
	        StudySummaryIO.writeSummaryToXHTML(study, new File(typeFolder, "summary.html"));
	        
	        //Write metadata to change log
	        File changelogFolder = StudyUtil.getNextChangelogDir(changelogRoot);
	        
	        StudyUtil.writeStudy(study, changelogFolder);

	        //Copy binary data into binaryitems folder
	        File binaryRoot = new File(typeFolder, "binaryitems");
			binaryRoot.mkdirs();

			StudyUtil.moveBinaryItems(jobFolder, binaryRoot);
			
			//delete job folder
			StudyUtil.deleteFolder(jobFolder);

			//update database
			org.nema.medical.mint.server.domain.Study studyData = new org.nema.medical.mint.server.domain.Study();
			studyData.setID(studyUUID);
			studyData.setStudyInstanceUID(study.getStudyInstanceUID());
			studyData.setPatientName(study.getValueForAttribute(0x00100010));
			studyData.setPatientID(study.getValueForAttribute(0x00100020));
			studyData.setAccessionNumber(study.getValueForAttribute(0x00080050));
			studyData.setDateTime(org.nema.medical.mint.server.domain.Study
					.now());
			studyDAO.insertStudy(studyData);
			// studyData.setStudyDateTime(study.getValueForAttribute(0x00080020));
			
			Change updateInfo = new Change();
			updateInfo.setId(UUID.randomUUID().toString());
			updateInfo.setStudyID(studyUUID);
			updateInfo.setDescription("Initial Creation of Study");
			updateInfo.setIndex(0);
			updateDAO.saveChange(updateInfo);

			jobInfo.setStatus(JobStatus.SUCCESS);
			jobInfo.setStatusDescription("complete");

		} catch (Exception e) {
			jobInfo.setStatus(JobStatus.FAILED);
			jobInfo.setStatusDescription(e.getMessage());
			LOG.error("unable to process job " + jobID, e);
		}

		jobInfoDAO.saveOrUpdateJobInfo(jobInfo);

	}
}
