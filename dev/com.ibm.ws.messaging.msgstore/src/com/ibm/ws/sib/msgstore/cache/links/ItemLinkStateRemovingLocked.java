package com.ibm.ws.sib.msgstore.cache.links;
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
 *  Reason           Date    Origin     Description
 * --------------- -------- ---------- ----------------------------------------
 *  326643.2       09/12/05   gareth    Use singleton objects for item link state
 * ============================================================================
 */

public class ItemLinkStateRemovingLocked implements ItemLinkState
{
    private static final ItemLinkStateRemovingLocked _instance = new ItemLinkStateRemovingLocked();

    private static final String _toString = "RemovingLocked";

    static ItemLinkState instance()
    {
        return _instance;
    }

    /**
     * private constructor so state can only 
     * be accessed via instance method.
     */
    private ItemLinkStateRemovingLocked() {}

    public String toString()
    {
        return _toString;
    }
}

