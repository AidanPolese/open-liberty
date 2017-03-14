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
package com.ibm.ws.kernel.filemonitor.internal;

import java.util.Arrays;
import java.util.Map;

import org.hamcrest.Description;
import org.jmock.api.Action;
import org.jmock.api.Invocation;

/**
 * This class allows JMock objects to read the answers from a map
 * when they're invoked. This is a lot easier than setting
 * up an expectation for every possible method parameter,
 * and it also reduces the clutter of satisfied expectations
 * in JMock assertion failed messages.
 */
class MapReader<T, V> implements Action {

    private final Map<T, V> map;

    /**
     * @param map
     */
    public MapReader(Map<T, V> map) {
        this.map = map;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hamcrest.SelfDescribing#describeTo(org.hamcrest.Description)
     */
    @Override
    public void describeTo(Description description) {
        description.appendText("answering from map " + Arrays.toString(map.entrySet().toArray()));

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jmock.api.Invokable#invoke(org.jmock.api.Invocation)
     */
    @Override
    public Object invoke(Invocation inv) throws Throwable {
        return map.get(inv.getParameter(0));
    }

    public static <T, V> Action readFromMap(Map<T, V> map) {
        return new MapReader<T, V>(map);
    }

}