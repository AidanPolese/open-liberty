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

public class MessageStoreStateStarting implements MessageStoreState
{
    private static final MessageStoreStateStarting _instance = new MessageStoreStateStarting();

    private static final String _toString = "Starting";

    static MessageStoreState instance()
    {
        return _instance;
    }

    /**
     * private constructor so state can only 
     * be accessed via instance method.
     */
    private MessageStoreStateStarting() {}

    public String toString()
    {
        return _toString;
    }
}
