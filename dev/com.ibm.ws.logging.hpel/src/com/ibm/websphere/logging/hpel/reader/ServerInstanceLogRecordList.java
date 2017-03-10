// %Z% %I% %W% %G% %U% [%H% %T%]
/*
 * IBM Confidential OCO Source Material
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2009, 2011
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
 * F017049-18504      8.0        03/07/2010   spaungam    Add support for subprocess reading
 * F017049-18504.1    8.0        03/07/2010   spaungam    Update javadoc
 * 650538             8.0        05/14/2010   spaungam    Correct the CBE location value
 * F017049-22352      8.0        08/23/2010   belyi       Update javadoc for range() method 
 * 669339             8.0        09/07/2010   mcasile     A little more javaDoc focused on getChildren
 * 665629             8.0        12/07/2010   belyi       Remove unnecessary size() method.
 * 681015             8.0        02/25/2011   belyi       Add getStartTime() method to the interface
 */
package com.ibm.websphere.logging.hpel.reader;

import java.util.Date;
import java.util.Map;
import java.util.Properties;

/**
 * A list of log records originating from one process.
 * <p>
 * Example of intended usage:
 * <code>
 * <pre>
 * for (ServerInstanceLogRecordList pid: reader.getLogListForCurrentServerInstance()) {
 * 	Properties header = pid.getHeader();
 * 	&lt;process header&gt;
 * 	for (RepositoryLogRecord record: pid) {
 * 		&lt;process record&gt;
 * 	}
 * 	pid.close();
 * }
 * </pre>
 * </code>
 * 
 * An extension for z/OS to start with a controller and get all servants (and merge them) can be seen at {@link MergedRepository}
 * Take special note of the getChildren method on the <code>ServerInstanceLogRecordList</code> object.
 *  
 * An example of getting all records for the controller and all servants on z/OS could look like this:
 * <code>
 * <pre>
 * for (ServerInstanceLogRecordList pid: reader.getLogListForCurrentServerInstance()) {
 * 	Properties header = pid.getHeader();
 * 	&lt;process header&gt;
 * 	for (RepositoryLogRecord record: pid) {	// This PID is the controller
 * 		&lt;process controller record&gt;
 * 	}
 *  Map<String, ServerInstanceLogRecordList>servantMap = pid.getChildren() ;
 *  Iterator<String> servantKeys = servantMap.keySet().iterator() ;
 *  while (servantKeys.hasNext()) {
 *  	// Map label has key information to identify which child process. This can be used to get just one servant,
 *      //  here we are going to print the results for all servants
 *  	String label = servantKeys.next() ;			
 *   	ServerInstanceLogRecordList servantRecs = servantMap.get(label) ;	// Extract the child process ServerInstanceLogRecordList
 *      Properties subHeader = servantRecs.getHeader() ;
 * 	    for (RepositoryLogRecord subRec: servantRecs) {		// Pull all records for this servant
 * 		    &lt;process servant record&gt;
 * 	    }
 *  	servantRecs.close() ;   	
 *  }
 *
 * 	pid.close();
 * }
 * </pre>
 * </code>
 * 
 * @ibm-api
 */
public interface ServerInstanceLogRecordList extends Iterable<RepositoryLogRecord> {
	public static final String HEADER_VERSION = "Version";
	public static final String HEADER_VERBOSE_VERSION = "VerboseVersion";
	public static final String HEADER_SERVER_NAME ="ServerName";
	public static final String HEADER_PROCESSID = "ProcessId";
	public static final String HEADER_SERVER_TIMEZONE = "ServerTimeZone";
	public static final String HEADER_SERVER_LOCALE_LANGUAGE = "ServerLocaleLanguageCode";
	public static final String HEADER_SERVER_LOCALE_COUNTRY = "ServerLocaleCountryCode";
	// Special fields filled for CBE
	public static final String HEADER_HOSTNAME = "HostName";
	public static final String HEADER_HOSTADDRESS = "HostAddress";
	public static final String HEADER_HOSTTYPE = "HostType";
	public static final String HEADER_ISZOS = "isZOS";
	public static final String HEADER_ISSERVER = "isServer";
	public static final String HEADER_ISTHINCLIENT = "isThinClient";

	public static final String HEADER_PROCESSNAME = "processId";
	public static final String HEADER_ADDRESSSPACEID = "addressSpaceId";
	public static final String HEADER_JOBID = "jobId";
	public static final String HEADER_JOBNAME = "jobName";
	public static final String HEADER_SYSTEMNAME = "systemName";
	public static final String HEADER_TCBADDRESSNAME = "tcbAddress";
	public static final String HEADER_SERVERNAME = "serverName";

	/**
	 * Returns the header belonging to records from this process.
	 * 
	 * These properties help describe the process and environment that the log records originated from.
	 * 
	 * @return properties belonging to all records in this list.
	 */
	public Properties getHeader();
	
	/**
	 * Returns a subset of records from this query result.
	 * <p>
	 * Example of use:
	 * <ul>
	 * <li><code>range(offset, length)</code> returns iterator over the <code>length</code> records after skipping first <code>offset</code> records.
	 * <li><code>range(offset, -1)</code> returns iterator over the rest of records after skipping first <code>offset</code> records.
	 * <li><code>range(-offset, length)</code> returns iterator over the <code>length</code> records starting with <code>offset</code> record before last.
	 * <li><code>range(0, -1)</code> returns iterator over all records in the result.
	 * </ul>
	 * @param offset the number of records to skip from the beginning of the result. A negative
	 * 	value means to skip <code>-offset</code> records from the end of the result. Values greater than 
	 * {@link #size()} will result in an empty subset being returned.
	 * @param length the maximum number of records to include in this subset. A negative value
	 * 	means that all records starting from the <code>offset</code> are returned.
	 * @return Iterable instance listing records in the subset.
	 */
	public Iterable<RepositoryLogRecord> range(int offset, int length);
	
	/**
	 * Returns the children for the process of a ServerInstanceLogRecordList.  ServerInstanceLogRecordList represents the log records for a given 
	 * process.  A process with subprocesses will return a map with each child subprocess represented by a ServerInstanceLogRecordList.  The key to 
	 * each entry is computed internally, and the caller must invoke this method in order to obtain the valid key for a specific entry.
	 * 
	 * @return the map that represents the children.  Each child subprocess is an entry in the map.  
	 */
	public Map<String, ServerInstanceLogRecordList> getChildren();
	
	/**
	 * Returns start time of this instance. Start time is the time of the first log or trace record written by this instance.
	 * 
	 * @return start time as a Date object or <code>null</code> if time can not be obtained.
	 */
	public Date getStartTime();
}
