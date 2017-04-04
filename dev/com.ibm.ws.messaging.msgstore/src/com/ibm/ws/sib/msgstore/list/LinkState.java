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
package com.ibm.ws.sib.msgstore.list;

/**
 * This class is a parent class for states of links. It holds 
 * a set of singleton instances of link state objects
 * that are used instead of an int/String so that state can be 
 * checked in a heap dump by looking at the object type.
 */
public interface LinkState
{
    /* Head of List - it does not participate in the list proper */
	public static final LinkState HEAD = LinkStateHead.instance();

    /* the link has been linked */
	public static final LinkState LINKED = LinkStateLinked.instance();

    /* the link been logically unlinked, but is still part of the list */
	public static final LinkState LOGICALLY_UNLINKED = LinkStateLogicallyUnlinked.instance();

    /* the link been removed from the list */
	public static final LinkState PHYSICALLY_UNLINKED = LinkStatePhysicallyUnlinked.instance();

    /* Tail of List - it does not participate in the list proper */
	public static final LinkState TAIL = LinkStateTail.instance();
}
