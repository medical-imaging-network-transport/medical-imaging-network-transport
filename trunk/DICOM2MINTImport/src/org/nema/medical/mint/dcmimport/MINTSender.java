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
import java.net.URI;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
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
import org.nema.medical.mint.dcm2mint.BinaryFileData;
import org.nema.medical.mint.dcm2mint.MetaBinaryPair;
import org.nema.medical.mint.metadata.Study;
import org.nema.medical.mint.metadata.StudyIO;
import org.nema.medical.mint.util.Iter;

/**
 * @author Uli Bubenheimer
 */
public final class MINTSender implements MINTSend {

    public MINTSender(final URI serverURI, final boolean useXMLNotGPB) {
        this.postURI = URI.create(serverURI + "/jobs/createstudy");
        this.useXMLNotGPB = useXMLNotGPB;
    }

    @Override
    public void send(final MetaBinaryPair studyData) throws IOException {
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

        for (final File binaryItemFile: Iter.iter(((BinaryFileData)(studyData.getBinaryData())).fileIterator())) {
            entity.addPart("binary", new FileBody(binaryItemFile));
        }

        httpPost.setEntity(entity);
        final String result = httpClient.execute(httpPost, new BasicResponseHandler());
        final Matcher matcher = responseMatchPattern.matcher(result);
        final boolean matched = matcher.find();
        if (!matched) {
            throw new IOException("Invalid server response" + result);
        }
        studyUUIDs.add(matcher.group(1));
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
            final Matcher matcher = statusMatchPattern.matcher(result);
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
            } else if (match.equals("SUCCESS")) {
                System.err.println(studyUUID + ": Server upload succeeded");
            } else {
                System.err.println(studyUUID + ": unknown server response:");
                System.err.println(result);
            }

            studyIter.remove();
        }

        return studyUUIDs.isEmpty();
    }

    private final URI postURI;
    private final boolean useXMLNotGPB;
    private final Collection<String> studyUUIDs = new LinkedList<String>();

    private static final Pattern responseMatchPattern = Pattern.compile("URL=createstudy/([^\"]+)");
    private static final Pattern statusMatchPattern = Pattern.compile("Status: ([^<]*)");
}
