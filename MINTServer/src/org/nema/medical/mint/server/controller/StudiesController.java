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
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nema.medical.mint.server.domain.Study;
import org.nema.medical.mint.server.domain.StudyDAO;
import org.nema.medical.mint.server.domain.StudyDAO.SearchKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class StudiesController {

	@Autowired
	protected File studiesRoot;
	
	@Autowired
	protected StudyDAO studyDAO = null;

    @RequestMapping("/studies")
    public String studies(HttpServletRequest req, final HttpServletResponse res, ModelMap map)
            throws IOException {
        List<Study> studies = new LinkedList<Study>();
        map.addAttribute("studies", studies);

        Map<StudyDAO.SearchKey, Object> searchParams = new HashMap<StudyDAO.SearchKey, Object>();
        for (StudyDAO.SearchKey key : SearchKey.values()) {
            String value = req.getParameter(key.name());
            if (value != null) {
                switch (key.type) {
                    case Date:
                        Date date;
                        try {
                            date = Utils.parseDate(value);
                        } catch (ParseException e) {
                            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid date: " + value);
                            return "error";
                        }
                        if (date.getTime() > System.currentTimeMillis()) {
                            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Future date '" + date + "' is not valid for 'since' queries.");
                            return "error";
                        }
                        searchParams.put(key,date);
                        break;
                    case Number:
                        Integer integer;
                        try {
                            integer = Integer.parseInt(value);
                        } catch (NumberFormatException e) {
                            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid parameter: " + key.name() + "," + value);
                            return "error";
                        }
                        searchParams.put(key,integer);
                        break;
                    case String:
                    default:
                        searchParams.put(key,value);
                }
            }
        }

        studies.addAll(studyDAO.findStudies(searchParams));
        return "studies";
    }
}
