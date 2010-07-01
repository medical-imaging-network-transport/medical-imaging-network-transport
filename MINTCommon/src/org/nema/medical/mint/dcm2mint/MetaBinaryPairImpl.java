package org.nema.medical.mint.dcm2mint;

import org.nema.medical.mint.common.metadata.Study;

/**
Type used to contain the result of the build.

This is returned by Dcm2MetaBuilder::finish().
*/
public final class MetaBinaryPairImpl implements MetaBinaryPair {
    /** The study's metadata */
    public final Study metadata = new Study();

    /** The study's binary data */
    public BinaryData binaryData;

	@Override
	public Study getMetadata() {
		return metadata;
	}

	@Override
	public BinaryData getBinaryData() {
		return binaryData;
	}

	public void setBinaryData(final BinaryData binaryData) {
		this.binaryData = binaryData;
	}
}