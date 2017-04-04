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
 * The ResourceAdapterModule model identifies a deployed resource adapter archive
 * (RAR).
 */
public interface ResourceAdapterModuleMBean extends J2EEModuleMBean {

    /**
     * A list of resource adapters contained in this resource adapter module. For
     * each resource adapter contained in the deployed RAR module, there must be one
     * ResourceAdapter OBJECT_NAME in the resourceAdapters list that identifies
     * it.
     */
    String[] getresourceAdapters();

}
