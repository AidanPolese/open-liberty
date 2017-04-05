
// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.websphere.servlet.event;


/**
 * Event listener interface used for notifications about servlets.
 * Most of these event have to do with the state management of a
 * servlet's lifecycle.
 * 
 * @ibm-api
 */

public interface ServletListener extends java.util.EventListener{

    /**
     * Triggered just prior to the execution of Servlet.init().
     *
     * @see javax.servlet.Servlet#init
     */
    public void onServletStartInit(ServletEvent evt);

    /**
     * Triggered just after the execution of Servlet.init().
     *
     * @see javax.servlet.Servlet#init
     */
    public void onServletFinishInit(ServletEvent evt);

    /**
     * Triggered just prior to the execution of Servlet.destroy().
     *
     * @see javax.servlet.Servlet#destroy
     */
    public void onServletStartDestroy(ServletEvent evt);

    /**
     * Triggered just after the execution of Servlet.destroy().
     *
     * @see javax.servlet.Servlet#destroy
     */
    public void onServletFinishDestroy(ServletEvent evt);

    /**
     * Triggered when the servlet has become available to process requests.
     * This event always occurs after the servlet had been initialized and
     * has been made available for execution by clients. This event can
     * also occur when the time has expired on an UnavailableException
     * that was thrown by the servlet.
     *
     * @see javax.servlet.UnavailableException
     */
    public void onServletAvailableForService(ServletEvent evt);

    /**
     * Triggered when the servlet has become unavailable to process requests.
     * This event will occur when the servlet throws an UnavailableException
     * or when the servlet is taken offline using some engine administration
     * mechanism.
     *
     * @see javax.servlet.UnavailableException
     */
    public void onServletUnavailableForService(ServletEvent evt);


    /**
     * Triggered when the servlet instance is permanently unloaded from the engine.
     * This event is always triggered when a servlet instance is going to be dereferenced
     * by the servlet engine. ServletListeners cannot rely on the onServlet[XXX]Destroy
     * events to recognize an unload because the servlet could also be unloaded because it
     * failed to initialize properly.  This event guarantees that the listener is notified
     * when the servlet instance is dereferenced by the engine.
     */
    public void onServletUnloaded(ServletEvent evt);
}
