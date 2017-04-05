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
 * Change activity:
 *
 *  Reason           Date    Origin     Description
 * --------------- -------- -------- ------------------------------------------
 *  343689         04/04/06 gareth    Modify trace output cont.
 * ============================================================================
 */

/**
 * A starter implementation of the Set interface. This does not
 * create a new Set and does not extend ManagedObject.
 * 
 * @see Set
 * @see AbstractCollectionView
 * @see AbstractSet
 */
public abstract class AbstractSetView
                extends AbstractCollectionView
                implements Set {
    /**
     * Default no argument constructor.
     */
    protected AbstractSetView()
    {} // AbstractSetView().
} // AbstractSetView.