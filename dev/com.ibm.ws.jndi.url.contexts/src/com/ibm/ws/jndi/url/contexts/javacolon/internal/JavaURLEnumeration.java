/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jndi.url.contexts.javacolon.internal;

import java.util.Iterator;
import java.util.Set;

import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

/**
 * @param <T>
 * 
 */
public class JavaURLEnumeration<T extends NameClassPair> implements NamingEnumeration<T> {

    private final Iterator<T> delegate;

    /**
     * @param allInstances
     */
    public JavaURLEnumeration(Set<T> allInstances) {
        this.delegate = allInstances.iterator();
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasMoreElements() {
        return delegate.hasNext();
    }

    /** {@inheritDoc} */
    @Override
    public T nextElement() {
        return delegate.next();
    }

    /** {@inheritDoc} */
    @Override
    public T next() throws NamingException {
        return delegate.next();
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasMore() throws NamingException {
        return delegate.hasNext();
    }

    /** {@inheritDoc} */
    @Override
    public void close() throws NamingException {
        // Ignore, nothing to do

    }

}
