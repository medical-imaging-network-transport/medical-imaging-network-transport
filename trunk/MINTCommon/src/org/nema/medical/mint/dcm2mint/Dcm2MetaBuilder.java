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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.dcm4che2.data.DicomElement;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.SpecificCharacterSet;
import org.dcm4che2.data.Tag;
import org.dcm4che2.data.TransferSyntax;
import org.dcm4che2.data.VR;
import org.nema.medical.mint.metadata.Attribute;
import org.nema.medical.mint.metadata.AttributeStore;
import org.nema.medical.mint.metadata.Instance;
import org.nema.medical.mint.metadata.Item;
import org.nema.medical.mint.metadata.Series;
import org.nema.medical.mint.util.Iter;

/**
\brief Class used to build up a StudyMeta message from a set of DICOM instances.

To use this class, create a StudyMeta instance, a binary item vector, and a tag normalization map
(see the accumulateFile signature). Call accumulateFile() for each DICOM instance P10 file in the
study's dataset. This class will populate the passed-in parameters with the data from the P10
instances.


\par Example Usage
\code
Dcm2MetaBuilder::GroupElementTagSet final studyLevelTags = ... get study level attribute tags ...
Dcm2MetaBuilder::GroupElementTagSet final seriesLevelTags = ... get series-level attribute tags ...
Dcm2MetaBuilder builder(studyLevelTags, seriesLevelTags);

foreach (Path final& p10Path, ... list of DICOM P10 files ...)
{
   builder.accumulateFile(p10Path);
}

Dcm2MetaBuilder::MetaBinaryPair final data = builder.finish();

... Use data to produce on-disk representation ...
\endcode

\par Study/Series Summary-Level Tag Insertion Rules.
Tags can be organized, by request, to the study summary and series summary section for each
series. To do this, the caller provides the constructor with maps containing the tags for the
attributes that should be stored in the corresponding summary sections.
\par
When processing an instance, the attributes of the instance are checked against these two maps.
If the attribute's tag is found in one of the summary maps, this indicates the attribute is
a summary-level attribute. The attribute is inserted into the corresponding summary table and,
as a result, \e not into individual instance's attribute table. If the attribute already
existed in the specific summary table (i.e.: from a previous instance being processed), this
copy of the attribute is essentially discarded.
\par
This behavior may result in summary tags whose values differ between instances being discarded.
It may also result in attributes appearing to be present in all instances even if some instances
happened to not have the attributes. Both of these cases would be extremely rare and the current
behavior is deemed acceptable.

@author Uli Bubenheimer
*/
public final class Dcm2MetaBuilder {

    /**
   Create an instance of the class with the specified summary-level tag maps.

   The maps are used in subsequent calls to accumulateFile().

   @param studyLevelTags The caller must fill this in with tags that are considered to be part of
   the study-level & patient-level summary set. The builder will extract the associated tags from
   the instances passed into this function and place them in the resulting study's study-level
   summary tags section. See the note below for tag insertion rules.
   @param seriesLevelTags The caller must fill this in with tags that are considered to be part of
   the series-level set for each series. See the note below for tag insertion rules.

    The caller may set a StudyInstanceUID on the study to constrain processing.
   \note If a given attribute tag is present in both maps, the studyLevelTags map takes
   precedence.
    */
   public Dcm2MetaBuilder(
           final Set<Integer> studyLevelTags,
           final Set<Integer> seriesLevelTags,
           final MetaBinaryPair metaBinaryPair) {
       this.studyLevelTags = studyLevelTags;
       this.seriesLevelTags = seriesLevelTags;
       this.metaBinaryPair = metaBinaryPair;
   }

   public static String extractStudyInstanceUID(final DicomObject dcmObj) {
       return dcmObj.getString(Tag.StudyInstanceUID);
   }

   /**
   This function completes the build of the meta data and returns the results.

   Normalizing the duplicate attributes in each series greatly reduces the size of the meta data
   and improves parsing times significantly.

   This should be called only after all P10 instances for the study have been processed with
   accumulateFile().
    */
   public void finish() {
       for (final Entry<String, Map<Integer, NormalizationCounter>> seriesTagsEntry: tagNormalizerTable.entrySet()) {
           final Series series = metaBinaryPair.getMetadata().getSeries(seriesTagsEntry.getKey());
           if (series == null) {
               throw new RuntimeException(
                       "Normalization: cannot find series " + seriesTagsEntry.getKey() + " in study data.");
           }

           final int nInstancesInSeries = series.instanceCount();
           if (nInstancesInSeries > 1) {
               for (final NormalizationCounter normCtr: seriesTagsEntry.getValue().values()) {
                   if (normCtr.count == nInstancesInSeries) {
                       // Move the attribute to the normalized section...
                       series.putNormalizedInstanceAttribute(normCtr.attr);

                       for (final Instance instanceMeta: Iter.iter(series.instanceIterator())) {
                           instanceMeta.removeAttribute(normCtr.attr.getTag());
                       }
                   }
               }
           }
       }
   }

