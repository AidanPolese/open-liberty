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
 * Identifies a Java VM being utilized by a server. For each Java VM which is running
 * threads associated with the J2EE server, its containers or its resources, there must
 * be one managed object that implements the JVM model. A JVM managed object
 * must be removed when the Java VM it manages is no longer running.
 */
public interface JVMMBean extends J2EEManagedObjectMBean {

    /**
     * Identifies the Java Runtime Environment version of this Java VM. The value
     * of javaVersion must be identical to the value of the system property java.version.
     */
    String getjavaVersion();

    /**
     * Identifies the Java Runtime Environment vendor of this Java VM. The value
     * of javaVendor must be identical to the value of the system property
     * java.vendor.
     */
    String getjavaVendor();

    /**
     * Identifies the node (machine) this JVM is running on. The value of the node
     * attribute must be the fully quailified hostname of the node the JVM is running on.
     */
    String getnode();

}
