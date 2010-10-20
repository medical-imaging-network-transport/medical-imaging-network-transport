package org.nema.medical.mint;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;

import org.dcm4che2.data.BasicDicomObject;
import org.dcm4che2.data.DicomElement;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.VR;
import org.dcm4che2.io.DicomOutputStream;
import org.nema.medical.mint.metadata.Attribute;
import org.nema.medical.mint.metadata.Instance;
import org.nema.medical.mint.metadata.Item;
import org.nema.medical.mint.metadata.Series;
import org.nema.medical.mint.metadata.StudyMetadata;
import org.nema.medical.mint.metadata.StudyIO;

/**
 * Code to convert MINT to DICOM. Relies heavily on dcm4che2 as a placeholder for the dicom object. 
 * Can support bulk loading and file-by-file reads.
 * The algorithm to walk through the metadata needs to be redesigned. 
 * 
 * @author gsevinc1, jvining1
 *
 */
public class MINT2DICOM 
{
	static MINTBinaryStreamReader binaryReader;
    private static VR getVR(String vrString)
    {
        return VR.valueOf(256*(vrString.toUpperCase().charAt(0)) + (vrString.toUpperCase().charAt(1)));
    }
    
    /**
     * Insert an attibute into the dicom object
     * @param currentObject Dicom object to insert into
     * @param newAttribute Attribute to insert
     * @param binaryRoot The URL to the binary root
     * @param useBulkLoading Whether bulk loading is being used or not
     * @throws IOException
     */
    private static void insertAttribute(DicomObject currentObject, Attribute newAttribute, URL binaryRoot, boolean useBulkLoading) throws IOException
    {
        if(newAttribute.hasSequenceItems())
        {
            //Loop through a sequence, get atrributes and recurse -- bad design on Jack's part but does not recurse more than 2 or 3 times. Need to rewrite.
            DicomElement sequenceObject = currentObject.putSequence(newAttribute.getTag());
            Iterator<Item> sequenceIterator = newAttribute.itemIterator();
            BasicDicomObject sequenceConstruct = new BasicDicomObject();
            while(sequenceIterator.hasNext())
            {
                Item curItem = sequenceIterator.next();
                Iterator<Attribute> itemAttributeIter = curItem.attributeIterator();
                while(itemAttributeIter.hasNext())
                {
                    Attribute curAttribute = itemAttributeIter.next();
                    MINT2DICOM.insertAttribute(sequenceConstruct, curAttribute, binaryRoot, useBulkLoading);
                }
            }
            sequenceObject.addDicomObject(sequenceConstruct);
        }
        else
        {
        	// If binary tags
            if(newAttribute.getVr().equalsIgnoreCase("OW") || newAttribute.getVr().equalsIgnoreCase("UN") || newAttribute.getVr().equalsIgnoreCase("OB"))
            {
            	// And there is supposed to be a binary items
            	if(newAttribute.getBid() >= 0)
            	{
            		// If we are using bulk loading
            		if(useBulkLoading)
            		{
            			//Get the data and put in the object
            			currentObject.putBytes(newAttribute.getTag(), getVR(newAttribute.getVr()), binaryReader.getBinaryData(newAttribute.getBid()));
            		}
            		else
            		{
            			// Create a connection to the binary item files
		                URL binaryAddress = new URL(binaryRoot, newAttribute.getBid() + ".dat");
		                URLConnection temp = binaryAddress.openConnection();
		                
		                int contentLength = temp.getContentLength();
		                if(contentLength > 0)
		                {
		                	//Get the data and put in the object
		                	//Here we again use the binary reader class we created, though we only use the functionality to 
		                	//read binary data from a stream given the content length
			                currentObject.putBytes(newAttribute.getTag(), getVR(newAttribute.getVr()), binaryReader.readBinaryData(temp.getInputStream(), contentLength));
			            }
            		}
            	}
            	else
            	{
            		// Get inline bytes and put into the dicom object
            		currentObject.putBytes(newAttribute.getTag(), getVR(newAttribute.getVr()), newAttribute.getBytes());
            	}
            }
            else
            {
            	// Get the string value and put into the dicom object
            	currentObject.putString(newAttribute.getTag(), getVR(newAttribute.getVr()), newAttribute.getVal());
            }
        }       
    }   
    
