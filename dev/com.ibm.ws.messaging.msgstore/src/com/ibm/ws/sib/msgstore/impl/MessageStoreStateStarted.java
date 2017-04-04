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

public class MessageStoreStateStarted implements MessageStoreState
{
    private static final MessageStoreStateStarted _instance = new MessageStoreStateStarted();

    private static final String _toString = "Started";

    static MessageStoreState instance()
    {
        return _instance;
    }

    /**
     * private constructor so state can only 
     * be accessed via instance method.
     */
    private MessageStoreStateStarted() {}

    public String toString()
    {
        return _toString;
    }
}
