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
package org.nema.medical.mint;

import java.io.File;
import java.util.Collection;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ManualController {

    @RequestMapping(value="manualKickOff")
    public void manualKickOff(final HttpServletResponse response) throws Exception {
        response.setContentType("text/plain");
        Logger.getLogger(this.getClass()).debug("Manual Kick-Off starting");
        try {
            runProcess();
        } catch(final Exception ex) {
            Logger.getLogger(this.getClass()).error("processDICOMFilesJob exception", ex);
            response.getWriter().println("Manual processing exception: " + ex.getLocalizedMessage());
            return;
        }

        response.getWriter().println("Manual processing done.");
    }

    private void runProcess() throws Exception {
        final CheckFileExists checkFileExists = new CheckFileExists();
        final Collection<File> files = checkFileExists.run();
        final HandleFileItemProcessor fileItemProcessor = new HandleFileItemProcessor();
        for (final File item: files) {
            fileItemProcessor.process(item);
        }
        final ProcessStudyFiles processStudyFiles = new ProcessStudyFiles();
        processStudyFiles.run();
    }
}
