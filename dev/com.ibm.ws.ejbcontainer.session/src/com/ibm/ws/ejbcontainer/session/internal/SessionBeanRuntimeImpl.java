/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.ejbcontainer.session.internal;

import org.osgi.service.component.annotations.Component;

import com.ibm.ws.ejbcontainer.osgi.SessionBeanRuntime;

/**
 * Provides the session bean runtime environment which enables session beans
 * in the core container.
 */
@Component(service = SessionBeanRuntime.class,
           name = "com.ibm.ws.ejbcontainer.session.runtime",
           property = "service.vendor=IBM")
public class SessionBeanRuntimeImpl implements SessionBeanRuntime {
    // Nothing currently needs to be done.  The presence of this class in the
    // service registry enables session beans.
}
