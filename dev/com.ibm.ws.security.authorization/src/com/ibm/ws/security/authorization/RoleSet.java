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
package com.ibm.ws.security.authorization;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A RoleSet is an immutable Set of role names (Strings) for use by the
 * AuthorizationTableService.
 * <p>
 * The Set is immutable in order to enforce read-only access to the
 * AuthorizationTableService by other services.
 * 
 * @see AuthorizationTableService
 */
public class RoleSet implements Set<String> {
    /**
     * A RoleSet containing no roles.
     */
    @SuppressWarnings("unchecked")
    public static final RoleSet EMPTY_ROLESET = new RoleSet(Collections.EMPTY_SET);

    private final Set<String> set;

    /**
     * Construct the immutable RoleSet based on the provided Set.
     * 
     * @param set
     */
    public RoleSet(Set<String> set) {
        this.set = Collections.unmodifiableSet(set);
    }

    /**
     * Construct the immutable RoleSet by combining another Set of roles
     * with an existing RoleSet.
     * 
     * @param roleSet existing roleSet, must not be null
     * @param another (optional) Set of roles
     */
    public RoleSet(RoleSet roleSet, Set<String> set) {
        Set<String> newSet = new HashSet<String>(roleSet.set);
        if (set != null)
            newSet.addAll(set);
        this.set = Collections.unmodifiableSet(newSet);
    }

    /**
     * {@inheritDoc}<p>
     * This method intentionally does not alter the map contents
     * and always answers false.
     */
    @Override
    public boolean add(String object) {
        return false;
    }

    /**
     * {@inheritDoc}<p>
     * This method intentionally does not alter the map contents
     * and always answers false.
     */
    @Override
    public boolean addAll(Collection<? extends String> collection) {
        return false;
    }

    /**
     * {@inheritDoc}<p>
     * This method intentionally does not alter the map contents.
     */
    @Override
    public void clear() {}

    /** {@inheritDoc} */
    @Override
    public boolean contains(Object object) {
        return set.contains(object);
    }

    /** {@inheritDoc} */
    @Override
    public boolean containsAll(Collection<?> collection) {
        return set.containsAll(collection);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isEmpty() {
        return set.isEmpty();
    }

    /** {@inheritDoc} */
    @Override
    public Iterator<String> iterator() {
        return set.iterator();
    }

    /**
     * {@inheritDoc}<p>
     * This method intentionally does not alter the map contents
     * and always answers false.
     */
    @Override
    public boolean remove(Object object) {
        return false;
    }

    /**
     * {@inheritDoc}<p>
     * This method intentionally does not alter the map contents
     * and always answers false.
     */
    @Override
    public boolean removeAll(Collection<?> collection) {
        return false;
    }

    /**
     * {@inheritDoc}<p>
     * This method intentionally does not alter the map contents
     * and always answers false.
     */
    @Override
    public boolean retainAll(Collection<?> collection) {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public int size() {
        return set.size();
    }

    /**
     * {@inheritDoc}<p>
     * This method intentionally answers null.
     */
    @Override
    public Object[] toArray() {
        return null;
    }

    /**
     * {@inheritDoc}<p>
     * This method intentionally answers null.
     */
    @Override
    public <T> T[] toArray(T[] array) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return set.toString();
    }

}
