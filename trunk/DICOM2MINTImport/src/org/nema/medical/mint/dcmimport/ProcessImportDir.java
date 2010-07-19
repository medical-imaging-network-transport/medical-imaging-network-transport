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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.data.TransferSyntax;
import org.dcm4che2.io.DicomInputStream;
import org.dcm4che2.io.StopTagInputHandler;
import org.nema.medical.mint.dcm2mint.BinaryData;
import org.nema.medical.mint.dcm2mint.BinaryFileData;
import org.nema.medical.mint.dcm2mint.BinaryMemoryData;
import org.nema.medical.mint.dcm2mint.Dcm2MetaBuilder;
import org.nema.medical.mint.dcm2mint.MetaBinaryPair;
import org.nema.medical.mint.dcm2mint.MetaBinaryPairImpl;
import org.nema.medical.mint.metadata.Study;
import org.nema.medical.mint.metadata.StudyIO;
import org.nema.medical.mint.util.Iter;

/**
 * @author Uli Bubenheimer
 */
public final class ProcessImportDir {

    public ProcessImportDir(final File importDir, final URI serverURI, final boolean useXMLNotGPB) {
        this.importDir = importDir;
        this.postURI = URI.create(serverURI + "/jobs/createstudy");
        this.useXMLNotGPB = useXMLNotGPB;
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

        for (final Map.Entry<String, Collection<File>> studyFiles: studyFileMap.entrySet()) {
            final String studyUID = studyFiles.getKey();
            assert studyUID != null;

            final BinaryData binaryData = USE_BINARYFILEDATA ? new BinaryFileData() : new BinaryMemoryData();
            try {
                final MetaBinaryPairImpl metaBinaryPair = new MetaBinaryPairImpl();
                metaBinaryPair.setBinaryData(binaryData);
                //Constrain processing
                metaBinaryPair.getMetadata().setStudyInstanceUID(studyUID);
                final Dcm2MetaBuilder builder = new Dcm2MetaBuilder(STUDY_LEVEL_TAGS, SERIES_LEVEL_TAGS, metaBinaryPair);
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
                    }
                    builder.accumulateFile(instanceFile, dcmObj, transferSyntax);
                }
                builder.finish();

                try {
                    send(metaBinaryPair, studyFiles.getValue());
                } catch (final IOException e) {
                    //Not a valid DICOM file?!
                    System.err.println("Problems sending study data for " + studyUID + ": " + e.getMessage());
                    e.printStackTrace();
                    continue;
                }
            } finally {
                if (USE_BINARYFILEDATA) {
                    //Delete all temporary files storing binary data from DICOM;
                    //do this ASAP as these files may be large and fill up the file system
                    for (final File binaryItemFile: Iter.iter(((BinaryFileData)binaryData).fileIterator())) {
                        binaryItemFile.delete();
                    }
                }
            }
        }
    }

    /**
     * @return true if responses for all uploaded studies are completed.
     * @throws IOException
     */
    public boolean handleResponses() throws IOException {
        final HttpClient httpClient = new DefaultHttpClient();
        final ResponseHandler<String> responseHandler = new BasicResponseHandler();
        final Iterator<String> studyIter = studyUUIDs.iterator();
        while (studyIter.hasNext()) {
            final String studyUUID = studyIter.next();
            final HttpGet httpGet = new HttpGet(postURI + "/" + studyUUID);
            final String result = httpClient.execute(httpGet, responseHandler);
            final Matcher matcher = STATUS_MATCH_PATTERN.matcher(result);
            final boolean matched = matcher.find();
            if (!matched) {
                System.err.println(studyUUID + ": unknown server response:");
                System.err.println(result);
                studyIter.remove();
                continue;
            }
            final String match = matcher.group(1).trim();
            if (match.equals("IN_PROGRESS")) {
                continue;
            } else if (match.equals("FAILED")) {
                System.err.println(studyUUID + ": Server upload failed:");
                System.err.println(result);
                //Do not delete the study's files in case of failure
                removeStudyFiles(studyUUID, false);
            } else if (match.equals("SUCCESS")) {
                System.err.println(studyUUID + ": Server upload succeeded");
                removeStudyFiles(studyUUID, true);
            } else {
                System.err.println(studyUUID + ": unknown server response:");
                System.err.println(result);
                //Do not delete the study's files in case of failure
                removeStudyFiles(studyUUID, false);
            }

            studyIter.remove();
        }

        return studyUUIDs.isEmpty();
    }

    private void removeStudyFiles(final String studyUUID, final boolean delete) {
        final Collection<File> studyFiles = undeletedStudyFiles.remove(studyUUID);
        if (delete) {
            for (final File undeletedStudyFile: studyFiles) {
                //TODO commented out for testing only
                //undeletedStudyFile.delete();
                //handledFiles.remove(undeletedStudyFile);
            }
        }
    }

    private void send(final MetaBinaryPair studyData, final Collection<File> studyFiles) throws IOException {
        final HttpClient httpClient = new DefaultHttpClient();
        final HttpPost httpPost = new HttpPost(postURI);
        final MultipartEntity entity = new MultipartEntity();

        final Study study = studyData.getMetadata();
        final ByteArrayOutputStream studyOutStream = new ByteArrayOutputStream();
        final String fileName = useXMLNotGPB ? "metadata.xml" : "metadata.gpb";
        if (useXMLNotGPB) {
            StudyIO.writeToXML(study, studyOutStream);
        } else {
            StudyIO.writeToGPB(study, studyOutStream);
        }
        final ByteArrayInputStream studyInStream = new ByteArrayInputStream(studyOutStream.toByteArray());
        //We must distinguish MIME types for GPB vs. XML so that the server can handle them properly
        entity.addPart(fileName, new InputStreamBody(studyInStream, (useXMLNotGPB ? "text/xml" : "application/octet-stream"), fileName));

        if (studyData.getBinaryData() instanceof BinaryFileData) {
            for (final File binaryItemFile: Iter.iter(((BinaryFileData)(studyData.getBinaryData())).fileIterator())) {
                entity.addPart("binary", new FileBody(binaryItemFile));
            }
        } else {
            assert studyData.getBinaryData() instanceof BinaryMemoryData;
            for (final byte[] binaryItemData: studyData.getBinaryData()) {
                final ByteArrayInputStream byteStream = new ByteArrayInputStream(binaryItemData);
                entity.addPart("binary", new InputStreamBody(byteStream, "binary"));
            }
        }

        httpPost.setEntity(entity);
        final String result = httpClient.execute(httpPost, new BasicResponseHandler());
        final Matcher matcher = RESPONSE_MATCH_PATTERN.matcher(result);
        final boolean matched = matcher.find();
        if (!matched) {
            throw new IOException("Invalid server response:\n" + result);
        }
        final String studyUUID = matcher.group(1);
        studyUUIDs.add(studyUUID);
        undeletedStudyFiles.put(studyUUID, studyFiles);
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

    private final File importDir;
    private final SortedSet<File> handledFiles = new TreeSet<File>();
    private final Map<String, Collection<File>> undeletedStudyFiles =
        Collections.synchronizedMap(new HashMap<String, Collection<File>>());
    private final URI postURI;
    private final boolean useXMLNotGPB;
    private final Collection<String> studyUUIDs =
        Collections.synchronizedList(new LinkedList<String>());

    private static final Set<Integer> STUDY_LEVEL_TAGS = getTags("StudyTags.txt");
    private static final Set<Integer> SERIES_LEVEL_TAGS = getTags("SeriesTags.txt");
    private static final Pattern RESPONSE_MATCH_PATTERN = Pattern.compile("URL=.*createstudy/([^\"]+)");
    private static final Pattern STATUS_MATCH_PATTERN = Pattern.compile("JobStatus'>([^<]*)</dd");
    private static final boolean USE_BINARYFILEDATA = false;
}
