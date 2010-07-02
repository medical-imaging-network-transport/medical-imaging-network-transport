/* Copyright (c) Vital Images, Inc. 2010. All Rights Reserved.
*
*    This is UNPUBLISHED PROPRIETARY SOURCE CODE of Vital Images, Inc.;
*    the contents of this file may not be disclosed to third parties,
*    copied or duplicated in any form, in whole or in part, without the
*    prior written permission of Vital Images, Inc.
*
*    RESTRICTED RIGHTS LEGEND:
*    Use, duplication or disclosure by the Government is subject to
*    restrictions as set forth in subdivision (c)(1)(ii) of the Rights
*    in Technical Data and Computer Software clause at DFARS 252.227-7013,
*    and/or in similar or successor clauses in the FAR, DOD or NASA FAR
*    Supplement. Unpublished rights reserved under the Copyright Laws of
*    the United States.
*/

package org.nema.medical.mint.dcm2mint;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Uli Bubenheimer
 */
public final class BinaryFileData implements BinaryData {
    /** The study's binary data */
    public final List<File> binaryItems = new ArrayList<File>();

    @Override
    public void add(final byte[] item) {
        //Stream to file
        assert item != null;
        try {
            final File tmpFile = File.createTempFile("mint", ".bin");
            //The file should be deleted as soon as it is passed on to somewhere else;
            //we have a deleteOnExit here just as a backup.
            tmpFile.deleteOnExit();
            final FileOutputStream outStream = new FileOutputStream(tmpFile);
            try {
                outStream.write(item);
            } finally {
                outStream.close();
            }

            binaryItems.add(tmpFile);
        } catch (final IOException ex) {
         //This would happen only in a catastrophic case
         throw new RuntimeException(ex);
        }
    }

    public File getBinaryFile(final int index) {
         return binaryItems.get(index);
    }

    @Override
    public byte[] getBinaryItem(final int index) {
         final File binaryItem = getBinaryFile(index);
            final long fileSize = binaryItem.length();
            //This should always be true, since we also wrote it from a byte array (which is bound by Integer.MAX_VALUE)
            assert fileSize <= Integer.MAX_VALUE;
            final byte[] dataBytes = new byte[(int)fileSize];
            try {
                final FileInputStream binStream = new FileInputStream(binaryItem);
                try {
                    int offset = 0;
                    for(;;) {
                        final int bytesRead = binStream.read(dataBytes, 0, (int)fileSize - offset);
                        if (bytesRead == 0) {
                            break;
                        }
                        offset += bytesRead;
                    }
                } finally {
                    binStream.close();
                }
            } catch (final IOException ex) {
                //This would happen only in a catastrophic case
                throw new RuntimeException(ex);
            }
            return dataBytes;
    }

    @Override
    public int size() {
        return binaryItems.size();
    }
}
