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
import org.nema.medical.mint.server.domain.MINTStudy;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.log4j.Logger;
import org.nema.medical.mint.server.domain.StudyDAO;

public class Utils {

    private static final Logger LOG = Logger.getLogger(Utils.class);

    public static void streamFile(final File source, final OutputStream out, final int bufferSize) throws IOException {
        final byte[] bytes = new byte[bufferSize];
        
        final FileInputStream in = new FileInputStream(source);
        try {
            while (true) {
                final int amountRead = in.read(bytes);
                if (amountRead == -1) {
                    break;
                }
                out.write(bytes, 0, amountRead);
            }
        } finally {
            in.close();
        }

        out.flush();
    }

    enum StudyStatus {
        OK,
        ABSENT,
        DELETED,
        INVALID_ID
    }

    public static StudyStatus validateStudyStatus(final File studiesRoot, final String studyUUID,
                                                  final HttpServletResponse response, final StudyDAO studyDAO)
            throws IOException {
        if (StringUtils.isBlank(studyUUID)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing study ID");
            return Utils.StudyStatus.INVALID_ID;
        }

        final File studyDir = new File(studiesRoot, studyUUID);
        if (studyDir.exists()) {
            if (studyDir.canRead()) {
                return Utils.StudyStatus.OK;
            }

            LOG.error("Unable to read directory for study: " + studyDir);
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid study requested: Not readable");
            return Utils.StudyStatus.ABSENT;
        }

        final MINTStudy study = studyDAO.findStudy(studyUUID);

        if (study != null) {
            if (study.getStudyVersion().equals("-1")) {
                LOG.error("Requested study has previously been deleted: " + studyUUID);
                response.sendError(HttpServletResponse.SC_GONE, "Invalid study requested: deleted");
                return Utils.StudyStatus.DELETED;
            }

            return Utils.StudyStatus.OK;
        }

        LOG.error("Unable to locate study " + studyUUID);
        response.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid study requested: Not found");
        return Utils.StudyStatus.ABSENT;
    }

    private Utils() {} // no instantiation
}
