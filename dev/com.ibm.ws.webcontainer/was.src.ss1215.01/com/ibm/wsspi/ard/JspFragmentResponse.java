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
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * JspFragmentResponse is a special type of FragmentResponse intended for use by IBM JSP 
 * custom tags created specifically for asynchronous includes. 
 * 
 *@ibm-private-in-use
 */
public interface JspFragmentResponse extends com.ibm.websphere.webcontainer.async.FragmentResponse{
	/**
	 * Insert the fragment contents into the response using the provided PrintWriter. 
	 * 
	 * @param req ServletRequest
	 * @param resp ServletResponse
	 * @param pw PrintWriter
	 * @throws ServletException
	 * @throws IOException
	 */
	public void insertFragmentFromJsp(ServletRequest req, ServletResponse resp,PrintWriter pw) throws ServletException, IOException;
    /**
     * Insert the blocking fragment into the response using the provided PrintWriter 
     * 
     * @param req ServletRequest
     * @param resp SevrletResponse
     * @param pw PrintWriter
     * @throws ServletException
     * @throws IOException
     */
    public void insertFragmentBlockingFromJsp(ServletRequest req, ServletResponse resp,PrintWriter pw) throws ServletException, IOException;

}

