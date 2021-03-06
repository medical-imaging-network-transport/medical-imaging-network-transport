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
import org.apache.log4j.Logger;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.JiBXException;
import org.nema.medical.mint.changelog.ChangeSet;
import org.nema.medical.mint.server.domain.Change;
import org.nema.medical.mint.server.domain.ChangeDAO;
import org.nema.medical.mint.server.domain.StudyDAO;
import org.nema.medical.mint.utils.DateTimeParseException;
import org.nema.medical.mint.utils.ISO8601DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class ChangeLogController {

    private static final Logger LOG = Logger.getLogger(ChangeLogController.class);

	@Autowired
	protected File studiesRoot;

	@Autowired
	protected ChangeDAO changeDAO = null;

    @Autowired
    protected StudyDAO studyDAO = null;

	@Autowired
	protected String xmlStylesheet;

	@Autowired
	protected Integer fileResponseBufferSize;

	@Autowired
	protected Integer fileStreamBufferSize;

	@RequestMapping("/changelog")
	public void changelogXML(
            @RequestParam(value = "since", required = false) final String since,
            @RequestParam(value = "limit", required = false) Integer limit,
            @RequestParam(value = "offset", required = false) Integer offset,
            @RequestParam(value = "consolidate", required = false, defaultValue = "true") boolean consolidate,
			final HttpServletResponse res) throws IOException, JiBXException {

		final List<org.nema.medical.mint.changelog.Change> changes = new ArrayList<org.nema.medical.mint.changelog.Change>();
 
		// TODO read limit from a config file
        if (limit == null) {
            limit = 50;
        }
        if (offset == null) {
            offset = 0;
        }
        final int firstIndex = offset * limit;

        final List<Change> changesFound;

        if(consolidate) {
        	//only get a list of most recent changes for each study
    		changesFound = changeDAO.findLastChanges();
        } else {
			if (since != null) {
				final Date date;
				try {
					final ISO8601DateUtils dateUtil = new org.nema.medical.mint.utils.JodaDateUtils();
	                date = dateUtil.parseISO8601Basic(since);
				} catch (final DateTimeParseException e) {
					res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid date: " + since);
					return;
				}
	            if (date.getTime() > System.currentTimeMillis()) {
	                LOG.warn(String.format("Changelog requested with invalid future start date %s",
	                        DateFormat.getDateTimeInstance().format(date)));
	                res.setStatus(HttpServletResponse.SC_NO_CONTENT);
	                return;
	            }
				changesFound = changeDAO.findChanges(date, firstIndex, limit);
			} else {
				changesFound = changeDAO.findChanges(firstIndex, limit);
			}
        }
		
		if (changesFound != null) {
			for (final Change change : changesFound) {
				changes.add(new org.nema.medical.mint.changelog.Change(
                        change.getStudyUUID(), change.getIndex(), change.getType(), change.getDateTime(),
                        change.getRemoteHost(), change.getRemoteUser(), change.getOperation()));
			}
		}	
		
		res.setBufferSize(fileResponseBufferSize);
		final ChangeSet changeSet = new ChangeSet(changes);
		final IBindingFactory bfact = BindingDirectory.getFactory("serverChangelog", ChangeSet.class);
		final IMarshallingContext mctx = bfact.createMarshallingContext();
		mctx.setIndent(2);
		mctx.startDocument("UTF-8", null, res.getOutputStream());
		mctx.getXmlWriter().writePI("xml-stylesheet", xmlStylesheet);
		mctx.marshalDocument(changeSet);
		mctx.endDocument();
	}

	@RequestMapping("/studies/{uuid}/changelog")
    public void studyChangelog(@PathVariable("uuid") final String uuid,
                               final HttpServletRequest req,
                               final HttpServletResponse res) throws IOException, JiBXException {

        final Utils.StudyStatus studyStatus = Utils.validateStudyStatus(studiesRoot, uuid, res, studyDAO);
        if (studyStatus != Utils.StudyStatus.OK) {
            return;
        }

		final List<org.nema.medical.mint.changelog.Change> changes = new ArrayList<org.nema.medical.mint.changelog.Change>();

        final List<Change> changesFound = changeDAO.findChanges(uuid);
		if (changesFound != null) {
			for (Change change : changesFound) {
				changes.add(new org.nema.medical.mint.changelog.Change(
                        change.getStudyUUID(), change.getIndex(), change.getType(), change.getDateTime(),
                        change.getRemoteHost(), change.getRemoteUser(), change.getOperation()));
			}
		}

		res.setBufferSize(fileResponseBufferSize);
        final ChangeSet changeSet = new ChangeSet(uuid, changes);
        final IBindingFactory bfact = BindingDirectory.getFactory("studyChangelog", ChangeSet.class);
        final IMarshallingContext mctx = bfact.createMarshallingContext();
		mctx.setIndent(2);
		mctx.startDocument("UTF-8", null, res.getOutputStream());
		mctx.getXmlWriter().writePI("xml-stylesheet", xmlStylesheet);
		mctx.marshalDocument(changeSet);
		mctx.endDocument();
	}

	@RequestMapping("/studies/{uuid}/changelog/{seq}")
	public void studiesChangeLog(
			@PathVariable("uuid") final String uuid,
			@PathVariable("seq") final String seq,
			final HttpServletRequest req,
			final HttpServletResponse res) throws IOException {

        final Utils.StudyStatus studyStatus = Utils.validateStudyStatus(studiesRoot, uuid, res, studyDAO);
        if (studyStatus != Utils.StudyStatus.OK) {
            return;
        }


		/*
		 * The path variable 'seq' when '0.gpb' is on the end of the URL seems
		 * to be only '0' for some reason. I'm not sure what the weirdness is
		 * with '.' in the URL. The following call should return the expected
		 * sequence (i.e., '0.gpb').
		 */
		String tmp = req.getAttribute("org.springframework.web.servlet.HandlerMapping.pathWithinHandlerMapping").toString();
        if (StringUtils.isBlank(tmp)) {
			tmp = seq;
		}

        if (StringUtils.isBlank(tmp)) {
			res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid sequence requested: Empty");
			return;
		}

        final Long sequence;
        String ext = null;
        final int extPoint = tmp.indexOf('.');
        if (extPoint > -1) {
			try {
				sequence = Long.valueOf(tmp.substring(0, extPoint));
				ext = tmp.substring(extPoint+1);
			} catch (final NumberFormatException e) {
				res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid sequence requested: NaN");
				return;
			}
		}else{
			try {
				sequence = Long.valueOf(tmp);
			} catch (final NumberFormatException e) {
				res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid sequence requested: NaN");
				return;
			}
		}

        if (StringUtils.isBlank(ext)) {
			ext = "xml";
		}

		if (sequence < 0) {
			res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid sequence requested: Negative");
			return;
		}
		if (sequence >= Integer.MAX_VALUE) {
			res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid sequence requested: Too large");
			return;
		}

		res.setBufferSize(fileResponseBufferSize);
		try {
			final File file = new File(studiesRoot, uuid + "/changelog/" + sequence + "/metadata." + ext);
			if (file.exists() && file.canRead()) {
				final OutputStream out = res.getOutputStream();
				res.setContentLength((int)file.length());
				res.setContentType("text/xml");
				Utils.streamFile(file, out, fileStreamBufferSize);
			} else {
				res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid study requested: Not found");
			}
		} catch (final IOException e) {
			if (!res.isCommitted()) {
				res.sendError(HttpServletResponse.SC_BAD_REQUEST,
						"Cannot provide study change log: File Read Failure");
			}
		}
	}
}
