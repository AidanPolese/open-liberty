/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011, 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.ejbcontainer.jitdeploy;

public class TestMethodCalls
{
    final String ivOperation;
    final boolean ivApplicationException;
    private final TestMethodCall[] ivCalls;
    private int ivCallIndex;

    TestMethodCalls(String operation, boolean appEx, TestMethodCall[] calls)
    {
        ivOperation = operation;
        ivApplicationException = appEx;
        ivCalls = calls;
    }

    public Object invoke(String methodName, Object... args)
    {
        return ivCalls[ivCallIndex++].invoke(methodName, args);
    }
}
