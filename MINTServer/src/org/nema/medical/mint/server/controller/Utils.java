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
package org.nema.medical.mint.server.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    public static final Date parseDate(String dateStr) throws ParseException {
        Date date = null;
        ParseException ex = null;
        for (String format : new String[]{"yyyy-MM-dd'T'HH:mm:ssz","yyyy-MM-dd'T'HH:mm:ss","yyyy-MM-dd","yyyyMMdd'T'HHmmss","yyyyMMdd"}) {
            try {
                date = new SimpleDateFormat(format).parse(dateStr);
                break;
            } catch (ParseException e) {
                // try next format, but throw the last error
                ex = e;
            }
        }
        if (date == null) {
            throw ex;
        }
        return date;
    }

    public static void streamFile(final File source, final OutputStream out) throws IOException {
        final byte[] bytes = new byte[8 * 1024];
        
        final FileInputStream in = new FileInputStream(source);
        try {
            while (true) {
                final int amountRead = in.read(bytes);
                if (amountRead == -1) {
                    break;
                }
                out.write(bytes, 0, amountRead);
            }
        } finally {
            in.close();
        }

        out.flush();
    }

    private Utils() {} // no instantiation
}
