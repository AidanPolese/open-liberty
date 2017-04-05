// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.ws.webcontainer.webapp;

import com.ibm.ws.container.BaseConfiguration;
import com.ibm.ws.webcontainer.VirtualHost;

public class WebGroupConfiguration extends BaseConfiguration {
	
	private String contextRoot;
	private VirtualHost webAppHost;
	private int versionID;

	public WebGroupConfiguration(String id) 
	{
		super(id);
	}
	
	/**
	 * Returns the contextRoot.
	 * @return String
	 */
	public String getContextRoot() {
		return contextRoot;
	}

	/**
	 * Sets the contextRoot.
	 * @param contextRoot The contextRoot to set
	 */
	public void setContextRoot(String contextRoot) {
		this.contextRoot = contextRoot;
	}

	/**
	 * Returns the isServlet2_3.
	 * @return boolean
	 */
	public boolean isServlet2_3() {
		return (versionID >= 23);
	}

	/**
	 * Returns the webAppHost.
	 * @return WebAppHost
	 */
	public VirtualHost getWebAppHost() {
		return webAppHost;
	}

	/**
	 * Sets the webAppHost.
	 * @param webAppHost The webAppHost to set
	 */
	public void setWebAppHost(VirtualHost webAppHost) {
		this.webAppHost = webAppHost;
	}

	/**
	 * @return
	 */
	public int getVersionID()
	{
		return versionID;
	}

	/**
	 * @param i
	 */
	public void setVersionID(int i)
	{
		versionID = i;
	}

}
