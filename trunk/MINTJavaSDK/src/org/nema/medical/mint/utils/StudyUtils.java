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
package org.nema.medical.mint.utils;

import org.nema.medical.mint.metadata.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.nema.medical.mint.utils.Iter.iter;

/**
 * @author Rex
 */
//TODO comment from code review: in general these methods are very verbose and could use a heap of refactoring for
//reuse and clarity. the code itself does not seem to be bad, but if there was bad code in there it would be hard to
//find
public final class StudyUtils {

    public static final String INITIAL_VERSION = "0";

    /**
     * This method should pull all data from sourceStudy into destinationStudy
     * and overwrite any existing values in destinationStudy. destinationStudy is considered the old study,
     * sourceStudy is the new study. Both studies must be denormalized.
     *
     * @param destinationStudy
     * @param sourceStudy
     * @param excludedBinaryIds
     */
    public static void mergeStudy(final StudyMetadata destinationStudy, final StudyMetadata sourceStudy,
                                  final Collection<Integer> excludedBinaryIds) {
        //Merge study level attributes
        for (final Attribute attribute: iter(sourceStudy.attributeIterator())) {
            collectBidsInAttribute(destinationStudy.getAttribute(attribute.getTag()), excludedBinaryIds);
            destinationStudy.putAttribute(attribute);
        }

        //Merge series from study
        for (final Series series: iter(sourceStudy.seriesIterator())) {
            if (series.normalizedInstanceAttributeIterator().hasNext()) {
                throw new RuntimeException("New study not denormalized");
            }
            final Series thisSeries = destinationStudy.getSeries(series.getSeriesInstanceUID());

            if (thisSeries == null) {
                destinationStudy.putSeries(series);
            } else {
                if (thisSeries.normalizedInstanceAttributeIterator().hasNext()) {
                    throw new RuntimeException("Old study not denormalized");
                }
                //Merge attributes from series
                for (final Attribute attribute: iter(series.attributeIterator())) {
                    collectBidsInAttribute(thisSeries.getAttribute(attribute.getTag()), excludedBinaryIds);
                    thisSeries.putAttribute(attribute);
                }

                //Merge instances from series
                for (final Instance instance: iter(series.instanceIterator())) {
                    final Instance thisInstance = thisSeries.getInstance(instance.getSOPInstanceUID());

                    if (thisInstance == null) {
                        thisSeries.putInstance(instance);
                    } else {
                        //Check if transfer syntax is existing, update current if it is provided.
                        //This code will not work for changing transfer syntaxes for the same instance;
                        //we would need to translate the entire instance to the new transfer syntax, then merge,
                        //if we allowed this case, to at least account for big endian vs. little endian cases.
                        final String transferSyntaxUID = instance.getTransferSyntaxUID();
                        if (transferSyntaxUID != null && !transferSyntaxUID.isEmpty()) {
                            thisInstance.setTransferSyntaxUID(transferSyntaxUID);
                        }

                        //Merge attributes for instances
                        for (final Attribute attribute: iter(instance.attributeIterator())) {
                            collectBidsInAttribute(thisInstance.getAttribute(attribute.getTag()), excludedBinaryIds);
                            thisInstance.putAttribute(attribute);
                        }
                    }
                }
            }
        }
    }

