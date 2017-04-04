package com.ibm.ws.jtaextensions;

/* ***************************************************************************************************** */
/* COMPONENT_NAME: WAS.transactions                                                                      */
/*                                                                                                       */
/*  ORIGINS: 27                                                                                          */
/*                                                                                                       */
/* IBM Confidential OCO Source Material                                                                  */
/* 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2002, 2009 */
/* The source code for this program is not published or otherwise divested                               */
/* of its trade secrets, irrespective of what has been deposited with the                                */
/* U.S. Copyright Office.                                                                                */
/*                                                                                                       */
/* @(#) 1.31 SERV1/ws/code/was.transaction.impl/src/com/ibm/ws/jtaextensions/ExtendedJTATransactionImpl.java, WAS.transactions, WASX.SERV1 6/15/0
 /*                                                                                                       */
/*  DESCRIPTION:                                                                                         */
/*                                                                                                       */
/*                                                                                                       */
/*  Change History:                                                                                      */
/*                                                                                                       */
/*  Date      Programmer    Defect    Description                                                        */
/*  --------  ----------    ------    -----------                                                        */
/*  02-02-14  awilkins      LIDB850   Creation                                                           */
/*  27-02-02  beavenj       LIDB1220.151.1 Code instrumented for FFDC work                               */
/*  15-04-02  mallam        125321    null pointer exception                                             */
/*  09/09/02   gareth       ------    Move to JTA implementation                                         */
/*  02-11-21  awilkins        1507    JTS -> JTA. Thread local restructuring                             */
/*  02-12-17  awilkins    LIDB1673.12 Reworking for efficiency with JTA2                                 */
/*  17/01/03   awilkins       1673.9  Tidy-up exception handling                                         */
/*  21/02/03   gareth     LIDB1673.19 Make any unextended code final                                     */
/*  25/04/03   ihodges    16264  5    getLocalId should return 0 when no Tx                              */
/*  06/01/04   hursdlg    LIDB2775    zOS/distributed merge                                              */
/*  07/01/04  johawkes    LIDB2110    RA Uninstall                                                       */
/*  08/03/04  hursdlg     193574      Check for null sync on registration                                */
/*  10/03/04  mallam      193798      Wrapper afterCompletion's  in try/catch                            */
/*  25-03-04  mdobbie     LIDB3133-23 Added registerCallbackForCurrentTran mth                           */
/*  02-04-04  mdobbie     197035      Null SyncCallback param check added                                */
/*  23-04-04  mdobbie     199929      Minor code review updates                                          */
/*  27-04-04  johawkes    196302      Implement sync levels                                              */
/*  24/05/04  mallam      204225      exceptions on beforeCompletion                                     */
/*  12/07/05  hursdlg     281425      AfterCompletion status of unknown                                  */
/*  06/01/06  johawkes    306998.12   Use TraceComponent.isAnyTracingEnabled()                           */
/*  19/04/07  mallam      432276      Migrate PK41846 to WASX                                            */
/*  06/06/07  johawkes    443467      Repackaging                                                        */
/*  17/06/07  johawkes    444613      Repackaging                                                        */
/*  26/06/07  johawkes    446894      Fix JTM shutdown delay                                             */
/*  24/05/08  johawkes    522569      Perf trace                                                         */
/*  15/06/09  mallam      596067      package moves                                                      */
/* ***************************************************************************************************** */

import java.util.ArrayList;

import javax.transaction.RollbackException;
import javax.transaction.Status;

import com.ibm.ejs.ras.Tr;
import com.ibm.ejs.ras.TraceComponent;
import com.ibm.tx.TranConstants;
import com.ibm.tx.jta.TransactionManagerFactory;
import com.ibm.tx.jta.impl.LocalTIDTable;
import com.ibm.tx.jta.impl.RegisteredSyncs;
import com.ibm.tx.jta.impl.TranManagerSet;
import com.ibm.tx.jta.impl.TransactionImpl;
import com.ibm.websphere.jtaextensions.CallbackNotRegisteredException;
import com.ibm.websphere.jtaextensions.ExtendedJTATransaction;
import com.ibm.websphere.jtaextensions.NotSupportedException;
import com.ibm.websphere.jtaextensions.SynchronizationCallback;
import com.ibm.ws.ffdc.FFDCFilter;

public final class ExtendedJTATransactionImpl implements ExtendedJTATransaction {
    // Contains snapshots of the ArrayLists of registered syncs.
    // Snapshots are taken after every succesful register/unregister
    // Transactions hold an index into this list and it is these snapshots that are
    // traversed by before/afterCompletion.

    // The rule is that a register or unregister (for server syncs) is effective
    // for subsequently started transactions. Hence the snapshots.

