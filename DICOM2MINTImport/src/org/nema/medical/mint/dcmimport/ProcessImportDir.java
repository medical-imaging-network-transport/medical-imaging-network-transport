package org.nema.medical.mint.dcmimport;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.dcm4che2.io.DicomInputStream;
import org.nema.medical.mint.dcm2mint.BinaryFileData;
import org.nema.medical.mint.dcm2mint.Dcm2MetaBuilder;
import org.nema.medical.mint.dcm2mint.MetaBinaryPairImpl;

public final class ProcessImportDir {

	public ProcessImportDir(final File importDir, final MINTSend mintConsumer) {
		this.importDir = importDir;
		this.mintConsumer = mintConsumer;
	}

	public void run() {
		final Collection<File> resultFiles = new ArrayList<File>();
		findPlainFilesRecursive(importDir, resultFiles);
		for (final File plainFile: resultFiles) {
			try {
				final DicomInputStream dcmStream = new DicomInputStream(plainFile);
				try {
					final String studyUID = Dcm2MetaBuilder.extractStudyInstanceUID(dcmStream);
					final Collection<File> dcmFileData = studyFileMap.get(studyUID);
					if (dcmFileData == null) {
						studyFileMap.put(studyUID, Collections.singleton(plainFile));
					} else {
						dcmFileData.add(plainFile);
					}
				} finally {
					dcmStream.close();
				}
			} catch (final IOException e) {
				//Not a valid DICOM file?!
				System.err.println("Skipping file: " + plainFile);
			}
		}

        final Set<Integer> studyLevelTags = getTags("StudyTags.txt");
        final Set<Integer> seriesLevelTags = getTags("SeriesTags.txt");

        for (final Map.Entry<String, Collection<File>> studyFiles: studyFileMap.entrySet()) {
	        final String studyUID = studyFiles.getKey();
	        assert studyUID != null;

	        final BinaryFileData binaryData = new BinaryFileData();
	        final MetaBinaryPairImpl metaBinaryPair = new MetaBinaryPairImpl();
	        metaBinaryPair.setBinaryData(binaryData);
	        //Constrain processing
	        metaBinaryPair.getMetadata().setStudyInstanceUID(studyUID);
	        final Dcm2MetaBuilder builder = new Dcm2MetaBuilder(studyLevelTags, seriesLevelTags, metaBinaryPair);
	        for (final File instanceFile: studyFiles.getValue()) {
	            try {
	            	final DicomInputStream dcmStream = new DicomInputStream(instanceFile);
	            	try {
	                    builder.accumulateFile(instanceFile, dcmStream);
	            	} finally {
	            		dcmStream.close();
	            	}
	            } catch (final IOException ex) {
					//Not a valid DICOM file?!
					System.err.println("Mostly skipping file (inconsistent data may exist): " + instanceFile);
	            }
	        }
	        builder.finish();
	        mintConsumer.send(metaBinaryPair);
	    }
	}

	private static void findPlainFilesRecursive(final File targetFile, final Collection<File> resultFiles) {
		if (targetFile.isFile()) {
			resultFiles.add(targetFile);
		} else {
			assert targetFile.isDirectory();
			for (final File subFile: targetFile.listFiles()) {
				findPlainFilesRecursive(subFile, resultFiles);
			}
		}
	}

    private Set<Integer> getTags(final String resource) {
        final ClassLoader loader = this.getClass().getClassLoader();
        final Properties properties = new Properties();
        try {
            InputStream stream = loader.getResourceAsStream(resource);
            try {
                properties.load(stream);
            } finally {
                stream.close();
            }
        } catch(final IOException ex) {
            Logger.getLogger(this.getClass()).error("Unable to read tags file", ex);
        }
        final Set<Integer> tagSet = new HashSet<Integer>();
        for (final Object tagStr: properties.keySet()) {
            //Go to long as int is unsigned and insufficient here
            final int intTag = (int)Long.parseLong(tagStr.toString(), 16);
            tagSet.add(intTag);
        }
        return tagSet;
    }

	private final File importDir;
	private final MINTSend mintConsumer;
	private final Map<String, Collection<File>> studyFileMap = new HashMap<String, Collection<File>>();
}
