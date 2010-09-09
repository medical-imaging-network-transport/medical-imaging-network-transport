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

package org.nema.medical.mint.server.receiver;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.log4j.Logger;
import org.nema.medical.mint.dcm2mint.ProcessImportDir;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Uli Bubenheimer
 */
public class ReceiverDaemon {

    private static final Logger LOG = Logger.getLogger(ReceiverDaemon.class);

    @Autowired
    Boolean enableSCP;
    
    @Autowired
    Boolean enableProcessor;
    
    @Autowired
    File storageRootDir;
    
    @Autowired
    String aeTitle;
    
    @Autowired
    Integer port;
    
    @Autowired
    Integer reaperTimeoutMS;

    @Autowired
    URI serverURI;
    
    @Autowired
    Boolean useXMLNotGPB;
    
    @Autowired
    Boolean deletePhysicalFiles;
    
    @Autowired
    Boolean forceCreate;
    
    @Autowired
    Integer binaryInlineThreshold;

    DICOMReceive dcmRcv;

    ScheduledExecutorService dcm2MintExecutor;

    @PostConstruct
    public void postConstruct() {
        setUpCStoreSCP();
        setUpDICOM2MINT();
    }

    private void setUpCStoreSCP() {
        if (!enableSCP) {
            return;
        }
        dcmRcv = new DICOMReceive();
        dcmRcv.setStorageRootDir(storageRootDir);
        dcmRcv.setAETitle(aeTitle);
        dcmRcv.setPort(port);
        dcmRcv.setReaperTimeout(reaperTimeoutMS);
        
        try {
            dcmRcv.start();
        } catch (final IOException e) {
            //Need to catch and re-throw checked exceptions for @PostConstruct
            throw new RuntimeException(e);
        }
    }

    private void setUpDICOM2MINT() {
        if (!enableProcessor) {
            return;
        }
        dcm2MintExecutor = Executors.newScheduledThreadPool(2);
        //Create an instance of the Directory Processing Class
        final ProcessImportDir importProcessor = new ProcessImportDir(
                storageRootDir, serverURI, useXMLNotGPB, deletePhysicalFiles,
                forceCreate, binaryInlineThreshold);
        final Runnable checkResponsesTask = new Runnable() {
            public void run() {
                try {
                    importProcessor.handleResponses();
                    importProcessor.handleSends();
                } catch(final Throwable e) {
                    System.err.println("An exception occurred while uploading to the server:");
                    e.printStackTrace();
                }
            }
        };
        dcm2MintExecutor.scheduleWithFixedDelay(checkResponsesTask, 1, 1, TimeUnit.SECONDS);

        final Runnable dirTraverseTask = new Runnable() {
            public void run() {
                try {
                    importProcessor.processDir();
                } catch(final Throwable e) {
                    System.err.println("An exception occurred while processing files:");
                    e.printStackTrace();
                }
            }
        };
        dcm2MintExecutor.scheduleWithFixedDelay(dirTraverseTask, 0, 3, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void preDestroy() {
        //TODO The tear-downs below could be done in parallel

        //Tear down CStore SCP
        if (dcmRcv != null) {
            dcmRcv.stop();
            dcmRcv = null;
        }
        
        //Tear down DICOM-to-MINT
        if (dcm2MintExecutor != null) {
            dcm2MintExecutor.shutdown();
            try {
                if (!dcm2MintExecutor.awaitTermination(2, TimeUnit.SECONDS)) {
                    dcm2MintExecutor.shutdownNow();
                    if (!dcm2MintExecutor.awaitTermination(1, TimeUnit.SECONDS)) {
                        LOG.warn("DICOM-to-MINT Executor service did not terminate.");
                    }
                }
            } catch (final InterruptedException e) {
                dcm2MintExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
            dcm2MintExecutor = null;
        }
    }
}
