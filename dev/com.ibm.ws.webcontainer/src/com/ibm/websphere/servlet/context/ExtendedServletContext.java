// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2008
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.websphere.servlet.context;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

import com.ibm.websphere.servlet.filter.IFilterConfig;
import com.ibm.websphere.webcontainer.async.AsyncRequestDispatcher;

/**
 * Servlet Context Extensions for IBM WebSphere Application Server
 * 
 * @ibm-api
 */
public interface ExtendedServletContext extends ServletContext{
	/**
	 * Gets the IFilterConfig object for this context or creates
     * one if it doesn't exist.
	 * @param id
	 * @return
	 */
        public IFilterConfig getFilterConfig(String id);
    
        /**
         * Adds a filter against a specified mapping into this context
         * @param mapping
         * @param config
         */
        public void addMappingFilter(String mapping, IFilterConfig config);
        

        /**
         * Returns an asynchronous request dispatcher to do asynchronous includes
         * @param path
         */
        public AsyncRequestDispatcher getAsyncRequestDispatcher(String path);
        
        /**
         * Returns a map of all the dynamic servlet registrations keyed by name
         */
        public Map<String, ? extends ServletRegistration.Dynamic> getDynamicServletRegistrations();

}
