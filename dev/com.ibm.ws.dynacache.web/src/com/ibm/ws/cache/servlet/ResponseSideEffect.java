// 1.4, 9/30/04
// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 1997, 2004
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.ws.cache.servlet;

import java.io.Serializable;

import javax.servlet.http.HttpServletResponse;

/**
 * When a JSP must be executed without its parent JSP being executed
 * (i.e., the child is not in the cache but the parent was in the cache),
 * the side effect of any code in the parent that changes the response
 * object is also cached so that the response can be put back in the 
 * correct state for child execution. 
 * This interface provides a method to apply the side effect to the 
 * response object.  
 * This interface is supported in the AddCookieSideEffect,
 * ContentLengthSideEffect, ContentTypeSideEffect, DateHeaderSideEffect,
 * DefaultStatusSideEffect, HeaderSideEffect and StatusSideEffect
 * classes. 
 */
public interface ResponseSideEffect 
extends Serializable
{
    /**
     * This executes the side effect on the response object. 
     * 
     * @param response The response object that the side effect applies to.
     */
    public void
    performSideEffect(HttpServletResponse response);
}
