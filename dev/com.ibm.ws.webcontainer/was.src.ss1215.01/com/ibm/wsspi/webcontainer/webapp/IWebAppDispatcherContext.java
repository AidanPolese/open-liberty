// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
//@ibm-private-in-use
//
//CHANGE HISTORY
//Defect	Date		Modified By		Description
//--------------------------------------------------------------------------------------
//

package com.ibm.wsspi.webcontainer.webapp;

import java.io.IOException;

import javax.servlet.DispatcherType;

import com.ibm.ws.webcontainer.webapp.WebApp;
import com.ibm.wsspi.webcontainer.RequestProcessor;

public interface IWebAppDispatcherContext {
    public boolean isEnforceSecurity();

    public void sessionPreInvoke();

    public void sessionPostInvoke();

    public RequestProcessor getCurrentServletReference();

    public void pushException(Throwable th);

    public boolean isInclude();

    public boolean isForward();

    public WebApp getWebApp();

    public String getRelativeUri();

    public String getOriginalRelativeURI();
    
    public void sendError(int sc, String message, boolean ignoreCommittedException) throws IOException;
    
    public DispatcherType getDispatcherType();

}
