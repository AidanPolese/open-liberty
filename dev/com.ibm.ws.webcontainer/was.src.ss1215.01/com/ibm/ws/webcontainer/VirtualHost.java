// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2008
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
//
//  CHANGE HISTORY
// Defect       Date        Modified By     Description
//--------------------------------------------------------------------------------------
// 283348.1     07/26/05    ekoonce         Fix Tr.* statements
// 293696       07/27/05    mmolden         ServletRequest.getPathInfo() fails
// 296368       08/05/05    todkap          Nested exceptions lost for problems during application startup
// 323294       11/11/05    todkap          allow virtual hosts to use same host/port combinations    WAS.webcontainer
// 340680       01/24/06    mmolden         content type fails when static resource has path element
// PK37449      04/26/07    ekoonce         A THREAD DEADLOCK MAY OCCUR
// PK67698      09/02/08    mmolden         NULL POINTER EXCEPTION IN THE WEBCONTAINER CODE
//PK74092       11/10/08    mmolden         LOAD-ON-STARTUP INDICATION FOR SIPLETS IS NOT LOADING THE
//
package com.ibm.ws.webcontainer;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import java.util.logging.Logger;
import java.util.logging.Level;
import com.ibm.wsspi.webcontainer.logging.LoggerFactory;
import com.ibm.wsspi.webcontainer.util.URIMapper;
import com.ibm.ws.container.Container;
import com.ibm.ws.container.DeployedModule;
import com.ibm.ws.http.Alias;
import com.ibm.ws.webcontainer.core.BaseContainer;
import com.ibm.wsspi.webcontainer.servlet.IExtendedRequest;
import com.ibm.ws.webcontainer.exception.WebAppNotLoadedException;
import com.ibm.ws.webcontainer.exception.WebGroupVHostNotFoundException;
import com.ibm.ws.webcontainer.session.IHttpSessionContext;
import com.ibm.ws.webcontainer.webapp.WebApp;
import com.ibm.ws.webcontainer.webapp.WebAppConfiguration;
import com.ibm.ws.webcontainer.webapp.WebAppDispatcherContext;
import com.ibm.ws.webcontainer.webapp.WebGroup;
import com.ibm.ws.webcontainer.webapp.WebGroupConfiguration;
import com.ibm.wsspi.webcontainer.RequestProcessor;

public abstract class VirtualHost extends BaseContainer
{
        protected static Logger logger = LoggerFactory.getInstance().getLogger("com.ibm.ws.webcontainer");
	private static final String CLASS_NAME="com.ibm.ws.webcontainer.VirtualHost";
	
	protected VirtualHostConfiguration vHostConfig;
    
	public VirtualHost(String name, Container parent)
	{
		super(name, parent);
		requestMapper = new URIMapper(true);
 	}

	public void init(VirtualHostConfiguration vHostConfig)
	{
		this.vHostConfig = vHostConfig;
		// begin 280649    SERVICE: clean up separation of core and shell    WASCC.web.webcontainer    
		// resetting name causes problems when removeSubcontainer is called.  addSubcontainer was added with another value.
		//this.name = vHostConfig.getName();
		// end 280649    SERVICE: clean up separation of core and shell    WASCC.web.webcontainer    
	}

