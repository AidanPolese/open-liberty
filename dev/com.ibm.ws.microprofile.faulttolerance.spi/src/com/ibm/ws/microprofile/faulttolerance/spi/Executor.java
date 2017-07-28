/*
* IBM Confidential
*
* OCO Source Materials
*
* Copyright IBM Corp. 2017
*
* The source code for this program is not published or otherwise divested
* of its trade secrets, irrespective of what has been deposited with the
* U.S. Copyright Office.
*/
package com.ibm.ws.microprofile.faulttolerance.spi;

import java.util.concurrent.Callable;

/**
 *
 */
public interface Executor<T, R> {

    public R execute(Callable<R> callable, T context);

}
