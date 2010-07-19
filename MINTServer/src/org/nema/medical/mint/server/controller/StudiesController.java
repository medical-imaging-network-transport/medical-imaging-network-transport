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
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.nema.medical.mint.server.domain.Study;
import org.nema.medical.mint.server.domain.StudyDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class StudiesController {

	@Autowired
	protected File studiesRoot;
	
	@Autowired
	protected StudyDAO studyDAO = null;

	@ModelAttribute("studies")
	public List<Study> getStudies() {
		return new LinkedList<Study>();
	}

	@RequestMapping("/studies")
	public String studies(@RequestParam(value = "studyuid", required = false) final String studyUid,
			@ModelAttribute("studies") final List<Study> studies)
			throws IOException {

		if (StringUtils.isNotBlank(studyUid)) {
			final Study study = studyDAO.findStudy(studyUid);
			if (study != null) {
				studies.add(study);
			}
		} else {
			studies.addAll(studyDAO.getMostRecentStudies(50, 5 * 24 * 60 * 60));
		}
		
		// this will render the studies list using studies.jsp
		return "studies";
	}
	
}
