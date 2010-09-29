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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.JiBXException;
import org.nema.medical.mint.server.domain.Study;
import org.nema.medical.mint.server.domain.StudyDAO;
import org.nema.medical.mint.server.domain.StudyDAO.SearchKey;
import org.nema.medical.mint.studies.SearchResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class StudiesController {

	@Autowired
	protected File studiesRoot;
	
	@Autowired
	protected StudyDAO studyDAO = null;

    @RequestMapping("/studies")
    public void studies(HttpServletRequest req, final HttpServletResponse res, ModelMap map,
    		@RequestParam(value = "studyInstanceUID", required = false) String studyInstanceUID,
    		@RequestParam(value = "accessionNumber", required = false) String accessionNumber,
    		@RequestParam(value = "issuerOfAccessionNumber", required = false) String issuerOfAccessionNumber,
    		@RequestParam(value = "patientID", required = false) String patientID,
    		@RequestParam(value = "issuerOfPatientID", required = false) String issuerOfPatientID,
    		@RequestParam(value = "studyDateTimeFrom", required = false) String studyDateTimeFrom,
    		@RequestParam(value = "studyDateTimeTo", required = false) String studyDateTimeTo,
    		@RequestParam(value = "studyVersion", required = false) String studyVersion,
    		@RequestParam(value = "limit", required = false) Integer limit,
    		@RequestParam(value = "offset", required = false) Integer offset)
            throws IOException, JiBXException {

		// TODO read limit from a config file
        if (limit == null) limit = 50;
        if (offset == null) offset = 1;
        int firstIndex = (offset-1)*limit;
    	
        Map<StudyDAO.SearchKey, String> searchParams = new HashMap<StudyDAO.SearchKey, String>();

        // TODO change "studyDateTimeFrom" and "studyDateTimeTo" to
        // "minStudyDate/minStudyDateTime/maxStudyDate/maxStudyDateTime".
        // TODO rename "issuerOfAccessionNumber" and "issuerOfPatientID" to
        // "accessionNumberIssuer" and "patientIDIssuer"
        // TODO return error if request parameter not supported is provided
        if (studyInstanceUID != null) {
        	searchParams.put(SearchKey.studyInstanceUID, studyInstanceUID);
        }
        if (accessionNumber != null) {
        	searchParams.put(SearchKey.accessionNumber, accessionNumber);
        }
        if (issuerOfAccessionNumber != null) {
        	searchParams.put(SearchKey.issuerOfAccessionNumber, issuerOfAccessionNumber);
        }
        if (patientID != null) {
        	searchParams.put(SearchKey.patientID, patientID);
        }
        if (issuerOfPatientID != null) {
        	searchParams.put(SearchKey.issuerOfPatientID, issuerOfPatientID);
        }
        if (studyDateTimeFrom != null) {
        	searchParams.put(SearchKey.studyDateTimeFrom, studyDateTimeFrom);
        }
        if (studyDateTimeTo != null) {
        	searchParams.put(SearchKey.studyDateTimeTo, studyDateTimeTo);
        }
        if (studyVersion != null) {
        	searchParams.put(SearchKey.studyVersion, studyVersion);
        }
    	try {

        	// TODO studyDAO.findStudies currently uses "studyDateTimeFrom"
        	// and "studyDateTimeTo" the code block below needs to be updated
        	// when that is changed to
        	// "minStudyDate/minStudyDateTime/maxStudyDate/maxStudyDateTime".
        	Timestamp dateTimeFrom = null;
        	Timestamp dateTimeTo = null;
        	Timestamp dateFrom = null;
        	Timestamp dateTo = null;
        	if (studyDateTimeFrom != null){
        		Date timeFrom = Utils.parseISO8601Basic(studyDateTimeFrom);
        		dateTimeFrom = new Timestamp(timeFrom.getTime());
        		dateFrom = new Timestamp(timeFrom.getTime());
        	}
        	if (studyDateTimeTo != null){
        		Date timeTo = Utils.parseISO8601Basic(studyDateTimeTo);
        		dateTimeTo = new Timestamp(timeTo.getTime());
        		dateTo = new Timestamp(timeTo.getTime());
        	}
	        List<Study> studies = studyDAO.findStudies(searchParams, offset, limit);
            map.addAttribute("studies", studies);
	                
        	SearchResults searchResults = new SearchResults(req.getParameter("studyInstanceUID"), 
        			req.getParameter("accessionNumber"), req.getParameter("issuerOfAccessionNumber"), 
        			req.getParameter("patientID"), req.getParameter("issuerOfPatientID"), 
        			dateFrom, dateTimeFrom,	dateTo, dateTimeTo, StudyDAO.GMT.getID(), offset, limit);
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
    		mctx.marshalDocument(searchResults);
    		mctx.endDocument();

        } catch (ParseException e){
        	res.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }

    }
}
