// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
//CHANGE HISTORY
//Flag    Defect         Date         Modified By         Description
//--------------------------------------------------------------------------------------
//	  296658	08/07/05      todkap		  allow FilterConfig to override the default classloader used
//	 301121    	08/26/05      todkap		  WebApp fails to handle wsspi implementation of IFilterConfig    WASCC.web.webcontainer    
//
package com.ibm.wsspi.webcontainer.filter;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.ServletContext;

import com.ibm.wsspi.webcontainer.servlet.IServletContext;

/**
 * A representation of the configuration for a filter
 * 
 * @ibm-private-in-use
 */
public interface IFilterConfig extends com.ibm.websphere.servlet.filter.IFilterConfig {

    /**
     * Set the large icon
     * @param largeIcon
     */
    public void setLargeIcon(String largeIcon);

    /**
     * Set the small icon
     * @param smallIcon
     */
    public void setSmallIcon(String smallIcon);


    /**
     * Get the dispatch type
     * @return
     */
    public DispatcherType[] getDispatchType();

    //  begin 296658    allow FilterConfig to override the default classloader used    WASCC.web.webcontainer
    /**
     * Get the classloader where this Filter should be loaded from.
     * Default is WebApp's classloader.
     * @return
     */
	public ClassLoader getFilterClassLoader();

    /**
     * Get the filter class name
     * @return
     */
    public String getFilterClassName();

    /**
     * Set the ServletContext this Filter should be associated with.
     * @param ServletContext
     */
	public void setIServletContext(IServletContext servletContext);

	/**
	 * Set whether resource should be considered internal. 
	 * 	 * @return
	 */
	public void setInternal(boolean isInternal);
	
	/**
	 * Checks if resource should be considered internal.
	 * @return
	 */
	public boolean isInternal();

    public void setFilter(Filter filter);

    public void setFilterClass(Class<? extends Filter> filterClass);
    
    public Filter getFilter();

    public Class<? extends Filter> getFilterClass();
    
    public void setFilterClassName(String className);

	
}
