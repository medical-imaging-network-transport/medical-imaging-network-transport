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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.nema.medical.mint.common.StudyUtil;
import org.nema.medical.mint.metadata.Attribute;
import org.nema.medical.mint.metadata.Instance;
import org.nema.medical.mint.metadata.Series;
import org.nema.medical.mint.server.domain.Study;
import org.nema.medical.mint.server.domain.StudyDAO;
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

	@Autowired
	protected StudyDAO studyDAO = null;

	private void bufferedRead(final InputStream inputStream, final OutputStream outputStream) throws IOException {
		final byte[] bytes = new byte[8 * 1024];
		while (true) {
			synchronized (bytes) {
				final int amountRead = inputStream.read(bytes);
				if (amountRead == -1) {
					break;
				}
				outputStream.write(bytes, 0, amountRead);
			}
		}
		outputStream.flush();
	}
	
	@ModelAttribute("changes")
	public List<Change> getChanges() {
		return new LinkedList<Change>();
	}
	
	@RequestMapping("/studies/{uuid}/changelog")
	public String updates(@PathVariable("uuid") final String uuid,
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
			@RequestParam(value = "metadataType", required = false) String metadataType,
			@PathVariable("uuid") final String uuid,
			@PathVariable final String seq,
			final HttpServletResponse httpServletResponse) throws IOException {
		if (StringUtils.isBlank(uuid)) {
			// Shouldn't happen...but could be +++, I suppose
			httpServletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid study requested: Missing");
			return;
		}
		final Long sequence;
		try {
			sequence = Long.valueOf(seq);
		} catch (final NumberFormatException e) {
			httpServletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid sequence requested: NaN");
			return;
		}
		if (sequence < 0) {
			httpServletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid sequence requested: Negative");
			return;
		}
		if (sequence >= Integer.MAX_VALUE) {
			httpServletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid sequence requested: Too large");
			return;
		}

		try {
			//Defaults to xml metadata
			if(StringUtils.isBlank(metadataType))
				metadataType = ".xml";
			
			final File metadataFile = new File(studiesRoot, uuid + "/changelog/" + sequence + "/metadata" + metadataType);
			if (metadataFile.exists() && metadataFile.canRead()) {
				final InputStream in = new FileInputStream(metadataFile);
				final OutputStream out = httpServletResponse.getOutputStream();
				try {
					final long itemsize = metadataFile.length();
					httpServletResponse.setContentLength((int)itemsize);
					httpServletResponse.setContentType("text/xml");
					final int bufsize = 16384;
					byte[] buf = new byte[bufsize];
					for (long i = 0; i < itemsize; i += bufsize) {
						int len = (int) ((i + bufsize > itemsize) ? (int)itemsize - i : bufsize);
						in.read(buf,0,len);
						out.write(buf,0,len);
					}

					httpServletResponse.getOutputStream().flush();
				} finally {
					in.close();
				}
			} else {
				httpServletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid study requested: Not found");
			}
		} catch (final IOException e) {
			if (!httpServletResponse.isCommitted()) {
				httpServletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST,
						"Cannot provide study change log: File Read Failure");
			}
		}
	}

}
