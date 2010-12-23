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
package org.nema.medical.mint.server.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.JiBXException;
import org.nema.medical.mint.datadictionary.MetadataType;
import org.nema.medical.mint.jobs.JobConstants;
import org.nema.medical.mint.server.domain.ChangeDAO;
import org.nema.medical.mint.server.domain.JobInfo;
import org.nema.medical.mint.server.domain.JobInfoDAO;
import org.nema.medical.mint.server.domain.JobStatus;
import org.nema.medical.mint.server.domain.StudyDAO;
import org.nema.medical.mint.server.processor.StudyCreateProcessor;
import org.nema.medical.mint.server.processor.StudyUpdateProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class JobsController {

	private static final Logger LOG = Logger.getLogger(JobsController.class);
	private static final List<String> supportedMetadataExtensions = Arrays
			.asList(".gpb", ".gpb.gz", ".xml", ".xml.gz");

	private ExecutorService executor;

	@Autowired
	protected File jobTemp;
	@Autowired
	protected File studiesRoot;

	@Autowired
	protected String xmlStylesheet;


    @Autowired
    protected HashMap<String, MetadataType> availableTypes;

	@Autowired
	protected StudyDAO studyDAO = null;
	@Autowired
	protected JobInfoDAO jobInfoDAO = null;
	@Autowired
	protected ChangeDAO updateDAO = null;

	@PostConstruct
	public void setupExecutor() {
		executor = Executors.newCachedThreadPool();
	}

	@PreDestroy
	public void stopExecutor() {
		executor.shutdown();
	}

	@RequestMapping(method = RequestMethod.GET, value = "/jobs/createstudy")
	public String createStudy() {
		return "studycreate";
	}

	@RequestMapping(method = RequestMethod.POST, value = "/jobs/createstudy")
	public void createStudy(HttpServletRequest req, HttpServletResponse res)
							throws IOException {

		String studyUUID = UUID.randomUUID().toString();
		String jobID = UUID.randomUUID().toString();

		File jobFolder = new File(jobTemp, jobID);
		jobFolder.mkdirs();

		// the list of files uploaded
		List<File> files = new ArrayList<File>();

		// the set of form parameters
		Map<String, String> params = new HashMap<String, String>();

		// Check that we have a file upload request
		boolean isMultipart = ServletFileUpload.isMultipartContent(req);
		if (!isMultipart) {
			res.sendError(HttpServletResponse.SC_BAD_REQUEST, "expected multipart form data");
			return;
		}

		try {
			handleUpload(req, jobFolder, files, params);
		} catch (FileUploadException e) {
			res.sendError(HttpServletResponse.SC_BAD_REQUEST, "unable to parse multipart form data");
			return;
		}

		Iterator<File> iterator = files.iterator();
		if (!iterator.hasNext()) {
			res.sendError(HttpServletResponse.SC_BAD_REQUEST, "at least one file (containing metadata) is required.");
			return;
		}

		JobInfo jobInfo = new JobInfo();
		jobInfo.setId(jobID);
		jobInfo.setStudyID(studyUUID);
		jobInfo.setStatus(JobStatus.IN_PROGRESS);
		jobInfo.setStatusDescription("0% complete");
		String jobURI = req.getContextPath() + "/jobs/status/" + jobInfo.getId();
		jobInfoDAO.saveOrUpdateJobInfo(jobInfo);

		Principal principal = req.getUserPrincipal();
		String principalName = (principal != null) ? principal.getName() : null;

        final MetadataType dataDictionary = availableTypes.get("DICOM");

		StudyCreateProcessor processor = new StudyCreateProcessor(jobFolder,
				new File(studiesRoot, studyUUID), dataDictionary, req.getRemoteUser(),
				req.getRemoteHost(), principalName, jobInfoDAO, studyDAO,
				updateDAO);
		executor.execute(processor); // process immediately in the background

		res.setStatus(HttpServletResponse.SC_SEE_OTHER);
		res.setHeader("Location", jobURI);
	}


	@RequestMapping(method = RequestMethod.GET, value = "/jobs/updatestudy")
	public String updateStudy() {
		return "studyupdate";
	}

	@RequestMapping(method = RequestMethod.POST, value = "/jobs/updatestudy")
	public void updateStudy(HttpServletRequest req, HttpServletResponse res)
								throws IOException {

		String jobID = UUID.randomUUID().toString();
		File jobFolder = new File(jobTemp, jobID);
		jobFolder.mkdirs();

		// the list of files uploaded
		List<File> files = new ArrayList<File>();

		// the set of form parameters
		Map<String, String> params = new HashMap<String, String>();

		// Check that we have a file upload request
		boolean isMultipart = ServletFileUpload.isMultipartContent(req);
		if (!isMultipart) {
			res.sendError(HttpServletResponse.SC_BAD_REQUEST, "expected multipart form data");
			return;
		}

		try {
			handleUpload(req, jobFolder, files, params);
		} catch (FileUploadException e) {
			res.sendError(HttpServletResponse.SC_BAD_REQUEST, "unable to parse multipart form data");
			return;
		}

		if (files.size() < 1) {
			res.sendError(HttpServletResponse.SC_BAD_REQUEST, "at least one file (containing metadata) is required.");
			return;
		}

		if (!params.containsKey(JobConstants.HTTP_MESSAGE_PART_STUDYUUID)) {
			res.sendError(HttpServletResponse.SC_BAD_REQUEST, "missing parameter "
                    + JobConstants.HTTP_MESSAGE_PART_STUDYUUID);
			return;
		}

		final String studyUUID = params.get(JobConstants.HTTP_MESSAGE_PART_STUDYUUID);

        if (!params.containsKey(JobConstants.HTTP_MESSAGE_PART_OLDVERSION)) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "missing parameter "
                    + JobConstants.HTTP_MESSAGE_PART_OLDVERSION);
            return;
        }

        final String oldVersion = params.get(JobConstants.HTTP_MESSAGE_PART_OLDVERSION);

		JobInfo jobInfo = new JobInfo();
		jobInfo.setId(jobID);
		jobInfo.setStudyID(studyUUID);
		jobInfo.setStatus(JobStatus.IN_PROGRESS);
		jobInfo.setStatusDescription("0% complete");
		String jobURI = req.getContextPath() + "/jobs/status/" + jobInfo.getId();
		jobInfoDAO.saveOrUpdateJobInfo(jobInfo);

		File studyFolder = new File(studiesRoot, studyUUID);

		if(!studyFolder.exists())
		{
			res.sendError(HttpServletResponse.SC_BAD_REQUEST, "study with uuid " + studyUUID + " does not exists so cannot update");
			return;
		}

		Principal principal = req.getUserPrincipal();
		String principalName = (principal != null) ? principal.getName() : null;

		final StudyUpdateProcessor processor = new StudyUpdateProcessor(jobFolder,
				studyFolder, availableTypes, oldVersion, req.getRemoteUser(), req.getRemoteHost(),
				principalName, jobInfoDAO, studyDAO, updateDAO);
		executor.execute(processor); // process immediately in the background

		res.setStatus(HttpServletResponse.SC_SEE_OTHER);
		res.setHeader("Location", jobURI);

	}

	@RequestMapping(method = RequestMethod.GET, value = "/jobs/status/{uuid}")
	public void getJobStatus(final HttpServletRequest req,
			final HttpServletResponse res,
			@PathVariable("uuid") final String uuid) throws IOException, JiBXException {

		final JobInfo jobInfo = jobInfoDAO.findJobInfo(uuid);

		org.nema.medical.mint.jobs.JobStatus jobStatus = new org.nema.medical.mint.jobs.JobStatus(
				jobInfo.getId(), jobInfo.getStudyID(), jobInfo.getStatus().toString(),
				jobInfo.getCreateTime(), jobInfo.getUpdateTime());

		IBindingFactory bfact = BindingDirectory.getFactory("jobStatus",org.nema.medical.mint.jobs.JobStatus.class);
		IMarshallingContext mctx = bfact.createMarshallingContext();
		mctx.setIndent(2);
		mctx.startDocument("UTF-8", null, res.getOutputStream());
		mctx.getXmlWriter().writePI("xml-stylesheet", xmlStylesheet);
		mctx.marshalDocument(jobStatus);
		mctx.endDocument();

	}

	public void handleUpload(HttpServletRequest request, File jobFolder, List<File> files,
			Map<String, String> params) throws IOException, FileUploadException {

		byte buf[] = new byte[32 * 1024];
		int bid = 0;

		int fileCount = 0;
		LOG.info("creating local files");

		// Parse the request
		ServletFileUpload upload = new ServletFileUpload();
		FileItemIterator iter;
		iter = upload.getItemIterator(request);
		while (iter.hasNext()) {
			FileItemStream item = iter.next();
			String name = item.getFieldName();
			InputStream in = item.openStream();

			if (item.isFormField()) {
				String value = Streams.asString(in);
				LOG.debug("found form field " + name + " = " + value);
				params.put(name, value);
			} else {
				File file;

				// special handling for first file - must be metadata!
				if (files.isEmpty()) {
					String filename = item.getName();

					LOG.info("loading metadata from " + filename);
					for (String extension : supportedMetadataExtensions) {
						if (filename.endsWith(extension)) {
							filename = "metadata" + extension;
							break;
						}
					}

					// last resort, use content type!
					String contentType = item.getContentType();
					if ("text/xml".equals(contentType)) {
						filename = "metadata.xml";
					} else if ("application/octet-stream".equals(contentType)) {
						filename = "metadata.gpb";
					} else {
						// dump out and write the content... will fail later
						LOG.error("unable to determine metadata type for "
								+ item.getName());
						filename = "metadata.dat";
					}

					file = new File(jobFolder, filename);
				} else {
					file = new File(jobFolder, String.format("%d.dat", bid++));
				}

				FileOutputStream out = null;
				out = new FileOutputStream(file);
				try {
					while (true) {
						int len = in.read(buf);
						if (len < 0)
							break;
						out.write(buf, 0, len);
					}
				} finally {
					if (out != null) {
						out.close();
						files.add(file);
						fileCount++;
					}
				}
			}
		}
		LOG.info("created " + fileCount + " files.");
	}
}
