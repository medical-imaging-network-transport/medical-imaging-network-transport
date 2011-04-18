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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.nema.medical.mint.server.domain.MINTStudy;
import org.nema.medical.mint.server.domain.StudyDAO;
import org.nema.medical.mint.server.util.StorageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class StudyBinaryItemsController {

    @Autowired
    protected File studiesRoot;

    @Autowired
    protected Integer binaryItemResponseBufferSize;
    
    @Autowired
    protected Integer binaryItemStreamBufferSize;

    @Autowired
    protected StudyDAO studyDAO;

	@RequestMapping("/studies/{uuid}/{type}/binaryitems/{seq}")
    public void studiesBinaryItems(final HttpServletResponse res, HttpServletRequest req,
                                   @PathVariable("uuid") final String uuid,
                                   @PathVariable("type") final String type,
                                   @PathVariable("seq") final String seq
    ) throws IOException {

        final Utils.StudyStatus studyStatus = Utils.validateStudyStatus(studiesRoot, uuid, res, studyDAO);
        if (studyStatus != Utils.StudyStatus.OK) {
            return;
        }

        final File studyRoot = new File(studiesRoot, uuid);

        final Iterator<Integer> itemList;
        try {
        	itemList = parseItemList(seq, type, studyRoot);
        } catch (final NumberFormatException e) {
        	res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid binary item requested: NaN");
            return;
        }

        if (!itemList.hasNext()) {
        	res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to retrieve binary items. See server log for details.");
            LOG.error("Unable to locate binary items: " + seq + " or there are no binary items.");
            return;
        }
        
        LOG.debug("output buffer size was " + res.getBufferSize());
        res.setBufferSize(binaryItemResponseBufferSize);
        LOG.debug("output buffer size is now " + res.getBufferSize());
        final OutputStream out = res.getOutputStream();
        
        int i = itemList.next();

        File file = new File(studyRoot + "/" + type + "/binaryitems/" + i + "."
                + StorageUtil.BINARY_FILE_EXTENSION);
        if (!file.exists() || !file.canRead()) {
        	final File newFile =
                    new File(studyRoot + "/" + type + "/binaryitems/" + i + "."
                            + StorageUtil.EXCLUDED_BINARY_FILE_EXTENSION);
            if (newFile.exists() && newFile.canRead()) {
                file = newFile;
            } else {
                res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Unable to retrieve requested binary items. See server error log.");
                LOG.error("BinaryItemsFile " + file + " does not exist");
                return;
            }
        }


        // write the appropriate header
        final boolean multipart = itemList.hasNext();
        if (multipart) {
        	res.setContentType("multipart/x-mixed-replace; boundary=\"" + MP_BOUNDARY + "\"");
            out.write(("--" + MP_BOUNDARY).getBytes());
            
            final long itemsize = file.length();
            String index = Integer.toString(i);
            out.write("\nContent-Type: application/octet-stream\n".getBytes());
            out.write(("Content-ID: <" + index + "@" + uuid + ">\n").getBytes());
            out.write(("Content-Length: " + itemsize + "\n\n").getBytes());
        } else {
        	res.setContentType("application/octet-stream");
        	res.setContentLength((int) file.length());
        }
        
        out.flush();
        streamBinaryItem(file, out, binaryItemStreamBufferSize);
        
        if(multipart)
        {
        	out.write(("\n--" + MP_BOUNDARY).getBytes());
        }

        for (;itemList.hasNext();) {
        	i = itemList.next();
        	
        	file = new File(studyRoot + "/" + type + "/binaryitems/" + i + "." + StorageUtil.BINARY_FILE_EXTENSION);
            if (!file.exists() || !file.canRead()) {
            	final File newFile =
                        new File(studyRoot + "/" + type + "/binaryitems/" + i + "."
                                + StorageUtil.EXCLUDED_BINARY_FILE_EXTENSION);
                if (newFile.exists() && newFile.canRead()) {
                    file = newFile;
                } else {
                    res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                            "Unable to retrieve requested binary items. See server error log.");
                    LOG.error("BinaryItemsFile " + file + " does not exist");
                    return;
                }

            }
            
            final long itemsize = file.length();
            String index = Integer.toString(i);
            out.write("\nContent-Type: application/octet-stream\n".getBytes());
            out.write(("Content-ID: <" + index + "@" + uuid + ">\n").getBytes());
            out.write(("Content-Length: " + itemsize + "\n\n").getBytes());

            streamBinaryItem(file, out, binaryItemStreamBufferSize);
            
            out.write(("\n--" + MP_BOUNDARY).getBytes());
        }
        
        if (multipart) {
            out.write("--".getBytes());
        }
        
        out.flush();
    }

    private void streamBinaryItem(final File file, final OutputStream outputStream, final int bufferSize) throws IOException {
        final InputStream in = new FileInputStream(file);
        final byte[] bytes = new byte[bufferSize];
        try {
            while (true) {
                final int amountRead = in.read(bytes);
                if (amountRead == -1) {
                    break;
                }
                outputStream.write(bytes, 0, amountRead);
            }
            outputStream.flush();
        } finally {
            in.close();
        }
    }

	/**
	 * This method will scan through the study in the provided root directory
	 * and will create a list of bid's encountered in order. This order is
	 * expected to be the order in which the binary IDs exist in the study
	 * metadata document.
	 * 
	 * @param seq
	 * @param type
	 * @param studyRoot
	 * @return
	 * @throws NumberFormatException
	 * @throws IOException
	 */
    private Iterator<Integer> parseItemList(String seq, String type, File studyRoot) throws NumberFormatException, IOException {
        final List<Integer> itemList = new ArrayList<Integer>();

        if (seq.equals("all")) {
        	final File binaryRoot = new File(studyRoot, type + "/binaryitems");
        	binaryRoot.list();
        	
        	return new Iterator<Integer>() {
        		private Iterator<String> binaryNames = Arrays.asList(binaryRoot.list()).iterator();
        		private String next = null;
        		
				@Override
				public boolean hasNext() {
					getNext();
					
					return next != null;
				}

				@Override
				public Integer next() throws NumberFormatException {
					getNext();
					
					if(next == null)
						throw new NoSuchElementException();
					
					int result = Integer.valueOf(next.substring(0, next.indexOf('.')));
					
					next = null;
					
					return result;
				}
				
				private void getNext()
				{
					if(next == null && binaryNames.hasNext())
					{
						do
						{
							next = binaryNames.next();
						}while(!next.endsWith(StorageUtil.BINARY_FILE_EXTENSION) && binaryNames.hasNext());
						
						if(!next.endsWith(StorageUtil.BINARY_FILE_EXTENSION))
						{
							next = null;
						}
					}
				}

				@Override
				public void remove() {
					binaryNames.remove();
				}
			};
        } else {
			/*
			 * TODO speed this up by removing the need to build an entire list
			 * ahead of time. This is a slow operation if there are 100000
			 * items. This can be done by implementing a custom iterator similar
			 * to how to all method works.
			 */
            String[] elements = seq.split(",");

            for (String element : elements) {
                String[] range = element.split("-");

                if (range.length < 1 || range.length > 2) {
                    //failed to parse element, error message not set yet
                    return null;
                }

                int start = Integer.valueOf(range[0]);
                int end = start;
                if (range.length == 2) {
                    end = Integer.valueOf(range[1]);
                }

                if (start < 0 || end < 0) {
                    //failed to parse element, error message already set
                    return null;
                }

                //for each item in the range, add to itemList
                for (; start <= end; ++start) {
                    itemList.add(start);
                }
            }
        }

        return itemList.iterator();
    }

    private static final Logger LOG = Logger.getLogger(StudyBinaryItemsController.class);
    private static final String MP_BOUNDARY = "BinaryItemBoundary-7afb50349c2148c3a5d6a324891a481c";

}
