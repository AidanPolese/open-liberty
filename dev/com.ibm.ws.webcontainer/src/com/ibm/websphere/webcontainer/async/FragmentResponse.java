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

package com.ibm.websphere.webcontainer.async;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * FragmentResponse is a placeholder for the results of an async include so they can be inserted later in the response
 * 
 * @ibm-api
 *
 */
public interface FragmentResponse {
	/**
     * insertFragment marks the location a fragment's response contents are supposed to be inserted
     * without blocking
     * 
     * @param req ServletRequest
     * @param resp ServletResponse
     * @throws ServletException
     * @throws IOException
     */
    public void insertFragment(ServletRequest req, ServletResponse resp) throws ServletException, IOException;
    
    /**
     * insertFragmentBlocking waits for the include to complete and inserts the response content.
     * 
     * @param req ServletRequest
     * @param resp ServletResponse
     * @throws ServletException
     * @throws IOException
     */
    public void insertFragmentBlocking(ServletRequest req, ServletResponse resp) throws ServletException, IOException;
}
