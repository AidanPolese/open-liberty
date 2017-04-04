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
package com.ibm.ws.cdi.impl;

import javax.enterprise.inject.spi.BeanManager;

import org.jboss.weld.AbstractCDI;

import com.ibm.ws.cdi.CDIService;

/**
 * IBM implementation of CDI
 */
public class CDIImpl extends AbstractCDI<Object> {
    private final CDIService cdiService;

    public CDIImpl(CDIService cdiService) {
        this.cdiService = cdiService;
    }

    /** {@inheritDoc} */
    @Override
    public BeanManager getBeanManager() {
        return cdiService.getCurrentBeanManager();
    }

    public static void clear() {
        configuredProvider = null;
    }
}
