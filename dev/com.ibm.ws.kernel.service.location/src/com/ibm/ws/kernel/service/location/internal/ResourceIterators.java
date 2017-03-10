/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2010
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.kernel.service.location.internal;

import java.io.File;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

/**
 *
 */
final class ResourceIterators {
    private ResourceIterators() {
        throw new AssertionError("This class is not instantiable");
    }

    static class ChildIterator implements Iterator<String> {
        private final String[] children;
        private final File parent;
        private int index = 0;

        ChildIterator(File p, String[] kids) {
            parent = p;
            children = (kids == null) ? new String[0] : kids;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean hasNext() {
            if (index < children.length)
                return true;

            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String next() {
            if (index >= children.length)
                throw new NoSuchElementException("No more elements (p=" + parent.getAbsolutePath() + ")");

            String child = children[index++];
            File f = new File(parent, child);
            if (f.isDirectory())
                child += '/';

            return child;
        }

        /**
         * no-op.
         */
        @Override
        public void remove() {}
    }

    static class MatchingIterator implements Iterator<String> {
        private final Iterator<String> i;
        private final Pattern regex;
        private String next = null;

        MatchingIterator(Iterator<String> i, String regex) {
            this.i = i;
            this.regex = Pattern.compile(regex);
        }

        @Override
        public boolean hasNext() {
            String n;
            while (i.hasNext()) {
                n = i.next();
                if (regex.matcher(n).matches()) {
                    next = n;
                    return true;
                }
            }

            return false;
        }

        /**
         *
         */
        @Override
        public String next() {
            if (next == null)
                throw new NoSuchElementException("No more elements");

            return next;
        }

        /**
         *
         */
        @Override
        public void remove() {}
    }
}
