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
 * Reason          Date   Origin   Description
 * --------------- ------ -------- --------------------------------------------
 * Add MBean support to liberty release    051212
 * 91800           22-01-13 Sharath  Remove the StateExtended property since it does not make any sense in Liberty Messaging
 * 90454           30/01/13 sanjay   Fix for enabling listPeparedTransactions
 * 98155           04/04/13 Sharath  Adding the JavaDoc for MBean
 * ============================================================================
 */

package com.ibm.websphere.messaging.mbean;

import javax.management.MXBean;

/**
 * <p>
 * The MessagingEngineMBean is enabled by the wasJmsServer feature.
 * This MBean provides an interface to query the runtime information of
 * the messaging engine defined in the configuration.
 * <br><br>
 * JMX clients should use the ObjectName of this MBean to query it.
 * <br>
 * Partial Object Name: WebSphere:feature=wasJmsServer, type=MessagingEngine,name=* <br>
 * where name is unique for each messaging engine and is equal to the name of the messaging engine defined in configuration.
 * </p>
 * 
 * @ibm-api
 */
@MXBean
public interface MessagingEngineMBean {

    /**
     * Returns the name of the current messaging engine. Please note that it is not possible
     * to rename a messaging engine instance.
     * 
     * @return Name of the Messaging Engine
     */
    public String getName();

    /**
     * Return the state of the Messaging Engine.
     * 
     * @return State of the Messaging Engine
     * 
     */
    public String state();

    /**
     * Resets a corrupt destination such that on restart, it is deleted and recreated.
     * 
     * @param Name The name of the destination on the bus
     */
    public void resetDestination(String name) throws Exception;

    /**
     * Request the receiver to dump its xml representation.The dump file is created in the current server
     * directory
     * 
     * @param dumpSpec The dump specification string, eg. "com.ibm.ws.sib.msgstore.*:com.ibm..."
     */
    void dump(String dumpSpec);

    /**
     * Obtain a list of XIDs which are in-doubt.
     * Part of MBean interface for resolving in-doubt transactions in Message Store.
     * 
     * @return XIDs as array of strings
     */
    public String[] listPreparedTransactions();

    /**
     * Commit the given transaction.
     * Part of MBean interface for resolving in-doubt transactions in Message Store.
     * 
     * @param xid a string representing the xid of the transaction to be committed.
     */
    public void commitPreparedTransaction(String xid) throws Exception;

    /**
     * Rollback the given transaction.
     * Part of MBean interface for resolving in-doubt transactions in Message Store.
     * 
     * @param xid a string representing the xid of the transaction to be rolled back.
     */
    public void rollbackPreparedTransaction(String xid) throws Exception;

}