    /**
     * Iterates over excludeStudy and will remove from both studies any elements
     * that have a non-null exclude string.
     *
     * @param currentStudy
     * @param excludeStudy
     * @param excludedBinaryIds
     */
    public static void applyExcludes(StudyMetadata currentStudy, StudyMetadata excludeStudy, Collection<Integer> excludedBinaryIds) {
        //Remove study level attributes?
        for (Iterator<Attribute> i = excludeStudy.attributeIterator(); i.hasNext();) {
            Attribute attribute = i.next();

            if (isExclude(attribute.getExclude())) {
                collectBidsInAttribute(currentStudy.getAttribute(attribute.getTag()), excludedBinaryIds);

                //Non null exclude string means remove it
                currentStudy.removeAttribute(attribute.getTag());
                i.remove();
            }
        }

        //Remove series from study?
        for (Iterator<Series> i = excludeStudy.seriesIterator(); i.hasNext();) {
            Series excludeSeries = i.next();
            Series currentSeries = currentStudy.getSeries(excludeSeries.getSeriesInstanceUID());

            if (currentSeries == null)
                continue;

            if (isExclude(excludeSeries.getExclude())) {
                collectBidsInSeries(currentSeries, excludedBinaryIds);

                //Non null exclude string means exclude the series from the study
                currentStudy.removeSeries(excludeSeries.getSeriesInstanceUID());
                i.remove();
            } else {
                //Remove attributes from series?
                for (Iterator<Attribute> ii = excludeSeries.attributeIterator(); ii.hasNext();) {
                    Attribute attribute = ii.next();

                    if (isExclude(attribute.getExclude())) {
                        collectBidsInAttribute(currentSeries.getAttribute(attribute.getTag()), excludedBinaryIds);

                        //Non null exclude string means remove it
                        currentSeries.removeAttribute(attribute.getTag());
                        ii.remove();
                    }
                }

                //Remove normalized attributes from series?
                for (Iterator<Attribute> ii = excludeSeries.normalizedInstanceAttributeIterator(); ii.hasNext();) {
                    Attribute attribute = ii.next();

                    if (isExclude(attribute.getExclude())) {
                        //Non null exclude string means remove it
                        collectBidsInAttribute(currentSeries.getNormalizedInstanceAttribute(attribute.getTag()), excludedBinaryIds);

                        currentSeries.removeNormalizedInstanceAttribute(attribute.getTag());

                        ii.remove();
                    }
                }

                //Remove instances from series?
                for (Iterator<Instance> ii = excludeSeries.instanceIterator(); ii.hasNext();) {
                    Instance excludeInstance = ii.next();
                    Instance currentInstance = null;

                    currentInstance = currentSeries.getInstance(excludeInstance.getSOPInstanceUID());

                    if (currentInstance == null)
                        continue;

                    if (isExclude(excludeInstance.getExclude())) {
                        collectBidsInAttributeContainer(currentInstance, excludedBinaryIds);

                        currentSeries.removeInstance(excludeInstance.getSOPInstanceUID());
                        ii.remove();
                    } else {
                        //Remove attributes from instance?
                        for (Iterator<Attribute> iii = excludeInstance.attributeIterator(); iii.hasNext();) {
                            Attribute attribute = iii.next();

                            if (isExclude(attribute.getExclude())) {
                                //Non null exclude string means remove it
                                collectBidsInAttribute(currentInstance.getAttribute(attribute.getTag()), excludedBinaryIds);

                                currentInstance.removeAttribute(attribute.getTag());

                                iii.remove();
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Assumes that all bids will be within the instances.
     *
     * @param study
     * @param shiftAmount
     */
    public static void shiftStudyBids(StudyMetadata study, int shiftAmount)
    {
        for(Iterator<Series> i = study.seriesIterator(); i.hasNext();)
        {
            for(Iterator<Instance> ii = i.next().instanceIterator(); ii.hasNext();)
            {
                for(Iterator<Attribute> iii = ii.next().attributeIterator(); iii.hasNext();)
                {
                    Attribute a = iii.next();

                    Queue<Attribute> sequence = new LinkedList<Attribute>();
                    sequence.add(a);

                    while(!sequence.isEmpty())
                    {
                    	Attribute curr = sequence.remove();

                    	//Check if bid exists
                    	int bid = curr.getBid();
                        if(bid >= 0)
                        {
                            bid += shiftAmount;
                            curr.setBid(bid);
                            // frameCount is relative to bid, no need to change it
                        }

                        //Add children to queue
                    	for(Iterator<Item> iiii = curr.itemIterator(); iiii.hasNext();)
                    	{
                    		for(Iterator<Attribute> iiiii = iiii.next().attributeIterator(); iiiii.hasNext();)
                    		{
                    			sequence.add(iiiii.next());
                    		}
                    	}
                    }
                }
            }
        }
    }

    //TODO there shouldn't be any binary ids at the series level; remove this method once this is clear in MINT group
    private static void collectBidsInSeries(final Series series, final Collection<Integer> destinationCollection) {
        if (series == null || destinationCollection == null) {
            return;
        }

        collectBidsInAttributeContainer(series, destinationCollection);

        for (final Attribute attr: iter(series.normalizedInstanceAttributeIterator())) {
            collectBidsInAttribute(attr, destinationCollection);
        }

        for (final Instance inst: iter(series.instanceIterator())) {
            collectBidsInAttributeContainer(inst, destinationCollection);
        }
    }

    private static void collectBidsInAttributeContainer(final AttributeContainer attributes,
                                                        final Collection<Integer> destinationCollection) {
        if (attributes == null || destinationCollection == null) {
            return;
        }

        for (final Attribute attr: iter(attributes.attributeIterator())) {
            collectBidsInAttribute(attr, destinationCollection);
        }
    }

    private static void collectBidsInAttribute(final Attribute attribute,
                                               final Collection<Integer> destinationCollection) {
        if (attribute == null || destinationCollection == null) {
            return;
        }

        final Queue<Attribute> sequence = new LinkedList<Attribute>();
        sequence.add(attribute);

        while (!sequence.isEmpty()) {
            final Attribute curr = sequence.remove();

            final int bid = curr.getBid();
            if (bid >= 0) {
                final int frameCount = curr.getFrameCount();
                if (frameCount > 1) {
                    for (int newBid = bid; newBid < (bid + frameCount); ++newBid) {
                        destinationCollection.add(newBid);
                    }
                } else {
                    destinationCollection.add(bid);
                }
            }

            //Add children to queue
            for (final Item item: iter(curr.itemIterator())) {
                for (final Attribute attr: iter(item.attributeIterator())) {
                    sequence.add(attr);
                }
            }
        }
    }

    /**
     * Parse the study and removes any elements that are listed as exclude.
     *
     * @param study
     */
    public static void removeStudyExcludes(final StudyMetadata study) {
        //Remove study level attributes?
        removeExcludes(study.attributeIterator());
        //Remove series from study?
        removeExcludes(study.seriesIterator());

        //Descend into remaining series
        for (final Series series: iter(study.seriesIterator())) {
            //Remove attributes from series?
            removeExcludes(series.attributeIterator());
            //Remove normalized attributes from series?
            removeExcludes(series.normalizedInstanceAttributeIterator());
            //Remove instance from series?
            removeExcludes(series.instanceIterator());

            //Descend into remaining instances
            for (final Instance instance: iter(series.instanceIterator())) {
                //Remove attributes from instance?
                removeExcludes(instance.attributeIterator());
            }
        }
    }

    private static void removeExcludes(final Iterator<? extends Excludable> iter) {
        while (iter.hasNext()) {
            final Excludable element = iter.next();

            if (isExclude(element.getExclude())) {
                iter.remove();
            }
        }
    }

    /**
     * @param exclude
     * @return true iff the provided string is a valid "exclude" string
     */
    public static boolean isExclude(String exclude) {
        return exclude != null;
    }

    /**
     * Will go through each series and push all normalized attributes into each
     * instance in that series.
     *
     * @param study
     */
    public static void denormalizeStudy(final StudyMetadata study) {
        for (final Series series: iter(study.seriesIterator())) {
            for (final Iterator<Attribute> attrIter = series.normalizedInstanceAttributeIterator(); attrIter.hasNext();) {
                final Attribute attr = attrIter.next();

                for (final Instance instance: iter(series.instanceIterator())) {
                    instance.putAttribute(attr);
                }

                attrIter.remove();
            }
        }
    }

    //"??" is "illegal" VR used by old SIEMENS modalities (or at least so says DCM4CHE Javadoc)
    private static final Collection<String> binaryVRs =
            new HashSet<String>(Arrays.asList("SQ", "OW", "OB", "OF", "UN", "??"));

    static boolean isBinaryVR(final String vr) {
        return binaryVRs.contains(vr);
    }

    static boolean isNonBinaryFloatVR(final String vr) {
        return "FL".equals(vr) || "FD".equals(vr);
    }

    /**
     * Reverse array
     * @param value array to be reversed
     */
    static void reverse(final byte[] value) {
        final int byteCnt = value.length;
        //Ignore center byte if an odd number of bytes
        final int halfByteCnt = byteCnt >> 1;
        for (int i = 0; i < halfByteCnt; ++i) {
            final int otherIndex = byteCnt - 1 - i;
            final byte buf = value[i];
            value[i] = value[otherIndex];
            value[otherIndex] = buf;
        }
    }

    /**
     * Convert attribute to little endian if it is FD/FL with big endian binary representation
     * @param attr attribute to convert
     * @param hasBigEndianTransferSyntax indicated endianness of attribute's binary representation
     * @return the potentially converted attribute.
     */
    public static Attribute standardizedAttribute(final Attribute attr, final boolean hasBigEndianTransferSyntax) {
        if (!hasBigEndianTransferSyntax || !isNonBinaryFloatVR(attr.getVr()) || attr.getBytes() == null
                || attr.getBytes().length <= 1) {
            return attr;
        } else {
            final Attribute normalizedAttr;
            try {
                normalizedAttr = (Attribute) attr.clone();
            } catch(final CloneNotSupportedException e) {
                //Should never happen
                throw new RuntimeException(e);
            }
            reverse(normalizedAttr.getBytes());
            return normalizedAttr;
        }
    }

    /**
     * Performs a normalization algorithm on the provided StudyMetadata. The study must be valid except for
     * attributes not having been normalized to the series-level yet.
     *
     * @param study
     */
    public static void normalizeStudy(final StudyMetadata study) {
    	final Collection<Attribute> tempNormalizedInstanceAttributeList = new ArrayList<Attribute>();

        //For each series
        for (final Series series: iter(study.seriesIterator())) {
            tempNormalizedInstanceAttributeList.clear();

            if (series.instanceCount() > 1) {
                //For each Instance in the series

                //Prime list by loading all attributes in from first instance
                final Iterator<Instance> ii = series.instanceIterator();
                {
                    final Instance instance = ii.next();
                    for (final Attribute attr: iter(instance.attributeIterator())) {
                        final String vr = attr.getVr();
                        //Do not normalize binary VRs or non-binary float VRs
                        if (!isBinaryVR(vr) && !isNonBinaryFloatVR(vr)) {
                	        tempNormalizedInstanceAttributeList.add(attr);
                        }
                    }
                }

                //Loop over the rest of the attributes
                while (ii.hasNext()) {
                    final Instance instance = ii.next();

                    //For each attribute in the instance
                    for (final Iterator<Attribute> normAttrIter = tempNormalizedInstanceAttributeList.iterator();
                         normAttrIter.hasNext();) {
                        final Attribute normalA = normAttrIter.next();
                        final Attribute a = instance.getAttribute(normalA.getTag());
                        if (a == null || isBinaryVR(a.getVr()) || !equalNonBinaryAttributes(a, normalA)) {
                            normAttrIter.remove();
                        }
                    }
                }

                /*
                 * All attributes left in the list were found in all instances
                 * for this series.
                 */
                for (final Attribute a: tempNormalizedInstanceAttributeList) {
                    series.putNormalizedInstanceAttribute(a);

                    for (final Instance instanceMeta : iter(series.instanceIterator())) {
                        instanceMeta.removeAttribute(a.getTag());
                    }
                }
            }
        }
    }

    /**
     * If passed-in attributes have inline binary data (as in the case of floats), the binary
     * data must have been normalized to a single endianness (typically little endian) by
     * the caller to correctly determine equality.
     *
     * @param a1
     * @param a2
     * @return true if equal
     */
    public static boolean equalNonBinaryAttributes(final Attribute a1, final Attribute a2) {
        return a1 == a2 || a1 != null && a1.equals(a2);
    }

    public static void writeStudy(StudyMetadata study, File studyFolder) throws IOException {
        StudyIO.writeToGPB(study, new File(studyFolder, "metadata.gpb"));
        StudyIO.writeToXML(study, new File(studyFolder, "metadata.xml"));
        StudyIO.writeToGPB(study, new File(studyFolder, "metadata.gpb.gz"));
        StudyIO.writeToXML(study, new File(studyFolder, "metadata.xml.gz"));
        StudyIO.writeSummaryToXML(study, new File(studyFolder, "summary.xml"));
    }

    /**
     * @return the version to set a study to on creation
     */
    public static String getBaseVersion() {
        return INITIAL_VERSION;
    }

    /**
     * Generates the next version string after the provided string.
     * An example of this for a 0 based index version system would be to pass in "3" and get back "4".
     * Values don't need to be sequential but the 'next' value should at least be bigger than the current.
     *
     * @param current
     * @return the next version string
     */
    public static String getNextVersion(String current) {
        String result = null;

        try {
            long l = Long.parseLong(current);
            result = Long.toString(l + 1L);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Failed to generate next version number.");
        }

        return result;
    }

    public static String tagString(final int tag) {
        return String.format("%1$08X", tag);
    }
}
