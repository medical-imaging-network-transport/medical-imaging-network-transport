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

package org.nema.medical.mint.dcmimport;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.TransferSyntax;
import org.dcm4che2.io.DicomInputStream;
import org.nema.medical.mint.dcm2mint.BinaryFileData;
import org.nema.medical.mint.dcm2mint.Dcm2MetaBuilder;
import org.nema.medical.mint.dcm2mint.MetaBinaryPairImpl;

/**
 * @author Uli Bubenheimer
 */
public final class ProcessImportDir {

    public ProcessImportDir(final File importDir, final MINTSend mintConsumer) {
        this.importDir = importDir;
        this.mintConsumer = mintConsumer;
    }

    public void run() {
        final Collection<File> resultFiles = new ArrayList<File>();
        findPlainFilesRecursive(importDir, resultFiles);
        for (final File plainFile: resultFiles) {
            try {
                final DicomInputStream dcmStream = new DicomInputStream(plainFile);
                try {
                    //TODO Somewhat wasteful to parse the entire file just to get to the study UID
                    final DicomObject dcmObj = dcmStream.readDicomObject();
                    final String studyUID = Dcm2MetaBuilder.extractStudyInstanceUID(dcmObj);
                    final Collection<File> dcmFileData = studyFileMap.get(studyUID);
                    if (dcmFileData == null) {
                        studyFileMap.put(studyUID, Collections.singleton(plainFile));
                    } else {
                        dcmFileData.add(plainFile);
                    }
                } finally {
                    dcmStream.close();
                }
            } catch (final IOException e) {
                //Not a valid DICOM file?!
                System.err.println("Skipping file: " + plainFile);
            }
        }

        final Set<Integer> studyLevelTags = getTags("StudyTags.txt");
        final Set<Integer> seriesLevelTags = getTags("SeriesTags.txt");

        for (final Map.Entry<String, Collection<File>> studyFiles: studyFileMap.entrySet()) {
            final String studyUID = studyFiles.getKey();
            assert studyUID != null;

            final BinaryFileData binaryData = new BinaryFileData();
            final MetaBinaryPairImpl metaBinaryPair = new MetaBinaryPairImpl();
            metaBinaryPair.setBinaryData(binaryData);
            //Constrain processing
            metaBinaryPair.getMetadata().setStudyInstanceUID(studyUID);
            final Dcm2MetaBuilder builder = new Dcm2MetaBuilder(studyLevelTags, seriesLevelTags, metaBinaryPair);
            for (final File instanceFile: studyFiles.getValue()) {
                final TransferSyntax transferSyntax;
                final DicomObject dcmObj;
                try {
                    final DicomInputStream dcmStream = new DicomInputStream(instanceFile);
                    try {
                        transferSyntax = dcmStream.getTransferSyntax();
                        dcmObj = dcmStream.readDicomObject();
                    } finally {
                        dcmStream.close();
                    }
                } catch (final IOException e) {
                    //Not a valid DICOM file?!
                    System.err.println("Skipping file: " + instanceFile);
                    continue;
                }
                builder.accumulateFile(instanceFile, dcmObj, transferSyntax);
            }
            builder.finish();
            mintConsumer.send(metaBinaryPair);
        }
    }

    private static void findPlainFilesRecursive(final File targetFile, final Collection<File> resultFiles) {
        if (targetFile.isFile()) {
            resultFiles.add(targetFile);
        } else {
            assert targetFile.isDirectory();
            for (final File subFile: targetFile.listFiles()) {
                findPlainFilesRecursive(subFile, resultFiles);
            }
        }
    }

    private Set<Integer> getTags(final String resource) {
        final ClassLoader loader = this.getClass().getClassLoader();
        final Properties properties = new Properties();
        try {
            InputStream stream = loader.getResourceAsStream(resource);
            try {
                properties.load(stream);
            } finally {
                stream.close();
            }
        } catch(final IOException ex) {
            Logger.getLogger(this.getClass()).error("Unable to read tags file", ex);
        }
        final Set<Integer> tagSet = new HashSet<Integer>();
        for (final Object tagStr: properties.keySet()) {
            //Go to long as int is unsigned and insufficient here
            final int intTag = (int)Long.parseLong(tagStr.toString(), 16);
            tagSet.add(intTag);
        }
        return tagSet;
    }

    private final File importDir;
    private final MINTSend mintConsumer;
    private final Map<String, Collection<File>> studyFileMap = new HashMap<String, Collection<File>>();
}
