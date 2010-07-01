/**
 * 
 */
package org.nema.medical.mint.dcm2mint;

import org.nema.medical.mint.common.metadata.Study;

public interface MetaBinaryPair {
	Study getMetadata();
	BinaryData getBinaryData();
}