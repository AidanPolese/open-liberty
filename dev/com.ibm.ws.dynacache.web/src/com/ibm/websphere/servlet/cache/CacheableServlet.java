// 1.6, 9/30/04
// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 1997, 2004
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.websphere.servlet.cache;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

/**
 * This interface identifies cacheable servlets to the fragment cache. 
 * The cache will call the getId() and getSharingPolicy() methods to 
 * obtain the caching metadata for a given execution of the servlet.
 * @ibm-api 
 */
public interface CacheableServlet extends Serializable {

    /**
     * This executes the algorithm to compute the cache id.
     *
     * @param request The HTTP request object.
     * @return The cache id.  A null indicates that the servlet should 
     * not be cached.
     * @ibm-api 
     */
    public String getId(HttpServletRequest request);    

    /**
     * This returns the sharing policy for this cache entry.
     * See com.ibm.websphere.servlet.cache.EntryInfo for possible
     * values.
     *
     * @param request The HTTP request object.
     * @return The sharing policy
     * @ibm-api 
     */
    public int getSharingPolicy(HttpServletRequest request);
}
