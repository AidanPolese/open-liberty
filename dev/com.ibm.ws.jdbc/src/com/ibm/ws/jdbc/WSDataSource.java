/*
* IBM Confidential
*
* OCO Source Materials
*
* WLP Copyright IBM Corp. 2015
*
* The source code for this program is not published or otherwise divested 
* of its trade secrets, irrespective of what has been deposited with the 
* U.S. Copyright Office.
*/
package com.ibm.ws.jdbc;

import javax.sql.DataSource;

/**
 * Extensions for use by internal components. Internal components may cast a DataSource to WSDataSource
 * in order to use these methods.
 */
public interface WSDataSource extends DataSource {
    /**
     * <p>Returns the cached value of <code>DatabaseMetaData.getDatabaseProductName</code> from the first connection
     * that was obtained via this data source.
     * 
     * <p>This method is useful as a performance optimization for obtaining the database product name in the typical
     * case where a connection has been previously obtained from the data source. If a connection hasn't been obtained
     * yet, it will just return a null value, in which case it will be the user's responsibility to obtain a connection. 
     *  
     * @return the database product name. Null if a connection has not yet been obtained.
     */
    String getDatabaseProductName();
}
