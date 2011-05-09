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

import org.dcm4che2.data.DicomElement;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.io.DicomCodingException;
import org.dcm4che2.io.DicomInputStream;

import java.io.*;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Not thread-safe, due to contained mutable cache objects.
 *
 * @author Uli Bubenheimer
 *
 */
public final class BinaryDcmData implements BinaryData {
    private static abstract class FileTagpath {
        public FileTagpath(final File dcmFile, final int[] tagPath) {
            this.dcmFile = dcmFile;
            this.tagPath = tagPath;
        }

        public abstract byte[] getBinaryData() throws IOException;

        protected final DicomObject getRootDicomObject() throws IOException {
            if (!dcmFile.equals(cachedRootDicomObjectFile)) {
                final DicomObject newRootDicomObject;
                final DicomInputStream stream = new DicomInputStream(
                        new BufferedInputStream(new FileInputStream(dcmFile), 600000));
                try {
                    newRootDicomObject = stream.readDicomObject();
                } finally {
                    stream.close();
                }

                cachedRootDicomObject = newRootDicomObject;
                cachedRootDicomObjectFile = dcmFile;
            }
            return cachedRootDicomObject;
        }

        private static DicomObject cachedRootDicomObject;
        private static File cachedRootDicomObjectFile;

        protected final File getDcmFile() {
            return dcmFile;
        }

        protected final int[] getTagPath() {
            return tagPath;
        }

        private final File dcmFile;
        private final int[] tagPath;
    }

    private static final class NativeFileTagpath extends FileTagpath{
        public NativeFileTagpath(final File dcmFile, final int[] tagPath, final int offset, final int size) {
            super(dcmFile, tagPath);
            this.offset = offset;
            this.size = size;
        }

        @Override
        public byte[] getBinaryData() throws IOException {
            final DicomObject dcmObj = getRootDicomObject();
            final byte[] binaryData = dcmObj.getBytes(getTagPath());
            if (binaryData.length == size) {
                // single-frame image
                return binaryData;
            } else {
                // multi-frame image
                final byte[] frame = new byte[size];
                System.arraycopy(binaryData, offset, frame, 0, size);
                return frame;
            }
        }

        @Override
        public String toString() {
            return "<" + getDcmFile() + "," + Arrays.toString(getTagPath()) + "," + offset + "," + size + ">";
        }

        /** Offset in PixelData at which to find image.  This might
         * overflow an int, but System.arrayCopy does not take longs. */
        private final int offset;
        /** Length of image in PixelData */
        private final int size;
    }

    private static final class EncapsulatedFileTagpath extends FileTagpath {
        public EncapsulatedFileTagpath(final File dcmFile, final int[] tagPath, final int startingFragment,
                                       final int fragmentCount) {
            super(dcmFile, tagPath);
            this.startingFragment = startingFragment;
            this.fragmentCount = fragmentCount;
        }

        @Override
        public byte[] getBinaryData() throws IOException {
            final DicomObject dcmObj = getRootDicomObject();
            final DicomElement binaryData = dcmObj.get(getTagPath());
            if (fragmentCount == 1) {
                return binaryData.getFragment(startingFragment);
            } else {
                assert fragmentCount > 1;
                int fragmentIdx = startingFragment;

                final int endFragment = startingFragment + fragmentCount;

                //First determine frame size
                int frameSize = 0;
                do {
                    final byte[] fragmentBytes = binaryData.getFragment(fragmentIdx++);
                    frameSize += fragmentBytes.length;
                } while (fragmentIdx < endFragment);

                //Now assemble the actual frame data
                fragmentIdx = startingFragment;
                int frameByteIdx = 0;
                final byte[] frameBytes = new byte[frameSize];
                do {
                    final byte[] fragmentBytes = binaryData.getFragment(fragmentIdx++);
                    final int fragmentSize = fragmentBytes.length;
                    System.arraycopy(fragmentBytes, 0, frameBytes, frameByteIdx, fragmentSize);
                    frameByteIdx += fragmentSize;
                } while (fragmentIdx < endFragment);

                return frameBytes;
            }
        }

        @Override
        public String toString() {
            //TODO
            return "<" + getDcmFile() + "," + Arrays.toString(getTagPath()) + "," + startingFragment + ","
                    + fragmentCount + ">";
        }

        private final int startingFragment;
        private final int fragmentCount;
    }

    private final List<FileTagpath> binaryItems = new ArrayList<FileTagpath>();

    private class BinaryItemStream extends InputStream {
        public BinaryItemStream(final FileTagpath fileTagPath) {
            this.tagPath = fileTagPath;
        }

        @Override
        public int read() throws IOException, DicomCodingException {
            final byte[] binaryItem = getBinaryItem();
            if (pos >= binaryItem.length) {
                return -1;
            }

            //Convert to char first, so that negative byte values do not become negative int values
            return (char) binaryItem[pos++];
        }

        @Override
        public int read(final byte[] b, final int off, int len) throws IOException {
            final byte[] binaryItem = getBinaryItem();
            final int readableBytes = binaryItem.length - pos;
            if (readableBytes <= 0) {
                return -1;
            }

            if (readableBytes < len) {
                len = readableBytes;
            }
            System.arraycopy(binaryItem, pos, b, off, len);
            pos += len;
            return len;
        }

        @Override
        public int available() throws IOException {
            final byte[] binaryItem = getBinaryItem();
            return binaryItem.length - pos;
        }

        private byte[] getBinaryItem() throws IOException, DicomCodingException {
            byte[] binaryItem = binaryItemRef.get();
            if (binaryItem == null) {
                binaryItem = fileTagpathToFile(tagPath);
                binaryItemRef = new WeakReference<byte[]>(binaryItem);
            }
            return binaryItem;
        }
        
        private Reference<byte[]> binaryItemRef = new WeakReference<byte[]>(null);
        private final FileTagpath tagPath;
        private int pos = 0;
    }

    public void addNative(final File dcmFile, final int[] tagPath, final int offset, final int length) {
        assert dcmFile != null;
        assert tagPath.length >= 1;
        assert offset >= 0;
        assert length >= 1;
        final FileTagpath storeElem = new NativeFileTagpath(dcmFile, tagPath, offset, length);
        binaryItems.add(storeElem);
    }

    public void addEncapsulated(final File dcmFile, final int[] tagPath, final int startingFragment,
                                final int fragmentCount) {
        assert dcmFile != null;
        assert tagPath.length >= 1;
        assert startingFragment >= 0;
        assert fragmentCount >= 1;
        final FileTagpath storeElem = new EncapsulatedFileTagpath(dcmFile, tagPath, startingFragment, fragmentCount);
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
        try {
            return binaryItemPath.getBinaryData();
        } catch (final UnsupportedOperationException e) {
            //Something wrong with the DICOM format
            final DicomCodingException newEx = new DicomCodingException("DICOM syntax error at: " + binaryItemPath);
            newEx.initCause(e);
            throw newEx;
        }
    }
}
