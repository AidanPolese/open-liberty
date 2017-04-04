// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2008
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.ws.webcontainer.async;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.ibm.wsspi.webcontainer.async.WSAsyncEvent;
import com.ibm.wsspi.webcontainer.logging.LoggerFactory;
import com.ibm.wsspi.webcontainer.servlet.AsyncContext;

public class AsyncListenerEntry {
    protected static Logger logger = LoggerFactory.getInstance().getLogger("com.ibm.ws.webcontainer.async");
    private static final String CLASS_NAME = "com.ibm.ws.webcontainer.async.AsyncListenerEntry";

    private AsyncListener asyncListener;
    private ServletRequest servletRequest;
    private ServletResponse servletResponse;
    private boolean initialized = false;
    private AsyncContext asyncContext;

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public AsyncListenerEntry(AsyncContext asyncContext, AsyncListener listener){
        this.asyncContext = asyncContext;
        this.asyncListener = listener;
    }

    public AsyncListenerEntry(AsyncContext asyncContext, AsyncListener listener,ServletRequest servletRequest, ServletResponse servletResponse){
        this.asyncContext = asyncContext;
        this.asyncListener = listener;
        this.servletRequest = servletRequest;
        this.servletResponse = servletResponse;
        initialized = true;

    }

    public AsyncListener getAsyncListener() {
        return asyncListener;
    }

    public ServletRequest getServletRequest() {
        return servletRequest;
    }

    public ServletResponse getServletResponse() {
        return servletResponse;
    }

    public void invokeOnTimeout() {

        AsyncEvent asyncEvent = new AsyncEvent(asyncContext,servletRequest, servletResponse);
        try {
            if (logger.isLoggable(Level.FINEST))
                logger.logp(Level.FINEST,CLASS_NAME,"invokeOnTimeout","listener->"+asyncListener);
            asyncListener.onTimeout(asyncEvent);
        } catch (IOException e) {
            logger.logp(Level.WARNING,CLASS_NAME,"invokeOnTimeout","an.io.related.error.has.occurred.during.the.processing.of.the.given.AsyncEvent");
        }
    }

    public void invokeOnComplete(long elapsedTime) {
        AsyncEvent asyncEvent = new WSAsyncEvent(asyncContext,servletRequest, servletResponse,elapsedTime);
        try {
            if (logger.isLoggable(Level.FINEST))
                logger.logp(Level.FINEST,CLASS_NAME,"invokeOnComplete","listener->"+asyncListener);
            asyncListener.onComplete(asyncEvent);
        } catch (IOException e) {
            logger.logp(Level.WARNING,CLASS_NAME,"invokeOnComplete","an.io.related.error.has.occurred.during.the.processing.of.the.given.AsyncEvent");
        }
        catch (Exception e) {
            logger.logp(Level.WARNING,CLASS_NAME,"invokeOnComplete", "uncaught.exception.during.AsyncListener.onComplete", new Object[] {this.getAsyncListener().getClass().getName(), e});
        }
    }

    public void invokeOnError(Throwable th) {
        AsyncEvent asyncEvent = new AsyncEvent(asyncContext,servletRequest, servletResponse,th);
        try {
            if (logger.isLoggable(Level.FINEST))
                logger.logp(Level.FINEST,CLASS_NAME,"invokeOnError","listener->"+asyncListener);
            asyncListener.onError(asyncEvent);
        } catch (IOException e) {
            logger.logp(Level.WARNING,CLASS_NAME,"invokeOnError","an.io.related.error.has.occurred.during.the.processing.of.the.given.AsyncEvent");
        }
    }

    public void invokeOnStartAsync() {
        AsyncEvent asyncEvent = new AsyncEvent(asyncContext,servletRequest, servletResponse);
        try {
            if (logger.isLoggable(Level.FINEST))
                logger.logp(Level.FINEST,CLASS_NAME,"invokeOnStartAsync","listener->"+asyncListener);
            asyncListener.onStartAsync(asyncEvent);
        } catch (IOException e) {
            logger.logp(Level.WARNING,CLASS_NAME,"invokeOnStartAsync","an.io.related.error.has.occurred.during.the.processing.of.the.given.AsyncEvent");
        }
    }
}
