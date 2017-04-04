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
package com.ibm.wsspi.application;

import java.util.concurrent.Future;

/**
 *
 */
public interface Application {
    public Future<Boolean> start();

    public Future<Boolean> stop();

    public void restart();

    public ApplicationState getState();
}
