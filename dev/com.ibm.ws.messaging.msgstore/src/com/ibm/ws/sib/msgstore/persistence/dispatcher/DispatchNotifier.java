package com.ibm.ws.sib.msgstore.persistence.dispatcher;
/*
 * 
 * 
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * Copyright IBM Corp. 2012
 * 
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 * 
 *
 * Change activity:
 *
 * Reason           Date     Origin  Description
 * --------------- -------- -------- ------------------------------------------
 * 184390.1.3      03/03/03 schofiel Creation
 * 306998.20       09/01/06 gareth   Add new guard condition to trace statements
 * 345250          15/02/06 gareth   Improve serviceability for dispatcher problems
 * SIB0112d.ms.2   28/06/07 gareth   MemMgmt: SpillDispatcher improvements - datastore
 * ============================================================================
 */

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.msgstore.MessageStoreConstants;
import com.ibm.ws.sib.msgstore.PersistenceException;
import com.ibm.ws.sib.utils.ras.SibTr;

/**
 * Instances of this class are used to notify threads dispatching work that
 * dispatched work has all been accepted.
 */
public final class DispatchNotifier
{
    private static TraceComponent tc = SibTr.register(DispatchNotifier.class,
                                                      MessageStoreConstants.MSG_GROUP,
                                                      MessageStoreConstants.MSG_BUNDLE);

    // Number of requests associated with this dispatch request
    private int _numRequests;

    // True if notification was signalled
    private boolean _notified;

    // True if the dispatch can be rejected
    private boolean _isRejectable;

    // True if dispatch could not be honoured in full (could have been partially accepted)
    private boolean _dispatchRejected;

    public DispatchNotifier(int numRequests, boolean isRejectable)
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
        {
            SibTr.entry(this, tc, "<init>", new Object[] {Integer.valueOf(numRequests), Boolean.valueOf(isRejectable)});
            SibTr.exit(this, tc, "<init>");
        }

        _numRequests = numRequests;
        _isRejectable = isRejectable;
        _notified = false;
    }

    public synchronized void notifyDispatch()
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "notifyDispatch");

        if (_numRequests > 0)
        {
            _numRequests--;
            if (_numRequests == 0)
            {
                _notified = true;
                this.notify();
            }
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "notifyDispatch");
    }

    public synchronized void forceNotify()
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "forceNotify");

        if (!_notified)
        {
            _notified = true;
            this.notify();
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "forceNotify");
    }

    public boolean isRejectable()
    {
        return _isRejectable;    
    }

    public synchronized void notifyRejected()
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "notifyRejected");

        if (_isRejectable)
        {
            _dispatchRejected = true;
            this.forceNotify();
        }
        else
        {
            if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) SibTr.event(this, tc, "This dispatch is not rejectable!");
            if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "notifyRejected");
            throw new IllegalStateException("This dispatch is not rejectable!");
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "notifyRejected");
    }

    public synchronized void waitForDispatch() throws PersistenceException
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "waitForDispatch");

        while (!_notified)
        {
            try
            {
                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(this, tc, "Waiting for dispatch");

                this.wait();

                if (_dispatchRejected)
                {
                    // Defect 345250
                    // The dispatcher is not currently accepting new work as it has
                    // hit a problem. Details of the problem should have been output
                    // to the logs by the dispatcher at the point where it originated.
                    PersistenceException pe = new PersistenceException("DISPATCHER_CANNOT_ACCEPT_WORK_SIMS1500");
                    if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) SibTr.event(this, tc, "The dispatcher cannot accept work.", pe);
                    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "waitForDispatch");
                    throw pe;
                }
            }
            catch (InterruptedException e)
            {
                //No FFDC Code Needed.
            }
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "waitForDispatch");
    }
}
