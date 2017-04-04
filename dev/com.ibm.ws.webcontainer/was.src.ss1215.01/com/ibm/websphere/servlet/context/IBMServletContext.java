// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2008
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
//CHANGE HISTORY
//Flag    Defect         Date         Modified By         Description
//--------------------------------------------------------------------------------------
//      303840          09/08/05        todkap            move sendError from IServletContext to IBMServletContext    WASCC.web.webcontainer
//      331946          12/13/05        todkap            Provide getContextPath method in IBMServletContext    WAS.webcontainer
//      436940          05/11/07        mmolden           70FVT: Using RRD from a JSP causes ClassCastException
//  


package com.ibm.websphere.servlet.context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSessionListener;

import com.ibm.websphere.servlet.error.ServletErrorReport;
import com.ibm.websphere.webcontainer.async.AsyncRequestDispatcher;

import java.util.Properties;

/**
 * 
 * @ibm-private-in-use
 * 
 * This interface extends the javax.servlet.ServletContext functionality to provide additional
 * useful functions for third party products requiring tighter integration into the WebSphere
 * webcontainer.
 * 
 * This interface provides methods to dynamically add and remove servlet instances into web module.
 * The interface <code>IBMServletContext</code> can also be used to drive httpsession events on
 * listeners defined  in deployment descriptor(web.xml) of a web module. This helps 
 * applications to develop customized <code>javax.servlet.http.HttpSession</code> implementations.
 * 
 * <p>
 * For Example; to get at IBMServletContext
 * <p><blockquote><pre>
 *    IBMServletContext sessCtx = (IBMServletContext) servletContext;
 * </pre></blockquote><p>
 * 
 * 
 *  @since   WAS5.0
 */
public interface IBMServletContext extends ExtendedServletContext
{
    /**
     * To drive HttpSession created event
     * 
     * @param      event  object on which event is to be triggered.
     * @since WAS 5.0
     */
    public void fireSessionCreated(HttpSessionEvent event);
    /**
     * To drive HttpSession invalidated event
     * 
     * @param      event  object on which event is to be triggered.
     * @since WAS 5.0
     */
    public void fireSessionDestroyed(HttpSessionEvent event);

    /**
     * To drive attribute added event
     * 
     * @param      event  object on which event is to be triggered.
     * @since WAS 5.0
     */
    public void fireSessionAttributeAdded(HttpSessionBindingEvent event);

    /**
     * To drive attribute replaced event
     * 
     * @param      event  object on which event is to be triggered
     * @since WAS 5.0
     */
    public void fireSessionAttributeReplaced(HttpSessionBindingEvent event);

    /**
     * To drive attribute removed event
     * 
     * @param      event  object on which event is to be triggered
     * @since WAS 5.0
     */
    public void fireSessionAttributeRemoved(HttpSessionBindingEvent event);

    /**
     *   To check if timeout is set in deployment descriptor of the web module(web.xml) or not
     * 
     *   @return  true if session timeout is set
     *             false if session timeout is set to zero or not set.
     *   @since WAS 5.0
     */
    public boolean isSessionTimeoutSet();

    /**
     * To get at session timeout used by web module
     * 
     * @return returns session timeout of the web module.
     * @since WAS 5.0
     */
    public int getSessionTimeout();
    
    
    /**
     * To get the context root associated with this web module
     * 
     * Returns the context root for this context
     * @return
     * @since WAS 6.1
     */
    public String getContextPath();

    

    /**
     * To add servlet instance dynamically into a web module. If a security manager exists, accessing this method
     * requires com.ibm.websphere.security.WebSphereRuntimePermission with  target name "accessServletContext".
     * 
     * @param  servletName     Name of the Servlet that is being added
     * 		    servletClass    Class name of the servlet
     * 		    mappingURI      URI for the servlet
     *          initParameters  init parameters of the servlet
     * 
     * @throws SecurityException  if a security manager exists and it doesn't allow the current operation .
     * 			ServletException   if a servlet with same name already exists
     * 
     * @since WAS 5.01
     * 
     * @deprecated
     * 
     */
    public void addDynamicServlet(String servletName, String servletClass, String mappingURI, Properties initParameters) throws ServletException, java.lang.SecurityException;


    /**
     * To remove servlet instance dynamically from a web module. If a security manager exists, accessing this method
     * requires com.ibm.websphere.security.WebSphereRuntimePermission with  target name "accessServletContext".
     * 
     * @param  servletName     Name of the Servlet that is to be removed
     * 
     * @throws SecurityException  if a security manager exists and it doesn't allow the  current operation .
     * 
     * @since WAS 5.01
     * 
     * @deprecated
     */
    public void removeDynamicServlet(String servletName) throws java.lang.SecurityException;

    /**
        * To add session listener dynamically into a web module. If a security manager exists, accessing this method
        * requires com.ibm.websphere.security.WebSphereRuntimePermission with  target name "accessServletContext".
        * 
        * @param  listener     Instance of HttpSessionListener
        * 
        * @throws SecurityException  if a security manager exists and it doesn't allow the  current operation .
        * 
        * @since WAS 5.01
        * 
        * @deprecated
        */
    public void addHttpSessionListener(HttpSessionListener listener) throws java.lang.SecurityException;

    /**
         * To load a servlet instance. If servlet is already loaded, this method simply returns. If a security manager exists, accessing this method
         * requires com.ibm.websphere.security.WebSphereRuntimePermission with  target name "accessServletContext".
         * 
         * @param  servletName     Name of the Servlet that is to be loaded
         * 
         * @throws SecurityException  if a security manager exists and it doesn't allow the current operation .
         * 			ServletException   if a servlet with same name already exists
         * 
         * @since WAS 5.01
         * 
         * @deprecated
         * 
         */
    public void loadServlet(String servletName) throws ServletException, java.lang.SecurityException;

    
    /**
     * Sends an error response back to the client. This method will inspect the 
     * information in the servlet error report specified, and invoke any error
     * pages that have been defined by this application.
     * 
     * @param request
     * @param response
     * @param e
     */
    public void sendError(HttpServletRequest request, HttpServletResponse response, ServletErrorReport e);
}
