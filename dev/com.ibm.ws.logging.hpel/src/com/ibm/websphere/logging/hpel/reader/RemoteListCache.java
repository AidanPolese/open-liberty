//%Z% %I% %W% %G% %U% [%H% %T%]
/*
 * IBM Confidential OCO Source Material
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2009,2011
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 *
 * Change History:
 *
 * Reason           Version        Date       User id     Description
 * ----------------------------------------------------------------------------
 * F017049-22352      8.0        08/04/2010   belyi       Initial interface code
 */
package com.ibm.websphere.logging.hpel.reader;

import java.io.Serializable;

/**
 * Repository result cache to improve performance of multiple calls
 * 
 * @ibm-api
 */
public interface RemoteListCache extends Serializable {

	/**
	 * gets indicator that this instance contain statistics for all files in the result
	 * @return <code>true</code> if all cache info is complete.
	 */
	public boolean isComplete();
	
	/**
	 * gets number of records in the result based on the information available in this cache.
	 * @return size which could potentially smaller than the full result if {@link #isComplete()} returns <code>false</code>
	 */
	public int getSize();
	
}
