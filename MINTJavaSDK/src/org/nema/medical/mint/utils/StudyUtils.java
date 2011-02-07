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

import org.nema.medical.mint.datadictionary.MetadataType;
import org.nema.medical.mint.metadata.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.nema.medical.mint.utils.Iter.iter;

/**
 * @author Rex
 */
public final class StudyUtils {

    public static final String INITIAL_VERSION = "0";

    /**
     * This method should pull all data from sourceStudy into destinationStudy
     * and overwrite any existing values in destinationStudy. destinationStudy is considered the old study,
     * sourceStudy is the new study.
     *
     * @param sourceStudy
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
            final Series thisSeries = destinationStudy.getSeries(series.getSeriesInstanceUID());

            if (thisSeries == null) {
                destinationStudy.putSeries(series);
            } else {
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
                        //Check if transfer syntax is existing, update current if it is provided
                        //TODO this code will not work for changing transfer syntaxes for the same instance;
                        //we need to translate the entire instance to the new transfer syntax, then merge,
                        //if we allow this case.
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
     * that have a non-null exclude string. Will return true always unless
     * something catastrophically unexpected occurs.
     *
     * @param currentStudy
     * @param excludeStudy
     * @return true
     */
    public static boolean applyExcludes(StudyMetadata currentStudy, StudyMetadata excludeStudy, Collection<Integer> excludedBinaryIds) {
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
                        collectBidsInInstance(currentInstance, excludedBinaryIds);

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

        return true;
    }

    /**
     * Will return true always unless something catastrophically unexpected
     * occurs.  Assumes that all bids will be within the instances.
     *
     * @param study
     * @param shiftAmount
     * @return true except in case of a catastrophic failure.
     */
    public static boolean shiftStudyBids(StudyMetadata study, int shiftAmount)
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

