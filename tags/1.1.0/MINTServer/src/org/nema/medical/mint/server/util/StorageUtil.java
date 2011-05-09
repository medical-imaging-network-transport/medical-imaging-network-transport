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
package org.nema.medical.mint.server.util;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.nema.medical.mint.datadictionary.MetadataType;
import org.nema.medical.mint.metadata.StudyMetadata;
import org.nema.medical.mint.utils.StudyTraversals;
import org.nema.medical.mint.utils.StudyUtils;
import org.nema.medical.mint.utils.StudyValidation;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author Rex
 *
 */
public final class StorageUtil {
    private static final Logger LOG = Logger.getLogger(StorageUtil.class);

    public static final String BINARY_FILE_EXTENSION = "dat";
    public static final String EXCLUDED_BINARY_FILE_EXTENSION = "exclude";

    /**
     * This method will looks for BINARY_FILE_EXTENSION files and will try to
     * perform a parseInt on the front of the files. The largest int parsed will
     * be returned. -1 will be returned when not BINARY_FILE_EXTENSION files are
     * encountered.
     *
     * This method assumes that the BINARY_FILE_EXTENSION files start with an
     * integer and will need to be re thought if what binary item IDs are is
     * redesigned.
     *
     * @param directory
     * @return highest number
     */
    public static int getHighestNumberedBinaryItem(File directory)
    {
        String[] fileNames = directory.list();

        int max = -1;
        for(String name : fileNames)
        {
            if(name.endsWith(BINARY_FILE_EXTENSION))
            {
                try
                {
                    int n = Integer.parseInt(name.substring(0, name.indexOf('.')));

                    if(n > max)
                    {
                        max = n;
                    }else if(n == max){
                        LOG.warn("Multiple binary datafiles have the same number name! This is unexpected");
                    }
                }catch(NumberFormatException e){
                    LOG.debug("Binary datafile named '" + name + "' was not an integer, this is unexpected", e);
                }
            }
        }

        return max;
    }

    /**
     * This method will shift all binary item IDs found in the binaryDirectory
     * by the provided the shiftAmount and will also update the provided StudyMetadata
     * accordingly. It will throw an exception if something did not work.
     * Depending on the type of failure, the data
     * may be left in an inconsistent state, there is not guarantee that this
     * method will revert changes that have already been written.
     *
     * @param study
     * @param binaryDirectory
     * @param shiftAmount
     * @throws Exception if something went wrong
     */
    public static void shiftItemIds(final StudyMetadata study, final File binaryDirectory, final int shiftAmount)
            throws Exception {
    	if (shiftAmount == 0) {
    		return;
    	}

        //I know this if is not necessary, I put it here in order to be symmetric
        shiftBinaryFiles(binaryDirectory, shiftAmount);
        StudyUtils.shiftStudyBids(study, shiftAmount);
    }

    private static final class NumberFileNameComparator implements Comparator<String> {
        @Override
        public int compare(final String s1, final String s2) {
            final int ext1Idx = s1.indexOf('.');
            final int ext2Idx = s2.indexOf('.');
            if (ext1Idx == -1 || ext2Idx == -1) {
                return s1.compareTo(s2);
            }
            final int number1;
            final int number2;
            try {
                number1 = Integer.parseInt(s1.substring(0, ext1Idx));
                number2 = Integer.parseInt(s2.substring(0, ext2Idx));
            } catch (final NumberFormatException e) {
                return s1.compareTo(s2);
            }
            if (number1 < number2) {
                return -1;
            } else if (number1 == number2) {
                return s1.compareTo(s2);
            } else {
                return 1;
            }
        }
    }

    private static final NumberFileNameComparator numberFileNameComparator = new NumberFileNameComparator();

    /**
     * Will return true always unless something catastrophically unexpected
     * occurs.
     *
     * @param directory
     * @param shiftAmount
     * @throws Exception
     */
    private static void shiftBinaryFiles(final File directory, final int shiftAmount) throws Exception {
        final String[] fileNames = directory.list();
        //Sort file names so that we start renaming the highest index to be sure that the new name does not yet exist
        Arrays.sort(fileNames, numberFileNameComparator);
        ArrayUtils.reverse(fileNames);
        for (final String name: fileNames) {
            if (name.endsWith(BINARY_FILE_EXTENSION) || name.endsWith(EXCLUDED_BINARY_FILE_EXTENSION)) {
                if (!name.startsWith("metadata")) {
                    try {
                        final int oldBid;
                        final int extStart = name.indexOf('.');
                        if (extStart > 0) {
                        	oldBid = Integer.parseInt(name.substring(0, extStart));
                        } else {
                        	oldBid = Integer.parseInt(name);
                        }
                        final int newBid = oldBid + shiftAmount;

                        final String newName = newBid + "." + BINARY_FILE_EXTENSION;

                        //It is a binary file, shift it!
                        final File oldFile = new File(directory, name);
                        final File newFile = new File(directory, newName);
                        if (!oldFile.renameTo(newFile)) {
                            final String errorText = "Error moving/renaming file '" + oldFile.getPath() + " to "
                                    + newFile.getPath();
                            LOG.error(errorText);
                            throw new Exception(errorText);
                        }
                    } catch (final NumberFormatException e){
                        LOG.warn("Detected binary item file whose name was not an integer as was expected.", e);
                    }
                }
            }
        }
    }

