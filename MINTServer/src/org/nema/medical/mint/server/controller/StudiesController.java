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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.nema.medical.mint.common.metadata.BinaryItem;
import org.nema.medical.mint.common.metadata.BinaryItemIO;
import org.nema.medical.mint.server.domain.Study;
import org.nema.medical.mint.server.domain.StudyDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class StudiesController {

	public static final String NEWLINE = System.getProperty("line.separator");

	@Autowired
	protected File studiesRoot;

	@Autowired
	protected StudyDAO studyDAO = null;

	private void bufferedRead(final InputStream inputStream, final OutputStream outputStream) throws IOException {
		final byte[] bytes = new byte[8 * 1024];
		while (true) {
			synchronized (bytes) {
				final int amountRead = inputStream.read(bytes);
				if (amountRead == -1) {
					break;
				}
				outputStream.write(bytes, 0, amountRead);
			}
		}
		outputStream.flush();
	}

	@ModelAttribute("Studies")
	public List<Study> getStudies() {
		return new LinkedList<Study>();
	}

	@RequestMapping("/studies")
	public String studies(@RequestParam(value = "studyuid", required = false) final String studyUid,
			@ModelAttribute("Studies") final List<Study> studies)
			throws IOException {

		if (StringUtils.isNotBlank(studyUid)) {
			final Study study = studyDAO.findStudy(studyUid);
			if (study != null) {
				studies.add(study);
			}
		} else {
			studies.addAll(studyDAO.getMostRecentStudies(50, 24 * 60 * 60));
		}
		
		// this will render the studies list using studies.jsp
		return "studies";
	}

	@RequestMapping("/studies/{uuid}/DICOM/binaryitems/{seq}")
	public void studiesBinaryItems(@PathVariable final String uuid, @PathVariable final String seq,
			final HttpServletResponse httpServletResponse) throws IOException {
		if (StringUtils.isBlank(uuid)) {
			// Shouldn't happen...but could be +++, I suppose
			httpServletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid study requested: Missing");
			return;
		}
		final Long sequence;
		try {
			sequence = Long.valueOf(seq);
		} catch (final NumberFormatException e) {
			httpServletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid sequence requested: NaN");
			return;
		}
		if (sequence < 0) {
			httpServletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid sequence requested: Negative");
			return;
		}
		if (sequence >= Integer.MAX_VALUE) {
			httpServletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid sequence requested: Too large");
			return;
		}

		try {
			final File binaryItemFile = new File(studiesRoot, uuid + "/DICOM/binaryitems/" + sequence + ".dat");
			if (binaryItemFile.exists() && binaryItemFile.canRead()) {
				final InputStream in = new FileInputStream(binaryItemFile);
				final OutputStream out = httpServletResponse.getOutputStream();
				try {
					final long itemsize = binaryItemFile.length();
					httpServletResponse.setContentLength((int)itemsize);
					httpServletResponse.setContentType("application/octet-stream");
					final int bufsize = 16384;
					byte[] buf = new byte[bufsize];
					for (long i = 0; i < itemsize; i += bufsize) {
						int len = (int) ((i + bufsize > itemsize) ? (int)itemsize - i : bufsize);
						in.read(buf,0,len);
						out.write(buf,0,len);
					}

					httpServletResponse.getOutputStream().flush();
				} finally {
					in.close();
				}
			} else {
				httpServletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid study requested: Not found");
			}
		} catch (final IOException e) {
			if (!httpServletResponse.isCommitted()) {
				httpServletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST,
						"Cannot provide study binary items: File Read Failure");
			}
		}
	}

	@RequestMapping(value = { "/studies/{uuid}/DICOM/metadata", "/studies/{uuid}/metadata.gpb",
			"/studies/{uuid}/metadata.xml" })
	public void studiesMetadata(@PathVariable final String uuid, final HttpServletRequest httpServletRequest,
			final HttpServletResponse httpServletResponse) throws IOException {
		if (StringUtils.isBlank(uuid)) {
			httpServletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid study requested: Missing");
			return;
		}
		try {
			String metadata = null;
			if (StringUtils.endsWith(httpServletRequest.getPathInfo(),".gpb")) {
				httpServletResponse.setContentType("application/octet-stream");
				metadata = "/metadata.gpb";
			} else {
				httpServletResponse.setContentType("text/xml");
				metadata = "/metadata.xml";
			}
			final File file = new File(studiesRoot , uuid + "/DICOM/" + metadata);
			if (file.exists() && file.canRead()) {
				httpServletResponse.setContentLength(Long.valueOf(file.length()).intValue());
				final FileInputStream fileInputStream = new FileInputStream(file);
				try {
					final OutputStream outputStream = httpServletResponse.getOutputStream();
					bufferedRead(fileInputStream, outputStream);
				} finally {
					fileInputStream.close();
				}
			} else {
				httpServletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid study requested: Not found");
			}
		} catch (final IOException e) {
			if (!httpServletResponse.isCommitted()) {
				httpServletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST,
						"Cannot provide study metadata: File Read Failure");
			}
		}
	}

	@RequestMapping(value = { "/studies/{uuid}/DICOM/summary", "/studies/{uuid}/DICOM/summary.gpb", "/studies/{uuid}/DICOM/summary.html" })
	public void studiesSummary(@PathVariable final String uuid, final HttpServletRequest httpServletRequest,
			final HttpServletResponse httpServletResponse) throws IOException {
		if (StringUtils.isBlank(uuid)) {
			httpServletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid study requested: Missing");
			return;
		}
		try {
			String summary = null;
			if (StringUtils.endsWith(httpServletRequest.getPathInfo(),".gpb")) {
				httpServletResponse.setContentType("application/octet-stream");
				summary = "/summary.gpb";
			} else {
				httpServletResponse.setContentType("text/html");
				summary = "/summary.html";
			}
			final File file = new File(studiesRoot, uuid + "/DICOM/" + summary);
			if (file.exists() && file.canRead()) {
				httpServletResponse.setContentLength(Long.valueOf(file.length()).intValue());
				final FileInputStream fileInputStream = new FileInputStream(file);
				try {
					final OutputStream outputStream = httpServletResponse.getOutputStream();
					bufferedRead(fileInputStream, outputStream);
				} finally {
					fileInputStream.close();
				}
			} else {
				httpServletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid study requested: Not found");
			}
		} catch (final IOException e) {
			if (!httpServletResponse.isCommitted()) {
				httpServletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST,
						"Cannot provide study metadata: File Read Failure");
			}
		}
	}

}
