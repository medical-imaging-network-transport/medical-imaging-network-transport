package org.nema.medical.mint.dcmimport;

import java.io.File;
import java.util.Collection;

public final class ProcessImportDir {

	public ProcessImportDir(final File importDir, final String serverURL) {
		this.importDir = importDir;
		this.serverURL = serverURL;

	}

	public void run() {
//		final Collection<File> resultFiles = new ArrayList<File>();
//		findPlainFilesRecursive(importDir, resultFiles);
//		for (final File plainFile: resultFiles) {
//	        final File[] dirs = topDir.listFiles(directoryFilter);
//	        directoryItems = new TreeSet<File>(Arrays.asList(dirs));
//	        final Collection<File> returnedItems = new ArrayList<File>();
//	        for (final File dir: directoryItems) {
//	            final DirectoryItem dirItem = process(dir);
//	            final File associationDir = new File(monitoredLocation.getFile(), dirItem.getAssociationID());
//	            for (final String subDirItem: dirItem.getNewFiles()) {
//	                returnedItems.add(new File(associationDir, subDirItem));
//	            }
//	        }
//		}
//        if (!topDir.exists()) {
//            throw new RuntimeException("root directory not found: " + topDir);
//        }
//
//        return returnedItems;
	}

	private static void findPlainFilesRecursive(final File targetFile, final Collection<File> resultFiles) {
		if (targetFile.isFile()) {
			resultFiles.add(targetFile);
		} else {
			assert targetFile.isDirectory();
			for (final File subFile: targetFile.listFiles()) {
				findPlainFilesRecursive(subFile, resultFiles);
			}
		}
	}

	private final File importDir;
	private final String serverURL;
}
