/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012,2016
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.logging.internal.osgi;

import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.ibm.ws.logging.RoutedMessage;
import com.ibm.ws.logging.WsTraceHandler;
import com.ibm.ws.logging.WsTraceRouter;

/**
 * This class handles all logic associated with routing messages to various
 * wsTraceHandler services.
 *
 */
public class WsTraceRouterImpl implements WsTraceRouter {
    /**
     * Map of LogHandlerIDs to wsTraceHandlers.
     */
    private final ConcurrentMap<String, WsTraceHandler> wsTraceHandlerServices = new ConcurrentHashMap<String, WsTraceHandler>();

    static RecursionCounter counter = new RecursionCounter();
    /**
     * Earlier traces issued before wsTraceHandler(s) are registered.
     *
     * When a new wsTraceHandler is registered the earlier traces are delivered to it.
     *
     * Only the previous 200 traces are kept (can't keep them all since there's no telling
     * when or if a wsTraceHandler will be registered).
     */
    private Queue<RoutedMessage> earlierTraces;
    private static final ReentrantReadWriteLock REWRLOCK = new ReentrantReadWriteLock(true);

    /**
     * CTOR, protected.
     */
    protected WsTraceRouterImpl() {}

    /**
     * @param earlierTraces a queue of traces that were issued prior to this
     *            WsTraceRouter getting activated.
     */
    @Override
    public void setEarlierTraces(Queue<RoutedMessage> earlierTraces) {
        this.earlierTraces = earlierTraces;
    }

    /**
     * Add the wsTraceHandler ref. 1 or more LogHandlers may be set.
     */
    public void setWsTraceHandler(String id, WsTraceHandler ref) {
        if (id != null && ref != null) {
            REWRLOCK.writeLock().lock();
            try {
                wsTraceHandlerServices.put(id, ref);

                /*
                 * Route prev traces to the new LogHandler.
                 *
                 * This is primarily for solving the problem during server init where the WsTraceRouterImpl
                 * is registered *after* we've already issued some early startup traces. We cache
                 * these early traces in the "earlierTraces" queue in BaseTraceService, which then
                 * passes them to WsTraceRouterImpl once it's registered.
                 */
                if (earlierTraces == null) {
                    return;
                }
                for (RoutedMessage earlierTrace : earlierTraces.toArray(new RoutedMessage[earlierTraces.size()])) {
                    if (earlierTrace != null) {
                        routeTo(earlierTrace, id);
                    }
                }
            } finally {
                REWRLOCK.writeLock().unlock();
            }
        }
    }

    /**
     * Remove the LogHandler ref.
     */
    public void unsetWsTraceHandler(String id, WsTraceHandler ref) {
        if (id != null) {
            if (ref == null) {
                wsTraceHandlerServices.remove(id);
            } else {
                wsTraceHandlerServices.remove(id, ref);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean route(RoutedMessage routedTrace) {
        boolean logNormally = true;
        REWRLOCK.readLock().lock();
        try {
            if (!(counter.incrementCount() > 1)) {
                // Cache message for WsTraceHandlers that haven't registered yet.
                if (earlierTraces != null) {
                    earlierTraces.add(routedTrace);
                }
                Set<String> routeAllMsgsToTheseLogHandlers = wsTraceHandlerServices.keySet();
                logNormally = routeToAll(routedTrace, routeAllMsgsToTheseLogHandlers);

            }
        } finally {
            REWRLOCK.readLock().unlock();
            counter.decrementCount();
        }
        return logNormally;

    }

    /**
     * Route the trace to all LogHandlers in the set.
     *
     * @return true if the set contained DEFAULT, which means the msg should be logged
     *         normally as well. false otherwise.
     */
    protected boolean routeToAll(RoutedMessage routedTrace, Set<String> logHandlerIds) {

        for (String logHandlerId : logHandlerIds) {
            routeTo(routedTrace, logHandlerId);
        }

        return true; //for now return true. Later we might have a config/flag to check whether to log normally or not.
    }

    /**
     * Route the traces to the LogHandler identified by the given logHandlerId.
     *
     * @param msg The fully formatted trace.
     * @param logRecord The associated LogRecord, in case the LogHandler needs it.
     * @param logHandlerId The LogHandler ID in which to route.
     */
    protected void routeTo(RoutedMessage routedTrace, String logHandlerId) {
        WsTraceHandler wsTraceHandler = wsTraceHandlerServices.get(logHandlerId);
        if (wsTraceHandler != null) {
            wsTraceHandler.publish(routedTrace);
        }
    }

}
