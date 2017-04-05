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
package com.ibm.wsspi.webcontainer.cache;

/**
 *
 */
public interface CacheManager {
    public javax.servlet.Servlet getProxiedServlet(javax.servlet.Servlet s);
    //public javax.servlet.Servlet getSingleThreadModelWrapper(javax.servlet.Servlet s);
    public boolean isStaticFileCachingEnabled(String contextRoot);
}
