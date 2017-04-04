// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
//
//CHANGE HISTORY
//Flag    Defect         Date         Modified By         Description
//--------------------------------------------------------------------------------------
//        PK45107         08/30/07    mmolden            EXCEPTION FROM SERVLET FILTER IS NOT PROPAGATED TO CLIENT
//        PM50111         12/12/11    anupag             Updated few Trace points
//
package com.ibm.wsspi.webcontainer;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.ws.util.WSThreadLocal;
import com.ibm.wsspi.webcontainer.servlet.AsyncContext;
import com.ibm.wsspi.webcontainer.servlet.IExtendedRequest;
import com.ibm.wsspi.webcontainer.servlet.IExtendedResponse;

/**
*
* 
* WebContainerRequestState is the thread local used to store per request info that can't be passed because of method
* signature requirements
* @ibm-private-in-use
*/
@SuppressWarnings("unchecked")
public class WebContainerRequestState {
	static protected Logger logger= Logger.getLogger("com.ibm.wsspi.webcontainer");
	private static final String CLASS_NAME="com.ibm.wsspi.webcontainer.WebContainerRequestState";
	private Map _attributes = null;
	private boolean _invokedFilters;
	private boolean completed;
	private boolean ardRequest;
    private boolean startAsync;
    private IExtendedRequest currentThreadsIExtendedRequest;
    private IExtendedResponse currentThreadsIExtendedResponse;
    public IExtendedResponse getCurrentThreadsIExtendedResponse() {
		return currentThreadsIExtendedResponse;
	}

	private AsyncContext asyncContext;

	public void setCurrentThreadsIExtendedRequest(IExtendedRequest currentThreadsIExtendedRequest) {
        this.currentThreadsIExtendedRequest = currentThreadsIExtendedRequest;
    }

    public boolean isAsyncMode() {
        return startAsync;
    }

    private static WSThreadLocal instance = new WSThreadLocal();
    
    public static WebContainerRequestState getInstance (boolean create) {
    	
        WebContainerRequestState tempState = null;
        tempState=(WebContainerRequestState) instance.get();
       	 
         if (tempState == null && create) {
       	  	tempState = createInstance();
         }
         
         if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINEST) ){
             logger.logp(Level.FINEST,CLASS_NAME,"getInstance","created->"+create+ ", request state->"+tempState);        //PM50111
         }
         return (tempState);
   }

	public static WebContainerRequestState createInstance() {
		WebContainerRequestState newState = new WebContainerRequestState();
		instance.set(newState);
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINEST) ){
            logger.logp(Level.FINEST,CLASS_NAME,"createInstance","created requestState -->"+ newState.toString());        
        }
		return newState;
	}
	
	public void init(){
		if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINEST) ){
			logger.logp(Level.FINEST,CLASS_NAME,"init","init for request State");        //PM50111 
		}
		if (_attributes!=null)
			_attributes.clear();
		_invokedFilters=false;
		completed=false;
		ardRequest = false;
		this.currentThreadsIExtendedRequest = null;
	    startAsync=false;
	    this.asyncContext = null;
	}

	public void setAttribute(String string, Object obj) {
		if (_attributes==null)
    		_attributes=new HashMap();
		_attributes.put(string, obj);
	}
	
    /* (non-Javadoc)
     * @see javax.servlet.ServletRequest#getAttribute(java.lang.String)
     */
    public Object getAttribute(String arg0) {
        //321485
    	if (_attributes==null)
    		return null;
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"getAttribute", " name --> " + arg0);
        }
    	Object obj = _attributes.get(arg0);
        return obj;
    }
    
    /* (non-Javadoc)
     * @see javax.servlet.ServletRequest#removeAttribute(java.lang.String)
     */
    public void removeAttribute(String arg0) {
        //321485
    	if (_attributes==null)
    		return;
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"removeAttribute", " name --> " + arg0);
        }
        Object oldValue=_attributes.remove(arg0);
    }

	public boolean isInvokedFilters() {
		if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {
            logger.logp(Level.FINE, CLASS_NAME,"isInvokedFilters", " _invokedFilters --> " + _invokedFilters);
        }
		return _invokedFilters;
	}

	public void setInvokedFilters(boolean filters) {
		_invokedFilters = filters;
		if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {
            logger.logp(Level.FINE, CLASS_NAME,"setInvokedFilters", " invokedFilters --> " + _invokedFilters);
        }
	}

	public boolean isCompleted() {
		if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {
            logger.logp(Level.FINE, CLASS_NAME,"isCompleted", " completed --> " + completed);
        }
		return completed;
	}
	
	public void setCompleted(boolean completed){
		this.completed = completed;
		if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {
            logger.logp(Level.FINE, CLASS_NAME,"setCompleted", " completed --> " + completed);
        }
	}
	
	public boolean isArdRequest(){
		return ardRequest;
	}
	
	public void setArdRequest(boolean ardRequest){
		this.ardRequest = ardRequest;
	}

    public void setAsyncMode(boolean b) {
       this.startAsync = b;
    }

    public IExtendedRequest getCurrentThreadsIExtendedRequest() {
        // TODO Auto-generated method stub
        return this.currentThreadsIExtendedRequest;
    }

    public AsyncContext getAsyncContext() {
        return this.asyncContext;
    }

    public void setAsyncContext(AsyncContext asyncContext2) {
        this.asyncContext = asyncContext2;
    }

	public void setCurrentThreadsIExtendedResponse(IExtendedResponse hres) {
		this.currentThreadsIExtendedResponse = hres;
	}


    
	
}
