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

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dcm4che2.data.DicomElement;

/**
 * @author Uli Bubenheimer
 */
public final class BinaryMemoryData implements BinaryData {
    /** The study's binary data */
    private final List<byte[]> binaryItems = new ArrayList<byte[]>();

    @Override
    public void add(final File dcmFile, final int[] tagPath, final DicomElement dcmElem) {
        add(dcmElem.getBytes());
    }

    void add(final byte[] binaryData) {
        binaryItems.add(binaryData);
    }

    @Override
    public byte[] getBinaryItem(final int index) {
        return binaryItems.get(index);
    }

    @Override
    public int size() {
        return binaryItems.size();
    }

    @Override
    public Iterator<byte[]> iterator() {
        return binaryItems.iterator();
    }
}
