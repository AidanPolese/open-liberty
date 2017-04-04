package com.ibm.ws.sib.msgstore.impl;
/*
 * 
 * 
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * Copyright IBM Corp. 2012
 * 
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 * 
 *
 * Change activity:
 *
 *  Reason         Date     Origin   Description
 * --------------- -------- -------- ------------------------------------------
 *  413861         10/01/07 gareth   Add state to MS.toString()
 * ============================================================================
 */

 
/**
 * This class is a parent class for MessageStore states. It
 * holds a set of singleton instances of named MessageStore state
 * objects that are used instead of an int/String so that state
 * can be checked in a heap dump by looking at the object type.
 */
public interface MessageStoreState
{
    /**
     * MessageStore has not yet been initialized.
     */
    public static final MessageStoreState STATE_UNINITIALIZED = MessageStoreStateUninitialized.instance();
    /**
     * MessageStore is in the process of starting.
     */
    public static final MessageStoreState STATE_STARTING = MessageStoreStateStarting.instance();
    /**
     * MessageStore has started.
     */
    public static final MessageStoreState STATE_STARTED = MessageStoreStateStarted.instance();
    /**
     * MessageStore is stopped.
     */
    public static final MessageStoreState STATE_STOPPED = MessageStoreStateStopped.instance();
}
