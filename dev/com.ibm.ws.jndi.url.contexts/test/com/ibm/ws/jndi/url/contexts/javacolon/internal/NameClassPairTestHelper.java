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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.naming.NameClassPair;

public class NameClassPairTestHelper {
    public static Set<NameClassPairTestHelper> newSet(NameClassPair... pairs) {
        return newSet(Arrays.asList(pairs));
    }

    public static Set<NameClassPairTestHelper> newSet(Collection<? extends NameClassPair> pairs) {
        Set<NameClassPairTestHelper> keys = new HashSet<NameClassPairTestHelper>();
        for (NameClassPair pair : pairs) {
            keys.add(new NameClassPairTestHelper(pair));
        }
        return keys;
    }

    private final NameClassPair pair;

    public NameClassPairTestHelper(NameClassPair pair) {
        this.pair = pair;
    }

    @Override
    public String toString() {
        return '[' + pair.getName() + "=" + pair.getClassName() + ']';
    }

    @Override
    public int hashCode() {
        return pair.getName().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || o.getClass() != NameClassPairTestHelper.class) {
            return false;
        }

        NameClassPairTestHelper helper = (NameClassPairTestHelper) o;
        return pair.getName().equals(helper.pair.getName()) && pair.getClassName().equals(helper.pair.getClassName());
    }
}
