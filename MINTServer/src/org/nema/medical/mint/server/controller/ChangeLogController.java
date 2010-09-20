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
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.JiBXException;
import org.nema.medical.mint.changelog.ChangeSet;
import org.nema.medical.mint.server.domain.Change;
import org.nema.medical.mint.server.domain.ChangeDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ChangeLogController {

	@Autowired
	protected File studiesRoot;

	@Autowired
	protected ChangeDAO changeDAO = null;

	@RequestMapping("/changelog")
	public void changelogXML(
            @RequestParam(value = "since", required = false) String since,
            @RequestParam(value = "limit", required = false) Integer pageSize,
            @RequestParam(value = "offset", required = false) Integer pageNum,
			final HttpServletRequest req,
			final HttpServletResponse res) throws IOException, JiBXException {

		List<org.nema.medical.mint.changelog.Change> changes = new LinkedList<org.nema.medical.mint.changelog.Change>();

		// TODO read pageSize from a config file
        if (pageSize == null) pageSize = 50;
        if (pageNum == null) pageNum = 1;
        int firstIndex = (pageNum-1)*pageSize;
        
        final List<Change> changesFound;
        
		if (since != null) {

			Date date = null;
			try {
				date = Utils.parseISO8601Basic(since);
			} catch (ParseException e) {
				res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid date: " + since);
				return;
			}
			if (date.getTime() > System.currentTimeMillis()) {
				res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Future date '" + date + "' is not valid for 'since' queries.");
				return;
			}
			changesFound = changeDAO.findChanges(date, firstIndex, pageSize);
		} else {
			changesFound = changeDAO.findChanges(firstIndex, pageSize);
		}
		
		if (changesFound != null) {
			for (Change change : changesFound) {
				changes.add(new org.nema.medical.mint.changelog.Change(change.getStudyUUID(),change.getIndex(),change.getType(),change.getDateTime(),change.getRemoteHost(),change.getRemoteUser(),change.getPrincipal()));
			}
		}
		ChangeSet changeSet = new ChangeSet(changes);
		IBindingFactory bfact = BindingDirectory.getFactory("serverChangelog",ChangeSet.class);
		IMarshallingContext mctx = bfact.createMarshallingContext();
		mctx.setIndent(2);
		mctx.marshalDocument(changeSet, "UTF-8", null, res.getOutputStream());
	}
	
	@RequestMapping("/studies/{uuid}/changelog")
    public void studyChangelog(@PathVariable("uuid") final String uuid,
                               final HttpServletRequest req,
                               final HttpServletResponse res) throws IOException, JiBXException {

		List<org.nema.medical.mint.changelog.Change> changes = new LinkedList<org.nema.medical.mint.changelog.Change>();

		if (StringUtils.isBlank(uuid)) {
            // Shouldn't happen...but could be +++, I suppose
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid study requested: Missing");
			return;
        }

        final List<Change> changesFound = changeDAO.findChanges(uuid);
		if (changesFound != null) {
			for (Change change : changesFound) {
				changes.add(new org.nema.medical.mint.changelog.Change(change.getStudyUUID(),change.getIndex(),change.getType(),change.getDateTime(),change.getRemoteHost(),change.getRemoteUser(),change.getPrincipal()));
			}
		}
		ChangeSet changeSet = new ChangeSet(uuid, changes);
		IBindingFactory bfact = BindingDirectory.getFactory("studyChangelog",ChangeSet.class);
		IMarshallingContext mctx = bfact.createMarshallingContext();
		mctx.setIndent(2);
		mctx.marshalDocument(changeSet, "UTF-8", null, res.getOutputStream());
	}

	@RequestMapping("/studies/{uuid}/changelog/{seq}")
	public void studiesChangeLog(
			@PathVariable("uuid") final String uuid,
			@PathVariable("seq") final String seq,
			final HttpServletRequest req,
			final HttpServletResponse res) throws IOException {

		String ext = null;
		
		/*
		 * The path variable 'seq' when '0.gpb' is on the end of the URL seems
		 * to be only '0' for some reason. I'm not sure what the weirdness is
		 * with '.' in the URL. The following call should return the expected
		 * sequence (i.e., '0.gpb').
		 */
		String tmp = req.getAttribute("org.springframework.web.servlet.HandlerMapping.pathWithinHandlerMapping").toString();
		if(StringUtils.isBlank(tmp))
		{
			tmp = seq;
		}
		
		if (StringUtils.isBlank(uuid)) {
			// Shouldn't happen...but could be +++, I suppose
			res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid study requested: Missing");
			return;
		}
		
		if(StringUtils.isBlank(tmp))
		{
			res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid sequence requested: Empty");
			return;
		}
		
		Long sequence;
		
		int extPoint = tmp.indexOf('.');
		if(extPoint > -1)
		{
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
		
		if(StringUtils.isBlank(ext))
		{
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
