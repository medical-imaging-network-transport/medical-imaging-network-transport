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
import java.security.Principal;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.JiBXException;
import org.nema.medical.mint.changelog.ChangeOperation;
import org.nema.medical.mint.server.domain.Change;
import org.nema.medical.mint.server.domain.ChangeDAO;
import org.nema.medical.mint.server.domain.MINTStudy;
import org.nema.medical.mint.studies.StudyRoot;
import org.nema.medical.mint.server.domain.StudyDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class StudyRootController {

	@Autowired
	protected String xmlStylesheet;

	@Autowired
	protected StudyDAO studyDAO;

	@Autowired
	protected File studiesRoot;
	
	@Autowired
	protected File typesRoot;
	
	@Autowired
	protected Integer fileResponseBufferSize;

    @Autowired
    protected ChangeDAO changeDAO;

	@RequestMapping(method = RequestMethod.GET, value = "/studies/{uuid}")
	public void studyRoot(@PathVariable("uuid") final String uuid,
				final HttpServletRequest req,
				final HttpServletResponse res) throws IOException, JiBXException {

        final Utils.StudyStatus studyStatus = Utils.validateStudyStatus(studiesRoot, uuid, res, studyDAO);
        if (studyStatus != Utils.StudyStatus.OK) {
            return;
        }

        final MINTStudy study = studyDAO.findStudy(uuid);
        final File studyDir = new File(studiesRoot, uuid);
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

    @RequestMapping(method = RequestMethod.DELETE, value = "/studies/{uuid}")
    public void deleteStudy(@PathVariable("uuid") final String uuid, final HttpServletRequest req,
                            final HttpServletResponse res) throws IOException {
        final Utils.StudyStatus studyStatus = Utils.validateStudyStatus(studiesRoot, uuid, res, studyDAO);
        if (studyStatus != Utils.StudyStatus.OK) {
            return;
        }

        final Principal principal = req.getUserPrincipal();
        final String principalName = (principal != null) ? principal.getName() : null;

        deleteStudy(uuid, studiesRoot, req.getRemoteUser(), req.getRemoteHost(), principalName, changeDAO, studyDAO);
        res.setStatus(204);
    }

    public static void deleteStudy(final String uuid, final File studiesRoot, final String remoteUser,
                                   final String remoteHost, final String principal, final ChangeDAO changeDAO,
                                   final StudyDAO studyDAO) throws IOException {
         final File studyDir = new File(studiesRoot, uuid);
         FileUtils.deleteDirectory(studyDir);

         final Change lastChange = changeDAO.findLastChange(uuid);
         if (lastChange == null) {
             throw new IOException("No changes in database for study UUID " + uuid);
         }

         final Change deleteInfo = new Change();
         deleteInfo.setId(UUID.randomUUID().toString());
         deleteInfo.setType("DICOM");
         deleteInfo.setStudyID(uuid);
         deleteInfo.setRemoteUser(remoteUser);
         deleteInfo.setRemoteHost(remoteHost);
         deleteInfo.setPrincipal(principal);
         deleteInfo.setIndex(lastChange.getIndex() + 1);
         deleteInfo.setOperation(ChangeOperation.DELETE);
         changeDAO.saveChange(deleteInfo);

         final MINTStudy studyData = new MINTStudy();
         studyData.setID(uuid);
         studyData.setDateTime(MINTStudy.now());
         studyData.setStudyVersion("-1");
         studyDAO.updateStudy(studyData);
     }

	private static final Logger LOG = Logger.getLogger(StudyRootController.class);

}
