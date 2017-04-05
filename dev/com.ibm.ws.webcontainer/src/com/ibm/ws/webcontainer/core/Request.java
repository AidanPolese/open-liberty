// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
//CHANGE HISTORY
//Flag    Defect         Date         Modified By         Description
//--------------------------------------------------------------------------------------
//      LIDB4408-1     02/22/06      todkap             LIDB4408-1 web container changes to limit pooling


package com.ibm.ws.webcontainer.core;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;

import com.ibm.ws.webcontainer.webapp.WebAppDispatcherContext;
import com.ibm.wsspi.webcontainer.IRequest;


public interface Request extends ServletRequest
{
	public String getRequestURI();
	
	public void start();
	
	public void finish() throws ServletException;
	
	public void initForNextRequest(IRequest req);
	
	public void setWebAppDispatcherContext(WebAppDispatcherContext ctx);	
	
	public WebAppDispatcherContext getWebAppDispatcherContext();
	
	public String getServletPath();
	
	public String getPathInfo();
	
	public Response getResponse();
    
    public void destroy();
}
