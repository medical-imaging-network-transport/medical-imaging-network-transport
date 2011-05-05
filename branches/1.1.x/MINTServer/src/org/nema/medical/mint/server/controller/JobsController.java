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

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.log4j.Logger;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.JiBXException;
import org.nema.medical.mint.datadictionary.MetadataType;
import org.nema.medical.mint.jobs.HttpMessagePart;
import org.nema.medical.mint.server.domain.*;
import org.nema.medical.mint.server.processor.StudyCreateProcessor;
import org.nema.medical.mint.server.processor.StudyUpdateProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

        final MetadataType dataDictionary = availableTypes.get("DICOM");

		StudyCreateProcessor processor = new StudyCreateProcessor(jobFolder,
				new File(studiesRoot, studyUUID), dataDictionary, req.getRemoteUser(),
				req.getRemoteHost(), jobInfoDAO, studyDAO, updateDAO);
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

		if (!params.containsKey(HttpMessagePart.STUDY_UUID.toString())) {
			res.sendError(HttpServletResponse.SC_BAD_REQUEST, "missing parameter " + HttpMessagePart.STUDY_UUID);
			return;
		}

		final String studyUUID = params.get(HttpMessagePart.STUDY_UUID.toString());

        final Utils.StudyStatus studyStatus = Utils.validateStudyStatus(studiesRoot, studyUUID, res, studyDAO);
        if (studyStatus != Utils.StudyStatus.OK) {
            return;
        }

        if (!params.containsKey(HttpMessagePart.OLD_VERSION.toString())) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "missing parameter " + HttpMessagePart.OLD_VERSION);
            return;
        }

        final String oldVersion = params.get(HttpMessagePart.OLD_VERSION.toString());

		JobInfo jobInfo = new JobInfo();
		jobInfo.setId(jobID);
		jobInfo.setStudyID(studyUUID);
		jobInfo.setStatus(JobStatus.IN_PROGRESS);
		jobInfo.setStatusDescription("0% complete");
		String jobURI = req.getContextPath() + "/jobs/status/" + jobInfo.getId();
		jobInfoDAO.saveOrUpdateJobInfo(jobInfo);

		File studyFolder = new File(studiesRoot, studyUUID);

		final StudyUpdateProcessor processor = new StudyUpdateProcessor(jobFolder, studyFolder, availableTypes,
                oldVersion, req.getRemoteUser(), req.getRemoteHost(), jobInfoDAO, studyDAO, updateDAO);
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
                    outer: {
                        for (String extension : supportedMetadataExtensions) {
                            if (filename.endsWith(extension)) {
                                filename = "metadata" + extension;
                                break outer;
                            }
                        }

                        //At this point, no proper filename has been established. Last resort, use content type!
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
                    }


					file = new File(jobFolder, filename);
				} else {
                    final String msgPartName = item.getFieldName();
                    try {
                        if (!msgPartName.startsWith("binary")) {
                            throw new Exception();
                        }
                        final String itemIdStr = msgPartName.substring("binary".length());
                        final int itemId = Integer.parseInt(itemIdStr);
                        file = new File(jobFolder, String.format("%d.dat", itemId));
                    } catch (final Exception e) {
                        throw new IOException("Invalid message part name for binary data: '" + msgPartName
                                + "'; must start with 'binary', followed by a number");
                    }
				}

                if (file.exists()) {
                    throw new IOException("File for message part already exists: '" + file.getName() + "'");
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
