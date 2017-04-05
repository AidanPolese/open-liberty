/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.javaee.dd.bval;

public interface Property {

    /**
     * @return &lt;property name="x">
     */
    String getName();

    /**
     * @return &lt;property>x&lt;/property>
     */
    String getValue();
}
