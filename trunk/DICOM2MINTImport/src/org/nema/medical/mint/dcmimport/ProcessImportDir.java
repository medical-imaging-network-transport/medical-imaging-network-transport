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

package org.nema.medical.mint.dcmimport;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.data.TransferSyntax;
import org.dcm4che2.io.DicomInputStream;
import org.dcm4che2.io.StopTagInputHandler;
import org.nema.medical.mint.dcm2mint.BinaryData;
import org.nema.medical.mint.dcm2mint.BinaryDcmData;
import org.nema.medical.mint.dcm2mint.Dcm2MetaBuilder;
import org.nema.medical.mint.dcm2mint.MetaBinaryPair;
import org.nema.medical.mint.dcm2mint.MetaBinaryPairImpl;
import org.nema.medical.mint.metadata.Study;
import org.nema.medical.mint.metadata.StudyIO;
import org.nema.medical.mint.util.Iter;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author Uli Bubenheimer
 */
public final class ProcessImportDir {

    public ProcessImportDir(final File importDir, final URI serverURI, final boolean useXMLNotGPB,
            final boolean deletePhysicalInstanceFiles, final boolean forceCreate,
            final int binaryInlineThreshold) {
        this.importDir = importDir;
        this.createURI = URI.create(serverURI + "/jobs/createstudy");
        this.queryURI = URI.create(serverURI + "/studies");
        this.updateURI = URI.create(serverURI + "/jobs/updatestudy");
        this.useXMLNotGPB = useXMLNotGPB;
        this.deletePhysicalInstanceFiles = deletePhysicalInstanceFiles;
        this.forceCreate = forceCreate;
        this.binaryInlineThreshold = binaryInlineThreshold;
    }

    public void processDir() {
        final SortedSet<File> resultFiles = new TreeSet<File>();
        findPlainFilesRecursive(importDir, resultFiles);
        resultFiles.removeAll(handledFiles);
        handledFiles.addAll(resultFiles);
        final Map<String, Collection<File>> studyFileMap = new HashMap<String, Collection<File>>();
        for (final File plainFile: resultFiles) {
            try {
                final String studyUID;
                final DicomInputStream dcmStream = new DicomInputStream(plainFile);
                try {
                    //Only read & parse DICOM file up to StudyInstanceUID tag
                    dcmStream.setHandler(new StopTagInputHandler(Tag.StudyInstanceUID + 1));
                    final DicomObject dcmObj = dcmStream.readDicomObject();
                    studyUID = Dcm2MetaBuilder.extractStudyInstanceUID(dcmObj);
                    if (studyUID == null) {
                        throw new IOException("DICOM file without study instance UID - skipping: " + plainFile.getPath());
                    }
                } finally {
                    dcmStream.close();
                }

                Collection<File> dcmFileData = studyFileMap.get(studyUID);
                if (dcmFileData == null) {
                    dcmFileData = new ArrayList<File>();
                    studyFileMap.put(studyUID, dcmFileData);
                }
                dcmFileData.add(plainFile);
            } catch (final IOException e) {
                //Not a valid DICOM file?!
                System.err.println("Skipping file: " + plainFile);
            }
        }

        outerLoop:
        for (final Map.Entry<String, Collection<File>> studyFiles: studyFileMap.entrySet()) {
            final String studyUID = studyFiles.getKey();
            assert studyUID != null;
            System.err.println("Creating MINT for study instance UID " + studyUID);
            final BinaryData binaryData = new BinaryDcmData();
            final MetaBinaryPairImpl metaBinaryPair = new MetaBinaryPairImpl();
            metaBinaryPair.setBinaryData(binaryData);
            //Constrain processing
            metaBinaryPair.getMetadata().setStudyInstanceUID(studyUID);
            final Dcm2MetaBuilder builder = new Dcm2MetaBuilder(STUDY_LEVEL_TAGS, SERIES_LEVEL_TAGS, metaBinaryPair);
            builder.setBinaryInlineThreshold(binaryInlineThreshold);
            final Iterator<File> instanceFileIter = studyFiles.getValue().iterator();
            while (instanceFileIter.hasNext()) {
                final File instanceFile = instanceFileIter.next();
                final TransferSyntax transferSyntax;
                final DicomObject dcmObj;
                try {
                    final DicomInputStream dcmStream = new DicomInputStream(instanceFile);
                    try {
                        transferSyntax = dcmStream.getTransferSyntax();
                        dcmObj = dcmStream.readDicomObject();
                    } finally {
                        dcmStream.close();
                    }
                } catch (final IOException e) {
                    //Not a valid DICOM file?!
                    System.err.println("Skipping file: " + instanceFile);
                    instanceFileIter.remove();
                    continue;
                } catch (final Error e) {
                    //Some catastrophic error
                    System.err.println("Fatal error while processing file: " + instanceFile);
                    throw e;
                } catch (final RuntimeException e) {
                    //Some catastrophic error
                    System.err.println("Fatal error while processing file: " + instanceFile);
                    throw e;
                }
                try {
                    builder.accumulateFile(instanceFile, dcmObj, transferSyntax);
                } catch (final UnsupportedOperationException e) {
                    System.err.println("Skipping study " + studyUID + ": DICOM syntax error in file " + instanceFile + ":");
                    System.err.println(e.getLocalizedMessage());
                    e.printStackTrace();
                    continue outerLoop;
                }
            }
            builder.finish();

            try {
                addToSendQueue(metaBinaryPair, studyFiles.getValue());
            } catch (final IOException e) {
                //Catastrophic error
                throw new RuntimeException(e);
            }
        }
    }

