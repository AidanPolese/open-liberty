/**
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.websphere.management.j2ee;

/**
 * The JDBCResource model identifies a JDBC resource. A JDBC resource manages
 * one or more JDBC data sources. For each JDBC resource provided on a server, there
 * must be one JDBCResource OBJECT_NAME in the servers resources list that
 * identifies it.
 */
public interface JDBCResourceMBean extends J2EEResourceMBean {

    /**
     * Identifies the JDBC data sources available on the corresponding JDBC
     * resource. For each JDBC data source available on this JDBC resource there must
     * be one JDBCDataSource OBJECT_NAME in the jdbcDataSources list.
     */
    String[] getjdbcDataSources();

}
