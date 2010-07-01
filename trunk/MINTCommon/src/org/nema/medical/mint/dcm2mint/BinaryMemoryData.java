/**
 * 
 */
package org.nema.medical.mint.dcm2mint;

import java.util.ArrayList;
import java.util.List;

public final class BinaryMemoryData implements BinaryData {
    /** The study's binary data */
    public final List<byte[]> binaryItems = new ArrayList<byte[]>();

	@Override
	public void add(final byte[] item) {
		binaryItems.add(item);
	}

	@Override
	public byte[] getBinaryItem(final int index) {
		return binaryItems.get(index);
	}

	@Override
	public int size() {
		return binaryItems.size();
	}
}