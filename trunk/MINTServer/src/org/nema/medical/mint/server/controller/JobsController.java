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
import java.util.Arrays;
import java.util.ArrayList;
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
import org.nema.medical.mint.server.domain.JobInfo;
import org.nema.medical.mint.server.domain.JobInfoDAO;
import org.nema.medical.mint.server.domain.JobStatus;
import org.nema.medical.mint.server.domain.StudyDAO;
import org.nema.medical.mint.server.domain.ChangeDAO;
import org.nema.medical.mint.server.processor.StudyCreateProcessor;
import org.nema.medical.mint.server.processor.StudyUpdateProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class JobsController {

	private static final Logger LOG = Logger.getLogger(JobsController.class);
	private static final List<String> supportedMetadataExtensions = Arrays
			.asList(".gpb", ".gpb.gz", ".xml", ".xml.gz", ".json", ".json.gz");

	private ExecutorService executor;

	@Autowired
	protected File jobTemp;
	@Autowired
	protected File studiesRoot;

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
	public String createStudy(HttpServletRequest req, HttpServletResponse res,
			ModelMap map) throws IOException {

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
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			map.put("error_msg", "expected multipart form data");
			return "error";
		}

		try {
			handleUpload(req, res, jobFolder, files, params);
		} catch (FileUploadException e) {
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			map.put("error_msg", "unable to parse multipart form data");
			return "error";
		}

		Iterator<File> iterator = files.iterator();
		if (!iterator.hasNext()) {
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			map.put("error_msg",
					"at least one file (containing metadata) is required.");
			return "error";
		}

		if (!params.containsKey("type")) {
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			map.put("error_msg", "misser parameter 'type'");
			return "error";
		}

		String type = params.get("type");

		if (StringUtils.isBlank(type)) {
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			map.put("error_msg", "misser parameter 'type'");
			return "error";
		}

		JobInfo jobInfo = new JobInfo();
		jobInfo.setId(jobID);
		jobInfo.setStudyID(studyUUID);
		jobInfo.setStatus(JobStatus.IN_PROGRESS);
		jobInfo.setStatusDescription("0% complete");
		map.addAttribute("job", jobInfo);
		map.addAttribute("joburi", req.getContextPath() + "/jobs/createstudy/" + jobInfo.getId());
		jobInfoDAO.saveOrUpdateJobInfo(jobInfo);

		StudyCreateProcessor processor = new StudyCreateProcessor(jobFolder,
				new File(studiesRoot, studyUUID), type, jobInfoDAO, studyDAO,
				updateDAO);
		executor.execute(processor); // process immediately in the background

		// this will render the job info using jobinfo.jsp
		return "jobinfo";
	}

	@RequestMapping(method = RequestMethod.GET, value = "/jobs/createstudy/{uuid}")
	public String getCreateStatus(HttpServletRequest req,
			HttpServletResponse res, ModelMap map,
			@PathVariable("uuid") final String uuid) throws IOException {

		JobInfo jobInfo = jobInfoDAO.findJobInfo(uuid);
		map.addAttribute("job", jobInfo);
		map.addAttribute("joburi", req.getContextPath() + "/jobs/createstudy/"
				+ jobInfo.getId());

		// this will render the job info using jobinfo.jsp
		return "jobinfo";
	}

	@RequestMapping(method = RequestMethod.GET, value = "/jobs/updatestudy")
	public String updateStudy() {
		return "studyupdate";
	}

	@RequestMapping(method = RequestMethod.POST, value = "/jobs/updatestudy")
	public String updateStudy(HttpServletRequest req, HttpServletResponse res,
			ModelMap map) throws IOException {

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
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			map.put("error_msg", "expected multipart form data");
			return "error";
		}

		try {
			handleUpload(req, res, jobFolder, files, params);
		} catch (FileUploadException e) {
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			map.put("error_msg", "unable to parse multipart form data");
			return "error";
		}

		if (files.size() < 1) {
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			map.put("error_msg",
					"at least one file (containing metadata) is required.");
			return "error";
		}

		if (!params.containsKey("studyUUID")) {
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			map.put("error_msg", "missing parameter studyUUID");
			return "error";
		}

		if (!params.containsKey("type")) {
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			map.put("error_msg", "misser parameter 'type'");
			return "error";
		}

		String studyUUID = params.get("studyUUID");
		String type = params.get("type");

		if (StringUtils.isBlank(type)) {
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			map.put("error_msg", "misser parameter 'type'");
			return "error";
		}

		JobInfo jobInfo = new JobInfo();
		jobInfo.setId(jobID);
		jobInfo.setStudyID(studyUUID);
		jobInfo.setStatus(JobStatus.IN_PROGRESS);
		jobInfo.setStatusDescription("0% complete");
		map.addAttribute("job", jobInfo);
		map.addAttribute("joburi", req.getContextPath() + "/jobs/updatestudy/" + jobInfo.getId());
		jobInfoDAO.saveOrUpdateJobInfo(jobInfo);
		
		File studyFolder = new File(studiesRoot, studyUUID);
		
		if(!studyFolder.exists())
		{
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			map.put("error_msg", "study with uuid " + studyUUID + " does not exists so cannot update");
			return "error";
		}

		StudyUpdateProcessor processor = new StudyUpdateProcessor(jobFolder,
				studyFolder, type, jobInfoDAO, studyDAO,
				updateDAO);
		executor.execute(processor); // process immediately in the background

		// this will render the job info using jobinfo.jsp
		return "jobinfo";
	}

	@RequestMapping(method = RequestMethod.GET, value = "/jobs/updatestudy/{uuid}")
	public String getUpdateStatus(HttpServletRequest req,
			HttpServletResponse res, ModelMap map,
			@PathVariable("uuid") final String uuid) throws IOException {

		JobInfo jobInfo = jobInfoDAO.findJobInfo(uuid);
		map.addAttribute("job", jobInfo);
		map.addAttribute("joburi", req.getContextPath() + "/jobs/updatestudy/" + jobInfo.getId());

		// this will render the job info using jobinfo.jsp
		return "jobinfo";
	}

	// returns false if a response.sendError() was discovered
	public void handleUpload(HttpServletRequest request,
			HttpServletResponse response, File jobFolder, List<File> files,
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
