//* ===========================================================================
//*
//* IBM SDK, Java(tm) 2 Technology Edition, v5.0
//* (C) Copyright IBM Corp. 2005, 2007
//*
//* The source code for this program is not published or otherwise divested of
//* its trade secrets, irrespective of what has been deposited with the U.S.
//* Copyright office.
//*
//* ===========================================================================
//
// Change History:
// Date     UserId      Defect          Description
// --------------------------------------------------------------------------------
// 09/21/04 gilgen      233448          Add copyright statement and change history.
// 10/08/04 gilgen      235345.1        Do bind at init time instead of start time.
// 04/05/05 gilgen      LIDB3618-2      Made extendable for Aio support
// 05/09/07 leeja       PK44756         Prevent hang on System.exit

package com.ibm.ws.tcpchannel.internal;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.ffdc.FFDCFilter;

/**
 * Class to handle the non-blocking accept work.
 */
public class NBAccept {
    private static final String CLASS_NAME = NBAccept.class.getName();
    private static final TraceComponent tc = Tr.register(NBAccept.class, TCPChannelMessageConstants.TCP_TRACE_NAME, TCPChannelMessageConstants.TCP_BUNDLE);

    private NBAcceptChannelSelector sharedAccept = null;
    private Thread sharedThread = null;
    private final Object workSync = new Object() {}; // use brackets/inner class to make lock appear in dumps using class name
    private boolean dedicatedAcceptThread = false;
    private boolean waitToAccept = false;

    protected Map<TCPPort, NBAcceptChannelSelector> endPointToAccept = null;

    /**
     * Constructor.
     * 
     * @param config
     */
    public NBAccept(TCPChannelConfiguration config) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.entry(tc, "NBAccept");
        }

        this.endPointToAccept = new HashMap<TCPPort, NBAcceptChannelSelector>();
        this.dedicatedAcceptThread = config.getAcceptThread();
        this.waitToAccept = config.getWaitToAccept();

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.exit(tc, "NBAccept");
        }
    }

    /**
     * Register an end point with this request processor.
     * This creates a listener socket for the end point and puts it
     * into the selector so that connections are accepted.
     * 
     * Tells the processor to start accepting requests for the
     * specified end point.
     * 
     * @param endPoint
     * @throws IOException
     */
    public void registerPort(TCPPort endPoint) throws IOException {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.entry(tc, "registerPort: " + endPoint.getServerSocket());
        }

        synchronized (this) {

            EndPointActionInfo work = new EndPointActionInfo(REGISTER_ENDPOINT, endPoint, workSync);

            if ((!dedicatedAcceptThread) && (!waitToAccept)) {
                if (sharedAccept == null) {
                    sharedAccept = new NBAcceptChannelSelector(waitToAccept);
                    sharedThread = new Thread(sharedAccept);

                    sharedThread.setName("Shared TCPChannel NonBlocking Accept Thread");
                    // all TCPChannel Thread should be daemon threads
                    sharedThread.setDaemon(true);
                    sharedThread.start();
                }
                synchronized (workSync) {
                    if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
                        Tr.event(this, tc, "Passing register to selector; " + endPoint.getServerSocket());
                    }
                    try {
                        sharedAccept.addWork(work);
                        workSync.wait();
                    } catch (InterruptedException x) {
                        // nothing to do
                    }
                } // end-sync
                endPointToAccept.put(endPoint, sharedAccept);

            } else {
                // Add a new dedicated accept
                NBAcceptChannelSelector dedicatedAccept = new NBAcceptChannelSelector(waitToAccept);
                Thread dedicatedThread = new Thread(dedicatedAccept);

                dedicatedThread.setName("Dedicated TCPChannel NonBlocking Accept Thread:" + endPoint.getListenPort());
                // all TCPChannel Thread should be daemon threads
                dedicatedThread.setDaemon(true);
                dedicatedThread.start();

                // Need a new sync, since the wait will give up control and
                // we don't want the removePort to gain control at this point.
                synchronized (workSync) {
                    if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
                        Tr.event(this, tc, "Passing register to dedicated selector; " + endPoint.getServerSocket());
                    }
                    try {
                        dedicatedAccept.addWork(work);
                        workSync.wait();
                    } catch (InterruptedException x) {
                        // nothing to do
                    }
                } // end-sync
                endPointToAccept.put(endPoint, dedicatedAccept);
            }
        }
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.exit(tc, "registerPort");
        }
    }

    /**
     * Removes an end point from the set of end points that we
     * are accepting connections on. This has the effect of removing
     * the server socket from the selector and closing it.
     * 
     * Tells the processor to stop accepting requests for the
     * specified end point. The listener socket will be closed.
     * 
     * @param endPoint
     */
    public void removePort(TCPPort endPoint) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.entry(tc, "removePort: " + endPoint.getServerSocket());
        }

        synchronized (this) {
            NBAcceptChannelSelector accept = endPointToAccept.get(endPoint);

            if (accept != null) {
                // PK44756 - prevent hang on System.exit by accept selector
                if (3100 <= accept.numExceptions) {
                    if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
                        Tr.event(tc, "Ignoring removePort call on fatal selector/system.exit path");
                    }
                    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
                        Tr.exit(tc, "removePort");
                    }
                    return;
                }
                EndPointActionInfo work = new EndPointActionInfo(REMOVE_ENDPOINT, endPoint, workSync);

                synchronized (workSync) {
                    if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
                        Tr.event(this, tc, "Passing remove to selector; " + endPoint.getServerSocket());
                    }
                    accept.addWork(work);
                    try {
                        workSync.wait();
                    } catch (InterruptedException x) {
                        // nothing to do
                    }
                } // end-sync

                if (accept == sharedAccept && accept.getUsageCount() <= 0) {
                    sharedAccept = null;
                }
            } else {
                IllegalArgumentException iae = new IllegalArgumentException("TCP Port to be removed is not registered.");
                if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
                    Tr.event(tc, "Throwing IllegalArgumentException");
                }
                FFDCFilter.processException(iae, CLASS_NAME + ".removePort", "387", this);
                throw iae;
            }
        } // end-sync-this

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.exit(tc, "removePort");
        }
    }

    final static int REGISTER_ENDPOINT = 1;
    final static int REMOVE_ENDPOINT = 0;

    protected static class EndPointActionInfo {
        protected int action;
        protected TCPPort endPoint;
        Object syncObject = null;

        EndPointActionInfo(int _action, TCPPort _endPoint, Object _syncObject) {
            this.action = _action;
            this.endPoint = _endPoint;
            this.syncObject = _syncObject;
        }
    }

}
