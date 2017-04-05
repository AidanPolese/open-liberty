/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2001, 2003
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.websphere.csi;

/**
 * CacheElement instances are what are stored in the EJBCache
 * implementation and are returned when enumerating the contents
 * of the EJBCache.
 * 
 * @see EJBCache
 */

public interface CacheElement {

    /**
     * Return the object associated with the CacheElement.
     */

    public Object getObject();

    /**
     * Return the key associated with the CacheElement.
     */

    public Object getKey();

} // CacheElement