    private void addToSendQueue(final MetaBinaryPair studyData, final Collection<File> studyFiles) throws IOException {
        //Write metadata to disk so that we don't run out of memory while working on more studies
        final Study study = studyData.getMetadata();
        final File studyMetaFile = File.createTempFile("metadata", useXMLNotGPB ? ".xml" : ".gpb");
        studyMetaFile.deleteOnExit();
        final OutputStream outStream = new BufferedOutputStream(new FileOutputStream(studyMetaFile));
        try {
            if (useXMLNotGPB) {
                StudyIO.writeToXML(study, outStream);
            } else {
                StudyIO.writeToGPB(study, outStream);
            }
        } finally {
            outStream.close();
        }

        final MetaBinaryFiles mbf = new MetaBinaryFiles();
        mbf.studyInstanceUID = study.getStudyInstanceUID();
        mbf.patientID = study.getValueForAttribute(Tag.PatientID);
        mbf.metadataFile = studyMetaFile;
        mbf.binaryData = studyData.getBinaryData();
        mbf.studyInstanceFiles = studyFiles;
        studySendQueue.add(mbf);
    }

    /**
     * @throws IOException
     */
    public void handleSends() throws Exception {
        if (!jobIDInfo.isEmpty()) {
            //To prevent disk churn leading to slowness on the server side, have it handle just one study at a time
            return;
        }
        final Iterator<MetaBinaryFiles> studySendIter = studySendQueue.iterator();
        if (studySendIter.hasNext()) {
            final MetaBinaryFiles sendData = studySendIter.next();
            studySendIter.remove();

            //Determine whether study exists and we need to perform an update
            final String studyInstanceUID = sendData.studyInstanceUID;
            System.err.println("Uploading study instance UID " + studyInstanceUID);
            final String studyUUID;
            if (forceCreate) {
                studyUUID = null;
            } else {
                studyUUID = doesStudyExist(studyInstanceUID, sendData.patientID);
            }

            try {
                send(sendData.metadataFile, sendData.binaryData, sendData.studyInstanceFiles, studyUUID);
            } catch (final IOException e) {
                System.err.println("Skipping study " + studyInstanceUID + ": I/O error while uploading study to server:");
                System.err.println(e.getLocalizedMessage());
                e.printStackTrace();
            } catch (final SAXException e) {
                System.err.println("Skipping study " + studyInstanceUID + ": error while parsing server response to study upload.");
            } finally {
                sendData.metadataFile.delete();
            }
        }
    }

    public boolean sendingDone() {
        return studySendQueue.isEmpty() && jobIDInfo.isEmpty();
    }

