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
 * F017049-18504      8.0        02/25/2010   spaungam    Add support for subprocess reading
 * 653791             8.0        05/25/2010   belyi       Make repository label available to the caller
 * F017049-22352      8.0        08/05/2010   belyi       Add findPrev and count methods for improved handling of cache
 */
package com.ibm.ws.logging.hpel;

import java.io.File;
import java.util.Map;

import com.ibm.ws.logging.object.hpel.RepositoryPointerImpl;

/**
 * Interface representing browsing capability over log files written by a single
 * instance of a server.
 */
public interface LogRepositoryBrowser {
	
	/**
	 * returns the file corresponding to the specified <code>location</code>.
	 * 
	 * @param location the log record position indicator received in an API call.
	 * @return File instance of a log file containing the log record or <code>null</code>
	 * 		if the file is not found in the repository.
	 */
	public File findFile(RepositoryPointerImpl location);
	
	/**
	 * returns the file next to the one specified by <code>location</code>.
	 * The behavior is similar to the {@link #findNext(File, long)}.
	 * 
	 * @param location the log record position indicator received in an API call.
	 * @param timelimit the time threshold after which we are not interested in log records.
	 * @return File instance of the file containing log records with timestamps bigger
	 * 		than in the file corresponding to <code>location</code> file <code>null</code>
	 * 		if there is no more files.
	 */
	public File findNext(RepositoryPointerImpl location, long timelimit);
	
	/**
	 * finds the log file containing log records written at the specified time.
	 * 
	 * @param timestamp time in milliseconds when log records were written
	 * @return File containing log records written at that time or <code>null</code> if
	 *        all records were written after that time.
	 */
	public File findByMillis(long timestamp);
	
	/**
	 * returns the log file written after the specified log file.
	 * If <code>timelimit</code> value is not negative the next file is return only
	 * if it contains log records with timestamps smaller than <code>timelimit</code>
	 * value.
	 * 
	 * @param current File instance relative to which we are looking for a new file.
	 * 		It should be an instance return in one of the other methods or <code>null</code>
	 *      to specify first file in the directory.
	 * @param timelimit the time threshold after which we are not interested in log records.
	 * @return File instance of the file containing log records with timestamps bigger
	 * 		than in the <code>current</code> file or <code>null</code> if there is no
	 * 		more files.
	 */
	public File findNext(File current, long timelimit);
	
	/**
	 * returns the log file written before the specified log file.
	 * If <code>timelimit</code> value is not negative the previous file is return only
	 * if it can contain log records with timestamps bigger than <code>timelimit</code>
	 * value.
	 * 
	 * @param current File instance relative to which we are looking for a new file.
	 * 		It should be an instance return in one of the other methods or <code>null</code>
	 *      to find the last file in the directory.
	 * @param timelimit the time threshold before which we are not interested in log records.
	 * @return File instance of the file containing log records with timestamps smaller
	 * 		than in the <code>current</code> file or <code>null</code> if there is no
	 * 		more files.
	 */
	public File findPrev(File current, long timelimit);
	
	/**
	 * counts number of files in the repository in between specified files.
	 * 
	 * @param first the file with the smallest timestamps or <code>null</code> if counting
	 * 		from the start of the repository.
	 * @param last the file with the biggest timestamps or <code>null</code> if counting till
	 * 		the end of the repository.
	 * @return number of files (including both first and last if they are not 'null') in the
	 * 		repository with timestamp values in between these of <code>first</code> and
	 * 		<code>last</code>
	 */
	public int count(File first, File last);
	
	/**
	 * retrieves the timestamp from the name of the file.
	 * 
	 * @param file to retrieve timestamp from.
	 * @return timestamp in millis or -1 if name's pattern does not correspond
	 * 			to the one used for files in the repository.
	 */
	public long getLogFileTimestamp(File file);
	
	/**
	 * returns id of the process writing log records in this location.
	 * 
	 * @return processId
	 */
	public String getProcessId();
	
	/**
	 * returns time of the first record in this location.
	 * 
	 * @return timestamp of the first record in the location.
	 */
	public long getTimestamp();
	
	/**
	 * returns label used for the process writing log records in this location.
	 * 
	 * @return label associated with the process.
	 */
	public String getLabel();
	
	/**
	 * returns map of browsers over log files written by sub-processes
	 * 
	 * @return map from sub-process names into browsers over their log files.
	 */
	public Map<String, LogRepositoryBrowser> getSubProcesses();
	
	/**
	 * returns Id identification to use in RepositoryPointerImpl for files return by this browser
	 * 
	 * @return array usable as instanceIds in RepositoryPointerImpl constructor.
	 */
	public String[] getIds();
	
}
