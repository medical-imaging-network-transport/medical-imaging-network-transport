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
package org.nema.medical.mint.metadata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.codec.binary.Base64;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;
import org.nema.medical.mint.metadata.GPB.StudyData;

public class StudyIO {
	
    /**
     * This method will try to load study information from a metadata file in
     * the provided directory
     *
     * @param directory This should be a folder with a gpb or xml metadata file
     *                  in it (or a gzip version of one of those)
     * @return Study loaded
     * @throws IOException if unable to read files from the directory
     */
    public static StudyMetadata loadStudy(File directory) throws IOException {
        ArrayList<File> possibleFiles = new ArrayList<File>();
        possibleFiles.add(new File(directory, "metadata.gpb.gz"));
        possibleFiles.add(new File(directory, "metadata.gpb"));
        possibleFiles.add(new File(directory, "metadata.xml.gz"));
        possibleFiles.add(new File(directory, "metadata.xml"));

        StudyMetadata study = null;
        for (File file : possibleFiles) {
            if (file.exists()) {
                study = StudyIO.parseFile(file);
                break;
            }
        }

        if (study == null) {
            throw new RuntimeException("unable to locate metadata file");
        }

        return study;
    }
    
    static public StudyMetadata parseFile(File file) throws IOException {
		String name = file.getName();
		if (name.endsWith(".xml") || name.endsWith(".xml.gz")) {
			return parseFromXML(file);
		}
		else if (name.endsWith(".gpb") || name.endsWith(".gpb.gz")) {
			return parseFromGPB(file);
		}
		else throw new IllegalArgumentException("unknown file type" + file);
	}
	
	static public void writeFile(StudyMetadata study, File file) throws IOException {
		String name = file.getName();
		if (name.endsWith(".xml") || name.endsWith(".xml.gz")) {
			writeToXML(study,file);
		}
		else if (name.endsWith(".gpb") || name.endsWith(".gpb.gz")) {
			writeToGPB(study,file);
		}
		else throw new IllegalArgumentException("unknown file type" + file);
		
	}
	
	static public StudyMetadata parseFromXML(File file) throws IOException {
		StudyMetadata study = null;
		InputStream in = new FileInputStream(file);
		if (file.getName().endsWith(".gz")) {
			in = new GZIPInputStream(in);
		}
		try {
			study = parseFromXML(in);
		} finally {
			in.close();
		}
		return study;
	}
	
	static public StudyMetadata parseFromXML(InputStream in) throws IOException {
		StudyMetadata study = null;
		try {
			IBindingFactory bfact = BindingDirectory.getFactory("metadata", StudyMetadata.class);
			IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
			study = (StudyMetadata)uctx.unmarshalDocument(in, null);
		} catch (JiBXException e) {
			throw new IOException("Exception while unmarshalling data.",e);
		}
		return study;
	}
	
	static public void writeToXML(StudyMetadata study, File file) throws IOException {
		OutputStream out = new FileOutputStream(file);
		if (file.getName().endsWith(".gz")) {
			out = new GZIPOutputStream(out);
		}
		try {
			writeToXML(study, out);
		} finally {
			out.close();
		}		
	}
	
	static public void writeToXML(StudyMetadata study, OutputStream out) throws IOException {
		try {
			String xmlStylesheet = "type=\"text/xsl\" href=\"style.xsl\"";

			IBindingFactory bfact = BindingDirectory.getFactory("metadata", StudyMetadata.class);
			IMarshallingContext mctx = bfact.createMarshallingContext();
			mctx.setIndent(2);
    		mctx.startDocument("UTF-8", null, out);
    		mctx.getXmlWriter().writePI("xml-stylesheet", xmlStylesheet);
    		mctx.marshalDocument(study);
    		mctx.endDocument();
		} catch (JiBXException e) {
			throw new IOException("Exception while marshalling data.",e);
		}		
	}
	
