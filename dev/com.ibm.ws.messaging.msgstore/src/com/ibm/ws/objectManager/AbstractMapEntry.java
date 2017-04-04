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
 * 
 * ============================================================================
 */

/**
 * AbstractMapEntry is an internal class which provides an implementation
 * of Map.Entry.
 */
abstract class AbstractMapEntry
                extends ManagedObject
                implements Map.Entry
{

    Object key;
    Token value;

    interface Type
    {
        Object get(AbstractMapEntry entry);
    } // interface Type.

    AbstractMapEntry(Object theKey) {
        key = theKey;
    } // AbstractMapEntry().

    AbstractMapEntry(Object theKey,
                     Token theValue) {
        key = theKey;
        value = theValue;
    } // AbstractMapEntry().

    /**
     * @see java.lang.Object#clone()
     */
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            // No FFDC code needed.
            return null;
        } // try...
    } // clone().

    /**
     * @see com.ibm.ws.objectManager.Map.Entry#getKey()
     */
    public Object getKey() {
        return key;
    }

    /**
     * @see com.ibm.ws.objectManager.Map.Entry#getValue()
     */
    public Token getValue() {
        return value;
    }

    /**
     * Sets the value for this entry
     * 
     * @param object the new value
     * @return the previous value
     */
    public Object setValue(Token object) {
        Object result = value;
        value = object;
        return result;
    }
} // class AbstractMapEntry.
