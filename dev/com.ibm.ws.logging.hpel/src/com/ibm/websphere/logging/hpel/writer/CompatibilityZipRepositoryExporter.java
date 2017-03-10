//%Z% %I% %W% %G% %U% [%H% %T%]
/*
 * IBM Confidential OCO Source Material
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2009,2010
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 *
 * Change History:
 *
 * Reason           Version        Date       User id     Description
 * ----------------------------------------------------------------------------
 * F001340-15950.1    8.0        09/04/2009   belyi       Initial HPEL code
 * 649536             8.0        04/23/2010   belyi       Enforce ".txt" extension on the file in the ZIP.
 * 653336             8.0        05/20/2010   belyi       Accept the whole HpelFormatter in the constructor
 * F017049-16916      8.0        08/18/2010   belyi       Rename class and clean up its javadoc for end users
 */
package com.ibm.websphere.logging.hpel.writer;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.ibm.websphere.logging.hpel.reader.HpelFormatter;

/**
 * Implementation of the {@link RepositoryExporter} interface exporting log records
 * into a compressed text file.
 * 
 * @ibm-api
 */
public class CompatibilityZipRepositoryExporter extends CompatibilityRepositoryExporter {
	private final static String BUNDLE_NAME = "com.ibm.ws.logging.hpel.resources.HpelMessages";
	private final static String className = CompatibilityZipRepositoryExporter.class.getName();
	private final static Logger logger = Logger.getLogger(className, BUNDLE_NAME);
	
	private final ZipOutputStream out;

	/**
	 * Creates an instance for storing records in a zipped file in a compatibility text format.
	 * 
	 * @param archiveFile   output file
	 * @param formatter     formatter to use when converting record messages into text
	 * @throws IOException  if an I/O error has occurred
	 */
	public CompatibilityZipRepositoryExporter(File archiveFile, HpelFormatter formatter)
			throws IOException {
		this(new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(archiveFile, false))), archiveFile.getName(), formatter);
	}
	
	private CompatibilityZipRepositoryExporter(ZipOutputStream out, String name, HpelFormatter formatter) throws IOException {
		super(out, formatter);
		this.out = out;
		if (name.endsWith(".zip")) {
			name = name.substring(0, name.length()-4);
		}
		// Enforce TXT extension to help applications depending on file name extension
		this.out.putNextEntry(new ZipEntry(name + ".txt"));
	}

	@Override
	public void close() {
		super.close();
		try {
			out.closeEntry();
			out.close();
		} catch (IOException ex) {
			logger.logp(Level.WARNING, className, "finish", "HPEL_ErrorClosingZipStream", ex);
		}
	}

}
