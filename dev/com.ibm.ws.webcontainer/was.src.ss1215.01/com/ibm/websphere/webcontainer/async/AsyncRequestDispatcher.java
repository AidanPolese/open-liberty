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

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * AsyncRequestDispatcher is a special RequestDispatcher used to execute includes asynchronously.
 * 
 * @ibm-api
 *
 */
public interface AsyncRequestDispatcher extends RequestDispatcher {
    /**
     * getFragmentResponse kicks off an asynchronous includes and returns a FragmentRespnse object
     * which is used later to insert the include contents.
     * 
     * @param req ServletRequest
     * @param resp ServletResponse
     * @return FragmentResponse
     * @throws ServletException
     * @throws IOException
     */
    public FragmentResponse getFragmentResponse(ServletRequest req, ServletResponse resp) throws ServletException, IOException ;
    /**
     * Set the AsyncRequestDispatcherConfig object used to customize messages and timeouts for
     * specific includes.
     * 
     * @param config
     */
    public void setAsyncRequestDispatcherConfig (AsyncRequestDispatcherConfig config);
}