    /**
     * This method will find the largest int named file in the changelog root
     * directory and will return a file to root/'the largest int + 1'. Calls
     * mkdirs on the file to return before returning it.
     *
     * @param changelogRoot
     * @return a file to root/'the largest int + 1'
     */
    public static File getNextChangelogDir(File changelogRoot)
    {
        int max = -1;

        for(String name : changelogRoot.list())
        {
            try
            {
                int tmp = Integer.parseInt(name);

                if(tmp > max)
                    max = tmp;

            }catch(NumberFormatException e){
                LOG.warn("Encountered a changelog folder that was not an integer: " + name, e);
            }
        }

        File nextChange = new File(changelogRoot, Integer.toString(max+1));
        nextChange.mkdirs();

        return nextChange;
    }

    /**
     * This method will return successfully if the given study has passed all
     * implemented validation checks. This is validation for studies that are
     * being 'created' or 'updated' it is not expected that this validation with
     * pass on studies already written to disk.
     *
     * @param study
     * @param binaryFolder
     */
    public static void validateStudy(final StudyMetadata study, final MetadataType type, final File binaryFolder)
            throws StudyTraversals.TraversalException {
        StudyValidation.validateStudy(study, type, getBinaryItemIds(binaryFolder));
    }

    /**
     * Retrieves binary item ids from a given binary items folder.
     *
     * @param binaryFolder
     * @return Returns true if no violations were detected.
     */
    public static Collection<Integer> getBinaryItemIds(File binaryFolder)
    {
        final Collection<Integer> binaryItemIds = new HashSet<Integer>();

        //Collect id from file names
        for(String file : binaryFolder.list())
        {
            if(!file.startsWith("metadata"))
            {
                try
                {
                    int bid = Integer.parseInt(file.substring(0,file.indexOf('.')));

                    binaryItemIds.add(bid);
                }catch(NumberFormatException e){
                    LOG.warn("Detected binary item file whose name was not an integer as was expected.", e);
                }
            }
        }

        return binaryItemIds;
}

	/**
	 * Will attempt to rename each file that is bid.dat where possible bids are
	 * passed in the excludedBids collection to bid.exclude. Will return -1 if
	 * successful and will return the bid of the rename that failed if something
	 * went wrong.
	 *
	 * @param existingBinaryFolder
	 * @param excludedBids
	 * @return -1 if successful, or the bid of the rename that failed if something
	 * went wrong.
	 */
    public static int renameExcludedFiles(File existingBinaryFolder,
			Collection<Integer> excludedBids) {
		for(int bid : excludedBids)
		{
			File oldFile = new File(existingBinaryFolder, bid + "." + BINARY_FILE_EXTENSION);
			File newFile = new File(existingBinaryFolder, bid + "." + EXCLUDED_BINARY_FILE_EXTENSION);

			if(!oldFile.renameTo(newFile))
				return bid;
		}

		return -1;
	}

	//TODO from code review of StudyUtils where this method used to live: this method doesn't belong in here - the rest of the methods operate on metadata. this belongs in a store
    public static void moveBinaryItems(final File jobFolder, final File studyBinaryFolder) throws IOException {
        for (final File file: jobFolder.listFiles()) {
            //Don't move metadata because it has no purpose in the destination
            final String fileName = file.getName();
            if (!fileName.startsWith("metadata")) {
                final File permfile = new File(studyBinaryFolder, fileName);
                // just moving the file since the reference implementation
                // is using the same MINT_ROOT for temp and perm storage
                // other implementations may want to copy/delete the file
                // if the temp storage is on a different device
                if (!file.renameTo(permfile)) {
                    final String message =
                            "Unable to move/rename file '" + file.getPath() + " to " + permfile.getPath();
                    LOG.error(message);
                    throw new IOException(message);
                }
            }
        }
    }
}
