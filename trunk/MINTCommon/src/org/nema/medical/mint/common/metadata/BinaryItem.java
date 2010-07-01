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

import org.nema.medical.mint.common.metadata.gpb.MINT2GPB.BulkItemData;
import org.nema.medical.mint.common.metadata.gpb.MINT2GPB.ECompressionMethod;

public class BinaryItem {

	public static enum Compression {
		NONE, ZIP
	};

	// the byte offset in the binary file where this item is located
	public long offset;
	// the type of compression if any
	public Compression compression = Compression.NONE;
	// the full uncompressed size of the item
	public long size;

	public long getOffset() {
		return offset;
	}

	public void setOffset(long offset) {
		this.offset = offset;
	}

	public Compression getCompression() {
		return compression;
	}

	public void setCompression(Compression compression) {
		this.compression = compression;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	//
	// Google Protocol Buffer support - package protection intentional
	//
	static BinaryItem fromGPB(BulkItemData itemData) {
		BinaryItem item = new BinaryItem();
		item.offset = itemData.getOffset();
		item.size = itemData.getExpandedLengthBytes();
		switch (itemData.getCompressionMethod()) {
		case kZip:
			item.compression = Compression.ZIP;
			break;
		case kNone:
			item.compression = Compression.NONE;
			break;
		default:
			throw new RuntimeException("unknown compression type "
					+ itemData.getCompressionMethod());
		}
		return item;
	}

	BulkItemData toGPB() {
		BulkItemData.Builder builder = BulkItemData.newBuilder();
		builder.setOffset(offset);
		builder.setExpandedLengthBytes(size);
		switch (compression) {
		case ZIP:
			builder.setCompressionMethod(ECompressionMethod.kZip);
			break;
		case NONE:
			builder.setCompressionMethod(ECompressionMethod.kNone);
			break;
		default:
			throw new RuntimeException("unknown compression type "
					+ compression);
		}

		BulkItemData data = builder.build();
		return data;
	}
	
	
}
