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

    private void runProcess() {
        final CheckFileExists checkFileExists = new CheckFileExists();
        final Collection<File> files = checkFileExists.run();
        final ProcessStudyFiles processStudyFiles = new ProcessStudyFiles();
        processStudyFiles.run();
    }
}
