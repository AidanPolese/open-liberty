package com.ibm.wsspi.webcontainer.filter;

import javax.servlet.DispatcherType;

import com.ibm.wsspi.webcontainer.servlet.IServletConfig;

public interface IFilterMapping {

	public abstract int getMappingType();

	/**
	 * Returns the filterConfig.
	 * @return FilterConfig
	 */
	public abstract IFilterConfig getFilterConfig();

	/**
	 * Returns the urlPattern.
	 * @return String
	 */
	public abstract String getUrlPattern();

	/**
	 * Sets the filterConfig.
	 * @param filterConfig The filterConfig to set
	 */
	public abstract void setFilterConfig(IFilterConfig filterConfig);

	/**
	 * Sets the urlPattern.
	 * @param urlPattern The urlPattern to set
	 */
	public abstract void setUrlPattern(String filterURI);

	public abstract IServletConfig getServletConfig();

	/**
	 * @return DispatcherType[]
	 */
	public abstract DispatcherType[] getDispatchMode();

	/**
	 * Sets the dispatchMode.
	 * @param dispatchMode The dispatchMode to set
	 */
	public abstract void setDispatchMode(DispatcherType[] dispatchMode);

}
