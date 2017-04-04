// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
//  CHANGE HISTORY
//Defect        Date        Modified By         Description
//--------------------------------------------------------------------------------------
//557339        10/15/08    mmolden             FP7001FVT: Server timeout FFDC after 5 mins, reply intermittent
package com.ibm.ws.webcontainer.servlet;

import java.io.IOException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.ServletResponseWrapper;


import java.util.Vector;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;
import java.util.logging.Level;

import com.ibm.websphere.servlet.response.IResponse;
import com.ibm.wsspi.webcontainer.logging.LoggerFactory;
import com.ibm.ejs.ras.TraceNLS;

//Alpine - Change import
//import com.ibm.ejs.sm.client.ui.NLS;
import com.ibm.wsspi.webcontainer.servlet.IExtendedResponse;


/**
 * This class provides restriction functionality for included responses.
 */
public class IncludedResponse extends HttpServletResponseProxy {
    private HttpServletResponse _resp;
    // Alpine - Changed to TraceNLS
    //private static NLS nls = new NLS("com.ibm.ws.webcontainer.resources.Messages");
    private static TraceNLS nls = TraceNLS.getTraceNLS(IncludedResponse.class, "com.ibm.ws.webcontainer.resources.Messages");
protected static Logger logger = LoggerFactory.getInstance().getLogger("com.ibm.ws.webcontainer.servlet");
	private static final String CLASS_NAME="com.ibm.ws.webcontainer.servlet.IncludedResponse";

    public IncludedResponse() {
    }

    public void setProxiedHttpServletResponse(HttpServletResponse resp) {
        _resp = resp;
    }

    public HttpServletResponse getProxiedHttpServletResponse() {
        return _resp;
    }
    
    public String getContentType()
    {
    	return _resp.getContentType();
    }

    public HttpServletResponse getWASProxiedHttpServletResponse() {
        if(_resp instanceof IExtendedResponse)
            return _resp;

        if(_resp instanceof ServletResponseWrapper) {
            ServletResponse res = _resp;
            while((res = ((ServletResponseWrapper) res).getResponse()) != null) {
                if(res instanceof IExtendedResponse)
                    break;
            }

            //Wrapper set by customer doesn't have wrapped response object,
            //throw exception as per spec
            if(res == null)
                throw new IllegalArgumentException("Response cannot be null in javax.servlet.ServletResponseWrapper");
            return(HttpServletResponse) res;
        } else {

            //wrapper is not of type ResponseWrapper, so throw exception to the 
            //application as per spec
            throw new IllegalArgumentException("ResponseWrapper is not of javax.servlet.ServletResponseWrapper type");
        }
    }

    public int getStatusCode() {
        return 0;
    }

    //------------- HttpServletResponse ------------------------//
    public void addCookie(Cookie cookie) {
        //throw new IllegalStateException(nls.getString("Illegal.from.included.servlet","Illegal from included servlet"));
        logger.logp(Level.FINE, CLASS_NAME,"addCookie", nls.getString("Illegal.from.included.servlet", "Illegal from included servlet"), "addCookie");
    }
    public void sendError(int sc, String message) throws IOException {
        getWriter().println("<!-- Error " + sc + ":" + message + " -->");
    }
    public void sendError(int sc) throws IOException {
        getWriter().println("<!-- Error " + sc + " -->");
    }
    public void sendRedirect(String location) {
        //throw new IllegalStateException(nls.getString("Illegal.from.included.servlet","Illegal from included servlet"));
        logger.logp(Level.FINE, CLASS_NAME,"sendRedirect", nls.getString("Illegal.from.included.servlet", "Illegal from included servlet"), "sendRedirect");
    }
    // PQ97429
    public void sendRedirect303(String location) {
        //throw new IllegalStateException(nls.getString("Illegal.from.included.servlet","Illegal from included servlet"));
        logger.logp(Level.FINE, CLASS_NAME,"sendRedirect303", nls.getString("Illegal.from.included.servlet", "Illegal from included servlet"), "sendRedirect303");
    }
    // PQ97429
    public void setDateHeader(String name, long date) {
        //throw new IllegalStateException(nls.getString("Illegal.from.included.servlet","Illegal from included servlet"));
        logger.logp(Level.FINE, CLASS_NAME,"setDateHeader", nls.getString("Illegal.from.included.servlet", "Illegal from included servlet"), "setDateHeader");
    }
    public void setStatus(int sc, String sm) {
        //throw new IllegalStateException(nls.getString("Illegal.from.included.servlet","Illegal from included servlet"));
        logger.logp(Level.FINE, CLASS_NAME,"setStatus", nls.getString("Illegal.from.included.servlet", "Illegal from included servlet"), "setStatus");
    }
    public void setStatus(int sc) {
        //throw new IllegalStateException(nls.getString("Illegal.from.included.servlet","Illegal from included servlet"));
        logger.logp(Level.FINE, CLASS_NAME,"setStatus", nls.getString("Illegal.from.included.servlet", "Illegal from included servlet"), "setStatus");
    }
    public void setIntHeader(String name, int value) {
        //throw new IllegalStateException(nls.getString("Illegal.from.included.servlet","Illegal from included servlet"));
        logger.logp(Level.FINE, CLASS_NAME,"setIntHeader", nls.getString("Illegal.from.included.servlet", "Illegal from included servlet"), "setIntHeader");
    }
    public void setHeader(String name, String value) {
	    setHeader(name,value,true);
    }
    
