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

import org.nema.medical.mint.datadictionary.AttributeType;
import org.nema.medical.mint.datadictionary.MetadataType;
import org.nema.medical.mint.metadata.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author Rex
 */
public final class StudyUtils {

    public static final String INITIAL_VERSION = "0";

    public static class ValidationException extends Exception {
        public ValidationException() {
        }

        public ValidationException(Throwable cause) {
            super(cause);
        }

        public ValidationException(String message) {
            super(message);
        }

        public ValidationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
    /**
     * This method should pull all data from the provided study into 'this'
     * study and overwrite any existing values in 'this' study.
     *
     * @param sourceStudy
     */
    public static void mergeStudy(StudyMetadata destinationStudy, StudyMetadata sourceStudy, Collection<Integer> excludedBinaryIds) {
        //Merge study level attributes
        for (Iterator<Attribute> i = sourceStudy.attributeIterator(); i.hasNext();) {
            Attribute attribute = i.next();

            collectBidsInAttribute(destinationStudy.getAttribute(attribute.getTag()), excludedBinaryIds);
            destinationStudy.putAttribute(attribute);
        }

        //Merge series from study
        for (Iterator<Series> i = sourceStudy.seriesIterator(); i.hasNext();) {
            Series series = i.next();
            Series thisSeries = destinationStudy.getSeries(series.getSeriesInstanceUID());

            if (thisSeries != null) {
                //Merge attributes from series
                for (Iterator<Attribute> ii = series.attributeIterator(); ii.hasNext();) {
                    Attribute attribute = ii.next();

                    collectBidsInAttribute(thisSeries.getAttribute(attribute.getTag()), excludedBinaryIds);
                    thisSeries.putAttribute(attribute);
                }

                //Merge instances from series
                for (Iterator<Instance> ii = series.instanceIterator(); ii.hasNext();) {
                    Instance instance = ii.next();
                    Instance thisInstance = thisSeries.getInstance(instance.getSOPInstanceUID());

                    if (thisInstance != null) {
                        //Check if transfer syntax is existing, update current if it is provided
                        String transferSyntaxUID = instance.getTransferSyntaxUID();
                        if (transferSyntaxUID != null && !transferSyntaxUID.isEmpty()) {
                            thisInstance.setTransferSyntaxUID(transferSyntaxUID);
                        }

                        //Merge attributes for instances
                        for (Iterator<Attribute> iii = instance.attributeIterator(); iii.hasNext();) {
                            Attribute attribute = iii.next();

                            collectBidsInAttribute(thisInstance.getAttribute(attribute.getTag()), excludedBinaryIds);
                            thisInstance.putAttribute(attribute);
                        }
                    } else {
                        thisSeries.putInstance(instance);
                    }
                }
            } else {
                destinationStudy.putSeries(series);
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
     * instance in that series.  Will return true always unless something
     * catastrophically unexpected occurs.
     *
     * @param study
     * @return true
     */
    public static boolean denormalizeStudy(StudyMetadata study) {
        for (Iterator<Series> i = study.seriesIterator(); i.hasNext();) {
            Series s = i.next();

            for (Iterator<Attribute> ii = s.normalizedInstanceAttributeIterator(); ii.hasNext();) {
                Attribute a = ii.next();

                for (Iterator<Instance> iii = s.instanceIterator(); iii.hasNext();) {
                    Instance inst = iii.next();

                    inst.putAttribute(a);
                }

                ii.remove();
            }
        }

        return true;
    }

    /**
     * Performs a normalization algorithm on the provided StudyMetadata. Currently this is
     * a very forgiving normalization. We allow study level attributes to come in as instance
     * level attributes and we then normalize them up to the study level using the data dictionary.
     * Ideally this should be changed to force validation before it even gets to this point.
     *
     * //TODO validate where the attributes are and what attributes are present before even getting
     * to the normalization portion.
     * <p/>
     *
     * @param study
     * @param dataDictionary
     * @return true
     * @throws java.io.IOException
     */
    public static boolean normalizeStudy(final StudyMetadata study, final MetadataType dataDictionary)
            throws IOException {
        if (!study.getType().equals(dataDictionary.getType())) {
            throw new RuntimeException("Mismatch of study type " + study.getType()
                    + " and data dictionary type " + dataDictionary.getType());
        }
    	List<AttributeType> seriesAttributes = dataDictionary.getSeriesAttributes().getAttributes();
    	List<AttributeType> studyAttributes = dataDictionary.getStudyAttributes().getAttributes();

    	List<Attribute> tempNormalizedStudyAttributeList = new LinkedList<Attribute>();
    	List<Attribute> tempNormalizedInstanceAttributeList = new LinkedList<Attribute>();

        //For each series
        for (Iterator<Series> i = study.seriesIterator(); i.hasNext();) {
            Series s = i.next();

            tempNormalizedInstanceAttributeList.clear();

            if (s.instanceCount() > 1)
            {
                //For each Instance in the series

                //Prime list by loading all attributes in from first instance
                Iterator<Instance> ii = s.instanceIterator();
                for (Iterator<Attribute> iii = ii.next().attributeIterator(); iii.hasNext();) {
                	tempNormalizedInstanceAttributeList.add(iii.next());
                }

                //Loop over the rest of the attributes
                while (ii.hasNext()) {
                    Instance inst = ii.next();

                    //For each attribute in the instance
                    for (Iterator<Attribute> iii = tempNormalizedInstanceAttributeList.iterator(); iii.hasNext();) {
                        Attribute normalA = iii.next();
                        Attribute a = inst.getAttribute(normalA.getTag());

                        if (!equalAttributes(a, normalA))
                            iii.remove();
                    }
                }

                /*
                 * All attributes left in the list were found in all instances
                 * for this series.
                 */
                for (Attribute a : tempNormalizedInstanceAttributeList) {

                	//If the normalized attribute should exist at the series level then we will put it
                	//there, otherwise it will go to the normalized instance attribute section.
                	boolean putInSeries = false;
                	for(AttributeType at : seriesAttributes)
                	{
                		if(StudyIO.hex2int(at.getTag()) == a.getTag())
                		{
                			putInSeries = true;
                		}
                	}

                	if(putInSeries)
                	{
                		s.putAttribute(a);
                	}
                	else
                	{
                		s.putNormalizedInstanceAttribute(a);
                	}

                    for (final Instance instanceMeta : Iter.iter(s.instanceIterator())) {
                        instanceMeta.removeAttribute(a.getTag());
                    }
                }
            }
        }

        //Whatever is left in the normalized attribute section of each series needs to be checked
        //to see if it should live at the study level. Every series normalized instance attribute section must
        //contain that same identical attribute for it to get moved to the study level and then only if
        //the data dictionary says it belongs at that level.
        Iterator<Series> seriesIter = study.seriesIterator();
        if(seriesIter.hasNext())
        {
        	//Get the first series and prime the tempNormalizedStudyAttributeList with any normalized instance
        	//attributes that should be normalized even further up to that level.
        	Series s = seriesIter.next();
        	 for(Iterator<Attribute> attributeIter = s.normalizedInstanceAttributeIterator(); attributeIter.hasNext();)
             {
             	Attribute a = attributeIter.next();
            	for(AttributeType at : studyAttributes)
             	{
             		if(a.getTag() == StudyIO.hex2int(at.getTag()))
             		{
             			tempNormalizedStudyAttributeList.add(a);
             		}
             	}
             }

        	 //Now for the rest of the series, see if any of the normalized study attributes
        	 //from the first series DONT exist exactly the same in their normalized instance attributes.
        	 //If series doesn't have the attribute exactly the same as it is in the normalized
        	 //study attributes list it gets removed from the study normalized list since it has to occur the same in
        	 //every series to be a candidate for normalization.
        	 while(seriesIter.hasNext())
        	 {
        		Series ss = seriesIter.next();
        		for(Iterator<Attribute> i = tempNormalizedStudyAttributeList.iterator(); i.hasNext();)
        		{
        			Attribute a = i.next();
        			if(!equalAttributes(a, ss.getNormalizedInstanceAttribute(a.getTag())))
        			{
        				tempNormalizedStudyAttributeList.remove(a);
        			}
        		}
        	 }

        	 for(Attribute a : tempNormalizedStudyAttributeList)
        	 {
        		 study.putAttribute(a);
        		 for (final Series instanceMeta : Iter.iter(study.seriesIterator())) {
                     instanceMeta.removeNormalizedInstanceAttribute(a.getTag());
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
    public static boolean equalAttributes(Attribute a, Attribute attr) {
        boolean equal;

        //True if both references point to the same object
        if (a == attr) {
            //references are the same (may be null)
            equal = true;
        } else if (a != null && attr != null) {
            //references are not equal and neither reference is null
            if (a.getTag() != attr.getTag()) {
                //Tags are not equal : false
                equal = false;
            } else if (!((a.getVr() == attr.getVr()) || (a.getVr() != null && a.getVr().equals(attr.getVr())))) {
                //VR are not equal : false
                equal = false;
            } else if (!(a.getBid() == attr.getBid())) {
                //Binary IDs not equal : false
                equal = false;
            } else if (a.getBid() >= 0) {
                //Binary IDs valid but frame counts not equal : false
                equal = (a.getFrameCount() == attr.getFrameCount());
            } else if (!((a.getVal() == attr.getVal()) || (a.getVal() != null && a.getVal().equals(attr.getVal())))) {
                //Value fields not equal : false
                equal = false;
            } else {
                //Tags, VRs, Binary IDs, and Values were all equal : true
                equal = true;
            }
        } else {
            //a != attr and one of them is null
            equal = false;
        }

        return equal;
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

    public static interface AttributeAction {
        void doAction(Attribute attribute) throws ValidationException;
    }

    private static void allAttributeTraverser(final StudyMetadata study, final AttributeAction action)
            throws ValidationException {
        attributeStoreTraverser(study, action);

        for (final Series series: Iter.iter(study.seriesIterator())) {
            attributeStoreTraverser(series, action);

            for (final Attribute attr: Iter.iter(series.normalizedInstanceAttributeIterator())) {
                hierarchicalAttributeTraverser(attr, action);
            }

            for (final Instance instance: Iter.iter(series.instanceIterator())) {
                attributeStoreTraverser(instance, action);
            }
        }
    }

    private static void attributeStoreTraverser(final AttributeStore attributes, final AttributeAction action)
            throws ValidationException {
        for (final Attribute attr: Iter.iter(attributes.attributeIterator())) {
            hierarchicalAttributeTraverser(attr, action);
        }
    }

    private static void hierarchicalAttributeTraverser(final Attribute attribute, final AttributeAction action)
            throws ValidationException {
        action.doAction(attribute);

        for (final Item item: Iter.iter(attribute.itemIterator())) {
            for (final Attribute attr: Iter.iter(item.attributeIterator())) {
                hierarchicalAttributeTraverser(attr, action);
            }
        }
    }

    public static void validateStudyMetadata(final StudyMetadata study) throws ValidationException {
        validateInstanceCounts(study);
        if (study.getType().equals("DICOM")) {
    	    validateDICOMTransferSyntax(study);

            allAttributeTraverser(study, new AttributeAction() {
                @Override
                public void doAction(final Attribute attribute) throws ValidationException {
                    if (attribute.getVr() == null) {
                        throw new ValidationException("Missing VR value for Attribute \""
                                + tagString(attribute.getTag())
                                + "\" in Study " + study.getStudyInstanceUID());
                    }
                }
            });
        }
    }

    /*
     * Verifies that the transferSyntaxUID value in <instance sopInstanceUID="123" transferSyntaxUID="456" >
     * is equal to the DICOM attribute <attr tag="00020010" vr="UI" val="456"> value.
     */
    private static void validateDICOMTransferSyntax(final StudyMetadata study) throws ValidationException {
    	//loop over each series
    	for (final Series s: Iter.iter(study.seriesIterator())) {
    		final Attribute normalizedTransferSyntaxAttribute = s.getNormalizedInstanceAttribute(0x00020010);
    		final String normalizedTransferSyntax =
                    (normalizedTransferSyntaxAttribute != null) ? normalizedTransferSyntaxAttribute.getVal() : null;
    		final boolean transferSyntaxNormalized = (normalizedTransferSyntax != null);

            //loop over the instances
    		for (final Instance instance: Iter.iter(s.instanceIterator())) {
    			//This is the instance level transfer syntax element attribute
    			//<instance sopInstanceUID="123" transferSyntaxUID="456" >
    			final String instanceTransferSyntax = instance.getTransferSyntaxUID();
    			//This is the dicom tag attribute for transfer syntax
    			//<attr tag="00020010" vr="UI" val="456">
    			final String instanceAttributeTransferSyntax =
                        transferSyntaxNormalized ? normalizedTransferSyntax : instance.getValueForAttribute(0x00020010);
    			//The two different transfer syntaxes must be equal in every case or the entire study is invalid.
    			if (!instanceTransferSyntax.equalsIgnoreCase(instanceAttributeTransferSyntax)) {
    				throw new ValidationException("Mismatch of Transfer Syntax " + instanceTransferSyntax
                            + " specified for SOP Instance UID " + instance.getSOPInstanceUID()
                            + " and transfer syntax " + instanceAttributeTransferSyntax
                            + " from instance file storage attributes");
    			}
    		}
    	}
    }

    /**
     * This method will return true if the given study has passed all
     * implemented validation checks. This is validation for studies that are
     * being 'created' or 'updated' it is not expected that this validation with
     * pass on studies already written to disk.
     *
     * @param study
     * @param binaryItemIds
     * @return true iff the given study has passed all implemented validation checks
     */
    public static void validateStudy(StudyMetadata study, final Collection<Integer> binaryItemIds)
            throws ValidationException {
        validateBinaryItemsReferences(study, binaryItemIds);
        validateStudyMetadata(study);
        //add other validation here
    }

    private static void validateInstanceCounts(final StudyMetadata study) throws ValidationException {
        int totalInstanceCount = 0;
        for (final Series series: Iter.iter(study.seriesIterator())) {
            final int seriesInstanceCount = series.getInstanceCount();
            if (seriesInstanceCount != series.instanceCount()) {
                throw new ValidationException("Instance count mismatch for series " + series.getSeriesInstanceUID());
            }
            totalInstanceCount += seriesInstanceCount;
        }

        final int studyInstanceCount = study.getInstanceCount();
        if (studyInstanceCount != totalInstanceCount) {
            throw new ValidationException("Instance count mismatch for study " + study.getStudyInstanceUID());
        }
    }

    /**
     * This method will determine if there the bid references from the study to
     * binary items are all existing and that there are no excess binary items.
     * The expect usage of this method is during study create and study update
     * to ensure the passed in metadata and binary items are in agreement (i.e.,
     * no unreferenced binary items and no bids in the metadata that point to
     * nothing).
     *
     * @param study
     * @param binaryItemIds
     * @return Returns true if no violations were detected.
     */
    public static void validateBinaryItemsReferences(final StudyMetadata study,
                                                     final Collection<Integer> binaryItemIds)
            throws ValidationException {
        final Collection<Integer> studyBids = new HashSet<Integer>();

          /*
           * Collect ids from attributes
           *
           * NOTE: StudyMetadata has a method that does almost exactly this except that
           * this method detects repeated bids (which is not allowed). Do not just
           * replace the below with that method without first considering this
           * fact.
           */

        allAttributeTraverser(study, new AttributeAction() {
            @Override
            public void doAction(final Attribute attribute) throws ValidationException {
                final int bid = attribute.getBid();
                if (bid >= 0) {
                    final int frameCount = attribute.getFrameCount();
                    if (frameCount >= 1) {
                        for (int newBid = bid; newBid < (bid + frameCount); newBid++) {
                            if (!studyBids.add(newBid)) {
                                //If the set already contained the bid, should be unique reference
                                throw new ValidationException("Duplicate binary ID in attribute \""
                                        + tagString(attribute.getTag()) + " of study "
                                        + study.getStudyInstanceUID());
                            }
                        }
                    } else {
                        if (!studyBids.add(bid)) {
                            //If the set already contained the bid, should be unique reference
                            throw new ValidationException("Duplicate binary ID in attribute \""
                                    + tagString(attribute.getTag()) + " of study "
                                    + study.getStudyInstanceUID());
                        }
                    }
                }
            }
        });


        if (!studyBids.equals(binaryItemIds)) {
            throw new ValidationException("Mismatch of available binary data items " + binaryItemIds
                    + " and binary IDs in metadata " + studyBids + " for study " + study.getStudyInstanceUID());
        }
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
        return String.format("%1$08x", tag);
    }
}
