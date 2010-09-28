package org.nema.medical.mint.studies;

import java.io.IOException;
//import java.io.StringWriter;
import java.io.Writer;
import java.io.FileWriter;
import java.sql.Timestamp;

import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.JiBXException;
import org.junit.Test;
import org.nema.medical.mint.studies.Study;

public class SearchResultsTest {

	@Test
	public void testOutput() throws Exception {
		SearchResults sr = getSearchResults(4);		
		try {
//			Writer writer = new StringWriter();
			Writer writer = new FileWriter("C:/out.txt");
			IBindingFactory bfact = BindingDirectory.getFactory(SearchResults.class);
			IMarshallingContext mctx = bfact.createMarshallingContext();
			mctx.setIndent(2);
			mctx.marshalDocument(sr, "UTF-8", null, writer);
		} catch (JiBXException e) {
			throw new IOException("Exception while marshalling data.",e);
		}		
	}

	private SearchResults getSearchResults(int numStudies) {
		SearchResults sr = new SearchResults(null,"12345",null,null,null,null,null,null,null,null,0,0);
		for (int i=0; i<numStudies; i++) {
			Study study = new Study("uuid" + i, new Timestamp(System.currentTimeMillis()),i);
			sr.addStudy(study);
		}
		return sr;
	}

}
