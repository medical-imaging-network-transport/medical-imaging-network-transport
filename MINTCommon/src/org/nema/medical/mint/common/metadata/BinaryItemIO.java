package org.nema.medical.mint.common.metadata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.nema.medical.mint.common.metadata.Mint2Gpb.BulkDataTable;
import org.nema.medical.mint.common.metadata.Mint2Gpb.BulkItemData;


public class BinaryItemIO {

	static public List<BinaryItem> parseIndex(File file) throws IOException {
		List<BinaryItem> items = null;
		FileInputStream stream = new FileInputStream(file);
		try {
			items = parseIndex(stream);
		} finally {
			stream.close();
		}
		return items;
	}

	static public List<BinaryItem> parseIndex(InputStream in)
			throws IOException {
		List<BinaryItem> items = new ArrayList<BinaryItem>();
		BulkDataTable data = BulkDataTable.parseFrom(in);
		for (BulkItemData itemData : data.getItemsList()) {
			items.add(BinaryItem.fromGPB(itemData));
		}
		return items;
	}

	static public void writeIndex(List<BinaryItem> items, File file)
			throws IOException {
		FileOutputStream out = new FileOutputStream(file);
		try {
			writeIndex(items, out);
		} finally {
			out.close();
		}
	}

	static public void writeIndex(List<BinaryItem> items, OutputStream out)
			throws IOException {
		BulkDataTable.Builder builder = BulkDataTable.newBuilder();
		for (BinaryItem item : items) {
			builder.addItems(item.toGPB());
		}
		builder.build().writeTo(out);
	}

	static public BinaryItem getItemFromIndex(File file, int i)
			throws IOException {
		BinaryItem item = null;
		if(i >=0){
			FileInputStream stream = new FileInputStream(file);
			try {
				item = getItemFromIndex(stream, i);
			} finally {
				stream.close();
			}
		}
		return item;
	}

	static public BinaryItem getItemFromIndex(InputStream in, int i)
			throws IOException {
		if (i >= 0) {
			BulkDataTable data = BulkDataTable.parseFrom(in);
			if (data.getItemsCount() > i) return BinaryItem.fromGPB(data.getItemsList().get(i));
		}
		return null;
	}

	/**
	 * rather than returning a large in-memory buffer, open the file, seek to
	 * the proper location, and stream the contents of the binary item to the
	 * provided output stream (typically for writing to a web response)
	 * 
	 * @param file the binary data file containing concatenated binary objects
	 * @param item data object containing the offset, size and compression
	 * @param out the output stream to write data to (e.g. a web response)
	 * @throws IOException
	 */
	static public void streamBinaryItem(File file, BinaryItem item, OutputStream out)
			throws IOException {
		FileInputStream fin = new FileInputStream(file);
		try {
			fin.skip(item.getOffset());

			// uncompress if necessary
			InputStream in;
			if (item.getCompression() == BinaryItem.Compression.ZIP) {
				in = new ZipInputStream(fin);
			} else {
				in = fin;
			}

			final int bufsize = 16384;
			final long itemsize = item.getSize();
			byte[] buf = new byte[bufsize];
			for (long i = 0; i < itemsize; i += bufsize) {
				int len = (int) ((i + bufsize > itemsize) ? (int)itemsize - i : bufsize);
				in.read(buf,0,len);
				out.write(buf,0,len);
			}

		} finally {
			fin.close();
		}
	}

	/**
	 * takes the binary item from the input stream and writes it to the end of the file
	 * uses the size from the BinaryItem but updates the offset after writing to the file
	 * @param in the stream to read binary data from
	 * @param item data object containing the size and compression; updated with new offset
	 * @param file the binary data file containing concatenated binary objects
	 * @throws IOException
	 */
	static public void appendBinaryItem(InputStream in, BinaryItem item, File file)
			throws IOException {
		long offset = file.length();
		FileOutputStream fout = new FileOutputStream(file, true);
		try {
			final int bufsize = 16384;
			final long itemsize = item.getSize();

			// compress if necessary
			OutputStream out;
			if (item.getCompression() == BinaryItem.Compression.ZIP) {
				out = new ZipOutputStream(fout);
			} else {
				out = fout;
			}

			byte[] buf = new byte[bufsize];
			for (int i = 0; i < itemsize; i += bufsize) {
				int len = (int) ((i + bufsize > itemsize) ? (int)itemsize - i : bufsize);
				in.read(buf,0,len);
				out.write(buf,0,len);
			}

			item.setOffset(offset);
		} finally {
			fout.close();
		}
	}
}
