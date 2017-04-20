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
package com.ibm.ws.cdi.web.liberty;

import org.osgi.service.component.annotations.Component;

import com.ibm.ws.cdi.web.impl.AbstractTerminalListenerRegistration;
import com.ibm.ws.webcontainer31.osgi.listener.PostEventListenerProvider;

/**
 * Register WeldTeminalListener on the servlet context. This listener needs to be the last HttpSessionlistener.
 */
@Component(name = "com.ibm.ws.cdi.weldTerminalListener",
                service = PostEventListenerProvider.class,
                immediate = true,
                property = { "service.vendor=IBM", "service.ranking:Integer=-1" })
public class WeldTerminalListenerRegistration extends AbstractTerminalListenerRegistration implements PostEventListenerProvider {}
