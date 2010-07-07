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
package org.nema.medical.mint.common.metadata;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.nema.medical.mint.metadata.Attribute;
import org.nema.medical.mint.metadata.Study;
import org.nema.medical.mint.metadata.StudyIO;

public class StudySummaryIO {

	static private final String VELOCITY_PROPERTIES = "velocity.properties";
	static private final String STUDY_ATTRIBUTES = "org/nema/medical/mint/common/metadata/StudySummary.properties";
	static private final String SUMMARY_TEMPLATE = "org/nema/medical/mint/common/metadata/StudySummary.vm";
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
