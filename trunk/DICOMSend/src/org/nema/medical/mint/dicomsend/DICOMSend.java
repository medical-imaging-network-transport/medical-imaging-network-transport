//------------------------------------------------------------------------------
//Copyright (c) 2010 Vital Images, Inc. All Rights Reserved.
//
//This is UNPUBLISHED PROPRIETARY SOURCE CODE of Vital Images, Inc.;
//the contents of this file may not be disclosed to third parties,
//copied or duplicated in any form, in whole or in part, without the
//prior written permission of Vital Images, Inc.
//
//RESTRICTED RIGHTS LEGEND:
//Use, duplication or disclosure by the Government is subject to
//restrictions as set forth in subdivision (c)(1)(ii) of the Rights
//in Technical Data and Computer Software clause at DFARS 252.227-7013,
//and/or in similar or successor clauses in the FAR, DOD or NASA FAR
//Supplement. Unpublished rights reserved under the Copyright Laws of
//the United States.
//------------------------------------------------------------------------------

package org.nema.medical.mint.dicomsend;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.data.UIDDictionary;
import org.dcm4che2.io.DicomInputStream;
import org.dcm4che2.io.StopTagInputHandler;
import org.dcm4che2.net.Association;
import org.dcm4che2.net.ConfigurationException;
import org.dcm4che2.net.DataWriter;
import org.dcm4che2.net.Device;
import org.dcm4che2.net.NetworkApplicationEntity;
import org.dcm4che2.net.NetworkConnection;
import org.dcm4che2.net.NoPresentationContextException;
import org.dcm4che2.net.PDVOutputStream;
import org.dcm4che2.net.TransferCapability;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author Uli Bubenheimer
 */
public final class DICOMSend {

    private static final Logger LOG = Logger.getLogger(DICOMSend.class);

    private final Collection<TransferCapability> transferCapabilities = new ArrayList<TransferCapability>();

    private final ExecutorService executor = new ThreadPoolExecutor(
            100, 100, 1, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());;

    private final NetworkConnection localConnection = new NetworkConnection();
    {
        localConnection.setTcpNoDelay(true);
    }

    private final NetworkApplicationEntity localAE = new NetworkApplicationEntity();
    {
        localAE.setNetworkConnection(localConnection);
        localAE.setAssociationAcceptor(false);
        localAE.setAssociationInitiator(true);
        localAE.setPackPDV(true);
    }

    private final NetworkConnection remoteConnection = new NetworkConnection();

    private final NetworkApplicationEntity remoteAE = new NetworkApplicationEntity();
    {
        remoteAE.setNetworkConnection(remoteConnection);
        remoteAE.setAssociationAcceptor(true);
        remoteAE.setAssociationInitiator(false);
    }

    private final Device device = new Device();
    {
        //There is no need to reap our association.
        device.setAssociationReaperPeriod(Integer.MAX_VALUE);
        device.setNetworkApplicationEntity(localAE);
        device.setNetworkConnection(localConnection);
    }

    private DICOMSend(final String host, final int port, final String localAETitle,
            final String remoteAETitle) {
        device.setDeviceName(localAETitle);
        remoteConnection.setHostname(host);
        remoteConnection.setPort(port);
        localAE.setAETitle(localAETitle);
        remoteAE.setAETitle(remoteAETitle);
    }

    public Association connect() throws ConfigurationException, IOException, InterruptedException {
        return localAE.connect(remoteAE, executor);
    }

    public void release(final Association association) throws InterruptedException {
        association.release(false);
        executor.shutdown();
    }

