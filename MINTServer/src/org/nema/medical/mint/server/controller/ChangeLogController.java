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
import java.io.OutputStream;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.nema.medical.mint.server.domain.Change;
import org.nema.medical.mint.server.domain.ChangeDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ChangeLogController {

	public static final String NEWLINE = System.getProperty("line.separator");

	@Autowired
	protected File studiesRoot;
	
	@Autowired
	protected ChangeDAO changeDAO = null;

	@ModelAttribute("changes")
	public List<Change> getChanges() {
		return new LinkedList<Change>();
	}
	
	@RequestMapping("/changelog")
	public String changelog(
			@RequestParam(value = "since", required = false) String since,
			@ModelAttribute("changes") final List<Change> changes,
			final HttpServletRequest req,
			final HttpServletResponse res) throws IOException {

		if (since != null) {
			
			Date date = null;
			try {
				date = Utils.parseDate(since);
			} catch (ParseException e) {
				res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid date range: " + since);
				return "error";
			}
			if (date.getTime() > System.currentTimeMillis()) {
				res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Future date '" + date + "' is not valid for 'since' queries.");
				return "error";
			}
			final List<Change> changesFound = changeDAO.findChanges(date);
			if (changesFound != null) {
				changes.addAll(changesFound);
			}
		} else {
			changes.addAll(changeDAO.getMostRecentChanges(50));
		}
		
		// this will render the studies list using studies.jsp
		return "changelog";
	}
	
	@RequestMapping("/studies/{uuid}/changelog")
	public String studyChangelog(@PathVariable("uuid") final String uuid,
			@ModelAttribute("changes") final List<Change> changes)
			throws IOException {

		if (StringUtils.isNotBlank(uuid)) {
			final List<Change> changesFound = changeDAO.findChanges(uuid);
			if (changesFound != null) {
				changes.addAll(changesFound);
			}
		} else {
			changes.addAll(changeDAO.getMostRecentChanges(50));
		}
		
		// this will render the studies list using studies.jsp
		return "changelog";
	}
	
	@RequestMapping("/studies/{uuid}/changelog/{seq}")
	public void studiesChangeLog(
			@PathVariable("uuid") final String uuid,
			@PathVariable("seq") final String seq,
			final HttpServletRequest req,
			final HttpServletResponse res) throws IOException {
		
		String ext = "xml";
		// todo use "/studies/{uuid}/changelog/{seq:.*}" to get extension and grab other types
		
		if (StringUtils.isBlank(uuid)) {
			// Shouldn't happen...but could be +++, I suppose
			res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid study requested: Missing");
			return;
		}
		final Long sequence;
		try {
			sequence = Long.valueOf(seq);
		} catch (final NumberFormatException e) {
			res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid sequence requested: NaN");
			return;
		}
		if (sequence < 0) {
			res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid sequence requested: Negative");
			return;
		}
		if (sequence >= Integer.MAX_VALUE) {
			res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid sequence requested: Too large");
			return;
		}

		try {
			final File file = new File(studiesRoot, uuid + "/changelog/" + sequence + "/metadata." + ext);
			if (file.exists() && file.canRead()) {
				final OutputStream out = res.getOutputStream();
				res.setContentLength((int)file.length());
				res.setContentType("text/xml");
				Utils.streamFile(file, out);
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
