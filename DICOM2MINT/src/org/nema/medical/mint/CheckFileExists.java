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

package org.nema.medical.mint;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.nema.medical.mint.AssocMap.AssocInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;

/**
 * @author Uli Bubenheimer
 *
 */
public final class CheckFileExists {
    public Collection<File> run() {
        final File topDir = monitoredLocation.getFile();

        final FileFilter directoryFilter = new FileFilter() {
            public boolean accept(final File pathname) {
                //Accept all directories, no non-directories
                return pathname.isDirectory();
            }
        };

        if (!topDir.exists()) {
            throw new RuntimeException("root directory not found: " + topDir);
        }
        final File[] dirs = topDir.listFiles(directoryFilter);
        directoryItems = new TreeSet<File>(Arrays.asList(dirs));
        final Collection<File> returnedItems = new ArrayList<File>();
        for (final File dir: directoryItems) {
            final DirectoryItem dirItem = process(dir);
            final File associationDir = new File(monitoredLocation.getFile(), dirItem.getAssociationID());
            for (final String subDirItem: dirItem.getNewFiles()) {
                returnedItems.add(new File(associationDir, subDirItem));
            }
        }

        return returnedItems;
    }

    private DirectoryItem process(final File dir) {
        final AssocInfo oldAssocInfo = assocMap.map.get(dir);
        final long newMod = dir.lastModified();
        if (oldAssocInfo == null || oldAssocInfo.lastUpdateTimestamp != newMod) {
            final SortedSet<File> newSubDirFiles = traverseSubDir(dir);
            if (!newSubDirFiles.isEmpty()) {
                final AssocInfo newAssocInfo = new AssocInfo();
                newAssocInfo.lastUpdateTimestamp = newMod;
                newAssocInfo.files = newSubDirFiles;
                assocMap.map.put(dir, newAssocInfo);

                final Set<File> oldFiles = (oldAssocInfo == null ? null : oldAssocInfo.files);
                final Set<String> createdFiles = extractCreatedFiles(oldFiles, newSubDirFiles);

                if (!createdFiles.isEmpty()) {
                    final DirectoryItem dirItem = new DirectoryItem();
                    dirItem.setAssociationID(dir.getName());
                    dirItem.setNewFiles(createdFiles);
                    return dirItem;
                }
            }
        }

        return null;
    }

    private static SortedSet<File> traverseSubDir(final File subDir)
    {
        final FileFilter fileFilter = new FileFilter() {
            @Override
            public boolean accept(final File pathname) {
                //Accept non-directories with the suffix ".rxmsg"
                final String fileName = pathname.getName();
                return !pathname.isDirectory() && fileName.endsWith(".rxmsg") && !fileName.equals("AssociationSummary.rxmsg");
            }
        };
        final File[] files = subDir.listFiles(fileFilter);
        return new TreeSet<File>(Arrays.asList(files));
    }

    private static Set<String> extractCreatedFiles(final Set<File> oldFiles, final Set<File> newFiles)
    {
        final Set<String> createdFileNames = new HashSet<String>();

        final Set<File> createdFiles = new TreeSet<File>(newFiles);
        if (oldFiles != null) {
            createdFiles.removeAll(oldFiles);
        }

        for (File file: createdFiles)
        {
            createdFileNames.add(file.getName());
        }

        return createdFileNames;
    }

    private Set<File> directoryItems;

    @Autowired
    private FileSystemResource monitoredLocation;

    @Autowired
    private AssocMap assocMap;
}
