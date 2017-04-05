/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2005, 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.security.authentication.cache;

import java.util.List;

/**
 * Listener to be notified when entries are evicted from the security Cache implementation.
 */
public interface CacheEvictionListener {

    public void evicted(List<Object> victims);
}
