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

/**
 * Represents &lt;schedule> in &lt;timer>.
 */
public interface TimerSchedule
{
    /**
     * @return &lt;second>, or null if unspecified
     */
    String getSecond();

    /**
     * @return &lt;minute>, or null if unspecified
     */
    String getMinute();

    /**
     * @return &lt;hour>, or null if unspecified
     */
    String getHour();

    /**
     * @return &lt;day-of-month>, or null if unspecified
     */
    String getDayOfMonth();

    /**
     * @return &lt;month>, or null if unspecified
     */
    String getMonth();

    /**
     * @return &lt;day-of-week>, or null if unspecified
     */
    String getDayOfWeek();

    /**
     * @return &lt;year>, or null if unspecified
     */
    String getYear();
}
