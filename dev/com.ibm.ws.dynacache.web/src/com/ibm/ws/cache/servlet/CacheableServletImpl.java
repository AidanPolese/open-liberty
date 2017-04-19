//1.8, 2/10/05
//IBM Confidential OCO Source Material
//5724-I63, 5724-H88, 5655-N01, 5733-W60 (C) COPYRIGHT International Business Machines Corp. 1997, 2005
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.
package com.ibm.ws.cache.servlet;


import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import com.ibm.websphere.cache.EntryInfo;
import com.ibm.websphere.servlet.cache.CacheableServlet;



public class CacheableServletImpl extends HttpServlet implements CacheableServlet
{
    private static final long serialVersionUID = -8913065289974983288L;
    
    /**
     * This implements the method in the CacheableServlet interface.
     *
     * @param request The HTTP request object.
     * @return The cache id.  A null indicates that the JSP should 
     * not be cached.
     */
    public String getId(HttpServletRequest request)
    {
        return null;
    }    

    /**
     * This implements the method in the CacheableServlet interface.
     *
     * @param request The HTTP request object.
     * @return The cache id.
     */
    public int getSharingPolicy(HttpServletRequest request)
    {
        return EntryInfo.NOT_SHARED;
    }
}
