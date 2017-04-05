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
 * Identifies a managed URL resource. For each managed URL resource provided by a
 * server there should be one managed object that implements the URLResource
 * model. It is specific to a server implementation which URL resources are exposed as
 * manageable and there are no specific requirements as to which URL resources
 * provided by a server are exposed as managed objects.
 */
public interface URLResourceMBean extends J2EEResourceMBean {

}
