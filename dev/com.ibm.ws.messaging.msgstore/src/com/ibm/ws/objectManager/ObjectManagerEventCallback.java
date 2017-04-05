package com.ibm.ws.objectManager;

/*
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * 5724-H88, 5724-J08, 5724-I63, 5655-W65, 5724-H89, 5722-WE2   Copyright IBM Corp., 2013
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
 * --------------- -------- -------- ------------------------------------------
 * 495856          04/02/08 gareth   Add event callbacks to ObjectManager 
 * ============================================================================
 */

/**
 * This interface defines methods that can be called on a
 * registered call back when defined events occur within the
 * ObjectManager.
 */
public interface ObjectManagerEventCallback {
    /**
     * Called when the ObjectManager has stopped either due to a specific
     * shutdown request or an unrecoverable error.
     * <ul>
     * <li> args
     * <ul>
     * <li>none.
     * </ul>
     * </ul>
     */
    public static final int objectManagerStopped = 0;

    /**
     * Called just before an Object store is opened.
     * <ul>
     * <li> args
     * <ul>
     * <li>ObjectStore being opened.
     * </ul>
     * </ul>
     */
    public static final int objectStoreOpened = 1;
    static final String[] eventNames = { "objectManagerStopped",
                                        "objectStoreOpened"
    };

    /**
     * Called when an event occurs.
     * 
     * @param event the type of event.
     * @param args defined for each event type.
     */
    public void notification(int event, Object[] args);
} // interface ObjectManagerEventCallback.