//%Z% %I% %W% %G% %U% [%H% %T%]

/*
 * IBM Confidential OCO Source Material
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2010,2011
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Provide interface for remote access to High Performance Extensible Logs through implicit use of MBeans
 *
 * Change History:
 *
 * Reason            Version    Date        User id     Description
 * ----------------------------------------------------------------------------
 * F017049-16882.1       8.0    01-27-2010    belyi     Part created.
 * F017049-18504         8.0    03/23/2010    belyi     Add support for subprocess reading
 * F017049-22352         8.0    08/05/2010    belyi     Add result caching info.
 */
package com.ibm.websphere.logging.hpel.reader;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

/**
 * Object representing a server instance in a query context.
 * 
 * This is the object return in a list from {@link RemoteAllResults} and which is used
 * in remote server instance specific queries to specify the instance to be queried.
 * 
 * @ibm-api
 */
public class RemoteInstanceDetails implements Serializable {
	private static final long serialVersionUID = -2815429026378868108L;
	
	final LogQueryBean query;
	final Date startTime;
	final String[] procPath;
	private RemoteListCache cache = null;
	
	/**
	 * creates instance with a specified time and query.
	 * 
	 * @param query criteria to use for queries on the instance
	 * @param startTime time at which that instance was active
	 * @param subProcs list of keys leading to the child sub-process.
	 */
	public RemoteInstanceDetails(LogQueryBean query, Date startTime, String[] subProcs) {
		this.query = query;
		this.startTime = startTime;
		this.procPath = subProcs;
	}
	
	/**
	 * gets start time of this instance
	 * @return this instance's value of startTime
	 */
	public Date getStartTime() {
		return this.startTime;
	}
	
	/**
	 * gets list of keys leading to the child sub-process
	 * @return this instance's value of subProcs
	 */
	public String[] getProcPath() {
		return this.procPath;
	}
	
	/**
	 * gets query used on this instance
	 * @return this instance's value of query
	 */
	public LogQueryBean getQuery() {
		return this.query;
	}
	
	/**
	 * gets cache for the query result on this instance
	 * @return this instance's value of cache
	 */
	public synchronized RemoteListCache getCache() {
		return cache;
	}
	
	/**
	 * sets cache for the query result on this instance
	 * @param cache new instance's value of cache
	 */
	public synchronized void setCache(RemoteListCache cache) {
		if (cache != null && (this.cache == null || !this.cache.isComplete())) {
			this.cache = cache;
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cache == null) ? 0 : cache.hashCode());
		result = prime * result + ((query == null) ? 0 : query.hashCode());
		result = prime * result
				+ ((startTime == null) ? 0 : startTime.hashCode());
		result = prime * result + Arrays.hashCode(procPath);
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RemoteInstanceDetails other = (RemoteInstanceDetails) obj;
		if (cache == null) {
			if (other.cache != null)
				return false;
		} else if (!cache.equals(other.cache))
			return false;
		if (query == null) {
			if (other.query != null)
				return false;
		} else if (!query.equals(other.query))
			return false;
		if (startTime == null) {
			if (other.startTime != null)
				return false;
		} else if (!startTime.equals(other.startTime))
			return false;
		if (!Arrays.equals(procPath, other.procPath))
			return false;
		return true;
	}
}
