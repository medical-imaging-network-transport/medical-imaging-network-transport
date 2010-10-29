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

package org.nema.medical.mint.datadictionary;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;


public class DataDictionaryIO {

	static public MetadataType parseFromXML(File file){
		MetadataType dicomDocument = null;
		try
		{
			
			InputStream in = new FileInputStream(file);
			try {
				dicomDocument = parseFromXML(in);
			} finally {
				in.close();
			}
		}
		catch(IOException ex)
		{
			//TODO - log exception
		}
		return dicomDocument;
	}
	
	static public MetadataType parseFromXML(InputStream in) throws IOException {
		MetadataType dicomDocument = null;
		try {
			IBindingFactory bfact = BindingDirectory.getFactory(MetadataType.class);
			IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
			dicomDocument = (MetadataType)uctx.unmarshalDocument(in, null);
		} catch (JiBXException e) {
			throw new IOException("Exception while unmarshalling data.",e);
		}
		return dicomDocument;
	}
	
	
	static public void writeToXML(MetadataType dataDictionary, File file) throws IOException {
		OutputStream out = new FileOutputStream(file);
		try {
			writeToXML(dataDictionary, out);
		} finally {
			out.close();
		}		
	}
	
	static public void writeToXML(MetadataType dataDictionary, OutputStream out) throws IOException {
		try {
			IBindingFactory bfact = BindingDirectory.getFactory(MetadataType.class);
			IMarshallingContext mctx = bfact.createMarshallingContext();
			mctx.setIndent(2);
			mctx.marshalDocument(dataDictionary, "UTF-8", null, out);
		} catch (JiBXException e) {
			throw new IOException("Exception while marshalling data.",e);
		}		
	}
	
}
