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
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.dcm4che2.data.DicomElement;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.io.DicomCodingException;
import org.dcm4che2.io.DicomInputStream;

/**
 * Not thread-safe, due to contained mutable cache objects.
 *
 * @author Uli Bubenheimer
 *
 */
public final class BinaryDcmData implements BinaryData {
    private static final class FileTagpath {
        public FileTagpath(final File dcmFile, final int[] tagPath) {
            this(dcmFile, tagPath, -1);
        }
        
        public FileTagpath(File dcmFile, int[] tagPath, int fragment) {
            this.dcmFile = dcmFile;
            this.tagPath = tagPath;
            this.fragment = fragment;
        }

        @Override
        public String toString() {
            return "<" + dcmFile + "," + Arrays.toString(tagPath) + ">";
        }

        public final File dcmFile;
        public final int[] tagPath;
        public final int fragment;
    }

    private final List<FileTagpath> binaryItems = new ArrayList<FileTagpath>();

    private DicomObject cachedRootDicomObject;
    private File cachedRootDicomObjectFile;

    private class BinaryItemStream extends InputStream {
        public BinaryItemStream(final FileTagpath fileTagPath) {
            this.tagPath = fileTagPath;
        }

        @Override
        public int read() throws IOException, DicomCodingException {
            byte[] binaryItem = binaryItemRef.get();
            if (binaryItem == null) {
                binaryItem = fileTagpathToFile(tagPath);
                binaryItemRef = new WeakReference<byte[]>(binaryItem);
            }
            if (pos >= binaryItem.length) {
                return -1;
            }

            //Convert to char first, so that negative byte values do not become negative int values
            return (char) binaryItem[pos++];
        }

        private Reference<byte[]> binaryItemRef = new WeakReference<byte[]>(null);
        private final FileTagpath tagPath;
        private int pos = 0;
    }

    @Override
    public void add(final File dcmFile, final int[] tagPath, final DicomElement dcmElem) {
        add(dcmFile, tagPath, dcmElem, -1);
    }
    
    public void add(File dcmFile, int[] tagPath, DicomElement dcmElem, int fragment) {
        final int[] newTagPath = new int[tagPath.length + 1];
        System.arraycopy(tagPath, 0, newTagPath, 0, tagPath.length);
        newTagPath[tagPath.length] = dcmElem.tag();
        final FileTagpath storeElem = new FileTagpath(dcmFile, newTagPath, fragment);
        binaryItems.add(storeElem);
    }

    @Override
    public byte[] getBinaryItem(final int index) {
        try {
            return fileTagpathToFile(binaryItems.get(index));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public InputStream getBinaryItemStream(final int index) {
        return new BinaryItemStream(binaryItems.get(index));
    }

    public Iterator<InputStream> streamIterator() {
        return new Iterator<InputStream>() {

            @Override
            public boolean hasNext() {
                return itemIterator.hasNext();
            }

            @Override
            public InputStream next() {
                return new BinaryItemStream(itemIterator.next());
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }

            private final Iterator<FileTagpath> itemIterator = binaryItems.iterator();
        };
    }

    @Override
    public int size() {
        return binaryItems.size();
    }

    @Override
    public Iterator<byte[]> iterator() {
        return new Iterator<byte[]>() {

            @Override
            public boolean hasNext() {
                return itemIterator.hasNext();
            }

            @Override
            public byte[] next() {
                try {
                    return fileTagpathToFile(itemIterator.next());
                } catch (final IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }

            private final Iterator<FileTagpath> itemIterator = binaryItems.iterator();
        };
    }

    private byte[] fileTagpathToFile(final FileTagpath binaryItemPath) throws IOException, DicomCodingException {
        final File targetDcmFile = binaryItemPath.dcmFile;
        if (!targetDcmFile.equals(cachedRootDicomObjectFile)) {
            final DicomObject newRootDicomObject;
            final DicomInputStream stream = new DicomInputStream(targetDcmFile);
            try {
                newRootDicomObject = stream.readDicomObject();
            } finally {
                stream.close();
            }

            cachedRootDicomObject = newRootDicomObject;
            cachedRootDicomObjectFile = targetDcmFile;
        }
        final byte[] binaryData;
        try {
            if (binaryItemPath.fragment >= 0) {
                // multiframe
                DicomElement element = cachedRootDicomObject.get(binaryItemPath.tagPath);
                binaryData = element.getFragment(binaryItemPath.fragment);
            } else {
                binaryData = cachedRootDicomObject.getBytes(binaryItemPath.tagPath);
            }
        } catch (final UnsupportedOperationException e) {
            //Something wrong with the DICOM format
            final DicomCodingException newEx = new DicomCodingException("DICOM syntax error at: " + binaryItemPath);
            newEx.initCause(e);
            throw newEx;
        }
        return binaryData;
    }
}
