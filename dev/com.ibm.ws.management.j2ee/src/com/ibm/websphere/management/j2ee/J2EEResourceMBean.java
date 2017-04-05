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
 * J2EEResource is the base model for all managed object types that represent J2EE
 * resources. J2EE resources are resources utilized by the J2EE core server to provide
 * the J2EE standard services required by the J2EE platform architecture. For each
 * J2EE standard service that a server provides, there must be one managed object that
 * implements the J2EEResource model of the appropriate type.
 */
public interface J2EEResourceMBean extends J2EEManagedObjectMBean {

}
