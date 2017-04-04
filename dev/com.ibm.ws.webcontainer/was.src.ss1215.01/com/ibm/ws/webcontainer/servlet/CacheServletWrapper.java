// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.


//  CHANGE HISTORY
//  Flag    Defect         Date         Modified By         Description
//--------------------------------------------------------------------------------------
//          324991          11/21/05    mmolden             61FVT: NullPtrExpn when logging out after admin console addNode
//          351214          03/02/06    mmolden             SVT:Restart Application under client load, get null pointers
//          366965          06/22/06    mmolden             Reduce number of lines enclosed in synchronized in    WASCC.web.webcontainer
//          541113          08/06/08    mmolden             PERF: Use StringBuilder instead of StringBuffer in WebContainer

//Code added as part of LIDB 2283-4
package com.ibm.ws.webcontainer.servlet;

import java.util.HashMap;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.logging.Logger;
import java.util.logging.Level;

import com.ibm.wsspi.webcontainer.collaborator.CollaboratorHelper;
import com.ibm.wsspi.webcontainer.logging.LoggerFactory;
import com.ibm.websphere.servlet.error.ServletErrorReport;
import com.ibm.wsspi.webcontainer.servlet.IExtendedRequest;
import com.ibm.ws.webcontainer.servlet.exception.NoTargetForURIException;
import com.ibm.ws.webcontainer.util.InvalidCacheTargetException;
import com.ibm.ws.webcontainer.webapp.WebApp;
import com.ibm.ws.webcontainer.webapp.WebAppDispatcherContext;
import com.ibm.ws.webcontainer.webapp.WebAppErrorReport;
import com.ibm.wsspi.webcontainer.RequestProcessor;
import com.ibm.wsspi.webcontainer.servlet.IServletWrapper;
import com.ibm.wsspi.webcontainer.servlet.ServletReferenceListener;

/**
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
@SuppressWarnings("unchecked")
public class CacheServletWrapper implements RequestProcessor, ServletReferenceListener
{
protected static Logger logger = LoggerFactory.getInstance().getLogger("com.ibm.ws.webcontainer.servlet");
	private static final String CLASS_NAME="com.ibm.ws.webcontainer.servlet.CacheServletWrapper";

	private String _servletPath;
	private String _pathInfo;
    private volatile IServletWrapper cacheTarget;

    private HashMap cache;
    private String cacheKeyStr;

    private WebApp webapp;
    private String requestUri;
	/**
	 * 
	 */
	public CacheServletWrapper(IServletWrapper wrapper, HttpServletRequest req, HashMap cache, StringBuilder cacheKey, WebApp webapp)
	{
		super();
		cacheTarget = wrapper;
		cacheTarget.addServletReferenceListener(this);
		this._pathInfo=req.getPathInfo();
		this._servletPath = req.getServletPath();
		this.requestUri = req.getRequestURI();
		this.cache = cache;
		this.cacheKeyStr = cacheKey.toString(); 
		this.webapp = webapp;
		cache.put(cacheKeyStr, this);
	}

	/* (non-Javadoc)
	 * @see com.ibm.ws.webcontainer.core.RequestProcessor#handleRequest(com.ibm.ws.webcontainer.core.Request, com.ibm.ws.webcontainer.core.Response)
	 */
	public void handleRequest(ServletRequest req, ServletResponse res) throws Exception
	{
        IServletWrapper target = this.cacheTarget;
		
    	if (target != null) {
    		try {
    			webapp.getFilterManager().invokeFilters((HttpServletRequest) req, (HttpServletResponse) res, webapp, target, CollaboratorHelper.allCollabEnum);
    		} catch (Throwable th){
    			webapp.handleException(th, req, res, this);
    		}
    	}
		else
			throw InvalidCacheTargetException.instance();

	}

	/**
	 * @return
	 */
	public String getPathInfo()
	{
		return _pathInfo;
	}

	/**
	 * @return
	 */
	public String getServletPath()
	{
		return _servletPath;
	}

	/**
	 * @param string
	 */
	public void setPathInfo(String string)
	{
		_pathInfo = string;
	}

	/**
	 * @param string
	 */
	public void setServletPath(String string)
	{
		_servletPath = string;
	}

	/**
	 * @return
	 */
	public WebApp getWebApp()
	{
		if (cacheTarget != null)
			return webapp;
		else
			throw InvalidCacheTargetException.instance();
	}
	
	/**
	 * @see com.ibm.ws.webcontainer.util.CacheWrapper#invalidate()
	 * 
	 * Called by the ServletWrapper when it is being destroy()ed. 
	 */
	public synchronized void invalidate()
	{
		// Remove this cached wrapper from the appropriate cache
        if (cacheTarget != null){
			if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))  //306998.15
				logger.logp(Level.FINE, CLASS_NAME,"invalidate", "first invalidate");
			cache.remove(cacheKeyStr);

			// nullify the cache target
			cacheTarget = null;

            
            cache = null;
			_servletPath = null;
			_pathInfo = null;
            webapp = null;
			cacheKeyStr = null;
		}
		else{
			if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))  //306998.15
				logger.logp(Level.FINE, CLASS_NAME,"invalidate", "additional invalidate");
		}
	}
	/**
	 * @return
	 */
	public String getRequestUri()
	{
		return requestUri;
	}

        // LIDB3816
        public String getCacheKeyString () {
             return this.cacheKeyStr;
        }

        // LIDB3816
        public IServletWrapper getCacheTarget () {
             return this.cacheTarget;
        }
        
    	public String toString(){
    		String s;
    		if (this.webapp==null)
    		{
    			s =
    			" Webapp nulled::  " + this.webapp + "  "
    					+ " ServletPath:: " + this._servletPath+ "  "
    					+ " CacheKey ::" + cacheKeyStr+ "  "
    					+" _pathInfo::"+this._pathInfo+ "  "
    					+" requestUri ::"+this.requestUri+ "  ";
    		}
    		else
    		{
    			 s =
    	    			" Webapp::  " + this.webapp + "  "
    	    					+ " ServletPath:: " + this._servletPath+ "  "
    	    					+ " CacheKey ::" + cacheKeyStr+ "  "
    	    					+" _pathInfo::"+this._pathInfo+ "  "
    	    					+" requestUri ::"+this.requestUri+ "  "
    	    					+"ApplicationName::"+this.webapp.getApplicationName()+ "  "
    	    					+"WebAppContxtPath::"+this.webapp.getContextPath();
    		}

    		return s;
    	}

    public boolean isInternal (){
    	return cacheTarget.isInternal();
    }
    
    public String getName (){
    	return cacheTarget.getServletName();
    }

}
