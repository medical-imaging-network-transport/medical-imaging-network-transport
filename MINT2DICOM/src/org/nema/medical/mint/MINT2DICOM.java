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

	/**
	 * getVR function converts a vrString to a VR object.
	 * @param vrString
	 * @return VR Object corresponding to the given vrString
	 */
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
    	String hexString = Integer.toHexString(newAttribute.getTag());
    	if(hexString.endsWith("0000"))
    	{
    		System.out.println("Group length found, skipping.");
    		return;
    	}
    	
        if(newAttribute.hasSequenceItems() || newAttribute.getVr().equalsIgnoreCase("SQ"))
        {
            //Loop through a sequence, get atrributes and recurse
            DicomElement sequenceObject = currentObject.putSequence(newAttribute.getTag());
            Iterator<Item> sequenceIterator = newAttribute.itemIterator();
            while(sequenceIterator.hasNext())
            {
                Item curItem = sequenceIterator.next();
                Iterator<Attribute> itemAttributeIter = curItem.attributeIterator();
                BasicDicomObject itemConstruct = new BasicDicomObject();
                while(itemAttributeIter.hasNext())
                {
                    Attribute curAttribute = itemAttributeIter.next();
                    MINT2DICOM.insertAttribute(itemConstruct, curAttribute, binaryRoot, useBulkLoading);
                }
                sequenceObject.addDicomObject(itemConstruct);
            }
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
            StudyMetadata mintData = StudyIO.parseFromGPB(metaFile.openStream());
            Iterator<Series> seriesIter = mintData.seriesIterator();
            Iterator<Attribute> studyAttributeIter;
            Iterator<Instance> instanceIter;
            Iterator<Attribute> seriesAttributeIter;
            Iterator<Attribute> seriesNormalizedAttributeIter;
            Iterator<Attribute> instanceAttributeIter;

            Series currentSeries;
            Instance nextInstance;
            BasicDicomObject dicomReconstruction;
			BasicDicomObject dicomInstanceReconstruction;
            DicomOutputStream dcmFileStream;

            String filePath = outputDir + "/" + mintData.getStudyInstanceUID();
            File dcmDir = new File(filePath);
            if(!dcmDir.exists())
            {
            	dcmDir.mkdir();
            }

            File dcmFile;
            // Loop through the metadata.
            for(int i = 0; seriesIter.hasNext(); i++)
            {
                dicomReconstruction = new org.dcm4che2.data.BasicDicomObject();

                studyAttributeIter = mintData.attributeIterator();
                while(studyAttributeIter.hasNext())
                {
                	Attribute nextAttr = studyAttributeIter.next();
                	insertAttribute(dicomReconstruction, nextAttr, binaryDir, useBulkLoading);
                }
                currentSeries = seriesIter.next();

                instanceIter = currentSeries.instanceIterator();
                seriesAttributeIter = currentSeries.attributeIterator();
                while(seriesAttributeIter.hasNext())
                {
                	Attribute nextAttr = seriesAttributeIter.next();
                	insertAttribute(dicomReconstruction, nextAttr, binaryDir, useBulkLoading);
                }

                seriesNormalizedAttributeIter = currentSeries.normalizedInstanceAttributeIterator();
                while(seriesNormalizedAttributeIter.hasNext())
                {
                	Attribute nextAttr = seriesNormalizedAttributeIter.next();
                	insertAttribute(dicomReconstruction, nextAttr, binaryDir, useBulkLoading);
                }

                while(instanceIter.hasNext())
                {	
					dicomInstanceReconstruction = new org.dcm4che2.data.BasicDicomObject();
					dicomInstanceReconstruction = dicomReconstruction;
                    nextInstance = instanceIter.next();
                    
                    //get transfer syntax uid
                    dicomInstanceReconstruction.putString(131088, VR.UI, nextInstance.getTransferSyntaxUID());
                    instanceAttributeIter = nextInstance.attributeIterator();

                    String sopInstanceUID = nextInstance.getSOPInstanceUID();
                    
                    while(instanceAttributeIter.hasNext())
                    {
                    	Attribute nextAttr = instanceAttributeIter.next();
                        insertAttribute(dicomInstanceReconstruction, nextAttr, binaryDir, useBulkLoading);
                    }
                    
                    //Create dicom file
                    dcmFile = new File(filePath, sopInstanceUID + ".dcm");
                    dcmFileStream = new DicomOutputStream(dcmFile);

                    //Write to dicom file
                    dcmFileStream.writeDicomFile(dicomInstanceReconstruction);
                    dcmFileStream.close();
                    i++;
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.exit(2);
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
    		System.exit(1);
    	}

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
				System.exit(2);
			}
        }

        readStudy(metadataFile, binaryDirectory, useBulkLoading, outputDir);
    }
}
