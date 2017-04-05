// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
//CHANGE HISTORY
//Flag    Defect         Date         Modified By         Description
//--------------------------------------------------------------------------------------
//        341303         01/25/06     mmolden             Change WebContainer APIs to allow modification of ServletConfig


//Code added as part of LIDB 2283-4

package com.ibm.ws.webcontainer.servlet;

import com.ibm.wsspi.webcontainer.servlet.IServletConfig;


public class ServletMapping
{
	private String urlPattern;
	private IServletConfig servletConfig;
	
	/**
	 * Constructor.
	 *
	 * @param config
	 * @param pattern
	 */
	public ServletMapping(IServletConfig config, String pattern)
	{
		this.servletConfig = config;
		this.urlPattern = pattern;
	}
	
	/**
	 * Constructor.
	 */
	public ServletMapping()
	{
	    // nothing
	}
	
	/**
	 * Returns the servletConfig.
	 * @return ServletConfig
	 */
	public IServletConfig getServletConfig() {
		return this.servletConfig;
	}

	/**
	 * Returns the urlPattern.
	 * @return String
	 */
	public String getUrlPattern() {
		return this.urlPattern;
	}

	/**
	 * Sets the servletConfig.
	 *
	 * @param config
	 */
	public void setServletConfig(IServletConfig config) {
		this.servletConfig = config;
	}

	/**
	 * Sets the urlPattern.
	 *
	 * @param pattern
	 */
	public void setUrlPattern(String pattern) {
		this.urlPattern = pattern;
	}
	
	public String toString()
	{
		return ""+this.servletConfig.getServletName()+":"+this.urlPattern;
	}

}
