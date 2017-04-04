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
package com.ibm.ws.container.service.metadata.internal;

import java.util.Arrays;

public class IndexList {
    private int nextValue;
    private int[] freeValues = new int[8];
    private int nextFreeIndex;

    public int reserve() {
        if (nextFreeIndex == 0) {
            return nextValue++;
        }

        return freeValues[--nextFreeIndex];
    }

    public void unreserve(int value) {
        if (nextFreeIndex == freeValues.length) {
            freeValues = Arrays.copyOf(freeValues, freeValues.length + freeValues.length);
        }
        freeValues[nextFreeIndex++] = value;
    }
}
