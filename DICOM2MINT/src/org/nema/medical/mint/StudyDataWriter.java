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

package org.nema.medical.mint;

import java.util.Collection;

import org.nema.medical.mint.dcm2mint.MetaBinaryPair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;

/**
 * @author Uli Bubenheimer
 */
public final class StudyDataWriter {
    @Autowired
    FileSystemResource dicomProcessorOutputDir;

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
    }
}