     /**
     Accumulates the tags for the DICOM P10 instance specified by path into the overall study
     metadata.

     @param dcmPath The path to the DICOM P10 instance. All instances accumulated for a given
     StudyMeta must be part of the same study or an exception will be thrown.
     @throws RuntimeException The instance referred to by the path either doesn't have a study
     instance UID or its study instance UID is not the same as previously accumulated instances.
      */
     public void accumulateFile(final File dcmPath, final DicomObject dcmObj,
             final TransferSyntax transferSyntax) {
         final String dataStudyInstanceUID = extractStudyInstanceUID(dcmObj);
         if (dataStudyInstanceUID != null) {
             if (metaBinaryPair.getMetadata().getStudyInstanceUID() == null) {
                 metaBinaryPair.getMetadata().setStudyInstanceUID(dataStudyInstanceUID);
             }
             else if (!metaBinaryPair.getMetadata().getStudyInstanceUID().equals(dataStudyInstanceUID)) {
                 throw new RuntimeException(dcmPath + " -- study instance uid (" + dataStudyInstanceUID +
                         ") does not match current study (" + metaBinaryPair.getMetadata().getStudyInstanceUID() + ')');
             }
         }

         final String seriesInstanceUID = dcmObj.getString(Tag.SeriesInstanceUID);
         if (seriesInstanceUID == null) {
             throw new RuntimeException(dcmPath + " -- missing series instance uid");
         }

         Series series = metaBinaryPair.getMetadata().getSeries(seriesInstanceUID);
         if (series == null) {
             series = new Series();
             series.setSeriesInstanceUID(seriesInstanceUID);
             metaBinaryPair.getMetadata().putSeries(series);
             assert !tagNormalizerTable.containsKey(seriesInstanceUID);
             tagNormalizerTable.put(seriesInstanceUID, new HashMap<Integer, NormalizationCounter>());
         }

         final Instance instance = new Instance();
         instance.setTransferSyntaxUID(transferSyntax.uid());
         instance.setSOPInstanceUID(dcmObj.getString(Tag.SOPInstanceUID));
         series.putInstance(instance);

         final SpecificCharacterSet charSet = dcmObj.getSpecificCharacterSet();

         // Now, iterate through all items in the object and store each appropriately.
         // This dispatches the Attribute storage to one of the study level, series level
         // or instance-level Attributes sets.
         final int[] emptyTagPath = new int[0];
         for (final DicomElement dcmElement: Iter.iter(dcmObj.datasetIterator())) {
             final int tag = dcmElement.tag();
             if (studyLevelTags.contains(tag)) {
                 if (metaBinaryPair.getMetadata().getAttribute(tag) == null) {
                     handleDICOMElement(dcmPath, charSet, dcmElement, metaBinaryPair.getMetadata(), null, emptyTagPath);
                 }
             }
             else if (seriesLevelTags.contains(tag)) {
                 if (series.getAttribute(tag) == null) {
                     handleDICOMElement(dcmPath, charSet, dcmElement, series, null, emptyTagPath);
                 }
             }
             else {
                 // tagNormalizerTable is only used for instance-level storage...
                 final Map<Integer, NormalizationCounter> seriesNormMap =
                     tagNormalizerTable.get(series.getSeriesInstanceUID());
                 assert seriesNormMap != null;
                 handleDICOMElement(dcmPath, charSet, dcmElement, instance, seriesNormMap, emptyTagPath);
             }
         }
     }

     private void handleDICOMElement(
             final File dcmPath,
             final SpecificCharacterSet charSet,
             final DicomElement dcmElem,
             final AttributeStore attrs,
             final Map<Integer, NormalizationCounter> seriesNormMap,
             final int[] tagPath) {
         final Store store = new Store(dcmPath, charSet, attrs, seriesNormMap, tagPath, dcmElem);
         final VR vr = dcmElem.vr();
         if (vr == null) {
             throw new RuntimeException("Null VR");
         } else if (vr == VR.OW || vr == VR.OB || vr == VR.OF || vr == VR.UN || vr == VR.UN_SIEMENS) {
             //Binary
             store.storeBinary();
         } else if (vr == VR.SQ) {
             store.storeSequence();
         } else {
             //Non-binary, non-sequence
             store.storePlain();
         }
     }

