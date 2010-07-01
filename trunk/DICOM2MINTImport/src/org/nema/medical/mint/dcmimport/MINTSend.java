package org.nema.medical.mint.dcmimport;

import org.nema.medical.mint.dcm2mint.MetaBinaryPair;

public interface MINTSend {
	/**
	 * Sends mint data for one complete study off to a consumer.
	 * @param studyData
	 */
	void send(MetaBinaryPair studyData);
}
