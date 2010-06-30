package org.nema.medical.mint.common.mint;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;

import com.google.gson.Gson;
import com.vitalimages.contentserver.mint.Mint2Gpb.StudyData;

public class StudyIO {
	
	static public Study parseFromXML(File file) throws IOException {
		Study study = null;
		FileReader reader = new FileReader(file);
		try {
			study = parseFromXML(reader);
		} finally {
			reader.close();
		}
		return study;
	}
	
	static public Study parseFromXML(Reader reader) throws IOException {
		Study study = null;
		try {
			IBindingFactory bfact = BindingDirectory.getFactory(Study.class);
			IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
			study = (Study)uctx.unmarshalDocument(reader, null);
		} catch (JiBXException e) {
			throw new IOException("Exception while unmarshalling data.",e);
		}
		return study;
	}
	
	static public void writeToXML(Study study, File file) throws IOException {
		FileWriter writer = new FileWriter(file);
		try {
			writeToXML(study, writer);
		} finally {
			writer.close();
		}		
	}
	
	static public void writeToXML(Study study, Writer writer) throws IOException {
		try {
			IBindingFactory bfact = BindingDirectory.getFactory(Study.class);
			IMarshallingContext mctx = bfact.createMarshallingContext();
			mctx.setIndent(2);

			mctx.marshalDocument(study, "UTF-8", null, writer);
		} catch (JiBXException e) {
			throw new IOException("Exception while marshalling data.",e);
		}		
	}
	
	static public Study parseFromJSON(File file) throws IOException {
		Study study = null;
		FileReader reader = new FileReader(file);
		try {
			study = parseFromJSON(reader);
		} finally {
			reader.close();
		}
		return study;
	}
	
	static public Study parseFromJSON(Reader reader) {
		Study study = new Gson().fromJson(reader, Study.class);
		return study;
	}
	
	static public void writeToJSON(Study study, File file) throws IOException {
		FileWriter writer = new FileWriter(file);
		try {
			writeToJSON(study, writer);
		} finally {
			writer.close();
		}				
	}	
	
	static public void writeToJSON(Study study, Writer writer) throws IOException {
		writer.write(new Gson().toJson(study));
	}	

	static public Study parseFromGPB(File file) throws IOException {
		Study study = null;
		FileInputStream stream = new FileInputStream(file);
		try {
			study = parseFromGPB(stream);
		} finally {
			stream.close();
		}
		return study;
	}
	
	static public Study parseFromGPB(InputStream in) throws IOException {
		StudyData data = StudyData.parseFrom(in);		
		return Study.fromGPB(data);		
	}
	
	static public void writeToGPB(Study study, File file) throws IOException {
		FileOutputStream out = new FileOutputStream(file);
		try {
			writeToGPB(study, out);
		} finally {
			out.close();
		}		
	}
	
	static public void writeToGPB(Study study, OutputStream out) throws IOException {
		StudyData data = study.toGPB();
		data.writeTo(out);
	}

    // used to convert Attribute.tag int to hex
	static public String int2hex(int tag) {
        return String.format("%08x", tag);
    }

    // used to convert Attribute.tag hex to int
	static public int hex2int(String hex) {
        if (hex.length()>8) throw new NumberFormatException("max value is 32 bytes (unsigned)");
        return (int)Long.parseLong(hex,16);
    }

    // used to convert Attribute.tag int to hex
	static public String int2bid(int tag) {
        return (tag >= 0) ? String.format("%d", tag) : "";
    }

    // used to convert Attribute.tag hex to int
	static public int bid2int(String bid) {
		if (bid == null || bid.length() == 0) {
			return -1;
		} else {
	        return Integer.parseInt(bid);		
		}
    }

}
