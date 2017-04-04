// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
//CHANGE HISTORY
//Defect        Date        Modified By         Description
//--------------------------------------------------------------------------------------
//PK27620       08/23/06    cjhoward            SERVLET FILTER IS NOT CALLED IN V6 FOR URL RESOURCES WHEN THESE ARE NOT FOUND.  IN V5, THE FILTER IS ALWAYS CALLED

package com.ibm.wsspi.webcontainer.servlet;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.ibm.websphere.servlet.error.ServletErrorReport;
import com.ibm.wsspi.webcontainer.webapp.NamespaceInvoker;
import com.ibm.wsspi.webcontainer.RequestProcessor;

/**
 * 
 * 
 * Generic RequestProcessor representation of a compiled servlet. Any entity which
 * ends up begin a Servlet will be wrapper by a wrapper of this type. This wrapper
 * may be added as targets into the ServletContext, and the webcontainer will
 * dispatch requests intended for this resource in an optimized way.
 * 
 * NOTE: Components wishing to provide their own servlet wrappers are strongly
 * urged to extend the GenericServletWrapper class, which has convenience methods
 * to help with better integration with the webcontainer
 * 
 * @see GenericServletWrapper
 * @ibm-private-in-use
 */
public interface IServletWrapper extends RequestProcessor
{
	/**
	 * Returns the servlet config associated with this servlet wrapper
	 * @return
	 */
	public IServletConfig getServletConfig();
	
	/**
	 * Returns the servlet context associated with this servlet wrapper.
	 * 
	 * @return
	 */
	
	public ServletContext getServletContext ();
	
	/**
	 * Sets the parent context for this servletwrapper
	 * @return
	 */
	public void setParent(IServletContext parent);
	
	/**
	 * Returns the servlet name of the servlet wrapped by this servlet wrapper
	 * @return
	 */
	public String getServletName();
	
	/**
	 * Instructs the webcontainer to use the specified ClassLoader to load the
	 * Servlet wrapped by this servlet wrapper.
	 * @param loader
	 */
	void setTargetClassLoader(ClassLoader loader);
	
	/**
	 * Returns the ClassLoader instance that was used to load, or will be used 
	 * to load the Servlet wrapped by this servlet wrapper
	 * @param loader
	 */
	public ClassLoader getTargetClassLoader();
	
	/**
	 * Sets the target Servlet that this wrapper should wrap
	 * @param target
	 */
	public void setTarget(Servlet target);
	
	/**
	 * Gets the target Servlet that this wrapper is wrapping
	 * @return
	 */
	public Servlet getTarget();
	
	/**
	 * Initializes this wrapper with the specified config. Depending on the
	 * startup weight specified in the config, the underlying target Servlet 
	 * will either be initialized within this call. 
	 * 
	 * NOTE: This initialization behaviour of the target Servlet can be controlled by 
	 * calling the setStartUpWeight() method on the IServletConfig
	 * 
	 * @see IServletConfig
	 * 
	 * @param config
	 * @throws Exception
	 */
	public void initialize(IServletConfig config) throws Exception;

	/**
	 * Initializes this wrapper with the specified config. Depending on the
	 * startup weight specified in the config, the underlying target Servlet 
	 * will either be initialized within this call. 
	 * 
	 * NOTE: This initialization behaviour of the target Servlet can be controlled by 
	 * calling the setStartUpWeight() method on the IServletConfig
	 * 
	 * @see IServletConfig
	 * 
	 * @param config
	 * @throws Exception
	 */
	public void loadOnStartupCheck() throws Exception;

	/**
	 * This method will be called by the webcontainer when a request is intended 
	 * for this wrapper. Classes directly implementing this interface (without
	 * extending GenericServletWrapper) will have to handle all aspects of the
	 * request processing.
	 * 
	 * NOTE: Components wishing to provide their own servlet wrappers are strongly
	 * urged to extend the GenericServletWrapper class, which has convenience methods
	 * to help with better integration with the webcontainer. Subclasses can delegate 
	 * request processing to the GenericServletWrapper by invoking the
	 * super.handleRequest() method
	 */
	public void handleRequest(ServletRequest req, ServletResponse res) throws Exception;
	
	/**
	 * Signals that this wrapper is going to be recycled
	 *
	 */
	public void prepareForReload();

	/**
	 * Adds a ServletReferenceListener to this wrapper
	 * @param wrapper
	 */
	public void addServletReferenceListener(ServletReferenceListener wrapper);
    
    /**
     * Returns the time when this wrapper was last accessed
     * @return
     */
    public long getLastAccessTime();
    
    /**
     * Signals that this wrapper should now be destroyed.
     *
     */
    public void destroy();

	/**
	 * @param request
	 * @param response
	 */
	public void service(ServletRequest request, ServletResponse response) throws IOException, ServletException;
	
//	 begin 268176    Welcome file wrappers are not checked for resource existence    WAS.webcontainer
	/**
	 * Returns whether the requested wrapper resource exists.
	 */
	public boolean isAvailable ();
//	 end 268176    Welcome file wrappers are not checked for resource existence    WAS.webcontainer
	
	/**
	 * Loads the servlet and calls the Servlet's init method with the previously passed IServletConfig.
	 * One component that calls this is SIP.
	 */
    public void load() throws Exception;

    //modifies the target for SingleThreadModel & caching
    public void modifyTarget(Servlet s);

}
