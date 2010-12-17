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

public class Utils {

    public static void streamFile(final File source, final OutputStream out, final int bufferSize) throws IOException {
        final byte[] bytes = new byte[bufferSize];
        
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