     private final class Store {
         public Store(final File dcmPath, final SpecificCharacterSet charSet, final AttributeStore attrs,
                 final Map<Integer, NormalizationCounter> seriesNormMap, final int[] tagPath,
                 final DicomElement elem) {
             this.dcmPath = dcmPath;
             this.charSet = charSet;
             this.attrs = attrs;
             this.seriesNormMap = seriesNormMap;
             this.tagPath = tagPath;
             this.elem = elem;
         }

         public void storePlain() {
             assert elem != null;
             Attribute attr = null;
             NormalizationCounter normCounter = null;
             final String strVal = getStringValue(elem, charSet);
             if (seriesNormMap != null) {
                 normCounter = seriesNormMap.get(elem.tag());
                 if (normCounter != null) {
                     final Attribute ncAttr = normCounter.attr;
                     if (areEqual(ncAttr, elem, strVal)) {
                         // The data is the same. Instead of creating a new Attribute just to throw it
                         // away shortly, re-use the previously created attribute.
                         attr = ncAttr;
                         ++normCounter.count;
                     }
                 }
             }

             if (attr == null) {
                 attr = newAttr(elem);
                 if (strVal != null) {
                     attr.setVal(strVal);
                 }

                 if (seriesNormMap != null && normCounter == null) {
                     // This is the first occurrence of this particular attribute
                     normCounter = new NormalizationCounter();
                     normCounter.attr = attr;
                     seriesNormMap.put(elem.tag(), normCounter);
                 }
             }

             assert attr != null;
             attrs.putAttribute(attr);
         }

         public void storeSequence() {
             final Attribute attr = newAttr(elem);
             attrs.putAttribute(attr);
             final int[] newTagPath = new int[tagPath.length + 2];
             System.arraycopy(tagPath, 0, newTagPath, 0, tagPath.length);
             newTagPath[tagPath.length] = elem.tag();
             for (int i = 0; i < elem.countItems(); ++i) {
                 newTagPath[tagPath.length + 1] = i;
                 final DicomObject dcmObj = elem.getDicomObject(i);
                 final Item newItem = new Item();
                 attr.addItem(newItem);
                 for (final DicomElement dcmElement: Iter.iter(dcmObj.datasetIterator())) {
                     // Don't use tag normalization in sequence items...
                     handleDICOMElement(dcmPath, charSet, dcmElement, newItem, null, newTagPath);
                 }
             }
         }

         public void storeBinary() {
             assert elem != null;

             final Attribute attr = newAttr(elem);
             assert attr != null;
//             final byte[] binaryData = elem.getBytes();
//             if (binaryData.length > 256) {
                 attr.setBid(metaBinaryPair.getBinaryData().size()); // Before we do the push back...
                 metaBinaryPair.getBinaryData().add(dcmPath, tagPath, elem);
//             } else {
//                 attr.setBytes(binaryData);
//             }
             attrs.putAttribute(attr);
         }

         private final File dcmPath;
         private final SpecificCharacterSet charSet;
         private final AttributeStore attrs;
         private final Map<Integer, NormalizationCounter> seriesNormMap;
         private final int[] tagPath;
         private final DicomElement elem;
     }

     private static boolean areNonValueFieldsEqual(final Attribute a, final DicomElement obj) {
         return a.getTag() == obj.tag() && obj.vr().toString().equals(a.getVr().toString());
     }

     private static String getStringValue(final DicomElement elem, final SpecificCharacterSet charSet) {
         return elem.getValueAsString(charSet, 0);
     }

     private static boolean areEqual(final Attribute a, final DicomElement elem, final String value) {
         if (areNonValueFieldsEqual(a, elem)) {
             if (value == null) {
                 return a.getVal() == null;
             }
             return a.getVal() != null && a.getVal().equals(value);
         }
         return false;
     }

     private static Attribute newAttr(final DicomElement obj) {
         final Attribute attr = new Attribute();
         attr.setTag(obj.tag());
         attr.setVr(obj.vr().toString());
         return attr;
     }

     private static class NormalizationCounter {

         /** The DICOM attribute. */
         Attribute attr;
         /** The number of instances in the series that have
             an identical value for the attribute.
         */
         long count = 1;
     }

     private final Set<Integer> studyLevelTags;
     private final Set<Integer> seriesLevelTags;
     private final Map<String, Map<Integer, NormalizationCounter>> tagNormalizerTable =
         new HashMap<String, Map<Integer, NormalizationCounter>>();
     private final MetaBinaryPair metaBinaryPair;
}
