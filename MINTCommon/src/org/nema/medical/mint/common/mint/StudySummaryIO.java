package org.nema.medical.mint.common.mint;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

public class StudySummaryIO {

	static private final String VELOCITY_PROPERTIES = "velocity.properties";
	static private final String STUDY_ATTRIBUTES = "com/vitalimages/contentserver/mint/StudySummary.properties";
	static private final String SUMMARY_TEMPLATE = "com/vitalimages/contentserver/mint/StudySummary.vm";
	static private Properties summaryAttrProps;
	static private Properties velocityProps;

	static private Properties getSummaryAttrProps() throws IOException {
		if (summaryAttrProps == null)
			synchronized (StudySummaryIO.class) {
				if (summaryAttrProps == null) {
					summaryAttrProps = getPropertiesFromResource(STUDY_ATTRIBUTES);
				}
			}
		return summaryAttrProps;
	}

	static private Properties getVelocityProperties() throws IOException {
		if (velocityProps == null)
			synchronized (StudySummaryIO.class) {
				if (velocityProps == null) {
					velocityProps = getPropertiesFromResource(VELOCITY_PROPERTIES);
				}
			}
		return velocityProps;
	}

	private static Properties getPropertiesFromResource(String resourceName)
			throws IOException {
		Properties results = new Properties();
		InputStream stream = null;
		try {
			stream = StudySummaryIO.class.getClassLoader().getResourceAsStream(resourceName);
			results.load(stream);
		} finally {
			if (stream != null) stream.close();
		}
		return results;
	}

	static public void writeSummaryToXHTML(Study study, File file)
			throws IOException {
		FileWriter writer = new FileWriter(file);
		try {
			StudySummaryIO.writeSummaryToXHTML(study, writer);
		} finally {
			writer.close();
		}
	}

	static public void writeSummaryToXHTML(Study study, Writer writer)
			throws IOException {
		Properties descriptions = StudySummaryIO.getSummaryAttrProps();
		Properties values = new Properties();
		try {
			VelocityEngine ve = new VelocityEngine();
			ve.init(getVelocityProperties());
			Template t = ve.getTemplate(SUMMARY_TEMPLATE);
			VelocityContext context = new VelocityContext();

			// put all descriptions, and the available values in the context
			context.put("descriptions", descriptions);
			for (Object key : descriptions.keySet()) {
				Attribute attr = study.getAttribute(StudyIO.hex2int(key
						.toString()));
				if (attr != null && attr.getVal() != null) {
					values.put(key, attr.getVal());
				}
			}
			context.put("values", values);
			context.put("studyInstanceUID", study.getStudyInstanceUID());

			/* now render the template */
			t.merge(context, writer);
		} catch (Exception e) {
			throw new RuntimeException("unable to write summary", e);
		}
	}
}
