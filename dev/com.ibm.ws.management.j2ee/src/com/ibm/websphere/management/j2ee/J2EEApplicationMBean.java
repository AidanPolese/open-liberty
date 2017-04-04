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
 * Identifies a J2EE application EAR that has been deployed.
 */
public interface J2EEApplicationMBean extends J2EEDeployedObjectMBean {

    /**
     * A list of J2EEModules that comprise this application. For each J2EE module
     * that is utilized by this application, there must be one J2EEModule
     * OBJECT_NAME in the modules list that identifies it.
     */
    String[] getmodules();

}