    private static final ArrayList<ArrayList<SynchronizationCallback>> _syncLevels = new ArrayList<ArrayList<SynchronizationCallback>>();
    private static int _syncLevel = -1;

    private static final TraceComponent tc = Tr.register(ExtendedJTATransactionImpl.class, TranConstants.TRACE_GROUP, TranConstants.NLS_FILE);

    /**
     * Returns the <code>CosTransactions::PropagationContext::TransIdentity::tid</code> for
     * the transaction currently associated with the calling thread.
     * 
     * @return the current transaction <code>tid</code> in the form of a byte array.
     *         If there is no active transaction currently associated with the thread,
     *         return null;
     */
    @Override
    public byte[] getGlobalId() {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.entry(tc, "getGlobalId");

        byte[] globalId = null;

        final TransactionImpl tran = ((TranManagerSet) TransactionManagerFactory.getTransactionManager()).getTransactionImpl();

        if (tran != null) {
            globalId = tran.getTID();
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.exit(tc, "getGlobalId", globalId);
        return globalId;
    }

    /**
     * Returns a process-unique identifier for the transaction currently associated
     * with the calling thread. The local-id is valid only within the local process.
     * The local-id is recovered as part of the state of a recovered transaction.
     * 
     * @return an integer that uniquely identifies the current transaction within
     *         the calling process. If there is no active transaction currently associated with the thread, returns 0;
     */
    @Override
    public int getLocalId() {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.entry(tc, "getLocalId");

        int localId = 0;

        final TransactionImpl tran = ((TranManagerSet) TransactionManagerFactory.getTransactionManager()).getTransactionImpl();

        if (tran != null) {
            localId = (int) tran.getLocalTID();
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.exit(tc, "getLocalId", localId);
        return localId;
    }

    /**
     * Register a {@link com.ibm.websphere.jtaextensions.SynchronizationCallback SynchronizationCallback} object with the transaction manager.
     * The registered <code>sync</code> receives notification of the completion
     * of each transaction mediated by the transaction manager in the local JVM.
     * 
     * @param sync An object implementing the {@link com.ibm.websphere.jtaextensions.SynchronizationCallback SynchronizationCallback} interface.
     *            the calling process. If there is no active transaction currently associated with the thread, returns 0;
     * 
     * @exception NotSupportedException Thrown if this method is called from an environment
     *                or at a time when the function is not available.
     * 
     */
    @Override
    public synchronized void registerSynchronizationCallback(SynchronizationCallback sync) throws NotSupportedException {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.entry(tc, "registerSynchronizationCallback", sync);

        // Disallow registration of null syncs
        if (sync == null) {
            final NullPointerException npe = new NullPointerException();
            if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
                Tr.exit(tc, "registerSynchronizationCallback", npe);
            throw npe;
        }

        // If this is the first registration we just add it on a new level and we're done
        if (_syncLevel < 0) {
            final ArrayList<SynchronizationCallback> newSyncList = new ArrayList<SynchronizationCallback>();
            newSyncList.add(sync);
            setLevel(newSyncList);

            if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
                Tr.exit(tc, "registerSynchronizationCallback", _syncLevel);
            return;
        }

        // We clone a new sync level from the last one and add this one to it

        // Get the current list
        final ArrayList currentSyncList = _syncLevels.get(_syncLevel);

        // Current list should not be null (unless we have a logic error)
        if (currentSyncList == null) {
            // We can recover though
            final ArrayList<SynchronizationCallback> newSyncList = new ArrayList<SynchronizationCallback>();
            newSyncList.add(sync);
            setLevel(newSyncList);

            if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
                Tr.exit(tc, "registerSynchronizationCallback", _syncLevel);
            return;
        }

        // Clone it with space for the new sync
        final ArrayList<SynchronizationCallback> newSyncList = new ArrayList<SynchronizationCallback>(currentSyncList);

        // Add the new sync on the end of the new list
        newSyncList.add(sync);

        // Make the new list current
        setLevel(newSyncList);

        // Garbage collect old levels which are no longer referenced
        garbageCollectUnusedLevels();

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.exit(tc, "registerSynchronizationCallback", _syncLevel);
    }

    /**
     * Register a SynchronizationCallback {@link com.ibm.websphere.jtaextensions.SynchronizationCallback SynchronizationCallback} object for the current transaction.
     * The registered <code>sync</code> receives notification of the completion
     * of the transaction in which it is registered.
     * 
     * @param sync An object implementing the {@link com.ibm.websphere.jtaextensions.SynchronizationCallback SynchronizationCallback} interface.
     * 
     * @exception NotSupportedException Thrown if this method is called from an environment
     *                or at a time when the function is not available.
     * 
     */
    @Override
    public synchronized void registerSynchronizationCallbackForCurrentTran(SynchronizationCallback sync) throws NotSupportedException {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.entry(tc, "registerSynchronizationCallbackForCurrentTran", sync);

        if (sync == null) {
            final String msg = "SynchronizationCallback is null";
            final NullPointerException npe = new NullPointerException(msg);
            if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
                Tr.exit(tc, "registerSynchronizationCallbackForCurrentTran", npe);
            throw npe;
        }

        final TransactionImpl tran = ((TranManagerSet) TransactionManagerFactory.getTransactionManager()).getTransactionImpl();

        if (tran == null) {
            final NotSupportedException nse = new NotSupportedException();
            if (tc.isEventEnabled()) {
                Tr.event(tc, "No current transaction");
            }
            if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
                Tr.exit(tc, "registerSynchronizationCallbackForCurrentTran", nse);
            throw nse;
        }

        // Create Synchronization wrapper for the callback object
        SynchronizationCallbackWrapper syncCallbackWrapper = new SynchronizationCallbackWrapper(sync, (int) tran.getLocalTID(), tran.getTID());

        try {
            tran.registerSynchronization(syncCallbackWrapper, RegisteredSyncs.SYNC_TIER_OUTER); //d432276
        } catch (RollbackException re) {
            FFDCFilter.processException(re, "com.ibm.ws.jtaextensions.ExtendedJTATransactionImpl.registerSynchronizationCallbackForCurrentTran", "325", this);
            final NotSupportedException nse = new NotSupportedException();
            if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
                Tr.exit(tc, "registerSynchronizationCallbackForCurrentTran", nse);
            throw nse;
        } catch (IllegalStateException ise) {
            FFDCFilter.processException(ise, "com.ibm.ws.jtaextensions.ExtendedJTATransactionImpl.registerSynchronizationCallbackForCurrentTran", "333", this);
            final NotSupportedException nse = new NotSupportedException();
            if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
                Tr.exit(tc, "registerSynchronizationCallbackForCurrentTran", nse);
            throw nse;
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.exit(tc, "registerSynchronizationCallbackForCurrentTran");
    }

    /**
     * Unregister a previously registered {@link com.ibm.websphere.jtaextensions.SynchronizationCallback SynchronizationCallback} object, <code>sync</code>. The object so
     * unregistered will receive no further callbacks
     * from transactions that subsequently complete.
     * 
     * 
     * @param sync A previously registered {@link com.ibm.websphere.jtaextensions.SynchronizationCallback SynchronizationCallback} object.
     * 
     * @exception CallbackNotRegisteredException Thrown if the specific <code>sync</code>
     *                is not registered with the transaction manager.
     * 
     */
    @Override
    public synchronized void unRegisterSynchronizationCallback(SynchronizationCallback sync) throws CallbackNotRegisteredException {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.entry(tc, "unRegisterSynchronizationCallback", sync);

        // We don't allow registration of null syncs
        if (sync == null || _syncLevel < 0) {
            final CallbackNotRegisteredException cnre = new CallbackNotRegisteredException();
            if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
                Tr.exit(tc, "unRegisterSynchronizationCallback", cnre);
            throw cnre;
        }

        // Get the current list
        final ArrayList currentSyncList = _syncLevels.get(_syncLevel);

        // If the current list is empty, this sync can't be registered
        if (currentSyncList == null || !currentSyncList.contains(sync)) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
                Tr.exit(tc, "unRegisterSynchronizationCallback", "Sync not registered");
            throw new CallbackNotRegisteredException();
        }

        // Clone a new level from the current and remove the last
        // occurence of the given sync from it
        final ArrayList newSyncList = new ArrayList(currentSyncList);

        final int pos = newSyncList.lastIndexOf(sync);
        newSyncList.remove(pos);

        // Make the new list current
        setLevel(newSyncList);

        // Garbage collect old levels which are no longer referenced
        garbageCollectUnusedLevels();

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.exit(tc, "unRegisterSynchronizationCallback");
    }

    /**
     * Notify all the registered syncs of the begin
     * of the completion phase of the given transaction
     */
    public static void beforeCompletion(TransactionImpl tran, int syncLevel) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.entry(tc, "beforeCompletion", new Object[] { tran, syncLevel });

        if (syncLevel >= 0) {
            final ArrayList syncs = _syncLevels.get(syncLevel);

            if (syncs != null) {
                final int localID = (int) tran.getLocalTID();
                final byte[] globalID = tran.getTID();
                final int numSyncs = syncs.size();

                for (int s = 0; s < numSyncs; s++) {
                    ((SynchronizationCallback) syncs.get(s)).beforeCompletion(localID, globalID);
                    // exception here means rollback
                }
            }
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.exit(tc, "beforeCompletion");
    }

    /**
     * Notify all the registered syncs of the end
     * of the completion phase of the given
     * transaction. The completion resulted in the
     * transaction having the given status.
     */
    public static void afterCompletion(TransactionImpl tran, int status, int syncLevel) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.entry(tc, "afterCompletion", new Object[] { tran, status, syncLevel });

        if (syncLevel >= 0) {
            final ArrayList syncs = _syncLevels.get(syncLevel);

            if (syncs != null) {
                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                    Tr.debug(tc, "syncs.length=" + syncs.size());

                final int localID = (int) tran.getLocalTID();
                final byte[] globalID = tran.getTID();
                // d281425 - status maybe committed, unknown or rolledback
                final boolean committed = !(status == Status.STATUS_ROLLEDBACK); // @281425C
                final int numSyncs = syncs.size();

                for (int s = 0; s < numSyncs; s++) {
                    try {
                        ((SynchronizationCallback) syncs.get(s)).afterCompletion(localID, globalID, committed);
                    } catch (Throwable t) {
                        // No FFDC needed
                        Tr.error(tc, "WTRN0074_SYNCHRONIZATION_EXCEPTION", new Object[] { "afterCompletion", syncs.get(s), t });
                        // absorb and allow other afterCompletions to run.
                    }
                }
            } else if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                Tr.debug(tc, "syncs is null");
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.exit(tc, "afterCompletion");
    }

    // Return true if there are callbacks
    // which can still be called otherwise false.
    public static boolean callbacksRegistered() {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.entry(tc, "callbacksRegistered");

        if (_syncLevel < 0) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
                Tr.exit(tc, "callbacksRegistered", Boolean.FALSE);
            return false;
        }

        final int maxLevels = _syncLevels.size();
        for (int level = 0; level < maxLevels; level++) {
            final ArrayList syncs = _syncLevels.get(_syncLevel);

            if (syncs != null && !syncs.isEmpty()) {
                if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
                    Tr.exit(tc, "callbacksRegistered", Boolean.TRUE);
                return true;
            }
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.exit(tc, "callbacksRegistered", Boolean.FALSE);
        return false;
    }

    /**
     * @return
     */
    private static int nextLevel() {
        final int numLevels = _syncLevels.size();

        // The idea is to fill in the gaps in the ArrayList
        // so we don't leak memory
        for (int i = 0; i < numLevels; i++) {
            if (_syncLevels.get(i) == null) {
                return i;
            }
        }

        return numLevels;
    }

    /**
     * @return index of current sync level
     */
    public static int getSyncLevel() {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            Tr.debug(tc, "Sync level: " + _syncLevel);
        return _syncLevel;
    }

    /**
     * Null out levels that will never be referenced again.
     * Null entries are candidates to be the next current level.
     */
    private static void garbageCollectUnusedLevels() {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.entry(tc, "garbageCollectUnusedLevels");

        final int numLevels = _syncLevels.size();
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            Tr.debug(tc, "Levels: " + numLevels);

        // No need to do anything if we only have one level
        if (numLevels < 2) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
                Tr.exit(tc, "garbageCollectUnusedLevels", "Doing nothing");
            return;
        }

        final int[] counts = new int[numLevels];

        // Check through all transactions to see which levels are still in use
        final TransactionImpl[] txns = LocalTIDTable.getAllTransactions();

        // Record the levels with transactions referring to them

        // Record the levels with transactions referring to them
        for (int i = txns.length; --i >= 0;) {
            // TransactionImpl has a dependency on CORBA which we definitely don't want
            // Skip this step to avoid a classcastexception
            // TODO can we work around the dependency?
            int level = 0;
            // final int level = ((com.ibm.ws.tx.jta.TransactionImpl) txns[i]).getExtJTASyncLevel();
            if (tc.isDebugEnabled()) {
                Tr.debug(tc, "Not generating any counts for garbageCollectUnusedLevels()");
            }

            if (level >= 0 && level < numLevels) {
                counts[level]++;
            }
        }

        // Now null out the levels with no references
        for (int i = counts.length; --i >= 0;) {
            // We never want to null out the current level
            if (i != _syncLevel) {
                if (counts[i] == 0) {
                    _syncLevels.set(i, null);
                }
            }
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.exit(tc, "garbageCollectUnusedLevels", _syncLevels.size());
    }

    /**
     * Utility method to make a list into the new current level
     * 
     * @param level
     */
    private static void setLevel(ArrayList level) {
        _syncLevel = nextLevel();

        if (_syncLevel == _syncLevels.size()) {
            _syncLevels.add(level);
        } else {
            _syncLevels.set(_syncLevel, level);
        }
    }
}
