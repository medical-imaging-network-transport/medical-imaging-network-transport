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
package org.nema.medical.mint.server.integration;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Test;

/**
 * Make a couple of basic requests to make sure that things are up and running.
 * 
 * @author Jeremy Huiskamp <jeremy.huiskamp@karoshealth.com>
 * @since Dec 9, 2010
 */
public class SmokeTest {
    final static String BASE_URL = "http://localhost:8080/MINTServer/";
    
    /**
     * Make sure the /MINTServer page exists and looks something like what
     * we're expecting.
     * 
     * @throws Exception
     */
    @Test
    public void checkRootPage() throws Exception {
        HttpResponse rsp = get("");
        assertEquals("Wrong response code for mint home page", 200, rsp.code);
        assertTrue("Mint home page doesn't mention mint",
                rsp.body.toLowerCase().contains("mint"));
    }
    
    @Test
    public void checkDICOMMetadata() throws Exception {
        HttpResponse rsp = get("types/DICOM");
        assertEquals("Wrong response code for types/DICOM", 200, rsp.code);
        // TODO: parse xml and validate
    }
    
    @Test
    public void checkStudies() throws Exception {
        HttpResponse rsp = get("studies");
        assertEquals("Wrong response code for studies", 200, rsp.code);
        // TODO: parse xml and validate
    }
    
    @Test
    public void checkChangelog() throws Exception {
        HttpResponse rsp = get("changelog");
        assertEquals("Wrong response code for changelog", 200, rsp.code);
        // TODO: parse xml and validate
    }

    public static class HttpResponse {
        String body;
        int code;
    }
    
    public static HttpResponse get(String path) throws IOException {
        return get(new URL(BASE_URL + path));
    }

    public static HttpResponse get(URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        HttpResponse result = new HttpResponse();
        result.code = conn.getResponseCode();

        InputStream in;
        try {
            in = conn.getInputStream();
        } catch (IOException e) {
            if (result.code / 100 == 2) {
                throw e;
            } else {
                return result;
            }
        }

        if (in != null) {
            StringBuilder sb = new StringBuilder();
            Reader inr = new InputStreamReader(in);
            int i;
            while ((i = inr.read()) > -1) {
                sb.append((char) i);
            }
            result.body = sb.toString();
        } else {
            result.body = "";
        }
        
        conn.disconnect();
        return result;
    }
}
