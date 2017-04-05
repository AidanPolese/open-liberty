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
//      PK17095        01/04/06        todkap              WEBAPPDISPATCHERCONTEXT CORRUPTION DUE TO MULTITHREAD USAGE OF    WAS.webcontainer    
//      PK18815        02/21/06        todkap              NULLPOINTEREXCEPTION WITH CLONED REQUEST AND STORED RESPONSE    WAS.webcontainer
//      LIDB4408-1     02/22/06        todkap              LIDB4408-1 web container changes to limit pooling
//

package com.ibm.ws.webcontainer.srt;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.websphere.servlet.request.IRequest;
import com.ibm.websphere.servlet.response.IResponse;
import com.ibm.ws.webcontainer.webapp.WebAppDispatcherContext;
import com.ibm.wsspi.webcontainer.logging.LoggerFactory;
import com.ibm.wsspi.webcontainer.servlet.IExtendedRequest;
import com.ibm.wsspi.webcontainer.servlet.IExtendedResponse;
import com.ibm.wsspi.webcontainer.servlet.IServletResponse;

public class SRTConnectionContext implements Cloneable
{
    //objects requiring cloning
    //==========================
    protected IExtendedRequest _request;
    protected WebAppDispatcherContext _dispatchContext = null;
    //==========================
    private IExtendedResponse _response;
    
protected static Logger logger = LoggerFactory.getInstance().getLogger("com.ibm.ws.webcontainer.srt");
	private static final String CLASS_NAME="com.ibm.ws.webcontainer.srt.SRTConnectionContext";

	public SRTConnectionContext() 
	{
		if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {
			logger.logp(Level.FINE, CLASS_NAME,"SRTConnectionContext", "Constructor");
        }
		_request = new SRTServletRequest(this);
		_response = new SRTServletResponse(this);
		_request.setWebAppDispatcherContext(_dispatchContext);
	}

	public void prepareForNextConnection(IRequest req, IResponse res)
	{
		if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {
			logger.entering(CLASS_NAME,"prepareForNextConnection");
			logger.logp(Level.FINE, CLASS_NAME, "prepareForNextConnection", "channel req->"+req
			        +", channel res->"+res
			        +", IExtendedRequest->"+_request+", IExtendedResponse"+_response);
        }
		_request.initForNextRequest(req);
		_response.initForNextResponse(res);
		if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {
            logger.exiting(CLASS_NAME,"prepareForNextConnection");
        }
	}
	
	public void start(){
		this._response.start();
		this._request.start();
	}

	public void finishConnection()
	{
	    if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {
            logger.entering(CLASS_NAME,"finishConnection");
            logger.logp(Level.FINE, CLASS_NAME, "finishConnection",
                    "IExtendedRequest->"+_request+", IExtendedResponse"+_response);
        }
		try
		{
			try
			{
				_response.finish();
			}
			catch (Throwable th)
			{
				com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(th, "com.ibm.ws.webcontainer.srt.SRTConnectionContext.finishConnection", "64", this);
			}		
			
			try
			{
				_request.finish();
			}
			catch (Throwable th)
			{
				com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(th, "com.ibm.ws.webcontainer.srt.SRTConnectionContext.finishConnection", "74", this);
				WebAppDispatcherContext dispatchContext = (WebAppDispatcherContext)_request.getWebAppDispatcherContext();
				dispatchContext.getWebApp().logError("Error while finishing the connection", th);
			}
			
				
            
			dispatchContextFinish();
		}
		finally
		{
			_request.initForNextRequest(null);
			_response.initForNextResponse(null);
		}
		 if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {
	            logger.exiting(CLASS_NAME,"finishConnection");
	     }
	}
    
    protected void dispatchContextFinish(){
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {
            logger.entering(CLASS_NAME,"dispatchContextFinish");
            logger.logp(Level.FINE, CLASS_NAME, "dispatchContextFinish",
                    "IExtendedRequest->"+_request+", IExtendedResponse"+_response);
        }
        try
        {
            this._dispatchContext.finish();
        }
        catch (Throwable th)
        {
            com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(th, "com.ibm.ws.webcontainer.srt.SRTConnectionContext.dispatchContextFinish", "84", this);
        }
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {
            logger.exiting(CLASS_NAME,"dispatchContextFinish");
        }
        
    }

	public IExtendedRequest getRequest()
	{
		return _request;
	}

	public IExtendedResponse getResponse()
	{
		return _response;
	}
    protected Object clone(SRTServletRequest clonedRequest,WebAppDispatcherContext clonedDispatchContext) throws CloneNotSupportedException
    {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {
            logger.logp(Level.FINE, CLASS_NAME,"clone", " entry");
        }

        SRTConnectionContext clonedConnContext= (SRTConnectionContext)super.clone();
        clonedConnContext._request=clonedRequest;
        clonedConnContext._dispatchContext=clonedDispatchContext;
        if (_response instanceof IServletResponse){
        	clonedConnContext._response = (IExtendedResponse)((IServletResponse)_response).clone();
        }
        

        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {
            logger.logp(Level.FINE, CLASS_NAME,"clone", " exit original -->" + this + " cloned -->" + clonedConnContext);
        }

        return clonedConnContext;
    }
    
    public void destroy(){
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {
            logger.logp(Level.FINE, CLASS_NAME,"destroy", " entry");
        }
        _request.destroy();
        _response.destroy();
        _dispatchContext = null;
        _request = null;
        _response = null;
        
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {
            logger.logp(Level.FINE, CLASS_NAME,"destroy", " exit");
        }

    }

	public void setResponse(IExtendedResponse extResp) {
		_response = extResp;
	}

    
}
