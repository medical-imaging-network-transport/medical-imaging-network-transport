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

import java.io.IOException;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.nema.medical.mint.common.domain.Study;
import org.nema.medical.mint.common.domain.StudyDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.SyndFeedOutput;

@Controller
public class AtomController {

	@Autowired
	protected StudyDAO studyDAO = null;

	@RequestMapping("atom")
	public void atom(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
			throws IOException {
		try {
			final SyndFeed syndFeed = new SyndFeedImpl();
			syndFeed.setFeedType("atom_0.3");
			syndFeed.setDescription("List of studies received in last 24 hours (max 50)");
			syndFeed.setLink(httpServletRequest.getRequestURL().toString());
			syndFeed.setPublishedDate(Calendar.getInstance(StudyDAO.GMT).getTime());
			syndFeed.setTitle("Latest Studies");

			final StringBuffer link = httpServletRequest.getRequestURL();
			if (StringUtils.isNotBlank(httpServletRequest.getPathInfo())) {
				link.setLength(link.indexOf(httpServletRequest.getPathInfo()));
			}
			link.append("/studies/");

			final List<SyndEntry> list = new LinkedList<SyndEntry>();
			for (final Study study : studyDAO.getMostRecentStudies(50, 24 * 60 * 60)) {
				final SyndEntry syndEntry = new SyndEntryImpl();
				syndEntry.setLink(link + study.getID() + "/summary");
				syndEntry.setPublishedDate(study.getUpdateTime());
				syndEntry.setTitle(study.getID());

				final SyndContent syndContent = new SyndContentImpl();
				syndContent.setType("text/html");
				syndContent.setValue(StringUtils.trimToEmpty(study.getPatientName()) + "<br/>"
						+ StringUtils.trimToEmpty(study.getStudyInstanceUID()));

				syndEntry.setDescription(syndContent);
				list.add(syndEntry);
			}
			syndFeed.setEntries(list);

			httpServletResponse.setContentType("application/xml; charset=UTF-8");
			new SyndFeedOutput().output(syndFeed, httpServletResponse.getWriter());
		} catch (final Exception e) {
			if (!httpServletResponse.isCommitted()) {
				httpServletResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Cannot generate ATOM");
			}
		}
	}
}
