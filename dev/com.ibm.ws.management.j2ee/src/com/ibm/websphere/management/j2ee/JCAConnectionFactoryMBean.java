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
 * Identifies a connection factory. For each connection factory available to a server,
 * there must be one managed object that implements the JCAConnectionFactory
 * model.
 */
public interface JCAConnectionFactoryMBean extends J2EEManagedObjectMBean {

    /**
     * The value of managedConnectionFactory must be a
     * JCAManagedConnectionFactory OBJECT_NAME that identifies the managed
     * connection factory associated with the corresponding connection factory.
     */
    String getmanagedConnectionFactory();

}
