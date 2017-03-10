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

package com.ibm.websphere.event;

import java.util.concurrent.ExecutorService;

/**
 * An {@code ExecutorServiceFactory} encapsulates the mechanism used to
 * acquire an instance of a named {@link ExecutorService} for delivering
 * events to event handlers.
 */
public interface ExecutorServiceFactory {

    ExecutorService getExecutorService(String name);

}
