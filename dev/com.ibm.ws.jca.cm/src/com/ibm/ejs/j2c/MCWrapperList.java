/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2001,2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ejs.j2c;

import java.util.ArrayList;

/* Note:
 *
 * The size is the number of mcWrapper that are in this list.
 * This is not the actual number of mcWrappers assigned to this list
 *
 * Example:
 *   1. When a mcWrapper is created, it is placed in the shared pool or the used
 *      pool, not in the free pool.  But we still need to keep track of this mcWrapper.
 *      At this time the real size of the arraylist is 0, but the totalSize is 1.
 *   2. When the mcWrapper is returned to the free pool, the totalSize and arrylist
 *      size may be equal.
 */

public final class MCWrapperList extends ArrayList<Object> {

    private static final long serialVersionUID = -4093886924342827512L; 

    protected MCWrapperList(int initSize) {
        super(initSize);
    }
}