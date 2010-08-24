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
import org.apache.log4j.Logger;
import org.nema.medical.mint.metadata.Study;
import org.nema.medical.mint.metadata.StudyIO;
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

	@RequestMapping("/studies/{uuid}/{type}/metadata")
	public void studiesMetadata(final @PathVariable("uuid") String uuid, 
								final @PathVariable("type") String type,
							    final HttpServletRequest req,
							    final HttpServletResponse res)
			throws IOException {
        if (StringUtils.isBlank(uuid)) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid study requested: Missing");
            return;
        }

        final File studyDir = new File(studiesRoot, uuid);
        if (!studyDir.exists() || !studyDir.canRead()) {
            LOG.error("Unable to locate directory for study: " + studyDir);
            res.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid study requested: Not found");
            return;
        }
        final File typeDir = new File(studyDir, type);
        if (!typeDir.exists() || !typeDir.canRead()) {
            LOG.error("Unable to locate directory for study: " + studyDir);
            res.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid study requested: Not found");
            return;
        }

        try {
            String filename;

            String uri = req.getRequestURI();
            boolean gzip = uri.endsWith(".gz");
            uri = StringUtils.substringBeforeLast(uri, ".gz");
            String extension = StringUtils.substringAfterLast(uri, ".");

            if ("gpb".equals(extension)) {
                res.setContentType("application/octet-stream");
                filename = "metadata.gpb";
            } else if ("xml".equals(extension) || uri.endsWith("metadata")) {
                res.setContentType("text/xml");
                filename = "metadata.xml";
            } else {
                res.sendError(HttpServletResponse.SC_NOT_FOUND, "Unknown metadata type.");
                return;
            }

            if (gzip) {
                filename = filename + ".gz";
                res.setContentType("application/gzip");
            }

            final File file = new File(typeDir, filename);
            if (!file.exists()) {
                Study study = StudyIO.loadStudy(typeDir);
                StudyIO.writeFile(study,file);
            }

            res.setContentLength(Long.valueOf(file.length()).intValue());
            Utils.streamFile(file, res.getOutputStream());
        } catch (final IOException e) {
            if (!res.isCommitted()) {
                res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Unable to provide study metadata. See server logs.");
            }
        }
	}
	
	private static final Logger LOG = Logger.getLogger(StudyMetadataController.class);
}
