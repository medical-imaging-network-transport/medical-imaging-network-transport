package org.nema.medical.mint.dcmimport;

import org.nema.medical.mint.dcm2mint.Dcm2MetaBuilder.MetaBinaryPair;

public interface MINTSend {
	/**
	 * Sends mint data for one complete study off to a handling instance.
	 * @param studyData
	 */
	void send(MetaBinaryPair studyData);
}
