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
package com.ibm.ws.kernel.boot.internal.commands;

import java.io.File;

/**
 * interface of package/dump processor
 */
public interface ArchiveProcessor {

    // If on windows, fix backslashes for regular expression use:
    public static final String REGEX_SEPARATOR = File.separator.equals("\\") ? "\\\\" : File.separator;

    public static final String REGEX_TIMESTAMP = "\\d\\d\\.\\d\\d\\.\\d\\d_\\d\\d\\.\\d\\d\\.\\d\\d";

    public static class Pair<U, V> {
        private final U pairKey;
        private final V pairValue;

        /**
         * @param pairKey
         * @param pairValue
         */
        public Pair(U pairKey, V pairValue) {
            super();
            this.pairKey = pairKey;
            this.pairValue = pairValue;
        }

        @Override
        public String toString() {
            return "Pair[" + pairKey + '=' + pairValue + ']';
        }

        /** {@inheritDoc} */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((pairKey == null) ? 0 : pairKey.hashCode());
            result = prime * result + ((pairValue == null) ? 0 : pairValue.hashCode());
            return result;
        }

        /**
         * @return the pairKey
         */
        public U getPairKey() {
            return pairKey;
        }

        /**
         * @return the pairValue
         */
        public V getPairValue() {
            return pairValue;
        }

        /** {@inheritDoc} */
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            @SuppressWarnings("rawtypes")
            Pair other = (Pair) obj;
            if (pairKey == null) {
                if (other.pairKey != null)
                    return false;
            } else if (!pairKey.equals(other.pairKey))
                return false;
            if (pairValue == null) {
                if (other.pairValue != null)
                    return false;
            } else if (!pairValue.equals(other.pairValue))
                return false;
            return true;
        }

    }
}
