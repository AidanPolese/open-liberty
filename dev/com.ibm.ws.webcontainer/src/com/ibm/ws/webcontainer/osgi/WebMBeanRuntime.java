/*
* IBM Confidential
*
* OCO Source Materials
*
* Copyright IBM Corp. 2015
*
* The source code for this program is not published or otherwise divested 
* of its trade secrets, irrespective of what has been deposited with the 
* U.S. Copyright Office.
*/
package com.ibm.ws.webcontainer.osgi;

import java.util.Iterator;

import org.osgi.framework.ServiceRegistration;

import com.ibm.wsspi.webcontainer.servlet.IServletConfig;
import com.ibm.wsspi.adaptable.module.Container;

/**
 * Web module JSR77 registration interface.
 */
public interface WebMBeanRuntime {
    /**
     * Register a web module mbean.
     *
     * @param appName The name of the application enclosing the web module.  May be null.
     * @param moduleName The name of the web module which is to be registered.
     * @param container The container of the web module.
     * @param ddPath The path to the descriptor of the web module.
     * @param servletConfigs Metadata for the servlets of the web module.
     *
     * @return The service registration of the web module.
     */
    ServiceRegistration<?> registerModuleMBean(String appName, String moduleName, Container container, String ddPath, Iterator<IServletConfig> servletConfigs);

    /**
     * Register a servlet mbean.
     *
     * @param appName The name of the application enclosing the web module.  May be null.
     * @param moduleName The name of the web module which is to be registered.
     * @param servletName The name of the servlet.
     *
     * @return The service registration of the servlet.
     */
    ServiceRegistration<?> registerServletMBean(String appName, String moduleName, String servletName);
}
