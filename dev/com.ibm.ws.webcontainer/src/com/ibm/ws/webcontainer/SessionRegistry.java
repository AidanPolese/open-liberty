// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//.
package com.ibm.ws.webcontainer;

import java.util.ArrayList;

import com.ibm.ws.container.DeployedModule;
import com.ibm.ws.webcontainer.session.IHttpSessionContext;
import com.ibm.ws.webcontainer.webapp.WebApp;

@SuppressWarnings("unchecked")
public interface SessionRegistry {
    IHttpSessionContext getSessionContext(DeployedModule webModuleConfig, WebApp ctx, String vhostName, ArrayList[] listeners) throws Throwable;
}
