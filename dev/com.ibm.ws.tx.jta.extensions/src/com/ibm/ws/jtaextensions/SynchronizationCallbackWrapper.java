package com.ibm.ws.jtaextensions;

/* ********************************************************************************* */
/* COMPONENT_NAME: WAS.transactions                                                  */
/*                                                                                   */
/*  ORIGINS: 27                                                                      */
/*                                                                                   */
/* IBM Confidential OCO Source Material                                              */
/* 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2002, 2004, 2008 */
/* The source code for this program is not published or otherwise divested           */
/* of its trade secrets, irrespective of what has been deposited with the            */
/* U.S. Copyright Office.                                                            */
/*                                                                                   */
/* %Z% %I% %W% %G% %U% [%H% %T%]                                                     */
/*                                                                                   */
/*  DESCRIPTION:                                                                     */
/*                                                                                   */
/*                                                                                   */
/*  Change History:                                                                  */
/*                                                                                   */
/*  Date      Programmer    Defect    Description                                    */
/*  --------  ----------    ------    -----------                                    */
/*  25-03-04  mdobbie   LIDB3133-23   Created                                        */
/*  23-04-04  mdobbie     199929      Minor code review updates                      */
/*  14-06-04  johawkes      209345    Organise imports                               */
/*  24-05-08  johawkes      522569    Perf trace ports                               */
/* ********************************************************************************* */

import javax.transaction.Status;
import javax.transaction.Synchronization;

import com.ibm.ejs.ras.Tr;
import com.ibm.ejs.ras.TraceComponent;
import com.ibm.tx.TranConstants;
import com.ibm.websphere.jtaextensions.SynchronizationCallback;

//
// This class wrappers a SynchronizationCallback as a javax.transaction.Synchronization type
// allowing it to be registered as a Synchronization with a transaction.
//
// Used by ExtendedJTATransactionImpl to allow SynchronizationCallback registration
// on a per-transaction basis.
//
public final class SynchronizationCallbackWrapper implements Synchronization
{
    private static final TraceComponent tc = Tr.register(SynchronizationCallbackWrapper.class, TranConstants.TRACE_GROUP, TranConstants.NLS_FILE);

    private final SynchronizationCallback _syncCallback;
    private final int _localId;
    private final byte[] _globalId;

    public SynchronizationCallbackWrapper(SynchronizationCallback syncCallback, int localId, byte[] globalId)
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.entry(tc, "SynchronizationCallbackWrapper", new Object[] { syncCallback, localId, globalId });

        _syncCallback = syncCallback;
        _localId = localId;
        _globalId = globalId;

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.exit(tc, "SynchronizationCallbackWrapper");
    }

    @Override
    public void beforeCompletion()
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.entry(tc, "beforeCompletion");

        _syncCallback.beforeCompletion(_localId, _globalId);

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.exit(tc, "beforeCompletion");
    }

    @Override
    public void afterCompletion(int status)
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.entry(tc, "afterCompletion", status);

        _syncCallback.afterCompletion(_localId, _globalId, (status == Status.STATUS_COMMITTED));

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.exit(tc, "afterCompletion");
    }
}
