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
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.nema.medical.mint.common.StudyUtil;
import org.nema.medical.mint.metadata.Attribute;
import org.nema.medical.mint.metadata.Instance;
import org.nema.medical.mint.metadata.Series;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class StudyBinaryItemsController {

    @Autowired
    protected File studiesRoot;

    @RequestMapping("/studies/{uuid}/DICOM/binaryitems/{seq}")
    public void studiesBinaryItems(@PathVariable("uuid") final String uuid, 
                                   @PathVariable("seq") final String seq,
                                   final HttpServletResponse httpServletResponse) throws IOException {
        if (StringUtils.isBlank(uuid)) {
            // Shouldn't happen...but could be +++, I suppose
            httpServletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request: Missing binary item id");
            return;
        }

        final File studyRoot = new File(studiesRoot, uuid);
        if (!studyRoot.exists()) {
            httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND, "Requested Study Not Found");
            return;
        }

        final List<Integer> itemList;
        try {
        	itemList = parseItemList(seq, studyRoot);
        } catch (final NumberFormatException e) {
            httpServletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid binary item requested: NaN");
            return;
        }

        if (itemList == null || itemList.isEmpty()) {
            httpServletResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to retreive binary items. See server log for details.");
            LOG.error("Unable to locate binary items: " + seq);
            return;
        }

        List<File> binaryItems = new ArrayList<File>(itemList.size());
        for (int i : itemList) {
            final File file = new File(studyRoot + "/DICOM/binaryitems/" + i + ".dat");
            if (!file.exists() || !file.canRead()) {
                httpServletResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to retreive requested binary items. See server error log.");
                LOG.error("BinaryItemsFile " + file + " does not exist");
                return;
            }
            binaryItems.add(file);
        }

        final OutputStream out = httpServletResponse.getOutputStream();

        // write the appropriate header
        final boolean multipart = binaryItems.size() > 0;
        if (multipart) {
            httpServletResponse.setContentType("multipart/x-mixed-replace; boundary=\"" + MP_BOUNDARY + "\"");
            out.write(("--" + MP_BOUNDARY).getBytes());
            out.flush();
        } else {
            httpServletResponse.setContentType("application/octet-stream");
            httpServletResponse.setContentLength((int) binaryItems.get(0).length());
        }

        for (File binaryItem : binaryItems) {
            if (multipart) {
                final long itemsize = binaryItem.length();
                out.write("\nContent-type: application/octet-stream\n".getBytes());
                out.write(("Content-length: " + itemsize + "\n\n").getBytes());
            }

            streamBinaryItem(binaryItem,out);
            
            if (multipart) {
                out.write(("\n--" + MP_BOUNDARY).getBytes());
                out.flush();
            }
        }
        if (multipart) {
            out.write("--".getBytes());
            out.flush();
        }
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

    private List<Integer> parseItemList(String seq, File studyRoot) throws NumberFormatException, IOException {
        final List<Integer> itemList = new ArrayList<Integer>();

        if (seq.equals("all")) {
            //need to return all items in current metadata
            File dicomRoot = new File(studyRoot, "DICOM");

            org.nema.medical.mint.metadata.Study study = StudyUtil.loadStudy(dicomRoot);

            // iterate through each instance and collect the bids
            for (Iterator<Series> i = study.seriesIterator(); i.hasNext();) {
                for (Iterator<Instance> ii = i.next().instanceIterator(); ii.hasNext();) {
                    for (Iterator<Attribute> iii = ii.next().attributeIterator(); iii.hasNext();) {
                        Attribute a = iii.next();

                        int bid = a.getBid();
                        if (bid >= 0) {
                            itemList.add(bid);
                        }
                    }
                }
            }
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

        return itemList;
    }

    private static final Logger LOG = Logger.getLogger(StudyBinaryItemsController.class);
    private static final String MP_BOUNDARY = "BinaryItemBoundary-7afb50349c2148c3a5d6a324891a481c";

}
