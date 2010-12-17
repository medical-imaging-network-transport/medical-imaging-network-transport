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
import java.io.FilenameFilter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.LinkedList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.JiBXException;
import org.nema.medical.mint.server.domain.MINTStudy;
import org.nema.medical.mint.studies.StudyRoot;
import org.nema.medical.mint.server.domain.StudyDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class StudyRootController {

	@Autowired
	protected String xmlStylesheet;

	@Autowired
	protected StudyDAO studyDAO = null;

	@Autowired
	protected File studiesRoot;
	
	@Autowired
	protected File typesRoot;
	
	@Autowired
	protected Integer fileResponseBufferSize;

	@RequestMapping("/studies/{uuid}")
	public void studyRoot(@PathVariable("uuid") final String uuid,
				final HttpServletRequest req,
				final HttpServletResponse res) throws IOException, JiBXException {

		if (StringUtils.isBlank(uuid)) {
            // Shouldn't happen...but could be +++, I suppose
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid study requested: Missing");
			return;
        }

        final File studyDir = new File(studiesRoot, uuid);
        if (!studyDir.exists() || !studyDir.canRead()) {
            LOG.error("Unable to locate directory for study: " + studyDir);
            res.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid study requested: Not found");
            return;
        }

        final File[] studyTypeFiles = studyDir.listFiles(
        		new FilenameFilter() {
        			
        			public boolean accept(File dir, String name){
        				File typeCandidate = new File(dir,name);
        				File typeRoot = new File(typesRoot, name + ".xml");
        				if (typeCandidate.isDirectory() & typeRoot.exists()){
       						return true;
        				}
        				return false;
        			}
        			
        		}
        );
		LinkedList<String> studyTypeFileList = new LinkedList<String>();
		
		for (File studyTypeFile : studyTypeFiles) {
			studyTypeFileList.add(studyTypeFile.getName());
		}
		
		MINTStudy study = studyDAO.findStudy(uuid);
		
		Timestamp lastUpdated;
		if (study.getLastModified() != null){
			lastUpdated = study.getLastModified();
		}
		else {
			lastUpdated = study.getDateTime();
		}
		
		StudyRoot studyRoot = new StudyRoot(study.getID(), lastUpdated,
				Integer.parseInt(study.getStudyVersion()), studyTypeFileList);

		IBindingFactory bfact = BindingDirectory.getFactory("studyRoot",StudyRoot.class);
		IMarshallingContext mctx = bfact.createMarshallingContext();
		mctx.setIndent(2);
		mctx.startDocument("UTF-8", null, res.getOutputStream());
		mctx.getXmlWriter().writePI("xml-stylesheet", xmlStylesheet);
		mctx.marshalDocument(studyRoot);
		mctx.endDocument();
		
	}
	private static final Logger LOG = Logger.getLogger(StudyRootController.class);

}