    private String doesStudyExist(final String studyInstanceUID, final String patientID) throws Exception {
        final List<NameValuePair> qparams = new ArrayList<NameValuePair>();
        qparams.add(new BasicNameValuePair("studyInstanceUID", studyInstanceUID));
        qparams.add(new BasicNameValuePair("patientID", patientID == null ? "" : patientID));
        final String fullURI = queryURI + "?" + URLEncodedUtils.format(qparams, "UTF-8");
        final HttpGet httpGet = new HttpGet(fullURI);
        final String response = httpClient.execute(httpGet, new BasicResponseHandler());
        final NodeList nodeList;
        try {
            final Document responseDoc = documentBuilder.parse(
                    new ByteArrayInputStream(response.getBytes()));
            nodeList = (NodeList) xPath.evaluate("/html/body/ol/li/dl/dd[@class='StudyUUID']", responseDoc, XPathConstants.NODESET);
        } catch(final Exception ex) {
            System.err.println("Querying for studyUID " + studyInstanceUID + ": unknown server response:");
            System.err.println(response);
            throw ex;
        }
        final int uuidCount = nodeList.getLength();
        switch (uuidCount) {
        case 0:
            return null;
        case 1:
            final Node node = nodeList.item(0);
            return node.getTextContent().trim();
        default:
            throw new Exception("Multiple matches for study UID " + studyInstanceUID);
        }
    }

    /**
     * @return true if responses for all uploaded studies are completed.
     * @throws IOException
     */
    public void handleResponses() throws IOException {
        final ResponseHandler<String> responseHandler = new BasicResponseHandler();
        final Iterator<Entry<String, Collection<File>>> studyIter = jobIDInfo.entrySet().iterator();
        while (studyIter.hasNext()) {
            final Entry<String, Collection<File>> studyEntry = studyIter.next();
            final String jobID = studyEntry.getKey();
            final HttpGet httpGet = new HttpGet(createURI + "/" + jobID);
            final String response = httpClient.execute(httpGet, responseHandler);
            final String statusStr;
            try {
                final Document responseDoc = documentBuilder.parse(
                        new ByteArrayInputStream(response.getBytes()));
                statusStr = xPath.evaluate("/html/body/dl/dd[@class='JobStatus']/text()", responseDoc);
            } catch(final Exception ex) {
                System.err.println("Querying job " + jobID + ": unknown server response:");
                System.err.println(response);
                studyIter.remove();
                continue;
            }

            final Collection<File> studyFiles = studyEntry.getValue();
            if (statusStr.equals("IN_PROGRESS")) {
                continue;
            } else if (statusStr.equals("FAILED")) {
                System.err.println("Querying job " + jobID + ": Server upload failed:");
                System.err.println(response);
                //Do not delete the study's files in case of failure
                removeStudyFiles(studyFiles, false);
            } else if (statusStr.equals("SUCCESS")) {
                System.err.println("Querying job " + jobID + ": Server upload succeeded");
                removeStudyFiles(studyFiles, true);
            } else {
                System.err.println("Querying job " + jobID + ": unknown server response:");
                System.err.println(response);
                //Do not delete the study's files in case of failure
                removeStudyFiles(studyFiles, false);
            }

            studyIter.remove();
        }
    }

    private void removeStudyFiles(final Collection<File> studyFiles, final boolean delete) {
        if (delete && deletePhysicalInstanceFiles) {
            for (final File undeletedStudyFile: studyFiles) {
                undeletedStudyFile.delete();
                handledFiles.remove(undeletedStudyFile);
            }
        }
    }

