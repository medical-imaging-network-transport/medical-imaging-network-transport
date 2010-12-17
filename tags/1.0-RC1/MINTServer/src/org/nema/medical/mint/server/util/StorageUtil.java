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

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.apache.log4j.Logger;
import org.nema.medical.mint.metadata.*;
import org.nema.medical.mint.metadata.StudyMetadata;
import org.nema.medical.mint.utils.StudyUtils;

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
     * accordingly. It will return true if everything happened all right, false
     * if something weird happened. Depending on the type of failure, the data
     * may be left in an inconsistent state, there is not guarantee that this
     * method will revert changes that have already been written.
     *
     * @param study
     * @param binaryDirectory
     * @param shiftAmount
     * @return true if successful
     */
    public static boolean shiftItemIds(StudyMetadata study, File binaryDirectory, int shiftAmount)
    {
    	if(shiftAmount == 0)
    	{
    		return true;
    	}

        /*
         * Need to shift the both the binary file names and the study bids to
         * stay consistent.
         */
        boolean success = true;

        //I know this if is not necessary, I put it here in order to be symmetric
        if(success)
            success &= shiftBinaryFiles(binaryDirectory, shiftAmount);

        if(success)
            success &= StudyUtils.shiftStudyBids(study, shiftAmount);

        return success;
    }

    /**
     * Will return true always unless something catastrophically unexpected
     * occurs.
     *
     * @param directory
     * @param shiftAmount
     * @return
     */
    private static boolean shiftBinaryFiles(File directory, int shiftAmount)
    {
        File[] files = directory.listFiles();

        for(File f : files)
        {
            String name = f.getName();

            if(name.endsWith(BINARY_FILE_EXTENSION) || name.endsWith(EXCLUDED_BINARY_FILE_EXTENSION))
            {
                if(!name.startsWith("metadata"))
                {
                    try
                    {
                        int bid;
                        int extStart = name.indexOf('.');
                        if(extStart > 0)
                        	bid = Integer.parseInt(name.substring(0,extStart));
                        else
                        	bid = Integer.parseInt(name);

                        bid += shiftAmount;

                        String newName = bid + "." + BINARY_FILE_EXTENSION;

                        //It is a binary file, shift it!
                        f.renameTo(new File(directory, newName));
                    }catch(NumberFormatException e){
                        LOG.warn("Detected binary item file whose name was not an integer as was expected.", e);
                    }
                }
            }else{
                //Not a binary file
            }
        }

        return true;
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
     * This method will return true if the given study has passed all
     * implemented validation checks. This is validation for studies that are
     * being 'created' or 'updated' it is not expected that this validation with
     * pass on studies already written to disk.
     *
     * @param study
     * @param binaryFolder
     * @return true iff the given study has passed all implemented validation checks
     */
    public static boolean validateStudy(StudyMetadata study, File binaryFolder)
    {
        return StudyUtils.validateStudy(study, getBinaryItemIds(binaryFolder));
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
}
