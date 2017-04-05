// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2008
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//

/**
 * @ibm-api	
 */

package com.ibm.websphere.servlet.container;

import com.ibm.websphere.servlet.request.IRequest;
import com.ibm.websphere.servlet.response.IResponse;

public abstract class WebContainer {


    /**
    *
    * @return The instance of the WebContainer
    * 
    * Call this method to get at an instance of the WebContainer
    */
	public static WebContainer getWebContainer() {
		
		return com.ibm.wsspi.webcontainer.WebContainer.getWebContainer();
	}
	
	/**
    *
    * @param req
    * @param res
    * @throws Exception
    * 
    * Call this method to force the webcontainer to handle the request. The request
    * should have enough information in it for the webcontainer to handle the request.
    */
	public abstract void handleRequest(IRequest req, IResponse res) throws Exception;
	
	
	/**
    *
    * @param classname
    * 
    * Adds a global servlet listener with the specified classname. 
    * The class must be on the classpath of all web applications. For example,
    * the /lib directory at the root of the application server.
    */
	public static void addGlobalListener(String className){
		com.ibm.ws.webcontainer.WebContainer.addGlobalListener(className);
	}
	
}
