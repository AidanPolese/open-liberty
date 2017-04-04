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

 
/**
 * This class is a parent class for transaction states. It holds 
 * a set of singleton instances of named transaction state objects
 * that are used instead of an int/String so that state can be 
 * checked in a heap dump by looking at the object type.
 */
public interface ItemLinkState
{
    public static final ItemLinkState STATE_ADDING_LOCKED = ItemLinkStateAddingLocked.instance();

    public static final ItemLinkState STATE_ADDING_UNLOCKED = ItemLinkStateAddingUnlocked.instance();;

    public static final ItemLinkState STATE_AVAILABLE = ItemLinkStateAvailable.instance();;

    public static final ItemLinkState STATE_LOCKED = ItemLinkStateLocked.instance();;

    public static final ItemLinkState STATE_LOCKED_FOR_EXPIRY = ItemLinkStateLockedForExpiry.instance();;

    public static final ItemLinkState STATE_NOT_STORED = ItemLinkStateNotStored.instance();;

    public static final ItemLinkState STATE_PERSISTENTLY_LOCKED = ItemLinkStatePersistentlyLocked.instance();;

    public static final ItemLinkState STATE_PERSISTING_LOCK = ItemLinkStatePersistingLock.instance();;

    public static final ItemLinkState STATE_REMOVING_EXPIRING = ItemLinkStateRemovingExpiring.instance();;

    public static final ItemLinkState STATE_REMOVING_LOCKED = ItemLinkStateRemovingLocked.instance();;

    public static final ItemLinkState STATE_REMOVING_PERSISTENTLY_LOCKED = ItemLinkStateRemovingPersistentlyLocked.instance();;

    public static final ItemLinkState STATE_REMOVING_WITHOUT_LOCK = ItemLinkStateRemovingWithoutLock.instance();;

    public static final ItemLinkState STATE_UNLOCKING_PERSISTENTLY_LOCKED = ItemLinkStateUnlockingPersistentlyLocked.instance();;

    public static final ItemLinkState STATE_UPDATING_DATA = ItemLinkStateUpdatingData.instance();;
}