    public void importConfiguration(final File configFile) throws IOException, SAXException {
        final DocumentBuilder documentBuilder;
        final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setCoalescing(true);
        documentBuilderFactory.setNamespaceAware(false);
        documentBuilderFactory.setValidating(false);
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (final ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        final InputStream configInStream = new BufferedInputStream(new FileInputStream(configFile));
        try {
            final NodeList nodeList;
            final Document configDoc = documentBuilder.parse(configInStream);
            try {
                nodeList = (NodeList) XPathFactory.newInstance().newXPath().evaluate(
                        "/dicomsend/prezcontext", configDoc, XPathConstants.NODESET);
            } catch (final XPathExpressionException e) {
                //This shouldn't happen
                throw new RuntimeException(e);
            }
            for (int i = 0; i < nodeList.getLength(); ++i) {
                final Node node = nodeList.item(i);
                final NamedNodeMap namedNodeMap = node.getAttributes();
                final String sopClassUID = namedNodeMap.getNamedItem("sopclassuid").getNodeValue();
                final String xferSyntaxUID = namedNodeMap.getNamedItem("xfersyntaxuid").getNodeValue();
                transferCapabilities.add(new TransferCapability(
                        sopClassUID, new String[] {xferSyntaxUID}, TransferCapability.SCU));
            }
        } finally {
            configInStream.close();
        }
    }

    public void initTransferCapability() {
        localAE.setTransferCapability(transferCapabilities.toArray(new TransferCapability[transferCapabilities.size()]));
    }

    private static void printUsage() {
        System.err.println("Usage: DICOMSend PORT ROOTDIR [CSTORESCPIP [REMOTEAETITLE [LOCALAETITLE]]]");
    }

    public static void main(final String[] args) {
        final long startTime = System.currentTimeMillis();
        if (args.length < 2 || args.length > 5) {
            LOG.fatal("Invalid number of arguments: " + args.length);
            printUsage();
            System.exit(1);
        }
        final int port;
        try {
            port = Integer.parseInt(args[0]);
        } catch (final NumberFormatException e) {
            LOG.fatal("Invalid port number: " + args[0]);
            printUsage();
            System.exit(2);
            return;
        }
        final File rootDir = new File(args[1]);
        if (!rootDir.exists()) {
            LOG.fatal("Invalid root directory: " + args[1]);
            printUsage();
            System.exit(3);
        }
        final String host = args.length > 2 ? args[2] : "127.0.0.1";
        final String remoteAETitle = args.length > 3 ? args[3] : "DICOMRECEIVE";
        final String localAETitle = args.length > 4 ? args[4] : "DICOMSEND";
        final DICOMSend dicomSend = new DICOMSend(host, port, localAETitle, remoteAETitle);
        try {
            dicomSend.importConfiguration(new File("dcmsndcfg.xml"));
        } catch (final Exception e) {
            LOG.fatal("Error while reading configuration file: " + e.getMessage());
            System.exit(4);
        }
        dicomSend.initTransferCapability();
        Association association = null;
        try {
            association = dicomSend.connect();
        } catch (final Exception e) {
            LOG.fatal("Failed to establish association with SCP: " + e.getMessage());
            System.exit(5);
            return; //to avoid uninitialized variable error for association
        }
        try {
            final Queue<File> fileQueue = new LinkedList<File>();
            fileQueue.add(rootDir);
            final byte[] fileBuffer = new byte[1024];
            fileLoop:
            while (!fileQueue.isEmpty()) {
                final File curFile = fileQueue.remove();

                if (curFile.isDirectory()) {
                    final File[] dirFiles = curFile.listFiles();
                    fileQueue.addAll(Arrays.asList(dirFiles));
                    continue;
                }

                final String transferSyntaxUID;
                final String sopClassUID;
                final String sopInstanceUID;
                final long fileMetaInformationEndPos;
                final InputStream inputStream;
                try {
                    inputStream = new BufferedInputStream(new FileInputStream(curFile));
                } catch (final FileNotFoundException e) {
                    LOG.info("Cannot open file - skipping: " + curFile);
                    continue;
                }
                try {
                    //Read start of file into buffer to test whether it's DICOM
                    inputStream.mark(fileBuffer.length);
                    int readPos = 0;
                    for (int bytesRead;
                        (bytesRead = inputStream.read(fileBuffer, readPos, fileBuffer.length - readPos)) > 0;
                        readPos += bytesRead);
                    try {
                        final DicomInputStream dcmInStream = new DicomInputStream(new ByteArrayInputStream(fileBuffer, 0, readPos));
                        try {
                            dcmInStream.setHandler(new StopTagInputHandler(Tag.SOPInstanceUID + 1));
                            final DicomObject dcmObj = dcmInStream.readDicomObject();
                            transferSyntaxUID = dcmInStream.getTransferSyntax().uid();
                            fileMetaInformationEndPos = dcmInStream.getEndOfFileMetaInfoPosition();
                            sopClassUID = dcmObj.getString(Tag.MediaStorageSOPClassUID, dcmObj.getString(Tag.SOPClassUID));
                            if (sopClassUID == null) {
                                LOG.error("Missing SOP Class UID in file - skipping: " + curFile);
                                continue;
                            }
                            sopInstanceUID = dcmObj.getString(Tag.MediaStorageSOPInstanceUID, dcmObj.getString(Tag.SOPInstanceUID));
                            if (sopInstanceUID == null) {
                                LOG.error("Missing SOP Instance UID in file - skipping: " + curFile);
                                continue;
                            }
                        } finally {
                            dcmInStream.close();
                        }
                    } catch (final IOException e) {
                        LOG.info("Cannot read file as DICOM - skipping: " + curFile);
                        continue;
                    }

                    //Reset to start of file
                    inputStream.reset();

                    final DataWriter dataWriter = new DataWriter() {
                        @Override
                        public final void writeTo(final PDVOutputStream outStream, final String reqTransferSyntaxUID) throws IOException {
                            try {
                                if (!transferSyntaxUID.equals(transferSyntaxUID)) {
                                    throw new IOException("Requested transfer syntax UID " + reqTransferSyntaxUID
                                            + " not matching stored transfer syntax UID " + transferSyntaxUID);
                                }

                                long bytesToSkip = fileMetaInformationEndPos;
                                while (bytesToSkip > 0) {
                                    bytesToSkip -= inputStream.skip(bytesToSkip);
                                }
                                outStream.copyFrom(inputStream);
                            } finally {
                                inputStream.close();
                            }
                        }
                    };
                    //Put the cstore operation in a loop, so that we can retry if the first attempt fails;
                    //for example, it's always possible that the SCP might have closed the association.
                    for (int i = 0; i < 2; ++i) {
                        try {
                            association.cstore(sopClassUID, sopInstanceUID, 0, dataWriter, transferSyntaxUID);
                            LOG.info("Stored " + curFile);
                            break;
                        } catch (final NoPresentationContextException e) {
                            LOG.error(
                                    "Presentation Context not supported for file (consider adding the next line to the configuration): "
                                    + curFile);
                            LOG.error(
                                    "<prezcontext sopclassuid=\"" + sopClassUID
                                    + "\" sopclassdesc=\"" + UIDDictionary.getDictionary().prompt(sopClassUID)
                                    + "\" xfersyntaxuid=\"" + transferSyntaxUID
                                    + "\" xfersyntaxdesc=\"" + UIDDictionary.getDictionary().prompt(transferSyntaxUID)
                                    + "\"/>");
                            continue fileLoop;
                        } catch (final IllegalStateException e) {
                            if (i == 0) {
                                LOG.warn("Error during C-STORE operation - association is in invalid state;" +
                                        " will attempt to reestablish association: " + e.toString());
                                //Assume that association is beyond recovery, so we can't even release it
                                association = dicomSend.connect();
                                //Second attempt
                                continue;
                            }
                            LOG.warn("Error during C-STORE operation - association is in invalid state;" +
                                    " retry failed - aborting: " + e.getMessage());
                            System.exit(6);
                        } catch (final InterruptedException e) {
                            LOG.fatal("Interrupted - exiting");
                            System.exit(7);
                        }
                    }
                } catch (final IOException e) {
                    LOG.error("Miscellaneous I/O error while processing file \"" + curFile + "\"", e);
                    System.exit(8);
                } catch (final Throwable e) {
                    LOG.error("Fatal error while processing file \"" + curFile + "\"", e);
                    System.exit(9);
                } finally {
                    try {
                        inputStream.close();
                    } catch (final IOException e) {
                        LOG.error("Error while closing bufferedInnerInputStream", e);
                    }
                }
            }
        } finally {
            try {
                dicomSend.release(association);
            } catch (final InterruptedException e) {
                LOG.fatal("Interrupted while releasing association with SCP - exiting");
                System.exit(10);
            }
        }
        final long endTime = System.currentTimeMillis();
        LOG.info("Total run time: " + String.format("%.2f", (endTime - startTime) / 1000.0f) + " seconds");
        System.exit(0);
    }
}
