// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
//CHANGE HISTORY
//Flag    Defect         Date         Modified By         Description
//--------------------------------------------------------------------------------------
//        329833         12/07/05      todkap         Add API to register the servlet filter listeners    WAS.webcontainer    
//

package com.ibm.websphere.servlet.event;


/**
 * This event context is used to register listeners for various servlet context events.
 * These events will be triggered by the servlet engine as appropriate during servlet
 * processing. An implementation of this event context is available to all servlets
 * as a ServletContext attribute by using the ServletContext.getAttribute()
 * method.
 *
 * <h3>Sample Usage (from within a servlet):</h3>
 * <pre>
 * ServletContextEventSource sces = (ServletContextEventSource)getServletContext().getAttribute(
 *                                 ServletContextEventSource.ATTRIBUTE_NAME);
 * sces.addServletErrorListener(myErrorListener);
 * </pre>
 * 
 * @ibm-api
 */
public interface ServletContextEventSource{
    /**
     * The ServletContext attribute name that the servlet context event source can be retrieved using.
     */
    public static final String ATTRIBUTE_NAME = "com.ibm.websphere.servlet.event.ServletContextEventSource";

    /**
     * Register a listener for application events.
     */
    public void addApplicationListener(ApplicationListener al);

    /**
     * Deregister a listener for application events.
     */
    public void removeApplicationListener(ApplicationListener al);

    /**
     * Register a listener for servlet invocation events.
     */
    public void addServletInvocationListener(ServletInvocationListener sil);

    /**
     * Deregister a listener for servlet invocation events.
     */
    public void removeServletInvocationListener(ServletInvocationListener sil);

    /**
     * Register a listener for servlet error events.
     */
    public void addServletErrorListener(ServletErrorListener sel);

    /**
     * Deregister a listener for servlet error events.
     */
    public void removeServletErrorListener(ServletErrorListener sel);

    /**
     * Register a listener for servlet events.
     */
    public void addServletListener(ServletListener sl);

    /**
     * Deregister a listener for servlet events.
     */
    public void removeServletListener(ServletListener sl);
    

    /**
     * Register a listener for filter invocation events.
     */
    public void addFilterInvocationListener(FilterInvocationListener fil);

    /**
     * Deregister a listener for filter invocation events.
     */
    public void removeFilterInvocationListener(FilterInvocationListener fil);
    
    /**
     * Register a listener for filter error events.
     */
    public void addFilterErrorListener(FilterErrorListener fil);

    /**
     * Deregister a listener for filter error events.
     */
    public void removeFilterErrorListener(FilterErrorListener fil);
    
    /**
     * Register a listener for filter events.
     */
    public void addFilterListener(FilterListener fil);

    /**
     * Deregister a listener for filter events.
     */
    public void removeFilterListener(FilterListener fil);

}
