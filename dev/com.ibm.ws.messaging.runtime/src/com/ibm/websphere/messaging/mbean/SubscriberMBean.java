/*
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * Copyright IBM Corp. 2012,2013
 * 
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 *
 * Change activity:
 *
 * Reason          Date     Origin   Description
 * --------------- ------   -------- --------------------------------------------
 *   Add MBean support to liberty release    051212
 * 98155           04/04/13 Sharath  Adding the JavaDoc for MBean
 * ============================================================================
 */

package com.ibm.websphere.messaging.mbean;

import javax.management.MXBean;

/**
 * <p>
 * The SubscriberMBean is enabled when a subscriber connects to the messaging engine.
 * A SubscriberMBean is initialized for each Subscriber connecting to the messaging engine.
 * Use the MBean programming interface to query runtime information about a Subscriber.
 * <br><br>
 * JMX clients should use the ObjectName of this MBean to query it
 * <br>
 * Partial Object Name: WebSphere:feature=wasJmsServer, type=Subscriber,name=* <br>
 * where name is unique for each subscriber and is equal to the name of the subscriber.
 * </p>
 * 
 * @ibm-api
 */
@MXBean
public interface SubscriberMBean {

    /**
     * The ID of the Subscriber represented
     * by this instance..
     * 
     * @return ID of the Subscriber
     */
    public String getId();

    /**
     * The name of the Subscriber represented
     * by this instance.
     * 
     * @return Name of the Subscriber
     */
    public String getName();

}