/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.http;

/**
 * An implementer of the VirtualHostListener interface registers as a service
 * in the service registry, and is then notified when context roots are added
 * and removed from a virtual host.
 * <p>
 * Note that nested context roots are allowed. Both {code}/a{code} and
 * {code}/a/b{code} can be separately registered.
 */
public interface VirtualHostListener {
    /**
     * Called when a new context root is registered with a given virtual host.
     * 
     * @param contextRoot The new context root.
     * @param virtualHost The target virtual host.
     */
    public void contextRootAdded(String contextRoot, VirtualHost virtualHost);

    /**
     * Called when a context root is removed from a given virtual host.
     * 
     * @param contextRoot The removed context root.
     * @param virtualHost The target virtual host.
     */
    public void contextRootRemoved(String contextRoot, VirtualHost virtualHost);
}