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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class StudyBinaryItemsController {

    @Autowired
    protected File studiesRoot;

	@RequestMapping("/studies/{uuid}/{type}/binaryitems/{seq}")
    public void studiesBinaryItems(final HttpServletResponse res, HttpServletRequest req,
                                   @PathVariable("uuid") final String uuid,
                                   @PathVariable("type") final String type,
                                   @PathVariable("seq") final String seq
    ) throws IOException {

		if (StringUtils.isBlank(uuid)) {
            // Shouldn't happen...but could be +++, I suppose
        	res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request: Missing binary item id");
            return;
        }

        final File studyRoot = new File(studiesRoot, uuid);
        if (!studyRoot.exists()) {
        	res.sendError(HttpServletResponse.SC_NOT_FOUND, "Requested Study Not Found");
            return;
        }

        final Iterator<Integer> itemList;
        try {
        	itemList = parseItemList(seq, type, studyRoot);
        } catch (final NumberFormatException e) {
        	res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid binary item requested: NaN");
            return;
        }

        if (!itemList.hasNext()) {
        	res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to retreive binary items. See server log for details.");
            LOG.error("Unable to locate binary items: " + seq + " or there are no binary items.");
            return;
        }

        final OutputStream out = res.getOutputStream();
        
        int i = itemList.next();
        File file = new File(studyRoot + "/" + type + "/binaryitems/" + i + ".dat");
        if (!file.exists() || !file.canRead()) {
        	res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to retreive requested binary items. See server error log.");
            LOG.error("BinaryItemsFile " + file + " does not exist");
            return;
        }

        // write the appropriate header
        final boolean multipart = itemList.hasNext();
        if (multipart) {
        	res.setContentType("multipart/x-mixed-replace; boundary=\"" + MP_BOUNDARY + "\"");
            out.write(("--" + MP_BOUNDARY).getBytes());
            
            final long itemsize = file.length();
            String index = file.getName().split("\\.")[0];
            out.write("\nContent-Type: application/octet-stream\n".getBytes());
            out.write(("Content-ID: <" + index + "@" + uuid + ">\n").getBytes());
            out.write(("Content-Length: " + itemsize + "\n\n").getBytes());
        } else {
        	res.setContentType("application/octet-stream");
        	res.setContentLength((int) file.length());
        }
        
        streamBinaryItem(file,out);
        
        if(multipart)
        {
        	out.write(("\n--" + MP_BOUNDARY).getBytes());
        }

        for (;itemList.hasNext();) {
        	i = itemList.next();
        	
            file = new File(studyRoot + "/" + type + "/binaryitems/" + i + ".dat");
            if (!file.exists() || !file.canRead()) {
            	res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to retreive requested binary items. See server error log.");
                LOG.error("BinaryItemsFile " + file + " does not exist");
                return;
            }
        	
            final long itemsize = file.length();
            String index = file.getName().split("\\.")[0];
            out.write("\nContent-Type: application/octet-stream\n".getBytes());
            out.write(("Content-ID: <" + index + "@" + uuid + ">\n").getBytes());
            out.write(("Content-Length: " + itemsize + "\n\n").getBytes());

            streamBinaryItem(file,out);
            
            out.write(("\n--" + MP_BOUNDARY).getBytes());
        }
        
        if (multipart) {
            out.write("--".getBytes());
        }
        
        out.flush();
    }

    private void streamBinaryItem(final File file, final OutputStream outputStream) throws IOException {
        final InputStream in = new FileInputStream(file);
        final byte[] bytes = new byte[16 * 1024];
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
        	//NOTE: the following code may no longer be used for all but haven't decided yet
//            //need to return all items in current metadata
//            File typeRoot = new File(studyRoot, type);
//
//            org.nema.medical.mint.metadata.Study study = StudyIO.loadStudy(typeRoot);
//
//            return study.getBinaryItemIDs().iterator();
        	final File binaryRoot = new File(studyRoot, type + "/binaryitems");
        	binaryRoot.list();
        	
        	return new Iterator<Integer>() {
        		private Iterator<String> binaryNames = Arrays.asList(binaryRoot.list()).iterator();

				@Override
				public boolean hasNext() {
					return binaryNames.hasNext();
				}

				@Override
				public Integer next() throws NumberFormatException {
					String next = binaryNames.next();
					return Integer.valueOf(next.substring(0, next.indexOf('.')));
				}

				@Override
				public void remove() {
					binaryNames.remove();
				}
			};
        } else {
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
