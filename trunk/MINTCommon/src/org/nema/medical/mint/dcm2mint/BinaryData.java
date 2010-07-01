/**
 * 
 */
package org.nema.medical.mint.dcm2mint;

public interface BinaryData {
	byte[] getBinaryItem(int index);
	int size();
	void add(byte[] item);
}