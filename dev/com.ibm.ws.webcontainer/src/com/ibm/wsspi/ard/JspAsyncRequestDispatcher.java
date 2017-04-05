// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2007
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// CHANGE HISTORY
// Defect       Date        Modified By         Description
//--------------------------------------------------------------------------------------
//

package com.ibm.wsspi.ard;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.ibm.websphere.webcontainer.async.AsyncRequestDispatcher;
import com.ibm.websphere.webcontainer.async.FragmentResponse;

/**
 * JspAsyncRequestDispatcher is intended for use by IBM JSP custom tags created specifically 
 * for asynchronous includes. 
 * 
 * @ibm-private-in-use
 *
 */
public interface JspAsyncRequestDispatcher extends AsyncRequestDispatcher{
	
	/**
	 * Return a new FragmentResponse object and execute the asynchronous include 
	 * 
	 * @param req ServletRequest
	 * @param resp ServletResponse
	 * @return new FragmentResponse
	 * @throws ServletException
	 * @throws IOException
	 */
	public FragmentResponse getFragmentResponse(ServletRequest req, ServletResponse resp) throws ServletException, IOException;

    /**
     * Return a new ServletResponseWrapper 
     * 
     * @param req ServletRequest
     * @param resp ServletResponse
     * @return ServletResponseWrapper 
     * @throws ServletException
     * @throws IOException
     */
    public ServletResponse getFragmentResponseWrapper(ServletRequest req, ServletResponse resp) throws ServletException, IOException;
    /**
     * Execute a special type of fragment response in which a wrapped response is provided. 
     * 
     * @param req ServletRequest
     * @param resp ServletRespnse
     * @return new FragmentResponse
     * @throws ServletException
     * @throws IOException
     */
    public FragmentResponse executeFragmentWithWrapper(ServletRequest req, ServletResponse resp) throws ServletException, IOException;

}

