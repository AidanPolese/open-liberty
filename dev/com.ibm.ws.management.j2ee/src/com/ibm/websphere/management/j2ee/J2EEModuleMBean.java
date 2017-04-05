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
 * The J2EEModule model is the base model for all of the J2EE Module types.
 * Managed objects that implement the J2EEModule model represent EAR, JAR,
 * WAR and RAR files that have been deployed.
 */
public interface J2EEModuleMBean extends J2EEDeployedObjectMBean {

    /**
     * Identifies the Java virtual machines on which this module is running. For each
     * JVM on which this module has running threads there must be one JVM
     * OBJECT_NAME in the javaVMs list that identifies it.
     * 
     * Each OBJECT_NAME in the J2EEModule javaVMs list must match one of
     * the Java VM names in the javaVMs attribute of the J2EEServer on which this
     * module is deployed.
     */
    String[] getjavaVMs();
}
