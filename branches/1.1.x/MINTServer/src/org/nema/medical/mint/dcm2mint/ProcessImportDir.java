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

package org.nema.medical.mint.dcm2mint;

import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.ProxySelectorRoutePlanner;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.data.TransferSyntax;
import org.dcm4che2.io.DicomInputStream;
import org.dcm4che2.io.StopTagInputHandler;
import org.nema.medical.mint.datadictionary.*;
import org.nema.medical.mint.jobs.HttpMessagePart;
import org.nema.medical.mint.metadata.StudyIO;
import org.nema.medical.mint.metadata.StudyMetadata;
import org.nema.medical.mint.utils.StudyTraversals;
import org.nema.medical.mint.utils.StudyValidation;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.net.ProxySelector;
import java.net.URI;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;

import static org.nema.medical.mint.utils.Iter.iter;

/**
 * @author Uli Bubenheimer
 */
public final class ProcessImportDir {
    private static final Logger LOG = Logger.getLogger(ProcessImportDir.class);   
    
    private final Map<String, JobInfo> jobIDInfo =
        Collections.synchronizedMap(new HashMap<String, JobInfo>());
    private final boolean deletePhysicalInstanceFiles;
    private final boolean forceCreate;
    private final int binaryInlineThreshold;
    private MetadataType dicomMetadataType = null;
    private Set<Integer> studyLevelTags;
    private Set<Integer> seriesLevelTags;
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
    
    public ProcessImportDir(final File importDir, final URI serverURI, final boolean useXMLNotGPB,
            final boolean deletePhysicalInstanceFiles, final boolean forceCreate,
            final int binaryInlineThreshold) throws IOException {
        this.importDir = importDir;
        this.createURI = URI.create(serverURI + "/jobs/createstudy");
        this.queryURI = URI.create(serverURI + "/studies");
        this.updateURI = URI.create(serverURI + "/jobs/updatestudy");
        this.jobStatusURI = URI.create(serverURI + "/jobs/status");
        this.dicomDatadictionaryURI = URI.create(serverURI + "/types/DICOM");
        this.useXMLNotGPB = useXMLNotGPB;
        this.deletePhysicalInstanceFiles = deletePhysicalInstanceFiles;
        this.forceCreate = forceCreate;
        this.binaryInlineThreshold = binaryInlineThreshold;
    }

    /**
     * Set the default proxy from the JRE on the passed {@code httpClient}.
     * @param httpClient the client to modify.
     */
    public static void applyDefaultProxySelector(final DefaultHttpClient httpClient) {
        //From HttpClient tutorial
        final ProxySelectorRoutePlanner routePlanner = new ProxySelectorRoutePlanner(
                httpClient.getConnectionManager().getSchemeRegistry(),
                ProxySelector.getDefault());
        httpClient.setRoutePlanner(routePlanner);
    }

