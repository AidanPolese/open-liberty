/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jmx_test.mbeans;

/**
 *
 */
public class Counter implements CounterMBean {

    private int i = 0;

    public Counter() {}

    @Override
    public int getValue() {
        return i;
    }

    @Override
    public void increment() {
        ++i;
    }

    @Override
    public void print() {
        System.out.println(i);
    }

    @Override
    public void reset() {
        i = 0;
    }
}
