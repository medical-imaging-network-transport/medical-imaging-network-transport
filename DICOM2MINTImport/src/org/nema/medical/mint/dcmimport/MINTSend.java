package org.nema.medical.mint.dcmimport;

import org.nema.medical.mint.dcm2mint.Dcm2MetaBuilder.MetaBinaryFilePair;

public interface MINTSend {
	/**
	 * Sends mint data for one complete study off to a consumer.
	 * @param studyData
	 */
	void send(MetaBinaryFilePair studyData);
}