    public void processDir() throws IOException{
    	//only re-initialize this if it hasn't been initialized.
    	//Initialization cannot take place in the constructor because this
    	//depends on the server to be already up and running. The server
    	//startup creates this class so it would be a catch22.
    	if(dicomMetadataType == null)
    	{
    		HttpGet httpGet = new HttpGet(dicomDatadictionaryURI);
    		String response = httpClient.execute(httpGet, new BasicResponseHandler());
        	InputStream in = new ByteArrayInputStream(response.getBytes());
        	this.dicomMetadataType = DataDictionaryIO.parseFromXML(in);
        	this.studyLevelTags = getStudyTags(dicomMetadataType);
        	this.seriesLevelTags = getSeriesTags(dicomMetadataType);
    	}
        LOG.info("Gathering files for allocation to studies.");
        final long fileGatherStart = System.currentTimeMillis();
        //As items may be removed from the set of handled files concurrently,
        //create a copy of a maximum set first; it won't hurt if it contains too many elements.
        final SortedSet<File> handledFilesCopy = new TreeSet<File>(handledFiles);
        final SortedSet<File> resultFiles = new TreeSet<File>();
        findPlainFilesRecursive(importDir, resultFiles, handledFilesCopy);
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
                LOG.warn("Skipping file: " + plainFile);
                e.printStackTrace();
            }
        }
        final long fileGatherEnd = System.currentTimeMillis();
        LOG.info("Gathering files and study allocation completed in "
                + String.format("%.1f", (fileGatherEnd - fileGatherStart) / 1000.0f) + " seconds.");

        outerLoop:
        for (final Map.Entry<String, Collection<File>> studyFiles: studyFileMap.entrySet()) {
            final String studyUID = studyFiles.getKey();
            assert studyUID != null;
            final Collection<File> instanceFiles = studyFiles.getValue();
            final int instanceFileCount = instanceFiles.size();
            LOG.info("Creating MINT for " + instanceFileCount + " instances of study instance UID " + studyUID + ".");
            final long mintConvertStart = System.currentTimeMillis();
            final BinaryData binaryData = new BinaryDcmData();
            final MetaBinaryPairImpl metaBinaryPair = new MetaBinaryPairImpl();
            metaBinaryPair.getMetadata().setType("DICOM");
            metaBinaryPair.setBinaryData(binaryData);
            //Constrain processing
            metaBinaryPair.getMetadata().setStudyInstanceUID(studyUID);
            final Dcm2MetaBuilder builder = new Dcm2MetaBuilder(studyLevelTags, seriesLevelTags, metaBinaryPair);
            builder.setBinaryInlineThreshold(binaryInlineThreshold);
            final Iterator<File> instanceFileIter = instanceFiles.iterator();
            while (instanceFileIter.hasNext()) {
                final File instanceFile = instanceFileIter.next();
                final TransferSyntax transferSyntax;
                final DicomObject dcmObj;
                try {
                    final DicomInputStream dcmStream = new DicomInputStream(instanceFile);
                    try {
                        dcmObj = dcmStream.readDicomObject();
                        transferSyntax = dcmStream.getTransferSyntax();
                    } finally {
                        dcmStream.close();
                    }
                } catch (final IOException e) {
                    //Not a valid DICOM file?!
                    LOG.warn("Skipping file: " + instanceFile);
                    e.printStackTrace();
                    instanceFileIter.remove();
                    continue;
                } catch (final RuntimeException e) {
                    //Some near-catastrophic error
                    LOG.error("Fatal error while processing file: " + instanceFile);
                    throw e;
                } catch (final Error e) {
                    //Some catastrophic error
                    LOG.fatal("Fatal error while processing file: " + instanceFile);
                    throw e;
                }
                try {
                    builder.accumulateFile(instanceFile, dcmObj, transferSyntax);
                } catch (final UnsupportedOperationException e) {
                    LOG.error("Skipping study " + studyUID + ": DICOM syntax error in file " + instanceFile + ":", e);
                    continue outerLoop;
                }
            }
            builder.finish();

            try {
                try {
                    StudyValidation.validateStudyMetadata(metaBinaryPair.getMetadata(), dicomMetadataType);
                    addToSendQueue(metaBinaryPair, instanceFiles);
                } catch (final StudyTraversals.TraversalException e) {
                    LOG.error("Skipping study " + studyUID + ": validation error in study metadata", e);
                }
            } catch (final IOException e) {
                //Catastrophic error
                throw new RuntimeException(e);
            }

            final long mintConvertEnd = System.currentTimeMillis();
            LOG.info("MINT creation for study instance UID " + studyUID + " ("
                    + instanceFileCount + " instances) completed in "
                    + String.format("%.1f", (mintConvertEnd - mintConvertStart) / 1000.0f) + " seconds.");
        }
    }

    private void addToSendQueue(final MetaBinaryPair studyData, final Collection<File> studyFiles) throws IOException {
        //Write metadata to disk so that we don't run out of memory while working on more studies
        final StudyMetadata study = studyData.getMetadata();
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
     * @throws Exception
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
            LOG.info("Uploading study instance UID " + studyInstanceUID);
            final StudyQueryInfo studyQueryInfo;
            if (forceCreate) {
                studyQueryInfo = null;
            } else {
                final long existQueryStart = System.currentTimeMillis();
                studyQueryInfo = doesStudyExist(studyInstanceUID, sendData.patientID);
                final long existQueryEnd = System.currentTimeMillis();
                final StringBuilder msg =
                    new StringBuilder("Completed querying server for existing study for study instance UID "
                        + studyInstanceUID + " in "
                        + String.format("%.1f", (existQueryEnd - existQueryStart) / 1000.0f) + " seconds. ");
                if (studyQueryInfo == null) {
                    msg.append("Study does not exist yet.");
                } else {
                    msg.append("Study exists, Study ID " + studyQueryInfo.studyUUID);
                }
                LOG.info(msg);
            }

            try {
                final long uploadStart = System.currentTimeMillis();
                final JobInfo jobInfo = send(sendData.metadataFile, sendData.binaryData, sendData.studyInstanceFiles,
                        studyQueryInfo);
                final long uploadEnd = System.currentTimeMillis();
                assert studyQueryInfo == null || studyQueryInfo.studyUUID.equals(jobInfo.studyID);
                LOG.info("Completed uploading MINT to server for study instance UID "
                        + studyInstanceUID + " (" + sendData.studyInstanceFiles.size() + " instances) in "
                        + String.format("%.1f", (uploadEnd - uploadStart) / 1000.0f) + " seconds, Job ID "
                        + jobInfo.getJobID() + ", Study ID " + jobInfo.getStudyID());
            } catch (final IOException e) {
                LOG.error("Skipping study " + studyInstanceUID + ": I/O error while uploading study to server:", e);
            } catch (final SAXException e) {
                LOG.error("Skipping study " + studyInstanceUID
                        + ": error while parsing server response to study upload.");
            } finally {
                sendData.metadataFile.delete();
            }
        }
    }

    public boolean sendingDone() {
        return studySendQueue.isEmpty() && jobIDInfo.isEmpty();
    }

    private StudyQueryInfo doesStudyExist(final String studyInstanceUID, final String patientID) throws Exception {
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
            nodeList = (NodeList) xPath.evaluate("/studySearchResults/study", responseDoc, XPathConstants.NODESET);
        } catch(final Exception ex) {
            LOG.error("Querying for studyUID " + studyInstanceUID + ": unknown server response:\n" + response);
            throw ex;
        }
        final int uuidCount = nodeList.getLength();
        switch (uuidCount) {
        case 0:
            return null;
        case 1:
            final Node node = nodeList.item(0);
            final StudyQueryInfo studyQueryInfo = new StudyQueryInfo();
            final NamedNodeMap attrMap = node.getAttributes();
            studyQueryInfo.studyUUID = attrMap.getNamedItem("studyUUID").getNodeValue();
            studyQueryInfo.studyVersion = attrMap.getNamedItem("version").getNodeValue();
            return studyQueryInfo;
        default:
            throw new Exception("Multiple matches for study UID " + studyInstanceUID);
        }
    }

    /**
     * @throws IOException
     */
    public void handleResponses() throws IOException {
        final ResponseHandler<String> responseHandler = new BasicResponseHandler();
        final Iterator<Entry<String, JobInfo>> studyIter = jobIDInfo.entrySet().iterator();
        while (studyIter.hasNext()) {
            final Entry<String, JobInfo> studyEntry = studyIter.next();
            final String jobID = studyEntry.getKey();
            final HttpGet httpGet = new HttpGet(jobStatusURI + "/" + jobID);
            final String response = httpClient.execute(httpGet, responseHandler);

            LOG.debug("Server job status response:\n" + response);

            final String statusStr;
            try {
                final Document responseDoc = documentBuilder.parse(
                        new ByteArrayInputStream(response.getBytes()));
                statusStr = xPath.evaluate("/jobStatus/@jobStatus", responseDoc);
            } catch(final Exception ex) {
                LOG.error("Querying job " + jobID + ": unknown server response:\n" + response);
                studyIter.remove();
                continue;
            }

            final JobInfo jobInfo = studyEntry.getValue();
            final Collection<File> studyFiles = jobInfo.getFiles();
            if (statusStr.equals("IN_PROGRESS")) {
                continue;
            } else if (statusStr.equals("FAILED")) {
                LOG.error("Querying job " + jobID + ": server processing failed:\n" + response);
                //Do not delete the study's files in case of failure
                removeStudyFiles(studyFiles, false);
            } else if (statusStr.equals("SUCCESS")) {
                final long approxJobEndTime = System.currentTimeMillis();
                //Always round down the job processing time, as it's usually too high anyway
                LOG.info("Querying job " + jobID + ": server processing completed in approximately "
                        + ((approxJobEndTime - jobInfo.getJobStartTime()) / 1000) + " seconds for "
                        + studyFiles.size() + " instance files, Study ID " + jobInfo.getStudyID() + ".");
                removeStudyFiles(studyFiles, true);
            } else {
                LOG.error("Querying job " + jobID + ": unknown server response:\n" + response);
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

    private JobInfo send(final File metadataFile, final BinaryData binaryData, final Collection<File> studyFiles,
            final StudyQueryInfo studyQueryInfo) throws IOException, SAXException {
        final HttpPost httpPost = new HttpPost(studyQueryInfo == null ? createURI : updateURI);
        final MultipartEntity entity = new MultipartEntity();

        if (studyQueryInfo != null) {
            entity.addPart(HttpMessagePart.STUDY_UUID.toString(), new StringBody(studyQueryInfo.studyUUID));
        }

        final StudyMetadata study =
                useXMLNotGPB ? StudyIO.parseFromXML(metadataFile) : StudyIO.parseFromGPB(metadataFile);
        if (studyQueryInfo != null) {
            //Specify current study version
            entity.addPart(HttpMessagePart.OLD_VERSION.toString(), new StringBody(studyQueryInfo.studyVersion));
        }

        //Pretty significant in-memory operations, so scoping to get references released ASAP
        {
            final byte[] metaInBuffer;
            {
                final ByteArrayOutputStream metaOut = new ByteArrayOutputStream(10000);
                if (useXMLNotGPB) {
                    StudyIO.writeToXML(study, metaOut);
                } else {
                    StudyIO.writeToGPB(study, metaOut);
                }
                metaInBuffer = metaOut.toByteArray();
            }
            final ByteArrayInputStream metaIn = new ByteArrayInputStream(metaInBuffer);
            //We must distinguish MIME types for GPB vs. XML so that the server can handle them properly
            entity.addPart(HttpMessagePart.METADATA.toString(), new InputStreamBody(metaIn,
                    useXMLNotGPB ? "text/xml" : "application/octet-stream", metadataFile.getName()));
        }

        //We support only one type
        assert binaryData instanceof BinaryDcmData;
        {
            int i = 0;
            for (final InputStream binaryStream: iter(((BinaryDcmData) binaryData).streamIterator())) {
                final String fileName = "binary" + i++;
                entity.addPart(fileName, new InputStreamBody(binaryStream, fileName));
            }
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
        final long uploadEndTime = System.currentTimeMillis();

        LOG.debug("Server response:" + response);

        final String jobID;
        final String studyID;
        final Document responseDoc = documentBuilder.parse(new ByteArrayInputStream(response.getBytes()));
        try {
            jobID = xPath.evaluate("/jobStatus/@jobID", responseDoc).trim();
            studyID = xPath.evaluate("/jobStatus/@studyUUID", responseDoc).trim();
        } catch(final XPathExpressionException e) {
            //This shouldn't happen
            throw new RuntimeException(e);
        }
        final JobInfo jobInfo = new JobInfo(jobID, studyID, studyFiles, uploadEndTime);
        jobIDInfo.put(jobID, jobInfo);
        return jobInfo;
    }

    private static void findPlainFilesRecursive(final File targetFile, final Collection<File> resultFiles,
                                                final Collection<File> handledFiles) {
        if (handledFiles.contains(targetFile)) {
            //Need to check whether file has already been handled right away, as otherwise the file may get
            //accessed or deleted by someone else while we're looking at it.
            return;
        }
        //Skip DICOM files which are not completely stored by dcmrcv yet.
        if (targetFile.getName().endsWith(".part")) {
            return;
        }
        if (targetFile.isFile()) {
            resultFiles.add(targetFile);
        } else if (targetFile.isDirectory()) {
            for (final File subFile: targetFile.listFiles()) {
                findPlainFilesRecursive(subFile, resultFiles, handledFiles);
            }
        } else {
            throw new RuntimeException("File " + targetFile.getAbsolutePath()
                    + " is not a normal file and not a directory");
        }
    }

    private static Set<Integer> getSeriesTags(final MetadataType mt) {
		final SeriesAttributesType seriesAttsParent = mt.getSeriesAttributes();
		final List<AttributeType> seriesAtts = seriesAttsParent.getAttributes();
        return getTags(seriesAtts);
    }

    private static Set<Integer> getStudyTags(final MetadataType mt) {
		final StudyAttributesType studyAttsParent = mt.getStudyAttributes();
		final List<AttributeType> studyAtts = studyAttsParent.getAttributes();
        return getTags(studyAtts);
    }

    private static Set<Integer> getTags(final Collection<AttributeType> attributeTypes) {
    	final Set<Integer> tagSet = new HashSet<Integer>();
		for (final AttributeType att: attributeTypes) {
			final int intTag = (int)Long.parseLong(att.getTag(), 16);
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

    private final DefaultHttpClient httpClient = new DefaultHttpClient();
    {
        applyDefaultProxySelector(httpClient);
    }

    private final File importDir;
    private final SortedSet<File> handledFiles = new ConcurrentSkipListSet<File>();
    private final URI createURI;
    private final URI queryURI;
    private final URI updateURI;
    private final URI dicomDatadictionaryURI;
	private final URI jobStatusURI;
    private final boolean useXMLNotGPB;
    private final Collection<MetaBinaryFiles> studySendQueue =
        new ConcurrentLinkedQueue<MetaBinaryFiles>();

    private static final class JobInfo {
        private final String jobID;
        private final String studyID;
        private final Collection<File> files;
        private final long jobStartTime;

        public JobInfo(final String jobID, final String studyID, final Collection<File> files, final long jobStartTime) {
            this.jobID = jobID;
            this.studyID = studyID;
            this.files = files;
            this.jobStartTime = jobStartTime;
        }
        public final String getJobID() {
            return jobID;
        }
        public final String getStudyID() {
            return studyID;
        }
        public final Collection<File> getFiles() {
            return files;
        }
        public final long getJobStartTime() {
            return jobStartTime;
        }
    }

    private static final class StudyQueryInfo {
        public String studyUUID;
        public String studyVersion;
    }
}