    public void setInternalHeader(String name, String value) {
	     setHeader(name, value, false);
    }
    
    public void setHeader(String name, String value, boolean checkInclude) {
        //throw new IllegalStateException(nls.getString("Illegal.from.included.servlet","Illegal from included servlet"));
    	if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {
            logger.logp(Level.FINE, CLASS_NAME,"setHeader", " name --> " + name + " value --> " + value + " checkInclude --> " + checkInclude);
        }
    	IExtendedResponse iresp = (IExtendedResponse) this.getWASProxiedHttpServletResponse();
        iresp.setHeader(name, value,checkInclude);
    }
    public void setContentLength(int len) {
        //throw new IllegalStateException(nls.getString("Illegal.from.included.servlet","Illegal from included servlet"));
        logger.logp(Level.FINE, CLASS_NAME,"setContentLength", nls.getString("Illegal.from.included.servlet", "Illegal from included servlet"), "setContentLength");
    }
    public void setContentType(String type) {
        //throw new IllegalStateException(nls.getString("Illegal.from.included.servlet","Illegal from included servlet"));
        logger.logp(Level.FINE, CLASS_NAME,"setContentType", nls.getString("Illegal.from.included.servlet", "Illegal from included servlet"), "setContentType");
    }
    
	public void setCharacterEncoding(String type) {
		//throw new IllegalStateException(nls.getString("Illegal.from.included.servlet","Illegal from included servlet"));
		logger.logp(Level.FINE, CLASS_NAME,"setCharacterEncoding", nls.getString("Illegal.from.included.servlet", "Illegal from included servlet"), "setCharacterEncoding");
	}

	/* (non-Javadoc)
	 * @see com.ibm.wsspi.webcontainer.servlet.IExtendedResponse#destroy()
	 */
	public void destroy() {
		logger.logp(Level.FINE, CLASS_NAME,"destroy", nls.getString("Illegal.from.included.servlet", "Illegal from included servlet"), "destroy");
		
	}

	/* (non-Javadoc)
	 * @see com.ibm.wsspi.webcontainer.servlet.IExtendedResponse#finish()
	 */
	public void finish() throws ServletException {
		logger.logp(Level.FINE, CLASS_NAME,"finish", nls.getString("Illegal.from.included.servlet", "Illegal from included servlet"), "finish");
		
	}

	/* (non-Javadoc)
	 * @see com.ibm.wsspi.webcontainer.servlet.IExtendedResponse#initForNextResponse(com.ibm.wsspi.webcontainer.IResponse)
	 */
	public void initForNextResponse(IResponse res) {
		logger.logp(Level.FINE, CLASS_NAME,"initForNextResponse", nls.getString("Illegal.from.included.servlet", "Illegal from included servlet"), "initForNextResponse");
		
	}

	/* (non-Javadoc)
	 * @see com.ibm.wsspi.webcontainer.servlet.IExtendedResponse#start()
	 */
	public void start() {
		logger.logp(Level.FINE, CLASS_NAME,"start", nls.getString("Illegal.from.included.servlet", "Illegal from included servlet"), "start");
		
	}

	//Begin 557339  FP7001FVT: Server timeout FFDC after 5 mins, reply intermittent
	public void closeResponseOutput(boolean b) {
	}
	//End 557339  FP7001FVT: Server timeout FFDC after 5 mins, reply intermittent
		
	public Vector[] getHeaderTable() {
		// TODO Auto-generated method stub
		return null;
	}

    @Override
    public void removeCookie(String cookieName) {
        // TODO Auto-generated method stub
        
    }


}
