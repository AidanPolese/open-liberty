// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
//  CHANGE HISTORY
//Defect        Date        Modified By         Description
//--------------------------------------------------------------------------------------
//LIDB3518-1.1  06-23-07    mmolden             ARD
//557339        10/15/08    mmolden             FP7001FVT: Server timeout FFDC after 5 mins, reply intermittent

package com.ibm.wsspi.webcontainer.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector; 
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;

import com.ibm.websphere.servlet.response.IResponse;
import com.ibm.wsspi.webcontainer.util.IResponseOutput;

/**
 * 
 * 
 * IExtendedResponse is an spi for websphere additions to the standard
 * ServletResponse methods
 * 
 * @ibm-private-in-use
 * 
 * @since   WAS7.0
 * 
 */

public interface IExtendedResponse extends ServletResponse, IResponseOutput
{
    public Vector[] getHeaderTable();
    public void addSessionCookie(Cookie cookie);
    public void removeCookie(String cookieName);
    //Begin:248739
    //Add methods for DRS-Hot failover to set internal headers without checking
    //if the request is an include.
    public void setInternalHeader(String name, String s);
    public void setHeader(String name, String s, boolean checkInclude);
    //End:248739
    //PQ97429
    public void sendRedirect303(String location) throws IOException;    
    //PQ97429
    public IResponse getIResponse();
    //340473
    public int getStatusCode();
    
    public void registerOutputMethodListener(IOutputMethodListener listener);
    public void fireWriterRetrievedEvent(PrintWriter pw);
    public void fireOutputStreamRetrievedEvent(ServletOutputStream sos);
	public void initForNextResponse(IResponse res);
	
	public void start();
	
	public void finish() throws ServletException, IOException;
	
    public void destroy();
	public void closeResponseOutput(boolean b); //557339 FP7001FVT: Server timeout FFDC after 5 mins, reply intermittent
	
	public boolean isOutputWritten();
    
}
