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

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

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

    @Test
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
            assertTrue(dcmData.size() == 0);
            int i = 0;
            for (@SuppressWarnings("unused") final byte[] bytes: dcmData) {
                ++i;
            }
            assertTrue(i == 0);
        }

        dcmData.add(dcmFile, new int[] {}, pixelDataDcmElem);
        {
            assertTrue(dcmData.size() == 1);
            int i = 0;
            for (final byte[] bytes: dcmData) {
                assertTrue(Arrays.equals(bytes, bytes1));
                ++i;
            }
            assertTrue(i == 1);
            assertTrue(Arrays.equals(dcmData.getBinaryItem(0), bytes1));
        }

        dcmData.add(dcmFile, new int[] {}, varPixelDataDcmElem);
        {
            assertTrue(dcmData.size() == 2);
            int i = 0;
            for (final byte[] bytes: dcmData) {
                assertTrue(Arrays.equals(bytes, bytes1));
                ++i;
            }
            assertTrue(i == 2);
            assertTrue(Arrays.equals(dcmData.getBinaryItem(0), bytes1));
            assertTrue(Arrays.equals(dcmData.getBinaryItem(1), bytes1));
        }

        dcmData.add(dcmFile, new int[] {}, overlayDataDcmElem);
        {
            assertTrue(dcmData.size() == 3);
            int i = 0;
            for (final byte[] bytes: dcmData) {
                if (i == 0 || i == 1) {
                    assertTrue(Arrays.equals(bytes, bytes1));
                } else {
                    assertTrue(i == 2);
                    assertTrue(Arrays.equals(bytes, bytes2));
                }
                ++i;
            }
            assertTrue(i == 3);
            assertTrue(Arrays.equals(dcmData.getBinaryItem(0), bytes1));
            assertTrue(Arrays.equals(dcmData.getBinaryItem(1), bytes1));
            assertTrue(Arrays.equals(dcmData.getBinaryItem(2), bytes2));
        }
    }
}
