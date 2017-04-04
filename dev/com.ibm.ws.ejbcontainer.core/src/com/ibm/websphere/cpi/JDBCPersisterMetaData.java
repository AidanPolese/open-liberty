/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 1999, 2001
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.websphere.cpi;

import javax.sql.DataSource;

/**
 * Extends PersisterMetaData, and adds methods specific to retrieving
 * metadata for use by JDBC persister. To support persisters tied to
 * non-JDBC back-ends, PersisterMetaData will likely have to be extended
 * in custom fashion.
 * 
 * @see com.ibm.websphere.cpi.PersisterMetaData
 */

public interface JDBCPersisterMetaData extends PersisterMetaData {

    /**
     * getDataSource: returns the DataSource to be used by the JDBC persister.
     * 
     * @return DataSource to be used by the JDBC persister.
     */
    public DataSource getDataSource();

}
