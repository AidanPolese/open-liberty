// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//

package com.ibm.websphere.servlet.event;


/**
 * Event listener interface used to receive notifications about filter errors.
 * 
 * @ibm-api
 */


public interface FilterErrorListener extends java.util.EventListener{

    /**
     * Triggered when an error occurs while executing the filter's init() method.
     * This method will be triggered if the filter throws an exception from its init() method.
     *
     * @see javax.servlet.Filter#init
     */
    public void onFilterInitError(FilterErrorEvent evt);

    /**
     * Triggered when an error occurs while executing the filter's doFilter() method.
     * This method will be triggered if the filter throws an exception from its
     * doFilter() method.
     *
     * @see javax.servlet.http.HttpServletResponse#sendError
     * @see javax.servlet.Filter#doFilter
     */
    public void onFilterDoFilterError(FilterErrorEvent evt);
 
    /**
     * Triggered when an error occurs while executing the filter's destroy() method.
     * This method will be triggered if the filter throws an exception from its destroy() method.
     *
     * @see javax.servlet.Servlet#destroy
     */
    public void onFilterDestroyError(FilterErrorEvent evt);

    
}
