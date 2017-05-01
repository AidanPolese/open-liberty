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

import org.hamcrest.Description;
import org.jmock.api.Action;
import org.jmock.api.Invocation;

/**
 * This class is useful for allowing JMock objects to just parrot back arguments
 * when invoked.
 */
class ArgumentEchoer<T> implements Action {

    public void describeTo(Description description) {
        description.appendText("mirroring argument");
    }

    public Object invoke(Invocation invocation) throws Throwable {
        return invocation.getParameter(0);
    }

    public static <T> Action echoArgument() {
        return new ArgumentEchoer<T>();
    }
}