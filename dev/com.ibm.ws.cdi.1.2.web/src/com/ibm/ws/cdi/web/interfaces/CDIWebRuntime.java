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
package com.ibm.ws.cdi.web.interfaces;

import javax.enterprise.inject.spi.BeanManager;

import com.ibm.ws.runtime.metadata.ModuleMetaData;
import com.ibm.wsspi.webcontainer.servlet.IServletContext;

/**
 *
 */
public interface CDIWebRuntime {

    /**
     * Indicate whether cdi is enabled for the current web module
     */
    public static final String CDI_ENABLED_ATTR = "com.ibm.ws.cdi.cdiEnabledApp";
    public static final String SESSION_NEEDS_PERSISTING = "com.ibm.ws.cdi.web.WeldServletRequestListener.SESSION_NEEDS_PERSISTING";

    /**
     * @param moduleMetaData
     * @return
     */
    BeanManager getModuleBeanManager(ModuleMetaData moduleMetaData);

    /**
     * @param isc
     * @return
     */
    boolean isCdiEnabled(IServletContext isc);

    /**
     * @return
     */
    BeanManager getCurrentBeanManager();

}
