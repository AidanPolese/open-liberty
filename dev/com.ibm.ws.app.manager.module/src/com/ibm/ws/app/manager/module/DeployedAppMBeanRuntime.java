/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.app.manager.module;

import java.util.List;

import org.osgi.framework.ServiceRegistration;

import com.ibm.ws.container.service.app.deploy.ModuleInfo;
import com.ibm.wsspi.adaptable.module.Container;

/**
 *
 */
public interface DeployedAppMBeanRuntime {
    ServiceRegistration<?> registerApplicationMBean(String appName, Container container, String ddPath, List<ModuleInfo> modules);
}
