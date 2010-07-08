package org.nema.medical.mint.common;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.nema.medical.mint.metadata.Attribute;
import org.nema.medical.mint.metadata.Instance;
import org.nema.medical.mint.metadata.Series;
import org.nema.medical.mint.metadata.Study;
import org.nema.medical.mint.metadata.StudyIO;

/**
 * 
 * @author rrobin20
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
	 * @return
	 * @throws IOException
	 */
	public static Study loadStudy(File directory) throws IOException
	{
		File metadataGPB = new File(directory,"metadata.gpb");
		File metadataXML = new File(directory,"metadata.xml");
		File metadataJSON = new File(directory,"metadata.json");

		Study study = null;

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
	 * This method will looks for .dat files and will try to perform a 
	 * parseInt on the front of the files.  The largest int parsed will be 
	 * returned.  -1 will be returned when not .dat files are encountered.
	 * 
	 * This method assumes that the .dat files start with an integer and will
	 * need to be re thought if what binary item IDs are is redesigned.
	 * 
	 * @param directory
	 * @return
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
	 * @return
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
				int bid = Integer.parseInt(name.substring(0,name.indexOf('.')));
				
				bid += shiftAmount;
				
				String newName = bid + "." + BINARY_FILE_EXTENSION;
				
				//It is a binary file, shift it!
				f.renameTo(new File(directory, newName));
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
	 * @return
	 */
	public static boolean applyExcludes(Study currentStudy, Study excludeStudy)
	{
		//Remove study level attributes?
		for(Iterator<Attribute> i = excludeStudy.attributeIterator(); i.hasNext();)
		{
			Attribute attribute = i.next();
			
			if(attribute.getExclude() != null)
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
			
			if(excludeSeries.getExclude() != null)
			{
				//Non null exclude string means exclude the series from the study
				currentStudy.removeSeries(excludeSeries.getSeriesInstanceUID());
				i.remove();
			}else{
				//Remove attributes from series?
				for(Iterator<Attribute> ii = excludeSeries.attributeIterator(); ii.hasNext();)
				{
					Attribute attribute = ii.next();
					
					if(attribute.getExclude() != null)
					{
						//Non null exclude string means remove it
						currentSeries.removeAttribute(attribute.getTag());
						i.remove();
					}
				}
				
				//Remove normalized attributes from series?
				for(Iterator<Attribute> ii = excludeSeries.normalizedInstanceAttributeIterator(); ii.hasNext();)
				{
					Attribute attribute = ii.next();
					
					if(attribute.getExclude() != null)
					{
						//Non null exclude string means remove it
						currentSeries.removeNormalizedInstanceAttribute(attribute.getTag());
						i.remove();
					}
				}
				
				//Remove instances from series?
				for(Iterator<Instance> ii = i.next().instanceIterator(); ii.hasNext();)
				{
					Instance excludeInstance = ii.next();
					Instance currentInstance = currentSeries.getInstance(excludeInstance.getSopInstanceUID(), excludeInstance.getTransferSyntaxUID());
					
					if(excludeInstance.getExclude() != null)
					{
						currentSeries.removeInstance(excludeInstance.getSopInstanceUID(), excludeInstance.getTransferSyntaxUID());
						ii.remove();
					}else{
						//Remove attributes from instance?
						for(Iterator<Attribute> iii = excludeInstance.attributeIterator(); iii.hasNext();)
						{
							Attribute attribute = iii.next();
							
							if(attribute.getExclude() != null)
							{
								//Non null exclude string means remove it
								currentInstance.removeAttribute(attribute.getTag());
								i.remove();
							}
						}
					}
				}
			}
		}
		
		return true;
	}
	
	/**
	 * Will go through each series and push all normalized attributes into each
	 * instance in that series.  Will return true always unless something
	 * catastrophically unexpected occurs.
	 * 
	 * @param study
	 * @return
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
	 * @return
	 */
	public static boolean normalizeStudy(Study study)
	{
		return true;
	}
}
