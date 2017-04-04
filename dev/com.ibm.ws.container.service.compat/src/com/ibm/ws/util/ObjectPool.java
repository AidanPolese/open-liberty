/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2010
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */
package com.ibm.ws.util;

//import com.ibm.ejs.ras.*;

/**
 *
 */

public class ObjectPool {

    private static final boolean DEBUG = false;
    // private static TraceComponent tc =
    // Tr.register(ObjectPool.class.getName(),"");

    private final String name;
    private final Object pool[];
    private int index;

    public ObjectPool(String name, int size) {
        pool = new Object[size];
        index = 0;
        this.name = name;
        // if (DEBUG && tc.isDebugEnabled() ) Tr.debug(tc, "Created " + name +
        // " of size " + size);
    }

    // Returns true if the object was added back to the pool
    public boolean add(Object o) {
        synchronized (pool) {
            if (index < pool.length) {
                pool[index++] = o;
                // if (DEBUG && tc.isDebugEnabled() )
                // Tr.debug(tc,"added to pool " + name + " at " + index);
                return true;
            }
        }
        // if (DEBUG && tc.isDebugEnabled() )
        // Tr.debug(tc,"pool " + name + " is full");
        return false;
    }

    public Object remove() {
        synchronized (pool) {
            if (index > 0) {
                Object o = pool[--index];
                pool[index] = null;
                // if (DEBUG && tc.isDebugEnabled() )
                // Tr.debug(tc,"removed " + index + " from pool " + name);
                return o;
            }
        }
        // if (DEBUG && tc.isDebugEnabled() )
        // Tr.debug(tc,"creating object for " + name + " pool");
        return createObject();
    }

    protected Object createObject() {
        return null;
    }

    public String getName() {
        return name;
    }

}