        return true;
    }

    private static void collectBidsInSeries(Series series,
                                            Collection<Integer> destinationCollection) {
        if (series == null || destinationCollection == null)
            return;

        for (Iterator<Attribute> i = series.attributeIterator(); i.hasNext();) {
            collectBidsInAttribute(i.next(), destinationCollection);
        }

        for (Iterator<Attribute> i = series.normalizedInstanceAttributeIterator(); i.hasNext();) {
            collectBidsInAttribute(i.next(), destinationCollection);
        }

        for (Iterator<Instance> i = series.instanceIterator(); i.hasNext();) {
            collectBidsInInstance(i.next(), destinationCollection);
        }
    }

    private static void collectBidsInInstance(Instance instance,
                                              Collection<Integer> destinationCollection) {
        if (instance == null || destinationCollection == null)
            return;

        for (Iterator<Attribute> i = instance.attributeIterator(); i.hasNext();) {
            collectBidsInAttribute(i.next(), destinationCollection);
        }
    }

    private static void collectBidsInAttribute(Attribute attribute,
                                               Collection<Integer> destinationCollection) {
        if (attribute == null || destinationCollection == null)
            return;

        Queue<Attribute> sequence = new LinkedList<Attribute>();
        sequence.add(attribute);

        while (!sequence.isEmpty()) {
            Attribute curr = sequence.remove();

            int bid = curr.getBid();
            if (bid >= 0) {
                int frameCount = curr.getFrameCount();
                if (frameCount >= 1) {
                    for (int newBid = bid; newBid < (bid + frameCount); newBid++) {
                        destinationCollection.add(newBid);
                    }
                } else {
                    destinationCollection.add(bid);
                }
            }

            //Add children to queue
            for (Iterator<Item> i = curr.itemIterator(); i.hasNext();) {
                for (Iterator<Attribute> ii = i.next().attributeIterator(); ii.hasNext();) {
                    sequence.add(ii.next());
                }
            }
        }
    }

    /**
     * Parse the study and removes any elements that are listed as exclude.
     *
     * @param study
     */
    public static void removeExcludes(StudyMetadata study) {
        //Remove study level attributes?
        for (Iterator<Attribute> i = study.attributeIterator(); i.hasNext();) {
            Attribute attribute = i.next();

            if (isExclude(attribute.getExclude())) {
                i.remove();
            }
        }

        //Remove series from study?
        for (Iterator<Series> i = study.seriesIterator(); i.hasNext();) {
            Series series = i.next();

            if (isExclude(series.getExclude())) {
                i.remove();
            } else {
                //Remove attributes from series?
                for (Iterator<Attribute> ii = series.attributeIterator(); ii.hasNext();) {
                    Attribute attribute = ii.next();

                    if (isExclude(attribute.getExclude())) {
                        ii.remove();
                    }
                }

                //Remove normalized attributes from series?
                for (Iterator<Attribute> ii = series.normalizedInstanceAttributeIterator(); ii.hasNext();) {
                    Attribute attribute = ii.next();

                    if (isExclude(attribute.getExclude())) {
                        ii.remove();
                    }
                }

                //Remove instances from series?
                for (Iterator<Instance> ii = series.instanceIterator(); ii.hasNext();) {
                    Instance instance = ii.next();

                    if (isExclude(instance.getExclude())) {
                        ii.remove();
                    } else {
                        //Remove attributes from instance?
                        for (Iterator<Attribute> iii = instance.attributeIterator(); iii.hasNext();) {
                            Attribute attribute = iii.next();

                            if (isExclude(attribute.getExclude())) {
                                iii.remove();
                            }
                        }
                    }
                }
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
     * instance in that series.  Will always return true unless something
     * catastrophically unexpected occurs.
     *
     * @param study
     * @return true
     */
    public static boolean denormalizeStudy(final StudyMetadata study) {
        for (final Series series: iter(study.seriesIterator())) {
            for (final Iterator<Attribute> attrIter = series.normalizedInstanceAttributeIterator(); attrIter.hasNext();) {
                final Attribute attr = attrIter.next();

                for (final Instance instance: iter(series.instanceIterator())) {
                    instance.putAttribute(attr);
                }

                attrIter.remove();
            }
        }

        return true;
    }


    //"??" is illegal VR used by old SIEMENS modalities (or at least so says DCM4CHE Javadoc)
    private static final Collection<String> binaryVRs =
            new HashSet<String>(Arrays.asList("SQ", "OW", "OB", "OF", "UN", "??"));
    private static boolean isBinaryVR(final String vr) {
        return binaryVRs.contains(vr);
    }

    /**
     * Performs a normalization algorithm on the provided StudyMetadata. The study must be valid except for
     * attributes not having been normalized to the series-level yet.
     *
     * @param study
     * @return true
     */
    public static boolean normalizeStudy(final StudyMetadata study) {
    	final Collection<Attribute> tempNormalizedInstanceAttributeList = new ArrayList<Attribute>();

        //For each series
        for (final Series series: iter(study.seriesIterator())) {
            tempNormalizedInstanceAttributeList.clear();

            if (series.instanceCount() > 1) {
                //For each Instance in the series

                //Prime list by loading all attributes in from first instance
                final Iterator<Instance> ii = series.instanceIterator();
                final Instance firstInstance = ii.next();
                for (final Attribute attr: iter(firstInstance.attributeIterator())) {
                    if (!isBinaryVR(attr.getVr())) {
                	    tempNormalizedInstanceAttributeList.add(attr);
                    }
                }

                //Loop over the rest of the attributes
                while (ii.hasNext()) {
                    final Instance inst = ii.next();

                    //For each attribute in the instance
                    for (final Iterator<Attribute> normAttrIter = tempNormalizedInstanceAttributeList.iterator();
                         normAttrIter.hasNext();) {
                        final Attribute normalA = normAttrIter.next();
                        final Attribute a = inst.getAttribute(normalA.getTag());
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

                    for (final Instance instanceMeta : Iter.iter(series.instanceIterator())) {
                        instanceMeta.removeAttribute(a.getTag());
                    }
                }
            }
        }

        return true;
    }

    /**
     * Returns true
     *
     * @param a
     * @param attr
     * @return true if equals
     */
    public static boolean equalNonBinaryAttributes(final Attribute a, final Attribute attr) {
        //True if both references point to the same object
        if (a == attr) {
            //references are the same (may be null)
            return true;
        } else if (a != null && attr != null) {
            //references are not equal and neither reference is null
            if (a.getTag() != attr.getTag()) {
                //Tags are not equal : false
                return false;
            } else if (a.getVr() != attr.getVr() && (a.getVr() == null || !a.getVr().equals(attr.getVr()))) {
                //VRs are not equal : false
                return false;
            } else if (a.getVal() != attr.getVal() && (a.getVal() == null || !a.getVal().equals(attr.getVal()))) {
                //Value fields not equal : false
                return false;
            } else {
                //Tags, VRs, and Values were all equal : true
                return true;
            }
        } else {
            //a != attr and one of them is null
            return false;
        }
    }

    public static void writeStudy(StudyMetadata study, File studyFolder) throws IOException {
        StudyIO.writeToGPB(study, new File(studyFolder, "metadata.gpb"));
        StudyIO.writeToXML(study, new File(studyFolder, "metadata.xml"));
        StudyIO.writeToGPB(study, new File(studyFolder, "metadata.gpb.gz"));
        StudyIO.writeToXML(study, new File(studyFolder, "metadata.xml.gz"));
        StudyIO.writeSummaryToXML(study, new File(studyFolder, "summary.xml"));
    }

    public static void moveBinaryItems(File jobFolder, File studyBinaryFolder) {
        Iterator<File> iterator = Arrays.asList(jobFolder.listFiles()).iterator();
        while (iterator.hasNext()) {
            File tempfile = iterator.next();

            //Don't move metadata because has no purpose in the destination
            if (!tempfile.getName().startsWith("metadata")) {
                File permfile = new File(studyBinaryFolder, tempfile.getName());
                // just moving the file since the reference implementation
                // is using the same MINT_ROOT for temp and perm storage
                // other implementations may want to copy/delete the file
                // if the temp storage is on a different device
                tempfile.renameTo(permfile);
            }
        }
        jobFolder.delete();
    }

    /**
     * Will delete all files in the jobs folder and then delete the jobs folder
     * itself.
     *
     * @param jobFolder
     */
    public static void deleteFolder(File jobFolder) {
        for (File f : jobFolder.listFiles()) {
            f.delete();
        }

        jobFolder.delete();
    }

    /**
     * @return the version to set a study to on creation
     */
    public static String getBaseVersion() {
        return INITIAL_VERSION;
    }

    /**
     * Generates the next version string after the provided string.  An example of this for a 0 based index version system would be to pass in "3" and get back "4".  Values don't need to be sequential but the 'next' value should at least be bigger than the current.
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
