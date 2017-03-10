/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015, 2016
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.logging;

import java.util.Queue;

/**
 * Liberty Internal Trace routing service. Routes messages to sundry logging streams.
 *
 */
public interface WsTraceRouter {

    /**
     * Route the given message.
     *
     * @param routedTrace Contains the LogRecord and various message formats.
     *
     * @return true if the message may be logged normally by the caller,
     *         (in addition to whatever logging was performed under this
     *         method), if desired.
     */
    public boolean route(RoutedMessage routedTrace);

    /**
     *
     */
    public void setEarlierTraces(Queue<RoutedMessage> earlierTraces);
}
