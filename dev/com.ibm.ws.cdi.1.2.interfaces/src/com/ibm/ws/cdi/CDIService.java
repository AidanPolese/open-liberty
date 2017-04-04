/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012, 2015
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.cdi;

import javax.enterprise.inject.spi.BeanManager;

/**
 * Provides access to CDI classes
 */
public interface CDIService {

    /**
     * Gets the bean manager for the calling class (obtained by walking the stack looking for a class which is in a BDA) or
     * for the current module ({@link #getCurrentModuleBeanManager()}) if there are no BDA classes on the stack.
     *
     * @return the current bean manager
     */
    public BeanManager getCurrentBeanManager();

    /**
     * Gets the bean manager for the current module
     *
     * @return the bean manager for the current module
     */
    public BeanManager getCurrentModuleBeanManager();
}
