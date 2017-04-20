// 1.6, 2/10/05
//IBM Confidential OCO Source Material
//5724-I63, 5724-H88, 5655-N01, 5733-W60 (C) COPYRIGHT International Business Machines Corp. 1997, 2005
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.
package com.ibm.ws.cache.servlet;

import javax.servlet.http.HttpServletResponse;

/**
 * This class is used by the FragmentComposer to remember the 
 * default status code
 * as part of the state that is remembered just
 * prior to the execution of a JSP so that it can be executed 
 * again without executing its parent JSP.
 */
public class DefaultStatusSideEffect implements ResponseSideEffect
{
    private static final long serialVersionUID = -2097619935728959111L;
    
    private int statusCode = 0;

    public String toString() {
       StringBuffer sb = new StringBuffer("Default status side effect: \n\t");
       sb.append("Status code: ").append(statusCode).append("\n");
       return sb.toString();
    }

    /**
     * Constructor with parameter.
     * 
     * @param statusCode The default status code.
     */
    public DefaultStatusSideEffect(int statusCode)
    {
        this.statusCode = statusCode;
    }

    /**
     * This resets the state of an HTTP response object to be just 
     * as it was prior to executing a JSP.
     *
     * @param response The response object.
     */
    public void performSideEffect(HttpServletResponse response)
    {
        response.setStatus(statusCode);
    }
}
