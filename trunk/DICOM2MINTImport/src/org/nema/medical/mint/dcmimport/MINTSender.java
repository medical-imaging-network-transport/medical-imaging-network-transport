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

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.nema.medical.mint.common.metadata.Study;
import org.nema.medical.mint.common.metadata.StudyIO;
import org.nema.medical.mint.dcm2mint.BinaryFileData;
import org.nema.medical.mint.dcm2mint.MetaBinaryPair;
import org.nema.medical.mint.util.Iter;

/**
 * @author Uli Bubenheimer
 */
public final class MINTSender implements MINTSend {

    public MINTSender(final URI serverURL) {
        this.serverURI = serverURL;
    }

    @Override
    public void send(final MetaBinaryPair studyData) throws IOException {
        final HttpClient httpclient = new DefaultHttpClient();
        final HttpPost httpPost = new HttpPost(serverURI);
        final MultipartEntity entity = new MultipartEntity();

        final Study study = studyData.getMetadata();
        final ByteArrayOutputStream studyOutStream = new ByteArrayOutputStream();
        StudyIO.writeToGPB(study, studyOutStream);
        final ByteArrayInputStream studyInStream = new ByteArrayInputStream(studyOutStream.toByteArray());
        entity.addPart("metadata.gpb", new InputStreamBody(studyInStream, "metadata.gpb"));

        for (final File binaryItemFile: Iter.iter(((BinaryFileData)(studyData.getBinaryData())).fileIterator())) {
            entity.addPart("binary", new FileBody(binaryItemFile));
        }

        httpPost.setEntity(entity);
        httpclient.execute(httpPost, new BasicResponseHandler());
    }

    final URI serverURI;
}