	static public void writeSummaryToXML(StudyMetadata study, File file) throws IOException {
		OutputStream out = new FileOutputStream(file);
		if (file.getName().endsWith(".gz")) {
			out = new GZIPOutputStream(out);
		}
		try {
			writeSummaryToXML(study, out);
		} finally {
			out.close();
		}		
	}
	
	static public void writeSummaryToXML(StudyMetadata study, OutputStream out) throws IOException {
		try {
			String xmlStylesheet = "type=\"text/xsl\" href=\"style.xsl\"";

			IBindingFactory bfact = BindingDirectory.getFactory("summary", StudyMetadata.class);
			IMarshallingContext mctx = bfact.createMarshallingContext();
			mctx.setIndent(2);
    		mctx.startDocument("UTF-8", null, out);
    		mctx.getXmlWriter().writePI("xml-stylesheet", xmlStylesheet);
    		mctx.marshalDocument(study);
    		mctx.endDocument();
		} catch (JiBXException e) {
			throw new IOException("Exception while marshalling data.",e);
		}		
	}
	
	static public StudyMetadata parseFromGPB(File file) throws IOException {
		StudyMetadata study = null;
		InputStream in = new FileInputStream(file);
		if (file.getName().endsWith(".gz")) {
			in = new GZIPInputStream(in);
		}
		try {
			study = parseFromGPB(in);
		} finally {
			in.close();
		}
		return study;
	}
	
	static public StudyMetadata parseFromGPB(InputStream in) throws IOException {
		StudyData data = StudyData.parseFrom(in);		
		return StudyMetadata.fromGPB(data);
	}
	
	static public void writeToGPB(StudyMetadata study, File file) throws IOException {
		OutputStream out = new FileOutputStream(file);
		if (file.getName().endsWith(".gz")) {
			out = new GZIPOutputStream(out);
		}
		try {
			writeToGPB(study, out);
		} finally {
			out.close();
		}		
	}
	
	static public void writeToGPB(StudyMetadata study, OutputStream out) throws IOException {
		StudyData data = study.toGPB();
		data.writeTo(out);
	}

    // used to convert int to hex for a DICOM tag
	static public String int2hex(int tag) {
        return String.format("%08X", tag);
    }

    // used to convert hex to int for a DICOM tag
	static public int hex2int(String hex) {
        if (hex.length()>8) throw new NumberFormatException("max value is 32 bits (unsigned)");
        return (int)Long.parseLong(hex,16);
    }

    // used to convert Attribute.bid int to String
	static public String int2bid(int tag) {
        return (tag >= 0) ? String.format("%d", tag) : "";
    }

    // used to convert Attribute.bid String to int
	static public int bid2int(String bid) {
		if (bid == null || bid.length() == 0) {
			return -1;
		} else {
	        return Integer.parseInt(bid);		
		}
    }
	
	// used to convert Attribute.bsize int to String
	static public String int2bsize(int tag) {
        return (tag != -1) ? String.format("%d", tag & 0xFFFFFFFFL) : "";
    }

    // used to convert Attribute.bsize String to int
	static public int bsize2int(String bsize) {
		if (bsize == null || bsize.length() == 0) {
			return -1;
		} else {
	        return (int) Long.parseLong(bsize);
		}
    }

    // used to convert base64 to byte[]
	static public byte[] base64decode(String string) {
		return Base64.decodeBase64(string.getBytes());
    }

    // used to convert byte[] to base64
	static public String base64encode(byte[] bytes) {
		return new String(Base64.encodeBase64(bytes));
    }
	
    // used to convert Metadata.version int to String
	static public String int2version(int version) {
        return (version >= 0) ? String.format("%d", version) : "";
    }

    // used to convert Metadata.version String to int
	static public int version2int(String version) {
		if (version == null || version.length() == 0) {
			return -1;
		} else {
	        return Integer.parseInt(version);		
		}
    }
	
	static public String boolean2ret(boolean ret) {
		return ret ? "RET" : "";
	}
	
	static public boolean ret2boolean(String ret) {
		if(null == ret || ret.length() == 0) {
			return false;
		} else {
			return true;
		}
	}
}
