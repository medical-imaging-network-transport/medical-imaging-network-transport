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
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.JiBXException;
import org.nema.medical.mint.server.domain.Study;
import org.nema.medical.mint.server.domain.StudyDAO;
import org.nema.medical.mint.studies.SearchResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class StudiesController {

	@Autowired
	protected String xmlStylesheet;

	@Autowired
	protected File studiesRoot;
	
	@Autowired
	protected StudyDAO studyDAO = null;

    @RequestMapping("/studies")
    public void studies(final HttpServletResponse res,
    		@RequestParam(value = "studyInstanceUID", required = false) String studyInstanceUID,
    		@RequestParam(value = "accessionNumber", required = false) String accessionNumber,
    		@RequestParam(value = "accessionNumberIssuer", required = false) String accessionNumberIssuer,
    		@RequestParam(value = "patientID", required = false) String patientID,
    		@RequestParam(value = "patientIDIssuer", required = false) String patientIDIssuer,
            @RequestParam(value = "minStudyDateTime", required = false) String minStudyDateTime,
            @RequestParam(value = "minStudyDate", required = false) String minStudyDate,
            @RequestParam(value = "maxStudyDateTime", required = false) String maxStudyDateTime,
            @RequestParam(value = "maxStudyDate", required = false) String maxStudyDate,
    		@RequestParam(value = "limit", required = false) Integer limit,
    		@RequestParam(value = "offset", required = false) Integer offset)
            throws IOException, JiBXException {

		// TODO read limit from a config file
        if (limit == null) limit = 50;
        if (offset == null) offset = 1;
    	
        // TODO return error if request parameter not supported is provided

    	try {
        	Timestamp dateTimeFrom = null;
        	Timestamp dateTimeTo = null;
            if (minStudyDate != null && StringUtils.isNotBlank(minStudyDate)){
                Date testParse = Utils.parseISO8601Date(minStudyDate);
            }
        	if (minStudyDateTime != null && StringUtils.isNotBlank(minStudyDateTime)){
        		dateTimeFrom = new Timestamp((Utils.parseISO8601(minStudyDateTime)).getTime());
        	}
            if (maxStudyDate != null && StringUtils.isNotBlank(maxStudyDate)){
                Date testParse = Utils.parseISO8601Date(maxStudyDate);
            }
            if (maxStudyDateTime != null && StringUtils.isNotBlank(maxStudyDateTime)){
                dateTimeTo = new Timestamp(Utils.parseISO8601(maxStudyDateTime).getTime());
            }
	        List<Study> studies = studyDAO.findStudies(studyInstanceUID, accessionNumber,
                    accessionNumberIssuer, patientID, patientIDIssuer, minStudyDateTime,
                    minStudyDate, maxStudyDateTime, maxStudyDate, limit, offset);

        	SearchResults searchResults = new SearchResults(studyInstanceUID, accessionNumber, accessionNumberIssuer,
        			patientID, patientIDIssuer, minStudyDate, dateTimeFrom, maxStudyDate, dateTimeTo, StudyDAO.GMT.getID(),
        			offset, limit);
            
        	for (Study foundStudy : studies){
        		Timestamp lastUpdated;
        		if (foundStudy.getLastModified() != null){
        			lastUpdated = foundStudy.getLastModified();
        		}
        		else {
        			lastUpdated = foundStudy.getDateTime();
        		}
        		org.nema.medical.mint.studies.Study studySearchResult = new org.nema.medical.mint.studies.Study(
        				foundStudy.getID(), lastUpdated,
        				Integer.parseInt(foundStudy.getStudyVersion()));
        		searchResults.addStudy(studySearchResult);
        	}
    		IBindingFactory bfact = BindingDirectory.getFactory("studySearchResults",SearchResults.class);
    		IMarshallingContext mctx = bfact.createMarshallingContext();
    		mctx.setIndent(2);
    		mctx.startDocument("UTF-8", null, res.getOutputStream());
    		mctx.getXmlWriter().writePI("xml-stylesheet", xmlStylesheet);
    		mctx.marshalDocument(searchResults);
    		mctx.endDocument();

        } catch (ParseException e){
        	res.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }

    }
}
