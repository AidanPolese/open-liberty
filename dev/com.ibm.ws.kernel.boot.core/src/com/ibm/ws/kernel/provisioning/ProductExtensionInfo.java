/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.kernel.provisioning;

/**
 * Product extension interface.
 */
public interface ProductExtensionInfo {

    /**
     * Retrieves the name of the product extension.
     * 
     * @return The name of the product extension.
     */
    public String getName();

    /**
     * Retrieves the location of the product extension.
     * 
     * @return The location of the product extension.
     */
    public String getLocation();

    /**
     * Retrieves the product extension ID.
     * 
     * @return The product ID.
     */
    public String getProductID();
}