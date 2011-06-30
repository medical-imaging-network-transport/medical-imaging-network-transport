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

import org.apache.commons.lang.StringUtils;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.JiBXException;
import org.nema.medical.mint.server.domain.MINTStudy;
import org.nema.medical.mint.server.domain.StudyDAO;
import org.nema.medical.mint.studies.SearchResultStudy;
import org.nema.medical.mint.studies.SearchResults;
import org.nema.medical.mint.utils.DateTimeParseException;
import org.nema.medical.mint.utils.JodaDateUtils;
import org.nema.medical.mint.utils.ISO8601DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

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
    		@RequestParam(value = "studyInstanceUID", required = false) final String studyInstanceUID,
    		@RequestParam(value = "accessionNumber", required = false) final String accessionNumber,
    		@RequestParam(value = "accessionNumberIssuer", required = false) final String accessionNumberIssuer,
    		@RequestParam(value = "patientID", required = false) final String patientID,
    		@RequestParam(value = "patientIDIssuer", required = false) final String patientIDIssuer,
            @RequestParam(value = "minStudyDateTime", required = false) final String minStudyDateTime,
            @RequestParam(value = "minStudyDate", required = false) final String minStudyDate,
            @RequestParam(value = "maxStudyDateTime", required = false) final String maxStudyDateTime,
            @RequestParam(value = "maxStudyDate", required = false) final String maxStudyDate,
    		@RequestParam(value = "limit", required = false) Integer limit,
    		@RequestParam(value = "offset", required = false) Integer offset)
            throws IOException, JiBXException {

		// TODO read limit from a config file
        if (limit == null) {
            limit = 50;
        }
        if (offset == null) {
            offset = 1;
        }

        // TODO return error if request parameter not supported is provided

    	try {
        	final Date dateTimeFrom;
        	final Date dateTimeTo;
            final Date dateFrom;
            final Date dateTo;
            final ISO8601DateUtils dateUtil = new org.nema.medical.mint.utils.JodaDateUtils();
            if (minStudyDate != null && StringUtils.isNotBlank(minStudyDate)){
                dateFrom = dateUtil.parseISO8601DateBasic(minStudyDate);
            } else {
                dateFrom = null;
            }
        	if (minStudyDateTime != null && StringUtils.isNotBlank(minStudyDateTime)){
                dateTimeFrom = dateUtil.parseISO8601Basic(minStudyDateTime);
        	} else {
                dateTimeFrom = null;
            }
            if (maxStudyDate != null && StringUtils.isNotBlank(maxStudyDate)){
                dateTo = dateUtil.parseISO8601DateBasic(maxStudyDate);
            } else {
                dateTo = null;
            }
            if (maxStudyDateTime != null && StringUtils.isNotBlank(maxStudyDateTime)){
                dateTimeTo = dateUtil.parseISO8601Basic(maxStudyDateTime);
            } else {
                dateTimeTo = null;
            }
	        final List<MINTStudy> studies = studyDAO.findStudies(studyInstanceUID, accessionNumber,
                    accessionNumberIssuer, patientID, patientIDIssuer, dateTimeFrom, dateFrom, dateTimeTo, dateTo,
                    limit, offset);

        	final SearchResults searchResults = new SearchResults(studyInstanceUID, accessionNumber,
                    accessionNumberIssuer, patientID, patientIDIssuer, minStudyDate,
                    dateTimeFrom == null ? null : new Timestamp(dateTimeFrom.getTime()), maxStudyDate,
                    dateTimeTo == null ? null : new Timestamp(dateTimeTo.getTime()), StudyDAO.GMT.getID(), offset,
                    limit);

        	for (final MINTStudy foundStudy: studies) {
        		final Timestamp lastUpdated;
        		if (foundStudy.getLastModified() != null) {
        			lastUpdated = foundStudy.getLastModified();
        		} else {
        			lastUpdated = foundStudy.getDateTime();
        		}
                final SearchResultStudy studySearchResult = new SearchResultStudy(
                        foundStudy.getID(), lastUpdated, foundStudy.getStudyVersion());
        		searchResults.addStudy(studySearchResult);
        	}
    		final IBindingFactory bfact = BindingDirectory.getFactory("studySearchResults", SearchResults.class);
    		final IMarshallingContext mctx = bfact.createMarshallingContext();
    		mctx.setIndent(2);
    		mctx.startDocument("UTF-8", null, res.getOutputStream());
    		mctx.getXmlWriter().writePI("xml-stylesheet", xmlStylesheet);
    		mctx.marshalDocument(searchResults);
    		mctx.endDocument();
        } catch (final DateTimeParseException e) {
        	res.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }
}
