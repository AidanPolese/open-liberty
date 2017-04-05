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
 * Identifies a physical JDBC data source. For each JDBC data source available to a
 * server there must be one managed object that implements the JDBCDataSource
 * model.
 */
public interface JDBCDataSourceMBean extends J2EEManagedObjectMBean {

    /**
     * The value of jdbcDriver must be an JDBCDriver OBJECT_NAME that
     * identifies the JDBC driver for the corresponding JDBC data source.
     */
    String getjdbcDriver();

}