    private void send(final File metadataFile, final BinaryData binaryData, final Collection<File> studyFiles, final String existingStudyUUID) throws IOException, SAXException {
        final HttpPost httpPost = new HttpPost(existingStudyUUID == null ? createURI : updateURI);
        final MultipartEntity entity = new MultipartEntity();

        //Need to specify the 'type' of the data being sent
        entity.addPart("type", new StringBody("DICOM"));
        if (existingStudyUUID != null) {
            entity.addPart("studyUUID", new StringBody(existingStudyUUID));
        }

        //We must distinguish MIME types for GPB vs. XML so that the server can handle them properly
        entity.addPart(metadataFile.getName(), new FileBody(metadataFile, useXMLNotGPB ? "text/xml" : "application/octet-stream"));

        //We support only one type
        assert binaryData instanceof BinaryDcmData;
        for (final InputStream binaryStream: Iter.iter(((BinaryDcmData) binaryData).streamIterator())) {
            entity.addPart("binary", new InputStreamBody(binaryStream, "binary"));
        }

        //Debugging only
//        int i = 0;
//        for (final InputStream binaryStream: Iter.iter(((BinaryDcmData) binaryData).streamIterator())) {
//            final OutputStream testout = new BufferedOutputStream(new FileOutputStream("E:/testdata/" + i), 10000);
//            for(;;) {
//                final int val = binaryStream.read();
//                if (val == -1) {
//                    break;
//                }
//                testout.write(val);
//            }
//            testout.close();
//            ++i;
//        }

        httpPost.setEntity(entity);

        final String response = httpClient.execute(httpPost, new BasicResponseHandler());
        final String jobID;
        final Document responseDoc = documentBuilder.parse(new ByteArrayInputStream(response.getBytes()));
        try {
            jobID = xPath.evaluate("/html/body/dl/dd[@class='JobID']/text()", responseDoc).trim();
        } catch(final XPathExpressionException e) {
            //This shouldn't happen
            throw new RuntimeException(e);
        }
        jobIDInfo.put(jobID, studyFiles);
    }

    private static void findPlainFilesRecursive(final File targetFile, final Collection<File> resultFiles) {
        if (targetFile.isFile()) {
            //Skip DICOM files which are not completely stored by dcmrcv yet.
            if (!targetFile.getName().endsWith(".part")) {
                resultFiles.add(targetFile);
            }
        } else {
            assert targetFile.isDirectory();
            for (final File subFile: targetFile.listFiles()) {
                findPlainFilesRecursive(subFile, resultFiles);
            }
        }
    }

    private static Set<Integer> getTags(final String resource) {
        final ClassLoader loader = ProcessImportDir.class.getClassLoader();
        final Properties properties = new Properties();
        try {
            InputStream stream = loader.getResourceAsStream(resource);
            try {
                properties.load(stream);
            } finally {
                stream.close();
            }
        } catch(final IOException ex) {
            Logger.getLogger(ProcessImportDir.class).error("Unable to read tags file", ex);
        }
        final Set<Integer> tagSet = new HashSet<Integer>();
        for (final Object tagStr: properties.keySet()) {
            //Go to long as int is unsigned and insufficient here
            final int intTag = (int)Long.parseLong(tagStr.toString(), 16);
            tagSet.add(intTag);
        }
        return tagSet;
    }

    private static class MetaBinaryFiles {
        public String studyInstanceUID;
        public String patientID;
        public File metadataFile;
        public BinaryData binaryData;
        public Collection<File> studyInstanceFiles;
    }

    private final HttpClient httpClient = new DefaultHttpClient();
    private final File importDir;
    private final SortedSet<File> handledFiles = new TreeSet<File>();
    private final URI createURI;
    private final URI queryURI;
    private final URI updateURI;
    private final boolean useXMLNotGPB;
    private final Collection<MetaBinaryFiles> studySendQueue =
        new ConcurrentLinkedQueue<MetaBinaryFiles>();
    private final Map<String, Collection<File>> jobIDInfo =
        Collections.synchronizedMap(new HashMap<String, Collection<File>>());
    private final boolean deletePhysicalInstanceFiles;
    private final boolean forceCreate;
    private final int binaryInlineThreshold;

    private static final Set<Integer> STUDY_LEVEL_TAGS = getTags("StudyTags.txt");
    private static final Set<Integer> SERIES_LEVEL_TAGS = getTags("SeriesTags.txt");
    private static final XPath xPath = XPathFactory.newInstance().newXPath();
    private static final DocumentBuilder documentBuilder;
    static {
        final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setCoalescing(true);
        documentBuilderFactory.setNamespaceAware(false);
        documentBuilderFactory.setValidating(false);
        try {
            documentBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (final ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }
}
