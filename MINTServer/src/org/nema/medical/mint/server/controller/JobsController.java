package org.nema.medical.mint.server.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.log4j.Logger;
import org.nema.medical.mint.common.domain.JobInfo;
import org.nema.medical.mint.common.domain.JobStatus;
import org.nema.medical.mint.common.metadata.Study;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class JobsController {

	private static final Logger LOG = Logger.getLogger(JobsController.class);

	@Autowired
	protected File mintHome;
	@Autowired
	protected File jobTemp;
	@Autowired
	protected File studyRoot;

	@RequestMapping(method = RequestMethod.GET, value = "/jobs/createstudy/{uuid}")
	public void getCreateStatus(@PathVariable final String uuid,
			final HttpServletResponse response) throws IOException {

	}

	@RequestMapping(method = RequestMethod.GET, value = "/jobs/updatestudy/{uuid}")
	public void getUpdateStatus(@PathVariable final String uuid,
			final HttpServletResponse response) throws IOException {

	}

	@RequestMapping(method = RequestMethod.POST, value = "/jobs/createstudy")
	public String createStudy(HttpServletRequest req, HttpServletResponse res, ModelMap map)
			throws IOException {

		String studyUUID = UUID.randomUUID().toString();
		String jobID = UUID.randomUUID().toString();
		
		File jobFolder = new File(mintHome, jobID);
		jobFolder.mkdir();

		// the list of files uploaded
		List<File> files = new ArrayList<File>();

		// the set of form parameters
		Map<String, String> params = new HashMap<String, String>();

		// Check that we have a file upload request
		boolean isMultipart = ServletFileUpload.isMultipartContent(req);
		if (!isMultipart) {
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			
			res.sendError(HttpServletResponse.SC_BAD_REQUEST,
					"expected multipart form data");
		}

		try {
			handleUpload(req, res, jobFolder, files, params);
		} catch (FileUploadException e) {
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			map.put("error","unable to parse multipart form data");
			return "error";
		}

		if (files.size() < 1) {
			res.sendError(HttpServletResponse.SC_BAD_REQUEST,
					"At least on file (containing metadata) is required.");
			return "error";
		} else {
			File metadata = files.get(0);
			Study study;
			if (metadata.getPath().endsWith(".xml")) {
				//study = StudyIO.parseFromXML(metadata);
			} else if (metadata.getPath().endsWith(".gbp")) {
				//study = StudyIO.parseFromGPB(metadata);
			} else {
				res.sendError(HttpServletResponse.SC_BAD_REQUEST,
						"Unknown metadata format");
				return "error";
			}

		}
		

		JobInfo info = new JobInfo();
		info.setId(jobID);
		info.setStudyID(studyUUID);
		info.setStatus(JobStatus.IN_PROGRESS);
		info.setStatusDescription("0% complete");
		map.addAttribute("jobinfo",info);

		// this will render the job info using jobinfo.jsp
		return "jobinfo";
		}

	@RequestMapping(method = RequestMethod.POST, value = "/jobs/updatestudy")
	public void updateStudy(HttpServletRequest req, HttpServletResponse res)
			throws IOException {

		String jobID = UUID.randomUUID().toString();
		File jobFolder = new File(mintHome, jobID);
		jobFolder.mkdir();

		// the list of files uploaded
		List<File> files = new ArrayList<File>();

		// the set of form parameters
		Map<String, String> params = new HashMap<String, String>();

		// Check that we have a file upload request
		boolean isMultipart = ServletFileUpload.isMultipartContent(req);
		if (!isMultipart) {
			res.sendError(HttpServletResponse.SC_BAD_REQUEST,
					"expected multipart form data");
		}

		try {
			handleUpload(req, res, jobFolder, files, params);
		} catch (FileUploadException e) {
			res.sendError(HttpServletResponse.SC_BAD_REQUEST,
					"unable to parse multipart form data");
			return;
		}

		if (files.size() < 1) {
			res.sendError(HttpServletResponse.SC_BAD_REQUEST,
					"At least one file (containing metadata) is required.");
			return;
		}

		if (!params.containsKey("studyUUID")) {
			res.sendError(HttpServletResponse.SC_BAD_REQUEST,
					"Missing parameter studyUUID.");
			return;
		}

	}

	// returns false if a response.sendError() was discovered
	public void handleUpload(HttpServletRequest request,
			HttpServletResponse response, File jobFolder, List<File> files,
			Map<String, String> params) throws IOException, FileUploadException {

		int bid = 0;

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
				params.put(name, Streams.asString(in));
			} else {
				File file;
				if (files.isEmpty()) {
					String contentType = item.getContentType();
					if ("text/xml".equals(contentType)) {
						file = new File(jobFolder, "metadata.xml");
					} else if("application/octet-stream".equals(contentType)) {
						file = new File(jobFolder, "metadata.gpb");
					} else {
						file = new File(jobFolder, "metadata.dat");
					}
				} else {
					file = new File(jobFolder, String.format("file%04d.dat",
							bid++));
				}

				FileOutputStream out = null;
				try {
					while (true) {
						byte buf[] = new byte[8 * 1024];
						int len = in.read(buf);
						if (len < 0)
							break;
						if (out == null) {
							// defer create so we don't create empty files
							out = new FileOutputStream(file);
							LOG.info("creating... " + file);
						}
						out.write(buf, 0, len);
					}
				} finally {
					if (out != null)
						out.close();
				}
				files.add(file);
			}
		}
	}
}
