// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//

package com.ibm.websphere.servlet.event;

/**
 * Event listener interface used for notifications about Servlet service invocations.
 * Implementors of this interface must be very cautious about the time spent processing
 * these events because they occur on the servlet's actual request processing path.
 * 
 * @ibm-api
 */
public interface ServletInvocationListener extends java.util.EventListener{

    /**
     * Triggered just prior to the execution of Servlet.service().
     *
     * @see javax.servlet.service
     */
    public void onServletStartService(ServletInvocationEvent evt);

    /**
     * Triggered just after the execution of Servlet.service().
     *
     * @see javax.servlet.service
     */
    public void onServletFinishService(ServletInvocationEvent evt);

}
