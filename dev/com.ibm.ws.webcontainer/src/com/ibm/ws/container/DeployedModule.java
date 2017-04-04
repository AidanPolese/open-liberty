// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.ws.container;

import com.ibm.ws.http.VirtualHost;
import com.ibm.ws.webcontainer.webapp.WebApp;
import com.ibm.ws.webcontainer.webapp.WebAppConfiguration;
import com.ibm.ws.webcontainer.webapp.WebGroup;
import com.ibm.ws.webcontainer.webapp.WebGroupConfiguration;

public abstract class DeployedModule 
{

	/**
	 * @return
	 */
	public ClassLoader getClassLoader() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return
	 */
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return
	 */
	public String getContextRoot() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return
	 */
	public String getDisplayName() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return
	 */
	public abstract WebAppConfiguration getWebAppConfig();
	/**
	 * @return
	 */
	public abstract WebApp getWebApp();

	/**
	 * @return
	 */
	public String getVirtualHostName() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return
	 */
	public WebGroupConfiguration getWebGroupConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return
	 */
	public WebGroup getWebGroup() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return
	 */
	public VirtualHost[] getVirtualHosts()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
