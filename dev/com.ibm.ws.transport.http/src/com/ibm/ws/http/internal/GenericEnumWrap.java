/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2009
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */
package com.ibm.ws.http.internal;

import java.util.Enumeration;
import java.util.Iterator;

/**
 * Utility class that will create an Enumeration wrapper.
 * 
 * @param <T>
 * 
 */
public class GenericEnumWrap<T> implements Enumeration<T> {

    /** The wrapped iterator object */
    private Iterator<T> myIterator = null;
    /** Singleton object instead of an iterator */
    private T singleton = null;

    /**
     * Constructor that wraps an Iterator object.
     * 
     * @param iter
     */
    public GenericEnumWrap(Iterator<T> iter) {
        this.myIterator = iter;
    }

    /**
     * Provide an enumeration wrapper for a single object.
     * 
     * @param item
     */
    public GenericEnumWrap(T item) {
        this.singleton = item;
    }

    /*
     * @see java.util.Enumeration#hasMoreElements()
     */
    public boolean hasMoreElements() {
        if (null == this.myIterator) {
            return (null == this.singleton);
        }
        return this.myIterator.hasNext();
    }

    /*
     * @see java.util.Enumeration#nextElement()
     */
    public T nextElement() {
        T rc = null;
        if (null == this.myIterator) {
            rc = this.singleton;
            this.singleton = null;
        } else {
            rc = this.myIterator.next();
        }
        return rc;
    }
}