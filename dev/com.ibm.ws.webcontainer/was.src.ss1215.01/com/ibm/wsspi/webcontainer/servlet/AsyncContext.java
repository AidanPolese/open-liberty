// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2009
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.wsspi.webcontainer.servlet;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import com.ibm.wsspi.webcontainer.async.WrapperRunnable;

import com.ibm.ws.webcontainer.async.AsyncListenerEntry;
import com.ibm.ws.webcontainer.async.AsyncServletReentrantLock;

/**
 * 
 * 
 * AsyncContext is a private spi for websphere components to make
 * use of Async Servlet features
 * 
 * @ibm-private-in-use
 * 
 * @since   WAS7.0
 * 
 */
public interface AsyncContext extends javax.servlet.AsyncContext{
    public void setRequestAndResponse(ServletRequest servletRequest, ServletResponse servletResponse);

    public void executeNextRunnable();

    public boolean isCompletePending();

	public void invalidate();

	List<AsyncListenerEntry> getAsyncListenerEntryList();

	public void initialize();

	public boolean isDispatchPending();

	public IServletContext getWebApp();

	public Collection<WrapperRunnable> getAndClearStartRunnables();

	public void addStartRunnable(WrapperRunnable wrapperRunnable);

	public void removeStartRunnable(WrapperRunnable wrapperRunnable);

	boolean isDispatching();

	public boolean cancelAsyncTimer();

	public void setInvokeErrorHandling(boolean b);

	public AsyncServletReentrantLock getErrorHandlingLock();

	public void setDispatching(boolean b);

	public boolean isComplete();
}
