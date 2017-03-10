
 /*
 * ============================================================================ 
 * IBM Confidential OCO Source Materials
 * 
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70   Copyright IBM Corp. 2010, 2011
 * 
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 * @end_prolog@
 *
 * Change activity:
 *
 * Reason          Date   Origin   Description
 * --------------- ------ -------- --------------------------------------------
 * 679712          112210 cumminsh Versioning exported packages    
 * 721444          121311 olteamh  Changed version  
 * ============================================================================
 */
/**

Provides classes and interfaces for reading log records stored in HPEL format.
<p>
HPEL repositories store log and trace content in a binary format which requires
an API to read.  Individual log and trace records are represented by the
<a href="RepositoryLogRecord.html">RepositoryLogRecord</a> interface.
A <a href="ServerInstanceLogRecordList.html">ServerInstanceLogRecordList</a>
is a collection of RepositoryLogRecords from one server process.  The
<a href="RepositoryReader.html">RepositoryReader</a> interface provides methods
for getting a ServerInstanceLogRecordList.
<p>
To read a local HPEL repository, use the
<a href="RepositoryReaderImpl.html">RepositoryReaderImpl</a> class to query for
a ServerInstanceLogRecordList.  Iterate over the ServerInstanceLogRecordList
to obtain the contained RepositoryLogRecord instances.  Additionally,
information related to all log records in the server process is accessible from
properties via the ServerInstanceLogRecordList's 
<a href="ServerInstanceLogRecordList.html#getHeader()">getHeader</a> method.  This
information is what would often be displayed in a log file's header.
<p>
As an example, to print out all of the records in a log repository located in
the directory "repositoryLocation" use the following code:
</p>

<code>
<pre>
RepositoryReaderImpl reader = new RepositoryReaderImpl("repositoryLocation");
for(ServerInstanceLogRecordList oneServerList: reader.getLogLists()) {
	oneServerList.getHeader().list(System.out);
	for (RepositoryLogRecord record: oneServerList) {
		System.out.println(record.toString());
	}
}
</pre>
</code>

<p>
While iterating through a ServerInstanceLogRecordList, a
<a href="RepositoryPointer.html">RepositoryPointer</a> can be obtained from any
RepositoryLogRecord.  A RepositoryPointer is a pointer to a location in the
repository, and can be used in queries to the RepositoryReader to obtain lists
of log records that begin with the record immediately following the location
denoted by the RepositoryPointer.
<p>
The following example illustrates how to search for a particular message in the
repository, then use the RepositoryPointer of that log record as a starting
point for a query for warning (or more severe) messages.
</p>

<code>
<pre>
RepositoryPointer rp = null;
for(RepositoryLogRecord record: reader.getLogListForCurrentServerInstance()) {
	if (record.getMessage().contains("open for e-business")) {
		rp = record.getRepositoryPointer();
	}
}
if (found != null) {
    for (RepositoryLogRecord record: reader.getLogListForServerInstance(rp, Level.WARNING, null)) {
	    System.out.println(record.toString());
    }
}
</pre>
</code>

<p>
Advanced filtering can be implemented by using RepositoryReaderImpl methods
accepting <a href="LogRecordFilter.html">LogRecordFilter</a> or
<a href="LogRecordHeaderFilter.html">LogRecordHeaderFilter</a> instances as
parameters. Queries which make use of the LogRecordHeaderFilter (which can
only utilize fields in the 
<a href="RepositoryLogRecordHeader.html">RepositoryLogRecordHeader</a>
interface) run more efficiently than queries which make use of a
LogRecordFilter (which can utilize fields in the entire RepositoryLogRecord
interface).  A set of predefined filters can be found in the
<a href="filters/package-summary.html">filters</a> package.
<p>
The following example illustrates how to list severe log records or messages
written to the standard error stream:
</p>

<code>
<pre>
LogRecordFilter filter = new LogRecordFilter() {
	public accept(RepositoryLogRecord record) {
		return Level.SEVERE.equals(record.getLevel()) || "SystemErr".equals(record.getLoggerName();
	}
}
for(RepositoryLogRecord record: reader.getLogListForServerInstance((Date)null, filter)) {
	System.out.println(record.toString());
}
</pre>
</code>


 * @version 1.1.0
 */
@org.osgi.annotation.versioning.Version("1.1.0")
package com.ibm.websphere.logging.hpel.reader;
