package com.ibm.ws.sib.msgstore.list;
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
 * Reason        Date        Origin       Description
 * ------------  --------    ----------   ---------------------------------------
 *               20/12/05    schofiel     Original
 * ============================================================================
 */

public class LinkStateHead implements LinkState
{
    private static final LinkStateHead _instance = new LinkStateHead();

    private static final String _toString = "Head";

    static LinkState instance()
    {
        return _instance;
    }

    /**
     * private constructor so state can only 
     * be accessed via instance method.
     */
    private LinkStateHead() {}

    public String toString()
    {
        return _toString;
    }
}

