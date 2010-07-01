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

import java.util.Collection;

import org.nema.medical.mint.dcm2mint.Dcm2MetaBuilder.MetaBinaryPair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;

/**
 * @author Uli Bubenheimer
 */
public final class StudyDataWriter {
    @Autowired
    FileSystemResource dicomProcessorOutputDir;

    //TODO
//    @Autowired
//    InstanceLogDAO instanceLogDAO;

    public void write(final Collection<MetaBinaryPair> items) throws Exception {
        //TODO
//        for (final MetaBinaryPair metaBinaryPair : items) {
//            final Study study = metaBinaryPair.metadata;
//            final UUID studyUUID = UUID.randomUUID();
//
//            final File studyOutputDir = new File(dicomProcessorOutputDir.getFile(), studyUUID.toString());
//            if (!studyOutputDir.exists()) {
//                studyOutputDir.mkdirs();
//            }
//
//
//            StudyIO.writeToGPB(study, new File(studyOutputDir, "metadata.gpb"));
//            StudyIO.writeToXML(study, new File(studyOutputDir, "metadata.xml"));
//
//            StudySummaryIO.writeSummaryToXHTML(study, new File(studyOutputDir, "summary.html"));
//
//            final File binaryItemsFile = new File(studyOutputDir, "binaryitems.dat");
//            final List<BinaryItem> binaryItems = new ArrayList<BinaryItem>(metaBinaryPair.binaryItems.size());
//            for (final byte[] byteArray : metaBinaryPair.binaryItems) {
//                final BinaryItem binaryItem = new BinaryItem();
//                binaryItem.setSize(byteArray.length);
//                final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
//                BinaryItemIO.appendBinaryItem(byteArrayInputStream, binaryItem, binaryItemsFile);
//                binaryItems.add(binaryItem);
//            }
//            BinaryItemIO.writeIndex(binaryItems, new File(studyOutputDir, "binaryindex.gpb"));

            //TODO
//            Iterator<Series> seriesIter = study.seriesIterator();
//            for (Series series; seriesIter.hasNext();) {
//                series = seriesIter.next();
//                Iterator<Instance> instanceIter = series.instanceIterator();
//                for (Instance instance; instanceIter.hasNext();) {
//                    instance = instanceIter.next();
//                    instanceLogDAO.logInstance("assoc", false, study.getStudyInstanceUID(),
//                            new Timestamp(System.currentTimeMillis()),
//                            study.getValueForAttribute(0x00100010), // patient name
//                            study.getValueForAttribute(0x00100020), // patient ID
//                            study.getValueForAttribute(0x00080050), // accession number
//                            instance.getXfer(),
//                            new Timestamp(System.currentTimeMillis()),
//                            new Timestamp(System.currentTimeMillis()),
//                            instance.getValueForAttribute(Tag.SOPInstanceUID),
//                            instance.getValueForAttribute(Tag.SOPClassUID),
//                            "aeTitle",
//                            "127.0.0.1",
//                            1L);
//                }
//
//            }
//        }
    }
}