	/**
	 * Method addWebApplication.
	 * @param deployedModule
     * @param extensionFactories
	 */
	//BEGIN: NEVER INVOKED BY WEBSPHERE APPLICATION SERVER (Common Component Specific)
    @SuppressWarnings("unchecked")
    public void addWebApplication(DeployedModule deployedModule, List extensionFactories) throws WebAppNotLoadedException
	{
		WebGroupConfiguration wgConfig = deployedModule.getWebGroupConfig();
		WebGroup wg = deployedModule.getWebGroup();
		String contextRoot = deployedModule.getContextRoot();

 		if (!contextRoot.startsWith("/"))
			contextRoot = "/" + contextRoot;
			
		if (contextRoot.endsWith("/") && !contextRoot.equals("/"))
			contextRoot = contextRoot.substring(0, contextRoot.length() - 1);
		
		String ct = contextRoot; // proper
			
		if (contextRoot.equals("/"))
			contextRoot += "*";
		else
			contextRoot += "/*";		
			
		String displayName = deployedModule.getDisplayName();

        WebGroup webGroup = (WebGroup) requestMapper.map(contextRoot);

		if (webGroup != null && ct.equalsIgnoreCase(webGroup.getConfiguration().getContextRoot()))
		{
			//begin 296368    Nested exceptions lost for problems during application startup    WAS.webcontainer    
   			List list = webGroup.getWebApps();
   			String originalName = "";
   			if(list != null && (list.size() > 0)){
   				WebApp originalWebApp = (WebApp)list.get(0);
   				originalName = originalWebApp.getWebAppName();
   			}
   			logger.logp(Level.SEVERE, CLASS_NAME,"addWebApplication", "context.root.already.in.use", new Object[] {displayName, contextRoot, originalName ,displayName});
			throw new WebAppNotLoadedException("Context root "+contextRoot+" is already bound. Cannot start application "+displayName);
			// end 296368    Nested exceptions lost for problems during application startup    WAS.webcontainer    

		}
		else
		{		
			webGroup = wg;
			// begin LIDB2356.1:	WebContainer work for incorporating SIP
			setWebAppVirtualHostList(deployedModule.getWebAppConfig());
			
			wgConfig.setWebAppHost(this);
			// end LIDB2356.1:	WebContainer work for incorporating SIP 
			webGroup.initialize(wgConfig);
			
		}
		
		try
		{
			webGroup.addWebApplication(deployedModule, extensionFactories);
			Object[] args = {displayName, vHostConfig.toString()};
			logger.logp(Level.INFO, CLASS_NAME,"addWebApplication", "module.[{0}].successfully.bound.to.virtualhost.[{1}]", args);
		}
		catch (Throwable t)
		{
			//requestMapper.removeMapping(contextRoot);
			//Do not need to remove mapping because we wait until we're sure we should add it!
            //PK67698 removeMapping(contextRoot); 
 			throw new WebAppNotLoadedException (t.getMessage(), t);		// 296368 added rootCause to newly created exception.
		}
		
		//PK67698 Start
		try
		{
			
            addMapping (contextRoot, webGroup);
            webGroup.notifyStart();
		}
		catch (Exception exc)
		{
		    // begin 296368    Nested exceptions lost for problems during application startup    WAS.webcontainer    
          if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)){
        	  logger.logp(Level.FINE, CLASS_NAME,"addWebApplication", "error adding mapping ", exc);    /*@283348.1*/
          }
            webGroup.destroy();
            throw new WebAppNotLoadedException("Context root "+contextRoot+" mapping unable to be bound. Application "+displayName + " unavailable.", exc);  
			// end 296368    Nested exceptions lost for problems during application startup    WAS.webcontainer    
		}   
		//PK67698 End
	}
	//END: NEVER INVOKED BY WEBSPHERE APPLICATION SERVER (Common Component Specific)
	
    @SuppressWarnings("unchecked")
	protected void setWebAppVirtualHostList(WebAppConfiguration config){
		Alias [] aliases =vHostConfig.getAliases();
		List virtualHostList = config.getVirtualHostList();
        for (int i=0; i < aliases.length; i++){
        	String hostName =aliases[i].getHostname();
        	String port = aliases[i].getPort();
        	virtualHostList.add( hostName + ":" + port);
        }

        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)){
        	logger.logp(Level.FINE, CLASS_NAME,"setWebAppVirtualHostList", "virtualHostList -->"  + virtualHostList);
        }
	}

	/**
	 * Method getMimeType.
	 * @param file
	 * @return String
	 */
	public String getMimeType(String withDot, String withoutDot)
	{		
		String type = vHostConfig.getMimeType(withoutDot);
		
		if (type == null)
			type = vHostConfig.getMimeType(withDot);

		return type;
	}

	/**
	 * Method getSessionContext.
	 * @param moduleConfig
	 * @param webApp
	 * @return IHttpSessionContext
	 */
    @SuppressWarnings("unchecked")
	public IHttpSessionContext getSessionContext(DeployedModule moduleConfig, WebApp webApp, ArrayList[] listeners) throws Throwable
	{
		//System.out.println("Vhost createSC");
		return ((WebContainer) parent).getSessionContext(moduleConfig, webApp, this.vHostConfig.getName(), listeners);
	}

	/**
	 * Method findContext.
	 * @param path
	 * @return ServletContext
	 */
	public ServletContext findContext(String path)
	{
		WebGroup g = (WebGroup) requestMapper.map(path);
		if (g != null)
			return g.getContext();
		else
			return null;
	}
	
	//PK37449 synchronizing method
	public synchronized void destroy()
	{
		super.destroy();
		
		requestMapper = null;
		this.vHostConfig = null;
	}
	
	
	public void handleRequest(ServletRequest req, ServletResponse res)
		throws Exception
	{
		//Begin 280335, Context-roots with dbcs characters are failed to be resolved.
		//Begin 293696    ServletRequest.getPathInfo() fails    WASCC.web.webcontainer
		WebAppDispatcherContext dispatchContext = (WebAppDispatcherContext)((IExtendedRequest) req).getWebAppDispatcherContext();
		String reqURI = dispatchContext.getDecodedReqUri();
//		String undecodedURI = ((Request) req).getRequestURI();
//		String reqURI = URLDecoder.decode(undecodedURI,WebContainer.getWebContainer().getURIEncoding());
//		((Request) req).getWebAppDispatcherContext().setDecodedReqUri(reqURI);
//		if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {
//			logger.logp(Level.FINE, CLASS_NAME,"handleRequest", "Looking for webgroup for --> (not decoded="+undecodedURI + "), (decoded=" + reqURI +")");
//		}
		//End 293696    ServletRequest.getPathInfo() fails    WASCC.web.webcontainer
		//End 280335
		RequestProcessor g = (RequestProcessor) requestMapper.map(reqURI);
		if (g != null) {
			g.handleRequest(req, res);
		}
		else {
			throw new WebGroupVHostNotFoundException(reqURI);
		}
	}
	
	/**
	 * Method removeWebApplication.
	 * @param deployedModule
	 */
	public void removeWebApplication(DeployedModule deployedModule)
	{
		String contextRoot = deployedModule.getContextRoot();	
		//begin 280649    SERVICE: clean up separation of core and shell    WASCC.web.webcontainer : reuse with other VirtualHost impls.
		removeWebApplication (deployedModule, contextRoot);
		//end 280649    SERVICE: clean up separation of core and shell    WASCC.web.webcontainer : reuse with other VirtualHost impls.
		
	}
	
	//begin 280649    SERVICE: clean up separation of core and shell    WASCC.web.webcontainer : reuse with other VirtualHost impls.   
	public void removeWebApplication (DeployedModule deployedModule, String contextRoot)
	{
		//boolean restarting = deployedModule.isRestarting();
		if (!contextRoot.startsWith("/"))
			contextRoot = "/" + contextRoot;
			
		if (contextRoot.endsWith("/") && !contextRoot.equals("/"))
			contextRoot = contextRoot.substring(0, contextRoot.length() - 1);
			
	       
                //PK37449 adding synchronization block
                WebGroup webGroup=null;
                    		
                if (requestMapper!=null)
                {
                    synchronized(this){
                        if (requestMapper != null) {
                            webGroup= (WebGroup) requestMapper.map(contextRoot);
                            removeMapping((contextRoot.equals("/")) ? (contextRoot + "*") : (contextRoot + "/*"));
                        }
                    }
                }
                    		
                if (webGroup!=null){
                    // Begin 284644, reverse removal of web applications from map to prevent requests from coming in after app removed
                                
                    //requestMapper.removeMapping((contextRoot.equals("/")) ? (contextRoot + "*") : (contextRoot + "/*")); // coz it was added that way
                	//if (!restarting)
                		removeMapping((contextRoot.equals("/")) ? (contextRoot + "*") : (contextRoot + "/*"));
                                
                    webGroup.removeWebApplication(deployedModule);
                    // End 284644, reverse removal of web applications from map to prevent requests from coming in after app removed
                
                    //PK37449 adding trace and call to removeSubContainer() from AbstractContainer.  
                    //        The call that was in WebGroup was removed.

                    if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
                        logger.logp(Level.FINE, CLASS_NAME,"removeWebApplication", "name: " + webGroup.getName());
                    //if (!restarting)
                    	removeSubContainer(webGroup.getName());
                    //PK37449 end
                }
	}
	//end  280649    SERVICE: clean up separation of core and shell    WASCC.web.webcontainer
    
    protected synchronized void addMapping(String contextRoot, WebGroup group) throws Exception{
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)){
            logger.logp(Level.FINE, CLASS_NAME,"addMapping", " contextRoot -->" + contextRoot + " group -->" + group.getName());
        }
        requestMapper.addMapping(contextRoot, group);
    }

    protected void removeMapping(String contextRoot){
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)){
            logger.logp(Level.FINE, CLASS_NAME,"removeMapping", " contextRoot -->" + contextRoot );
        }
        requestMapper.removeMapping(contextRoot);
    }

    
    
}
