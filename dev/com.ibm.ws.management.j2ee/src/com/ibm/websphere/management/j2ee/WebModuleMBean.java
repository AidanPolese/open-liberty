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
 * The WebModule model identifies a deployed WAR module.
 */
public interface WebModuleMBean extends J2EEModuleMBean {

    /**
     * A list of servlets contained in the deployed WAR module. For each servlet
     * contained in the deployed WAR module there must be one Servlet
     * OBJECT_NAME in the servlets list that identifies it.
     */
    String[] getservlets();
}
