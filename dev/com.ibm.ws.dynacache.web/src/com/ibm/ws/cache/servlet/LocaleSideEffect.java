// 1.5, 2/10/05
//IBM Confidential OCO Source Material
//5724-I63, 5724-H88, 5655-N01, 5733-W60 (C) COPYRIGHT International Business Machines Corp. 1997, 2005
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.
package com.ibm.ws.cache.servlet;

import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

/**
 * This class is used by the FragmentComposer to remember the content type
 * as part of the state that is remembered just
 * prior to the execution of a JSP so that it can be executed
 * again without executing its parent JSP.
 */
public class LocaleSideEffect implements ResponseSideEffect
{
    private static final long serialVersionUID = -1259252338439044960L;
    private Locale locale = null;


    public String toString() {
       StringBuffer sb = new StringBuffer("Locale side effect: \n\t");
       sb.append("Locale: ").append(locale).append("\n");
       return sb.toString();
    }


    /**
     * Constructor with parameter.
     *
     * @param locale The locale.
     */
    public LocaleSideEffect(Locale locale)
    {
        this.locale = locale;
    }

    /**
     * This resets the state of an HTTP response object to be just
     * as it was prior to executing a JSP.
     *
     * @param response The response object.
     */
    public void performSideEffect(HttpServletResponse response)
    {
       try {
           response.setLocale(locale);
       } catch (IllegalStateException ex) {
         com.ibm.ws.ffdc.FFDCFilter.processException(ex, "com.ibm.ws.cache.servlet.LocaleSideEffect.performSideEffect", "69", this);
       }
    }
}
