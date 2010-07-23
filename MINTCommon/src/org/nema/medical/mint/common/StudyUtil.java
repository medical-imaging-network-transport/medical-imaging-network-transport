package org.nema.medical.mint.common;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.nema.medical.mint.metadata.Attribute;
import org.nema.medical.mint.metadata.Instance;
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

    private static final String BINARY_FILE_EXTENSION = "dat";

    /**
     * This method will try to load study information from a metadata file in
     * the provided directory
     *
     * @param directory
     *            This should be a folder with a gpb, xml, or json metadata file
     *            in it.
     * @return Study loaded
     * @throws IOException
     */
    public static Study loadStudy(File directory) throws IOException
    {
        File metadataGPB = new File(directory,"metadata.gpb");
        File metadataXML = new File(directory,"metadata.xml");
        File metadataJSON = new File(directory,"metadata.json");

	Study study;

        if (metadataXML.exists()) {
            study = StudyIO.parseFromXML(metadataXML);
        } else if (metadataGPB.exists()) {
            study = StudyIO.parseFromGPB(metadataGPB);
        } else if (metadataJSON.exists()) {
            study = StudyIO.parseFromJSON(metadataJSON);
        } else {
            throw new RuntimeException("unable to locate metadata file");
        }

        return study;
    }

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

            if(name.endsWith(BINARY_FILE_EXTENSION))
            {
                if(!name.startsWith("metadata"))
                {
                    try
                    {
                        int bid = Integer.parseInt(name.substring(0,name.indexOf('.')));

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
     * occurs.
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

                    int bid = a.getBid();
                    if(bid >= 0)
                    {
                        bid += shiftAmount;
                        a.setBid(bid);
                    }
                }
            }
        }

        return true;
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
    public static boolean applyExcludes(Study currentStudy, Study excludeStudy)
    {
        //Remove study level attributes?
        for(Iterator<Attribute> i = excludeStudy.attributeIterator(); i.hasNext();)
        {
            Attribute attribute = i.next();

            if(isExclude(attribute.getExclude()))
            {
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

            if(isExclude(excludeSeries.getExclude()))
            {
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
                        if(currentSeries != null)
                        {
                            currentSeries.removeNormalizedInstanceAttribute(attribute.getTag());
                        }
                        ii.remove();
                    }
                }

                //Remove instances from series?
                for(Iterator<Instance> ii = excludeSeries.instanceIterator(); ii.hasNext();)
                {
                    Instance excludeInstance = ii.next();
                    Instance currentInstance = null;
                    if(currentSeries != null)
                    {
                        currentInstance = currentSeries.getInstance(excludeInstance.getSOPInstanceUID());
                    }

                    if(isExclude(excludeInstance.getExclude()))
                    {
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
                                if(currentInstance != null)
                                {
                                    currentInstance.removeAttribute(attribute.getTag());
                                }
                                iii.remove();
                            }
                        }
                    }
                }
            }
        }

        return true;
    }

    public static boolean isExclude(String exculde)
    {
        return exculde != null;
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
     * Performs a normalization algorithm on the provided Study
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
        StudyIO.writeToJSON(study, new File(studyFolder, "metadata.json"));
        StudyIO.writeToGPB(study, new File(studyFolder, "metadata.gpb.gz"));
        StudyIO.writeToXML(study, new File(studyFolder, "metadata.xml.gz"));
        StudyIO.writeToJSON(study, new File(studyFolder, "metadata.json.gz"));
//        StudySummaryIO.writeSummaryToXHTML(study, new File(studyFolder, "summary.html"));
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

        //Collect id from attributes
        for(Iterator<Series> i = study.seriesIterator(); i.hasNext();)
        {
            for(Iterator<Instance> ii = i.next().instanceIterator(); ii.hasNext();)
            {
                for(Iterator<Attribute> iii = ii.next().attributeIterator(); iii.hasNext();)
                {
                    Attribute a = iii.next();

                    int bid = a.getBid();
                    if(bid >= 0)
                    {
                        if(!studyBids.add(bid))
                        {
                            //If the set already contained the bid
                            return false;
                        }
                    }
                }
            }
        }

        return studyBids.equals(binaryItemIds);
    }
}
