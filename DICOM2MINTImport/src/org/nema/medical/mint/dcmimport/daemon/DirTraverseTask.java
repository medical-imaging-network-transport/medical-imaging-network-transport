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

package org.nema.medical.mint.dcmimport.daemon;

import java.util.Timer;
import java.util.TimerTask;

import org.nema.medical.mint.dcmimport.ProcessImportDir;

/**
 * @author Uli Bubenheimer
 */
public final class DirTraverseTask extends TimerTask {
    public DirTraverseTask(final Timer timer, final ProcessImportDir importDirProcessor) {
        this.timer = timer;
        this.importDirProcessor = importDirProcessor;
    }

    @Override
    public void run() {
        //Run the importing process against the specified directory
        //This will process and send the resulting MINT message to the MINTServer
        importDirProcessor.run();
        timer.schedule(new DirTraverseTask(timer, importDirProcessor), DELAY);
    }

    private static final int DELAY = 3000;

    private final Timer timer;
    private final ProcessImportDir importDirProcessor;
}
