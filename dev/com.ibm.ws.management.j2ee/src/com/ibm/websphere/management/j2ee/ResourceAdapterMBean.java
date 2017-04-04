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
 * Identifies a deployed resource adapter.
 */
public interface ResourceAdapterMBean extends J2EEManagedObjectMBean {

    /**
     * The value of jcaResource must be a JCAResource OBJECT_NAME that
     * identifies the JCA connector resource implemented by this ResourceAdapter.
     */
    String getjcaResource();

}