    private static void readStudy(URL metaFile, URL binaryDir, boolean useBulkLoading, File outputDir)
    {
    	try 
        {
        	// Read metadata
            StudyMetadata mintData = StudyIO.parseFromXML(metaFile.openStream());
            Iterator<Series> seriesIter = mintData.seriesIterator();
            
            // Loop through the metadata.
            for(int i = 0; seriesIter.hasNext(); i++)
            {
                BasicDicomObject dicomReconstruction = new org.dcm4che2.data.BasicDicomObject();
                
                Iterator<Attribute> studyAttributeIter = mintData.attributeIterator();
                while(studyAttributeIter.hasNext())
                {
                    MINT2DICOM.insertAttribute(dicomReconstruction, studyAttributeIter.next(), binaryDir, useBulkLoading);
                }
                Series currentSeries = seriesIter.next();
                
                Iterator<Instance> instanceIter = currentSeries.instanceIterator();
                Iterator<Attribute> seriesAttributeIter = currentSeries.attributeIterator();
                while(seriesAttributeIter.hasNext())
                {
                    MINT2DICOM.insertAttribute(dicomReconstruction, seriesAttributeIter.next(), binaryDir, useBulkLoading);
                }
                
                Iterator<Attribute> seriesNormalizedAttributeIter = currentSeries.normalizedInstanceAttributeIterator();
                while(seriesNormalizedAttributeIter.hasNext())
                {
                    MINT2DICOM.insertAttribute(dicomReconstruction, seriesNormalizedAttributeIter.next(), binaryDir, useBulkLoading);
                }
                
                while(instanceIter.hasNext())
                {
                    Instance tempInstance = instanceIter.next();
                    
                    //get transfer syntax uid
                    dicomReconstruction.putString(131088, VR.UI, tempInstance.getTransferSyntaxUID());
                    Iterator<Attribute> attributeIter = tempInstance.attributeIterator();
                    
                    while(attributeIter.hasNext())
                    {
                        MINT2DICOM.insertAttribute(dicomReconstruction, attributeIter.next(), binaryDir, useBulkLoading);
                    }
                    
                    //Create dicom file
                    File dcmFile = new File(outputDir, i + ".dcm");
                    DicomOutputStream dcmFileStream = new DicomOutputStream(dcmFile);
                   
                    //Write to dicom file
                    dcmFileStream.writeDicomFile(dicomReconstruction);
                    i++;
                }
            }
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }
    
    private static void printUsage() {
        System.err.println("Usage: MINT2DICOM {ServerURL} {OutputDirectory} {usebulkloading}");
        System.err.println("Converts a MINT study back to the DICOM standard");
        System.err.println("  Inputs:");
        System.err.println("    ServerURL        The URL to the Server (e.g http://10.181..../DICOM/");
        System.err.println("    OutputDirectory  The directory to output generated DICOM files to");
        System.err.println("    (true|false)     A boolean to determine if bulk loading is to be used (true|false)");
    }
    
    /**
     * The main function.
     * @param args
     * @throws MalformedURLException
     */
    public static void main(String[] args) throws MalformedURLException {
    	
    	if(args.length != 3)
    	{
    		printUsage();
    		return;
    	}
        // TODO Auto-generated method stub
        URL root = new URL(args[0]);
        URL binaryDirectory = new URL(args[0] + "binaryitems/");
        URL metadataFile = new URL(root,"metadata.gpb");
        File outputDir = new File(args[1]);
        outputDir.mkdirs();

    	binaryReader = new MINTBinaryStreamReader(binaryDirectory);
    	
        //If we are specified to use bulk loading
        boolean useBulkLoading = Boolean.parseBoolean(args[2]);
        if(useBulkLoading)
        {
        	try 
        	{
				binaryReader.readHttpStream();
			} 
        	catch (IOException e) 
			{
				e.printStackTrace();
			}
        }
        
        readStudy(metadataFile, binaryDirectory, useBulkLoading, outputDir);
        
    }

}