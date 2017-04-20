/*
* IBM Confidential
*
* OCO Source Materials
*
* Copyright IBM Corp. 2014
*
* The source code for this program is not published or otherwise divested 
* of its trade secrets, irrespective of what has been deposited with the 
* U.S. Copyright Office.
*/
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
