/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 1998, 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.websphere.csi;

/**
 * <code>InsufficientCacheSpaceException</code> used to be thrown by
 * <code>Cache</code> during an <code>insert()</code> operation when
 * the cache was "full" _and_ the cache was unable to evict objects
 * (either because the <code>EvictionStrategy</code> did not supply
 * a victim, or all victims are currently pinned).
 * <p>
 * 
 * This is kept only for backwards compatibility with down-level servers.
 * It is not currently used by any current code.
 * 
 */

public class InsufficientCacheSpaceException
                extends CSIException
{
    private static final long serialVersionUID = -3751156269968731288L;
}
