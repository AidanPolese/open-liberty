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
package com.ibm.ws.webcontainer.security;

/**
 * Interface methods to create, add, and get logged out tokens from the LoggedOutTokenMap DistributedMap
 */
public interface LoggedOutTokenCache {

    public Object getDistributedObjectLoggedOutToken(Object key);

    public Object putDistributedObjectLoggedOutToken(Object key, Object value, int timeToLive);

    public Object addTokenToDistributedMap(Object key, Object value);

}
