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
package org.nema.medical.mint.common;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.apache.log4j.Logger;
import org.nema.medical.mint.metadata.Attribute;
import org.nema.medical.mint.metadata.Instance;
import org.nema.medical.mint.metadata.Item;
import org.nema.medical.mint.metadata.Series;
import org.nema.medical.mint.metadata.Study;
import org.nema.medical.mint.metadata.StudyIO;
import org.nema.medical.mint.util.Iter;

/**
 * @author Rex
 *
 */
public final class StudyUtil {
    private static final Logger LOG = Logger.getLogger(StudyUtil.class);

    public static final String BINARY_FILE_EXTENSION = "dat";
    public static final String EXCLUDED_BINARY_FILE_EXTENSION = "exclude";
    
    public static final String INITIAL_VERSION = "0";

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
     * by the provided the shiftAmount and will also update the provided Study
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
    public static boolean shiftItemIds(Study study, File binaryDirectory, int shiftAmount)
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
            success &= shiftStudyBids(study, shiftAmount);

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
     * Will return true always unless something catastrophically unexpected
     * occurs.  Assumes that all bids will be within the instances.
     *
     * @param study
     * @param shiftAmount
     * @return
     */
    private static boolean shiftStudyBids(Study study, int shiftAmount)
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
    
    /**
     * This method should pull all data from the provided study into 'this'
     * study and overwrite any existing values in 'this' study.
     *
     * @param sourceStudy
     */
    public static void mergeStudy(Study destinationStudy, Study sourceStudy, Collection<Integer> excludedBinaryIds)
    {
        //Merge study level attributes
        for(Iterator<Attribute> i = sourceStudy.attributeIterator(); i.hasNext();)
        {
            Attribute attribute = i.next();

            collectBidsInAttribute(destinationStudy.getAttribute(attribute.getTag()), excludedBinaryIds);
            destinationStudy.putAttribute(attribute);
        }

        //Merge series from study
        for(Iterator<Series> i = sourceStudy.seriesIterator(); i.hasNext();)
        {
            Series series = i.next();
            Series thisSeries = destinationStudy.getSeries(series.getSeriesInstanceUID());

            if(thisSeries != null)
            {
                //Merge attributes from series
                for(Iterator<Attribute> ii = series.attributeIterator(); ii.hasNext();)
                {
                    Attribute attribute = ii.next();

                    collectBidsInAttribute(thisSeries.getAttribute(attribute.getTag()), excludedBinaryIds);
                    thisSeries.putAttribute(attribute);
                }

                //Merge instances from series
                for(Iterator<Instance> ii = series.instanceIterator(); ii.hasNext();)
                {
                    Instance instance = ii.next();
                    Instance thisInstance = thisSeries.getInstance(instance.getSOPInstanceUID());

                    if(thisInstance != null)
                    {
                        //Check if transfer syntax is existing, update current if it is provided
                        String transferSyntaxUID = instance.getTransferSyntaxUID();
                        if(transferSyntaxUID != null && !transferSyntaxUID.isEmpty())
                        {
                            thisInstance.setTransferSyntaxUID(transferSyntaxUID);
                        }

                        //Merge attributes for instances
                        for(Iterator<Attribute> iii = instance.attributeIterator(); iii.hasNext();)
                        {
                            Attribute attribute = iii.next();

                            collectBidsInAttribute(thisInstance.getAttribute(attribute.getTag()), excludedBinaryIds);
                            thisInstance.putAttribute(attribute);
                        }
                    }else{
                        thisSeries.putInstance(instance);
                    }
                }
            }else{
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
    public static boolean applyExcludes(Study currentStudy, Study excludeStudy, Collection<Integer> excludedBinaryIds)
    {
        //Remove study level attributes?
        for(Iterator<Attribute> i = excludeStudy.attributeIterator(); i.hasNext();)
        {
            Attribute attribute = i.next();

            if(isExclude(attribute.getExclude()))
            {
            	collectBidsInAttribute(currentStudy.getAttribute(attribute.getTag()), excludedBinaryIds);
            	
                //Non null exclude string means remove it
                currentStudy.removeAttribute(attribute.getTag());
                i.remove();
            }
        }

        //Remove series from study?
        for(Iterator<Series> i = excludeStudy.seriesIterator(); i.hasNext();)
        {
            Series excludeSeries = i.next();
            Series currentSeries = currentStudy.getSeries(excludeSeries.getSeriesInstanceUID());

            if(currentSeries == null)
            	continue;
            
            if(isExclude(excludeSeries.getExclude()))
            {
            	collectBidsInSeries(currentSeries, excludedBinaryIds);
            	
                //Non null exclude string means exclude the series from the study
                currentStudy.removeSeries(excludeSeries.getSeriesInstanceUID());
                i.remove();
            }else{
                //Remove attributes from series?
                for(Iterator<Attribute> ii = excludeSeries.attributeIterator(); ii.hasNext();)
                {
                    Attribute attribute = ii.next();

                    if(isExclude(attribute.getExclude()))
                    {
                    	collectBidsInAttribute(currentSeries.getAttribute(attribute.getTag()), excludedBinaryIds);
                    	
                        //Non null exclude string means remove it
                        currentSeries.removeAttribute(attribute.getTag());
                        ii.remove();
                    }
                }

                //Remove normalized attributes from series?
                for(Iterator<Attribute> ii = excludeSeries.normalizedInstanceAttributeIterator(); ii.hasNext();)
                {
                    Attribute attribute = ii.next();

                    if(isExclude(attribute.getExclude()))
                    {
                        //Non null exclude string means remove it
                        collectBidsInAttribute(currentSeries.getNormalizedInstanceAttribute(attribute.getTag()), excludedBinaryIds);
                        	
                        currentSeries.removeNormalizedInstanceAttribute(attribute.getTag());
                        
                        ii.remove();
                    }
                }

                //Remove instances from series?
                for(Iterator<Instance> ii = excludeSeries.instanceIterator(); ii.hasNext();)
                {
                    Instance excludeInstance = ii.next();
                    Instance currentInstance = null;
                    
                    currentInstance = currentSeries.getInstance(excludeInstance.getSOPInstanceUID());
                    
                    if(currentInstance == null)
                    	continue;

                    if(isExclude(excludeInstance.getExclude()))
                    {
                    	collectBidsInInstance(currentInstance, excludedBinaryIds);
                    	
                        currentSeries.removeInstance(excludeInstance.getSOPInstanceUID());
                        ii.remove();
                    }else{
                        //Remove attributes from instance?
                        for(Iterator<Attribute> iii = excludeInstance.attributeIterator(); iii.hasNext();)
                        {
                            Attribute attribute = iii.next();

                            if(isExclude(attribute.getExclude()))
                            {
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
    
	private static void collectBidsInSeries(Series series,
			Collection<Integer> destinationCollection) {
		if(series == null || destinationCollection == null)
			return;
		
		for(Iterator<Attribute> i = series.attributeIterator(); i.hasNext();)
        {
			collectBidsInAttribute(i.next(), destinationCollection);
        }
		
		for(Iterator<Attribute> i = series.normalizedInstanceAttributeIterator(); i.hasNext();)
        {
			collectBidsInAttribute(i.next(), destinationCollection);
        }
		
		for(Iterator<Instance> i = series.instanceIterator(); i.hasNext();)
        {
			collectBidsInInstance(i.next(), destinationCollection);
        }
	}
	
	private static void collectBidsInInstance(Instance instance,
			Collection<Integer> destinationCollection) {
		if(instance == null || destinationCollection == null)
			return;
		
		for(Iterator<Attribute> i = instance.attributeIterator(); i.hasNext();)
        {
			collectBidsInAttribute(i.next(), destinationCollection);
        }
	}

	private static void collectBidsInAttribute(Attribute attribute,
			Collection<Integer> destinationCollection) {
		if(attribute == null || destinationCollection == null)
			return;
		
		Queue<Attribute> sequence = new LinkedList<Attribute>();
        sequence.add(attribute);
        
        while(!sequence.isEmpty())
        {
        	Attribute curr = sequence.remove();
        	
        	int bid = curr.getBid();
            if(bid >= 0)
            {
                int frameCount = curr.getFrameCount();
                if (frameCount >= 1)
                {
                    for (int newBid = bid; newBid < (bid + frameCount); newBid++)
                    {
                        destinationCollection.add(newBid);
                    }
                } else {
                    destinationCollection.add(bid);
                }
            }
        	
            //Add children to queue
        	for(Iterator<Item> i = curr.itemIterator(); i.hasNext();)
        	{
        		for(Iterator<Attribute> ii = i.next().attributeIterator(); ii.hasNext();)
        		{
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
    public static void removeExcludes(Study study)
    {
    	//Remove study level attributes?
        for(Iterator<Attribute> i = study.attributeIterator(); i.hasNext();)
        {
            Attribute attribute = i.next();

            if(isExclude(attribute.getExclude()))
            {
            	i.remove();
            }
        }

        //Remove series from study?
        for(Iterator<Series> i = study.seriesIterator(); i.hasNext();)
        {
            Series series = i.next();

            if(isExclude(series.getExclude()))
            {
            	i.remove();
            }else{
                //Remove attributes from series?
                for(Iterator<Attribute> ii = series.attributeIterator(); ii.hasNext();)
                {
                    Attribute attribute = ii.next();

                    if(isExclude(attribute.getExclude()))
                    {
                    	ii.remove();
                    }
                }

                //Remove normalized attributes from series?
                for(Iterator<Attribute> ii = series.normalizedInstanceAttributeIterator(); ii.hasNext();)
                {
                    Attribute attribute = ii.next();

                    if(isExclude(attribute.getExclude()))
                    {
                        ii.remove();
                    }
                }

                //Remove instances from series?
                for(Iterator<Instance> ii = series.instanceIterator(); ii.hasNext();)
                {
                    Instance instance = ii.next();
                    
                    if(isExclude(instance.getExclude()))
                    {
                    	ii.remove();
                    }else{
                        //Remove attributes from instance?
                        for(Iterator<Attribute> iii = instance.attributeIterator(); iii.hasNext();)
                        {
                            Attribute attribute = iii.next();

                            if(isExclude(attribute.getExclude()))
                            {
                                iii.remove();
                            }
                        }
                    }
                }
            }
        }
    }

	/**
	 * Determines if the provided string is a valid "exclude" string.
	 * 
	 * @param exclude
	 * @return
	 */
    public static boolean isExclude(String exclude)
    {
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
    public static boolean denormalizeStudy(Study study)
    {
        for(Iterator<Series> i = study.seriesIterator(); i.hasNext();)
        {
            Series s = i.next();

            for(Iterator<Attribute> ii = s.normalizedInstanceAttributeIterator(); ii.hasNext();)
            {
                Attribute a = ii.next();

                for(Iterator<Instance> iii = s.instanceIterator(); iii.hasNext();)
                {
                    Instance inst = iii.next();

                    inst.putAttribute(a);
                }

                ii.remove();
            }
        }

        return true;
    }

    /**
     * Performs a normalization algorithm on the provided Study.
     * 
     * TODO make this algorithm normalize using information from a data dictionary.
     *
     * @param study
     * @return true
     */
    public static boolean normalizeStudy(Study study)
    {
        List<Attribute> list = new LinkedList<Attribute>();

        //For each series
        for(Iterator<Series> i = study.seriesIterator(); i.hasNext();)
        {
            Series s = i.next();

            list.clear();

            if(s.instanceCount() > 1)
            {
                //For each Instance in the series

                //Prime list by loading all attributes in from first instance
                Iterator<Instance> ii = s.instanceIterator();
                for(Iterator<Attribute> iii = ii.next().attributeIterator(); iii.hasNext();)
                {
                    list.add(iii.next());
                }

                //Loop over the rest of the attributes
                while(ii.hasNext())
                {
                    Instance inst = ii.next();

                    //For each attribute in the instance
                    for(Iterator<Attribute> iii = list.iterator(); iii.hasNext();)
                    {
                        Attribute normalA = iii.next();
                        Attribute a = inst.getAttribute(normalA.getTag());

                        if(!equalAttributes(a, normalA))
                            iii.remove();
                    }
                }

                /*
                 * All attributes left in the list were found in all instances
                 * for this series.
                 */
                for(Attribute a : list)
                {
                    // Move the attribute to the normalized section...
                    s.putNormalizedInstanceAttribute(a);

                    for (final Instance instanceMeta : Iter.iter(s.instanceIterator()))
                    {
                        instanceMeta.removeAttribute(a.getTag());
                    }
                }
            }
        }

        return true;
    }

    /**
     * Returns true
     * @param a
     * @param attr
     * @return true if equals
     */
    public static boolean equalAttributes(Attribute a, Attribute attr) {
        boolean equal;

        //True if both references point to the same object
        if(a == attr)
        {
            //references are the same (may be null)
            equal = true;
        }else if(a != null && attr != null){
            //references are not equal and neither reference is null
            if(a.getTag() != attr.getTag()){
                //Tags are not equal : false
                equal = false;
            }else if(!((a.getVr() == attr.getVr()) || (a.getVr() != null && a.getVr().equals(attr.getVr())))) {
                //VR are not equal : false
                equal = false;
            }else if(!(a.getBid() == attr.getBid())) {
                //Binary IDs not equal : false
                equal = false;
            }else if(a.getBid() >= 0) {
                //Binary IDs valid but frame counts not equal : false
                equal = (a.getFrameCount() == attr.getFrameCount());
            }else if(!((a.getVal() == attr.getVal()) || (a.getVal() != null && a.getVal().equals(attr.getVal())))) {
                //Value fields not equal : false
                equal = false;
            }else{
                //Tags, VRs, Binary IDs, and Values were all equal : true
                equal = true;
            }
        }else{
            //a != attr and one of them is null
            equal = false;
        }

        return equal;
    }

    public static void writeStudy(Study study, File studyFolder) throws IOException
    {
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
            if(!tempfile.getName().startsWith("metadata"))
            {
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
    public static void deleteFolder(File jobFolder)
    {
        for(File f : jobFolder.listFiles())
        {
            f.delete();
        }

        jobFolder.delete();
    }

    /**
     * This method will find the largest int named file in the changelog root
     * directory and will return a file to root/'the largest int + 1'. Calls
     * mkdirs on the file to return before returning it.
     *
     * @param changelogRoot
     * @return
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
     * @return
     */
    public static boolean validateStudy(Study study, File binaryFolder)
    {
        boolean result = true;

        result = result && validateBinaryItemsReferences(study, binaryFolder);
        //add other validation here and && it with result

        return result;
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
     * @param binaryFolder
     * @return Returns true if no violations were detected.
     */
    public static boolean validateBinaryItemsReferences(Study study, File binaryFolder)
    {
        Set<Integer> studyBids = new HashSet<Integer>(), binaryItemIds = new HashSet<Integer>();

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

		/*
		 * Collect id from attributes
		 * 
		 * NOTE: Study has a method that does almost exactly this except that
		 * this method detects repeated bids (which is not allowed). Do not just
		 * replace the below with that method without first considering this
		 * fact.
		 */
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
                    	
                    	int bid = curr.getBid();
                        if(bid >= 0)
                        {
                            int frameCount = curr.getFrameCount();
                            if (frameCount >= 1)
                            {
                                for (int newBid = bid; newBid < (bid + frameCount); newBid++)
                                {
                                    if (!studyBids.add(newBid))
                                    {
                                        //If the set already contained the bid, should be unique reference
                                        return false;
                                    }
                                }
                            } else {
                                if(!studyBids.add(bid))
                                {
                                    //If the set already contained the bid, should be unique reference
                                    return false;
                                }
                            }
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

        return studyBids.equals(binaryItemIds);
    }
    
	/**
	 * Will attempt to rename each file that is bid.dat where possible bids are
	 * passed in the excludedBids collection to bid.exclude. Will return -1 if
	 * successful and will return the bid of the rename that failed if something
	 * went wrong.
	 * 
	 * @param existingBinaryFolder
	 * @param excludedBids
	 * @return
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
    
    /**
     * Returns the version to set a study to on creation.
     * @return
     */
    public static String getBaseVersion()
    {
    	return INITIAL_VERSION;
    }
    
    /**
     * Generates the next version string after the provided string.  An example of this for a 0 based index version system would be to pass in "3" and get back "4".  Values don't need to be sequential but the 'next' value should at least be bigger than the current.
     * @param current
     * @return
     */
    public static String getNextVersion(String current)
    {
    	String result = null;
    	
    	try
    	{
    		long l = Long.parseLong(current);
    		
    		result = Long.toString(l + 1L);
    	}catch(NumberFormatException e){
    		LOG.error("Failed to generate next version number.");
    	}
    	
    	return result;
    }
}
