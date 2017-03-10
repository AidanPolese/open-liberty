//%Z% %I% %W% %G% %U% [%H% %T%]

/*
 * IBM Confidential OCO Source Material
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2001,2009
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 *
 * Change History:
 *
 * Reason            Version    Date        User id     Description
 * ----------------------------------------------------------------------------
 * PM48157             8.0      10-04-2011  shighbar    HPEL TextLog retention policy does not remove previous server instances logs.
 */

package com.ibm.ws.logging.hpel.impl;

import java.io.File;
import java.io.FileFilter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogRepositoryManagerTextImpl extends LogRepositoryManagerImpl {
	// Prefix & Extension for TextLog filename
	private final static String TEXTLOGPREF = "TextLog_";
	private final static String TEXTLOGEXT = ".log";

	// Format for Date part of TextLog filename
	private final static SimpleDateFormat DATEFORMAT = new SimpleDateFormat("yy.MM.dd_HH.mm.ss");

	public LogRepositoryManagerTextImpl(File directory, String pid, String label, boolean useDirTree) {
		super(directory, pid, label, useDirTree);
	}

	@Override
	protected File getLogFile(File parentLocation, long timestamp) {
		StringBuilder sb = new StringBuilder();
		sb.append(TEXTLOGPREF);
		sb.append(DATEFORMAT.format(new Date(timestamp)));
		sb.append(TEXTLOGEXT);
		return new File(parentLocation, sb.toString());
	}

	/*
	 * @Override public String getLogFilePid(File file) { return ""; }
	 */
	@Override
	public long getLogFileTimestamp(File file) {
		String name = file.getName();
		// Check name for extension
		if (name == null || name.length() == 0 || !name.startsWith(TEXTLOGPREF) || !name.endsWith(TEXTLOGEXT)) {
			return -1L;
		}
		long result = -1L;
		try {
			Date date = DATEFORMAT.parse(name.substring(TEXTLOGPREF.length(), name.length() - TEXTLOGEXT.length()));
			result = date.getTime();
		} catch (ParseException ex) {
			// Fall through to return -1L;
		}
		return result;
	}

	/**
	 * Returns the list of files in the repository.
	 * 
	 * @return array of File instances representing files in the repository.
	 */
	@Override
	protected File[] listRepositoryFiles() {
		FileFilter textLogFilter = new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.getName().startsWith(TEXTLOGPREF) && pathname.getName().endsWith(TEXTLOGEXT);
			}
		};
		return AccessHelper.listFiles(repositoryLocation, textLogFilter);
	}

}
