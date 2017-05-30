/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015, 2016
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.ejbcontainer;

import java.util.List;
import java.util.Set;

/**
 * Information about all the managed beans in a module.
 *
 * How to obtain an instance depends on the runtime environment:
 *
 * On Liberty, an instance can be obtained
 * via {@link com.ibm.wsspi.adaptable.module.Container#adapt} obtained
 * via {@link com.ibm.ws.container.service.app.deploy.ModuleInfo#getContainer},
 * and it should not be obtained after
 * the {@link com.ibm.ws.container.service.state.ModuleStateListener#moduleStarting} event.
 *
 * On traditional WAS, an instance can be obtained
 * via {@link com.ibm.ws.runtime.service.EJBContainer#getManagedBeanEndpoints}.
 */
public interface ManagedBeanEndpoints {
    /**
     * Returns the module version.
     */
    int getModuleVersion();

    /**
     * Return the list of interceptor classes for the managed beans in the module.
     */
    Set<String> getManagedBeanInterceptorClassNames();

    /**
     * Return the list of managed beans in the module.
     */
    List<ManagedBeanEndpoint> getManagedBeanEndpoints();
}
