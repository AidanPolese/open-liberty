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
package com.ibm.websphere.simplicity.config;

/**
 * Represents a config element that allows modification.
 */
public interface ModifiableConfigElement {

    /**
     * Modifies the element.
     */
    public void modify(ServerConfiguration config) throws Exception;
}
