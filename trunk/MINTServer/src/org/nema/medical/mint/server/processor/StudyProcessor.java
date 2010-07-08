package org.nema.medical.mint.server.processor;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.nema.medical.mint.metadata.Study;
import org.nema.medical.mint.metadata.StudyIO;
import org.nema.medical.mint.server.domain.JobInfo;
import org.nema.medical.mint.server.domain.JobInfoDAO;
import org.nema.medical.mint.server.domain.JobStatus;
import org.nema.medical.mint.server.domain.StudyDAO;

public class StudyProcessor extends TimerTask {

	static final Logger LOG = Logger.getLogger(StudyProcessor.class);
	
	final File jobFolder;
	final File studyFolder;
	private JobInfoDAO jobInfoDAO;
	private StudyDAO studyDAO;

	/**
	 * extracts files from the jobFolder, places them in the studyFolder
	 * updates the database
	 * @param jobFolder the folder containing the uploaded files - must contain metadata.xml or metadata.gpb
	 * @param studyFolder the target folder where the study will be created (will contain a DICOM subdir)
	 * @param jobInfoDAO needed to update the database
	 * @param studyDAO needed to update the database
	 */
	public StudyProcessor(File jobFolder, File studyFolder, JobInfoDAO jobInfoDAO, StudyDAO studyDAO) {
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
		
		try {
			Iterator<File> iterator = Arrays.asList(jobFolder.listFiles()).iterator();
			
			File dicomFolder = new File(studyFolder, "DICOM");
			dicomFolder.mkdirs();

			File metadataGPB = new File(jobFolder,"metadata.gpb");
			File metadataXML = new File(jobFolder,"metadata.xml");
			File metadataJSON = new File(jobFolder,"metadata.json");
			File metadata;

			Study study = null;

			if (metadataXML.exists()) {
				study = StudyIO.parseFromXML(metadataXML);
				metadata = metadataXML;
			} else if (metadataGPB.exists()) {
				study = StudyIO.parseFromGPB(metadataGPB);
				metadata = metadataGPB;
			} else if (metadataJSON.exists()) {
				study = StudyIO.parseFromJSON(metadataJSON);
				metadata = metadataJSON;
			} else {
				throw new RuntimeException("unable to locate metadata file");
			}

			StudyIO.writeToGPB(study, new File(dicomFolder, "metadata.gpb"));
			StudyIO.writeToXML(study, new File(dicomFolder, "metadata.xml"));
	        StudySummaryIO.writeSummaryToXHTML(study, new File(dicomFolder, "summary.html"));			

	        File binaryRoot = new File(dicomFolder, "binaryitems");
			binaryRoot.mkdirs();
			while (iterator.hasNext()) {
				File tempfile = iterator.next();
				File permfile = new File(binaryRoot, tempfile.getName());
				// just moving the file since the reference implementation
				// is using the same MINT_ROOT for temp and perm storage
				// other implementations may want to copy/delete the file
				// if the temp storage is on a different device
				tempfile.renameTo(permfile);
			}
			metadata.delete();
			metadata.getParentFile().delete();

			org.nema.medical.mint.server.domain.Study studyData = new org.nema.medical.mint.server.domain.Study();
			studyData.setID(studyUUID);
			studyData.setStudyInstanceUID(study.getStudyInstanceUID());
			studyData.setPatientName(study.getValueForAttribute(0x00100010));
			studyData.setPatientID(study.getValueForAttribute(0x00100020));
			studyData.setAccessionNumber(study.getValueForAttribute(0x00080050));
			studyData.setStudyDateTime(org.nema.medical.mint.server.domain.Study
					.now());
			studyDAO.saveOrUpdateStudy(studyData);
			// studyData.setStudyDateTime(study.getValueForAttribute(0x00080020));		

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
