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
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;
import org.nema.medical.mint.metadata.gpb.MINT2GPB.StudyData;

import com.google.gson.Gson;

public class StudyIO {
	
	static public Study parseFromXML(File file) throws IOException {
		Study study = null;
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
	
	static public Study parseFromXML(InputStream in) throws IOException {
		Study study = null;
		try {
			IBindingFactory bfact = BindingDirectory.getFactory(Study.class);
			IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
			study = (Study)uctx.unmarshalDocument(in, null);
		} catch (JiBXException e) {
			throw new IOException("Exception while unmarshalling data.",e);
		}
		return study;
	}
	
	static public void writeToXML(Study study, File file) throws IOException {
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
	
	static public void writeToXML(Study study, OutputStream out) throws IOException {
		try {
			IBindingFactory bfact = BindingDirectory.getFactory(Study.class);
			IMarshallingContext mctx = bfact.createMarshallingContext();
			mctx.setIndent(2);
			mctx.marshalDocument(study, "UTF-8", null, out);
		} catch (JiBXException e) {
			throw new IOException("Exception while marshalling data.",e);
		}		
	}
	
	static public Study parseFromJSON(File file) throws IOException {
		Study study = null;
		InputStream in = new FileInputStream(file);
		if (file.getName().endsWith(".gz")) {
			in = new GZIPInputStream(in);
		}
		try {
			study = parseFromJSON(in);
		} finally {
			in.close();
		}
		return study;
	}
	
	static public Study parseFromJSON(InputStream in) {
		Reader reader = new InputStreamReader(in);
		Study study = new Gson().fromJson(reader, Study.class);
		return study;
	}
	
	static public void writeToJSON(Study study, File file) throws IOException {
		OutputStream out = new FileOutputStream(file);
		if (file.getName().endsWith(".gz")) {
			out = new GZIPOutputStream(out);
		}
		try {
			writeToJSON(study, out);
		} finally {
			out.close();
		}				
	}	
	
	static public void writeToJSON(Study study, OutputStream out) throws IOException {
		Writer writer = new OutputStreamWriter(out);
		writer.write(new Gson().toJson(study));
		writer.flush();
	}	

	static public Study parseFromGPB(File file) throws IOException {
		Study study = null;
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
	
	static public Study parseFromGPB(InputStream in) throws IOException {
		StudyData data = StudyData.parseFrom(in);		
		return Study.fromGPB(data);		
	}
	
	static public void writeToGPB(Study study, File file) throws IOException {
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
