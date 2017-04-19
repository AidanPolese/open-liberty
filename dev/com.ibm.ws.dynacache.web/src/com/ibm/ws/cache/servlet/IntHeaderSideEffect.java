// 1.7, 2/10/05
//IBM Confidential OCO Source Material
//5724-I63, 5724-H88, 5655-N01, 5733-W60 (C) COPYRIGHT International Business Machines Corp. 1997, 2005
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.
package com.ibm.ws.cache.servlet;

import javax.servlet.http.HttpServletResponse;

/**
 * This class is used by the FragmentComposer to remember an int header
 * as part of the state that is remembered just
 * prior to the execution of a JSP so that it can be executed
 * again without executing its parent JSP.
 */
public class IntHeaderSideEffect implements ResponseSideEffect
{
    private static final long serialVersionUID = -9186017256661209156L;
    private String name = null;
    private int value = 0;
    private boolean set = true;

    public String toString() {
       StringBuffer sb = new StringBuffer("Integer header side effect: \n\t");
       sb.append("Name: ").append(name).append("\n\t");
       sb.append("Value: ").append(value).append("\n");
       return sb.toString();
    }


    /**
     * Constructor with parameter.
     *
     * @param name The header name.
     * @param value The header value.
     */
    public IntHeaderSideEffect(String name, int value,boolean set)
    {
        this.name = name;
        this.value = value;
        this.set = set;
    }

    /**
     * This resets the state of an HTTP response object to be just
     * as it was prior to executing a JSP.
     *
     * @param response The response object.
     */
    public void performSideEffect(HttpServletResponse response)
    {
        if (set)
           response.setIntHeader(name, value);
        else
           response.addIntHeader(name, value);
    }
}
