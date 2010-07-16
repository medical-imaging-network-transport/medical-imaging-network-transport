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
public class StudiesController {

	public static final String NEWLINE = System.getProperty("line.separator");

	@Autowired
	protected File studiesRoot;
	
	@Autowired
	protected ChangeDAO updateDAO = null;

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
			studies.addAll(studyDAO.getMostRecentStudies(50, 24 * 60 * 60));
		}
		
		// this will render the studies list using studies.jsp
		return "studies";
	}
	
	@RequestMapping("/studies/{uuid}/DICOM/binaryitems/{seq}")
	public void studiesBinaryItems(@PathVariable("uuid") final String uuid, @PathVariable final String seq,
			final HttpServletResponse httpServletResponse) throws IOException {
		if (StringUtils.isBlank(uuid)) {
			// Shouldn't happen...but could be +++, I suppose
			httpServletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid study requested: Missing");
			return;
		}
		
		final File studyRoot = new File(studiesRoot , uuid);
		
		final List<Long> itemList = parseItemList(seq, studyRoot, httpServletResponse);
		
		if(itemList == null)
		{
			//failed to parse seq, error message already added
			return;
		}
		
		//Make sure all the binary files requested are existing
		final List<File> binaryItems = new ArrayList<File>();
		final File binaryRoot = new File(studyRoot, "DICOM/binaryitems");
		
		for(long l : itemList)
		{
			File f = new File(binaryRoot, l + ".dat");
			
			if(!f.exists() || !f.canRead())
			{
				httpServletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid binary item request: Not found : " + l);
				return;
			}
			
			binaryItems.add(f);
		}
		
		//Send all binaryItems to the original requestor
		if(binaryItems.size() == 1)
		{
			//Normal process to send an single item back
			try {
				final File binaryItemFile = binaryItems.get(0);
				if (binaryItemFile.exists() && binaryItemFile.canRead()) {
					final InputStream in = new FileInputStream(binaryItemFile);
					final OutputStream out = httpServletResponse.getOutputStream();
					try {
						final long itemsize = binaryItemFile.length();
						httpServletResponse.setContentLength((int)itemsize);
						httpServletResponse.setContentType("application/octet-stream");
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
							"Cannot provide study binary items: File Read Failure");
				}
			}
		}else{
			//TODO use multipart MIME response to send all binaryItems
			try
			{
				final String boundary = "BinaryItem";
				httpServletResponse.setContentType("multipart/x-mixed-replace; boundary=\"" + boundary + "\"");
				
				final OutputStream out = httpServletResponse.getOutputStream();
				
				out.write(("--" + boundary).getBytes());
				out.flush();
				for(File binaryItem : binaryItems)
				{
					final int bufsize = 16384;
					byte[] buf = new byte[bufsize];
					
					final InputStream in = new FileInputStream(binaryItem);
					try 
					{
						out.write('\n');
						
						final long itemsize = binaryItem.length();
						out.write("Content-type: application/octet-stream\n".getBytes());
						out.write(("Content-length: " + itemsize + "\n\n").getBytes());
						
						for (long i = 0; i < itemsize; i += bufsize) {
							int len = (int) ((i + bufsize > itemsize) ? (int)itemsize - i : bufsize);
							in.read(buf,0,len);
							out.write(buf,0,len);
						}
						
						out.write(("\n--" + boundary).getBytes());
	
						out.flush();
					} finally {
						in.close();
					}
				}
				out.write("--".getBytes());
				out.flush();
			} catch (final IOException e) {
				if (!httpServletResponse.isCommitted()) {
					httpServletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST,
							"Cannot provide study binary items: File Read Failure");
				}
			}
		}
	}
	
	private List<Long> parseItemList(String seq, File studyRoot, final HttpServletResponse httpServletResponse) throws IOException
	{
		final List<Long> itemList = new ArrayList<Long>();
		
		if(seq.equals("all"))
		{
			//need to return all items in current metadata
			File dicomRoot = new File(studyRoot, "DICOM");
			
			org.nema.medical.mint.metadata.Study study = StudyUtil.loadStudy(dicomRoot);
			
			//Go through each instance and collect the bids
			for(Iterator<Series> i = study.seriesIterator(); i.hasNext();)
			{
				for(Iterator<Instance> ii = i.next().instanceIterator(); ii.hasNext();)
				{
					for(Iterator<Attribute> iii = ii.next().attributeIterator(); iii.hasNext();)
					{
						Attribute a = iii.next();
						
						int bid = a.getBid();
						if(bid >= 0)
						{
							itemList.add((long)bid);
						}
					}
				}
			}
		}else{
			String[] elements = seq.split(",");
			
			for(String element : elements)
			{
				String[] range = element.split("-");
				
				if(range.length < 1 || range.length > 2)
				{
					//failed to parse element, error message not set yet
					return null;
				}
				
				long start = parseLong(range[0], httpServletResponse);
				long end = start;
				if(range.length == 2)
				{
					end = parseLong(range[1], httpServletResponse);
				}
				
				if(start < 0 || end < 0)
				{
					//failed to parse element, error message already set
					return null;
				}
				
				//for each item in the range, add to itemList
				for(;start <= end; ++start)
				{
					itemList.add(start);
				}
			}
		}
		
		return itemList;
	}
	
	private Long parseLong(String str, final HttpServletResponse httpServletResponse) throws IOException
	{
		final Long num;
		try {
			num = Long.valueOf(str);
		} catch (final NumberFormatException e) {
			httpServletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid sequence requested: NaN");
			return -1l;
		}
		if (num < 0) {
			httpServletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid sequence requested: Negative");
			return -1l;
		}
		if (num >= Integer.MAX_VALUE) {
			httpServletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid sequence requested: Too large");
			return -1l;
		}
		
		return num;
	}

	@RequestMapping("/studies/{uuid}/DICOM/metadata")
	public void studiesMetadata(@PathVariable("uuid") final String uuid, final HttpServletRequest httpServletRequest,
			final HttpServletResponse httpServletResponse) throws IOException {
		if (StringUtils.isBlank(uuid)) {
			httpServletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid study requested: Missing");
			return;
		}
		try {
			String metadata = null;
			if (StringUtils.endsWith(httpServletRequest.getRequestURI(),".gpb")) {
				httpServletResponse.setContentType("application/octet-stream");
				metadata = "/metadata.gpb";
			} else {
				httpServletResponse.setContentType("text/xml");
				metadata = "/metadata.xml";
			}
			final File file = new File(studiesRoot , uuid + "/DICOM" + metadata);
			if (file.exists() && file.canRead()) {
				httpServletResponse.setContentLength(Long.valueOf(file.length()).intValue());
				final FileInputStream fileInputStream = new FileInputStream(file);
				try {
					final OutputStream outputStream = httpServletResponse.getOutputStream();
					bufferedRead(fileInputStream, outputStream);
				} finally {
					fileInputStream.close();
				}
			} else {
				httpServletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid study requested: Not found");
			}
		} catch (final IOException e) {
			if (!httpServletResponse.isCommitted()) {
				httpServletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST,
						"Cannot provide study metadata: File Read Failure");
			}
		}
	}

	@RequestMapping(value = { "/studies/{uuid}/DICOM/summary", "/studies/{uuid}/DICOM/summary.gpb", "/studies/{uuid}/DICOM/summary.html" })
	public void studiesSummary(@PathVariable("uuid") final String uuid, final HttpServletRequest httpServletRequest,
			final HttpServletResponse httpServletResponse) throws IOException {
		if (StringUtils.isBlank(uuid)) {
			httpServletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid study requested: Missing");
			return;
		}
		try {
			String summary = null;
			if (StringUtils.endsWith(httpServletRequest.getPathInfo(),".gpb")) {
				httpServletResponse.setContentType("application/octet-stream");
				summary = "/summary.gpb";
			} else {
				httpServletResponse.setContentType("text/html");
				summary = "/summary.html";
			}
			final File file = new File(studiesRoot, uuid + "/DICOM" + summary);
			if (file.exists() && file.canRead()) {
				httpServletResponse.setContentLength(Long.valueOf(file.length()).intValue());
				final FileInputStream fileInputStream = new FileInputStream(file);
				try {
					final OutputStream outputStream = httpServletResponse.getOutputStream();
					bufferedRead(fileInputStream, outputStream);
				} finally {
					fileInputStream.close();
				}
			} else {
				httpServletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid study requested: Not found");
			}
		} catch (final IOException e) {
			if (!httpServletResponse.isCommitted()) {
				httpServletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST,
						"Cannot provide study metadata: File Read Failure");
			}
		}
	}

}
