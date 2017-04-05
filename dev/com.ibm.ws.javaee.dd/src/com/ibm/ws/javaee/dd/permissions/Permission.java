/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.javaee.dd.permissions;

/**
 *
 */
public interface Permission {

    /**
     * @return &lt;class-name>
     */
    String getClassName();

    /**
     * @return &lt;name>
     */
    String getName();

    /*
     * @return &lt;actions>
     */
    String getActions();

}
