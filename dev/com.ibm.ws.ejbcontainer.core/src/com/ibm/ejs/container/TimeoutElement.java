/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 1998, 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ejs.container;

import java.util.Date;

import com.ibm.ejs.ras.Tr;
import com.ibm.ejs.ras.TraceComponent;

/**
 * <code>TimeoutElement</code> is a holder object used by the
 * <code>StatefuleBeanReaper</code> class to retain information on bean ids
 * and their associated timeout value
 */
public class TimeoutElement
{
    private static final TraceComponent tc = Tr.register(TimeoutElement.class,
                                                         "EJBCache",
                                                         "com.ibm.ejs.container.container");

    /**
     * Construct a <code>TimeoutElement</code> object, holding the specified
     * bean ID and the time out value
     * 
     * @param beanId The bean id of the session bean
     * @param timeoutVal The timeout value for the session bean
     */
    TimeoutElement(BeanId beanId, long timeoutVal)
    {
        this.beanId = beanId;
        this.timeout = timeoutVal;
        this.lastAccessTime = System.currentTimeMillis();
    }

    public String toString()
    {
        return "Bean ID = " + beanId + " : Timeout = " + timeout;
    }

    public boolean isTimedOut()
    {
        if (timeout > 0)
        {
            synchronized (this)
            {
                long now = System.currentTimeMillis();
                if (now - lastAccessTime >= timeout)
                {
                    if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled())
                        Tr.event(tc, "Session bean timed out",
                                 "Current Time: " + new Date(now) +
                                                 " Last Access Time: " + new Date(lastAccessTime) +
                                                 " Timeout: " + timeout + " ms");

                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Bean ID of the session bean
     */
    public final BeanId beanId;

    /**
     * Timeout value for the bean
     */
    public final long timeout;

    /**
     * Last time this bean was accessed
     */
    public volatile long lastAccessTime;

    /**
     * Whether the bean has been passivated or not
     */
    public volatile boolean passivated;
}
