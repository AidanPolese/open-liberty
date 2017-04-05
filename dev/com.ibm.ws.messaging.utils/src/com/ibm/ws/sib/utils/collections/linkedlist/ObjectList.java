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
 * Reason          Date         Origin        Description
 * --------------- ----------   -----------   ---------------------------------
 *                 2004.02.19   van Leersum   Original
 * ============================================================================
 */
package com.ibm.ws.sib.utils.collections.linkedlist;


/**
 * This class makes use of the linked list by extending the link
 * to contain a generic object.  It also acts as an example of how
 * to specialize the linked list.
 * 
 * @author drphill
 *
 */
public class ObjectList extends LinkedList {

    public static class ObjectListLink extends Link {
        private final Object _storedObject;

        private ObjectListLink(Object object) {
            super();
            _storedObject = object;
        }

        // reply the stored object
        public final Object getObject() {
            return _storedObject;
        }
    }

    /**
     * Add an object to the end of the linked list.  A new link
     * is created for the object. 
     * @param object
     */
    public final void addObject(Object object) {
        append(new ObjectListLink(object));
    }

    public final synchronized Object removeFirst() {
        Object object = null;
        ObjectListLink link = (ObjectListLink)getHead();
        if (null != link) {
            object = link.getObject();
            link.unlink();
        }
        return object;
    }

}
