/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2001
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.websphere.csi;

/**
 * <code>EJBModuleConfigData</code> instances contain all the
 * ConfigData required
 * by a container to correctly install and manage an EJB module. <p>
 */

public interface EJBModuleConfigData
                extends java.io.Serializable
{
    /**
     * Returns the EJBJar object for the module
     */
    public Object getModule();

    /**
     * getModuleBinding returns EJBJarBinding
     */
    public Object getModuleBinding();

    /**
     * getModuleExtension returns the EJBJarExtension object associated with
     * this module
     */
    public Object getModuleExtension();

}
