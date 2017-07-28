/*
* IBM Confidential
*
* OCO Source Materials
*
* Copyright IBM Corp. 2017
*
* The source code for this program is not published or otherwise divested
* of its trade secrets, irrespective of what has been deposited with the
* U.S. Copyright Office.
*/
package com.ibm.ws.microprofile.config.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.microprofile.config.spi.ConfigSource;

public class SortedSources implements Iterable<ConfigSource> {

    private SortedSet<ConfigSource> sources;

    public SortedSources() {
        sources = new TreeSet<ConfigSource>(ConfigSourceComparator.INSTANCE);
    }

    public SortedSources(SortedSet<ConfigSource> initialSources) {
        this();
        sources.addAll(initialSources);
    }

    public SortedSources unmodifiable() {
        sources = Collections.unmodifiableSortedSet(sources);
        return this;
    }

    /**
     * @param toAdd
     */
    public void addAll(Collection<ConfigSource> toAdd) {
        sources.addAll(toAdd);
    }

    /** {@inheritDoc} */
    @Override
    public Iterator<ConfigSource> iterator() {
        return sources.iterator();
    }

    /**
     * @return
     */
    public int size() {
        return sources.size();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("Config Sources: ");
        for (ConfigSource source : this) {
            builder.append("\n\t");
            builder.append(source.getOrdinal());
            builder.append(" = ");
            builder.append(source.getName());
        }
        return builder.toString();
    }

    /**
     * CURRENTLY ONLY USED BY UNIT TEST
     */
    public void add(ConfigSource source) {
        sources.add(source);
    }

}
