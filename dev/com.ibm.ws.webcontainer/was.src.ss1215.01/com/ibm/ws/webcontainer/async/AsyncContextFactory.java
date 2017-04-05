package com.ibm.ws.webcontainer.async;

import com.ibm.wsspi.webcontainer.servlet.AsyncContext;
import com.ibm.wsspi.webcontainer.servlet.IExtendedRequest;
import com.ibm.wsspi.webcontainer.servlet.IExtendedResponse;
import com.ibm.wsspi.webcontainer.webapp.IWebAppDispatcherContext;

public class AsyncContextFactory {
    protected static AsyncContextFactory self;
    
    public static AsyncContextFactory getAsyncContextFactory(){
        if (self==null)
            self=new AsyncContextFactory();
        return self;
    }
    
    public AsyncContext getAsyncContext(IExtendedRequest iExtendedRequest, IExtendedResponse iExtendedResponse, IWebAppDispatcherContext webAppDispatcherContext){
        return new AsyncContextImpl(iExtendedRequest, iExtendedResponse, webAppDispatcherContext);
    }
    
}
