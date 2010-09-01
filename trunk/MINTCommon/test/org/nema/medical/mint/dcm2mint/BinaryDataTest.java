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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.dcm4che2.data.BasicDicomObject;
import org.dcm4che2.data.DicomElement;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.data.TransferSyntax;
import org.dcm4che2.data.VR;
import org.dcm4che2.io.DicomOutputStream;
import org.junit.Test;

/**
 * @author Uli Bubenheimer
 */
public final class BinaryDataTest {

    //It's easy to mess things up in BinaryDcmData which would only turn up reliably if we add several
    // items, potentially in different ways, so I am bundling all tests into one method to mess things up good.
    @Test(expected = UnsupportedOperationException.class)
    public void testBinaryDcmData() throws IOException {
        final byte[] bytes1 = { 2, 1, 0, -1 };
        final byte[] bytes2 = { 3, 8 };

        final DicomObject dcmObj = new BasicDicomObject();
        final DicomElement pixelDataDcmElem = dcmObj.putBytes(Tag.PixelData, VR.OW, bytes1);
        final DicomElement varPixelDataDcmElem = dcmObj.putBytes(Tag.VariablePixelData, VR.OW, bytes1);
        final DicomElement overlayDataDcmElem = dcmObj.putBytes(Tag.OverlayData, VR.OW, bytes2);
        final File dcmFile = File.createTempFile("test", "dcm");
        final DicomOutputStream outStream = new DicomOutputStream(dcmFile);
        outStream.writeDataset(dcmObj, TransferSyntax.ImplicitVRLittleEndian);
        outStream.close();

        final BinaryDcmData dcmData = new BinaryDcmData();

        {
            assertEquals(dcmData.size(), 0);
            int i = 0;
            for (@SuppressWarnings("unused") final byte[] bytes: dcmData) {
                ++i;
            }
            assertEquals(i, 0);
        }

        dcmData.add(dcmFile, new int[] {}, pixelDataDcmElem);
        {
            assertEquals(dcmData.size(), 1);
            int i = 0;
            for (final byte[] bytes: dcmData) {
                assertArrayEquals(bytes, bytes1);
                ++i;
            }
            assertEquals(i, 1);
            assertArrayEquals(dcmData.getBinaryItem(0), bytes1);
        }

        dcmData.add(dcmFile, new int[] {}, varPixelDataDcmElem);
        {
            assertEquals(dcmData.size(), 2);
            int i = 0;
            for (final byte[] bytes: dcmData) {
                assertArrayEquals(bytes, bytes1);
                ++i;
            }
            assertEquals(i, 2);
            assertArrayEquals(dcmData.getBinaryItem(0), bytes1);
            assertArrayEquals(dcmData.getBinaryItem(1), bytes1);
        }

        dcmData.add(dcmFile, new int[] {}, overlayDataDcmElem);
        {
            assertEquals(dcmData.size(), 3);
            int i = 0;
            for (final byte[] bytes: dcmData) {
                if (i == 0 || i == 1) {
                    assertArrayEquals(bytes, bytes1);
                } else {
                    assertEquals(i, 2);
                    assertArrayEquals(bytes, bytes2);
                }
                ++i;
            }
            assertEquals(i, 3);
            assertArrayEquals(dcmData.getBinaryItem(0), bytes1);
            assertArrayEquals(dcmData.getBinaryItem(1), bytes1);
            assertArrayEquals(dcmData.getBinaryItem(2), bytes2);
        }
        
        {
            final InputStream stream = dcmData.getBinaryItemStream(1);
            final byte[] newBytes1 = new byte[bytes1.length];
            assertEquals(stream.available(), bytes1.length);
            final int bytesRead = stream.read(newBytes1);
            assertEquals(bytesRead, bytes1.length);
            assertEquals(stream.read(), -1);
            stream.close();
        }
        
        {
            final InputStream stream = dcmData.getBinaryItemStream(0);
            assertEquals(stream.available(), bytes1.length);
            assertEquals(stream.read(), (char) bytes1[0]);
            assertEquals(stream.read(), (char) bytes1[1]);
            assertEquals(stream.read(), (char) bytes1[2]);
            assertEquals(stream.read(), (char) bytes1[3]);
            assertEquals(stream.read(), -1);
            stream.close();
        }

        {
            final Iterator<InputStream> streamIter = dcmData.streamIterator();
            assertTrue(streamIter.hasNext());
            final InputStream stream = dcmData.streamIterator().next();
            final byte[] newBytes1 = new byte[bytes1.length];
            assertEquals(stream.available(), bytes1.length);
            final int bytesRead = stream.read(newBytes1);
            assertEquals(bytesRead, bytes1.length);
            assertEquals(stream.read(), -1);
            stream.close();
            //Trigger an expected exception
            streamIter.remove();
        }
    }
}
