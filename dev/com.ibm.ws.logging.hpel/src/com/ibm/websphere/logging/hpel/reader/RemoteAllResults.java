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
 */
package com.ibm.websphere.logging.hpel.reader;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Collection of instances satisfying query request.
 * 
 * Instances of this class are return from the remote queries and contain list of
 * {@link RemoteInstanceDetails} objects representing instances containing query
 * result. These objects should be used in server instance specific remote queries
 * to retrieve records satisfying the query created by the indicated instance.
 * 
 * @ibm-api
 */
public class RemoteAllResults implements Serializable {
	private static final long serialVersionUID = -1505131318053138547L;
	
	final LogQueryBean query;
	final ArrayList<Date> resultList = new ArrayList<Date>();
	
	/**
	 * creates an empty instance of the result corresponding to the query.
	 * 
	 * @param query filter criteria used in the request.
	 */
	public RemoteAllResults(LogQueryBean query) {
		this.query = query;
	}
	
	/**
	 * adds server instance into the list of this result.
	 * 
	 * @param startTime time at which server was running
	 */
	public void addInstance(Date startTime) {
		resultList.add(startTime);
	}
	
	/**
	 * returns list of server instances satisfying the request.
	 * 
	 * @return iterable over RemoteInstanceDetails objects
	 */
	public Iterable<RemoteInstanceDetails> getLogLists() {
		ArrayList<RemoteInstanceDetails> result = new ArrayList<RemoteInstanceDetails>();
		for (Date startTime: resultList) {
			result.add(new RemoteInstanceDetails(query, startTime, new String[0]));
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((query == null) ? 0 : query.hashCode());
		result = prime * result
				+ ((resultList == null) ? 0 : resultList.hashCode());
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
		RemoteAllResults other = (RemoteAllResults) obj;
		if (query == null) {
			if (other.query != null)
				return false;
		} else if (!query.equals(other.query))
			return false;
		if (resultList == null) {
			if (other.resultList != null)
				return false;
		} else if (!resultList.equals(other.resultList))
			return false;
		return true;
	}
	
}
