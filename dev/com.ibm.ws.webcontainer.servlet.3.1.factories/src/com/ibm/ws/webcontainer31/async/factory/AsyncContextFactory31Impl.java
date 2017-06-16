/*******************************************************************************
 * Copyright (c) 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.webcontainer31.async.factory;

import org.osgi.service.component.annotations.Component;

import com.ibm.ws.webcontainer.async.AsyncContextFactory;
import com.ibm.wsspi.webcontainer.servlet.AsyncContext;
import com.ibm.wsspi.webcontainer.servlet.IExtendedRequest;
import com.ibm.wsspi.webcontainer.servlet.IExtendedResponse;
import com.ibm.wsspi.webcontainer.webapp.IWebAppDispatcherContext;
import com.ibm.ws.webcontainer31.async.AsyncContext31Impl;

/**
 *
 */
@Component(service=AsyncContextFactory.class, property = { "service.vendor=IBM" })
public class AsyncContextFactory31Impl implements AsyncContextFactory{
   
    public AsyncContext getAsyncContext(IExtendedRequest iExtendedRequest, IExtendedResponse iExtendedResponse, IWebAppDispatcherContext webAppDispatcherContext){
        return new AsyncContext31Impl(iExtendedRequest, iExtendedResponse, webAppDispatcherContext);
    }
    
}
