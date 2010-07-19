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
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.nema.medical.mint.server.domain.StudyDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class StudyMetadataController {

	@Autowired
	protected File studiesRoot;
	
	@Autowired
	protected StudyDAO studyDAO = null;

	@RequestMapping("/studies/{uuid}/DICOM/metadata")
	public void studiesMetadata(@PathVariable("uuid") final String uuid, final HttpServletRequest httpServletRequest,
			final HttpServletResponse response) throws IOException {
		if (StringUtils.isBlank(uuid)) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid study requested: Missing");
			return;
		}
		try {
			String metadata = null;
			if (StringUtils.endsWith(httpServletRequest.getRequestURI(),".gpb")) {
				response.setContentType("application/octet-stream");
				metadata = "/metadata.gpb";
			} else {
				response.setContentType("text/xml");
				metadata = "/metadata.xml";
			}
			final File file = new File(studiesRoot , uuid + "/DICOM" + metadata);
			if (file.exists() && file.canRead()) {
				response.setContentLength(Long.valueOf(file.length()).intValue());
				Utils.streamFile(file, response.getOutputStream());
			} else {
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid study requested: Not found");
				return;
			}
		} catch (final IOException e) {
			if (!response.isCommitted()) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
						"Unable to provide study metadata. See server logs.");
				return;
			}
		}
	}
}
