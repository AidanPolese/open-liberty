/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.javaee.dd.ejb;

import com.ibm.ws.javaee.dd.common.Describable;

/**
 * Represents &lt;timer>.
 */
public interface Timer
                extends Describable
{
    /**
     * @return &lt;schedule>
     */
    TimerSchedule getSchedule();

    /**
     * @return &lt;start>, or null if unspecified
     */
    String getStart();

    /**
     * @return &lt;end>, or null if unspecified
     */
    String getEnd();

    /**
     * @return &lt;timeout-method>
     */
    NamedMethod getTimeoutMethod();

    /**
     * @return true if &lt;persistent> is specified
     * @see #isPersistent
     */
    boolean isSetPersistent();

    /**
     * @return &lt;persistent> if specified
     * @see #isSetPersistent
     */
    boolean isPersistent();

    /**
     * @return &lt;timezone>, or null if unspecified
     */
    String getTimezone();

    /**
     * @return &lt;info>, or null if unspecified
     */
    String getInfo();
}
