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
 * The J2EEServer model specifies the management information for a single J2EE
 * server core implementation. The J2EE server core identifies the server core of one
 * instance of a J2EE platform product as described in the Java 2 Enterprise Edition
 * Platform specification section 2.1, Architecture.
 */
public interface J2EEServerMBean extends J2EEManagedObjectMBean {

    /**
     * A list of all of the J2EEApplication and J2EEModule types deployed on this
     * J2EEServer.
     */
    String[] getdeployedObjects();

    /**
     * A list of resources available to this server.
     */
    String[] getresources();

    /**
     * A list of all Java virtual machines on which this J2EEServer has running
     * threads. For each Java virtual machine this server utilizes, there must be one JVM
     * OBJECT_NAME in the javaVMs list that identifies it.
     */
    String[] getjavaVMs();

    /**
     * Identifies the J2EE platform vendor of this J2EEServer. The value of serverVendor
     * is specified by the server vendor.
     */
    String getserverVendor();

    /**
     * Identifies the J2EE implemetation version of this J2EEServer. The value of
     * serverVersion is specified by the server vendor.
     */
    String getserverVersion();

}
