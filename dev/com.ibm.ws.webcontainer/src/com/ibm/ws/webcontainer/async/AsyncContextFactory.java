package com.ibm.ws.webcontainer.async;

import com.ibm.wsspi.webcontainer.servlet.AsyncContext;
import com.ibm.wsspi.webcontainer.servlet.IExtendedRequest;
import com.ibm.wsspi.webcontainer.servlet.IExtendedResponse;
import com.ibm.wsspi.webcontainer.webapp.IWebAppDispatcherContext;

public interface AsyncContextFactory {
  
    AsyncContext getAsyncContext(IExtendedRequest iExtendedRequest, 
                                 IExtendedResponse iExtendedResponse, 
                                 IWebAppDispatcherContext webAppDispatcherContext);
    
}
