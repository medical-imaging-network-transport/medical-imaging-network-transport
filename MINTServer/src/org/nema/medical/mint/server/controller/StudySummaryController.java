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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.nema.medical.mint.server.domain.StudyDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class StudySummaryController {

	@Autowired
	protected File studiesRoot;
	
	@Autowired
	protected StudyDAO studyDAO = null;

	@RequestMapping("/studies/{uuid}/DICOM/summary")
	public void studiesSummary(@PathVariable("uuid") final String uuid, final HttpServletRequest httpServletRequest,
			final HttpServletResponse httpServletResponse) throws IOException {
		if (StringUtils.isBlank(uuid)) {
			httpServletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid study requested: Missing Study UUID");
			return;
		}
		try {
			httpServletResponse.setContentType("text/html");
			final File file = new File(studiesRoot, uuid + "/DICOM/summary.html");
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
				httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid study requested: Not found");
			}
		} catch (final IOException e) {
			if (!httpServletResponse.isCommitted()) {
				httpServletResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
						"Cannot provide study metadata: File Read Failure");
			}
		}
	}

	private void bufferedRead(final InputStream inputStream, final OutputStream outputStream) throws IOException {
		final byte[] bytes = new byte[8 * 1024];
		while (true) {
			final int amountRead = inputStream.read(bytes);
			if (amountRead == -1) {
				break;
			}
			outputStream.write(bytes, 0, amountRead);
		}
		outputStream.flush();
	}

}
