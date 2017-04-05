/* ************************************************************************** */
/* COMPONENT_NAME: WAS.transactions                                           */
/*                                                                            */
/*  ORIGINS: 27                                                               */
/*                                                                            */
/* IBM Confidential OCO Source Material                                       */
/* 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2004          */
/* The source code for this program is not published or otherwise divested    */
/* of its trade secrets, irrespective of what has been deposited with the     */
/* U.S. Copyright Office.                                                     */
/*                                                                            */
/* %Z% %I% %W% %G% %U% [%H% %T%]                                              */
/*                                                                            */
/*  DESCRIPTION:                                                              */
/*                                                                            */
/*  Change History:                                                           */
/*                                                                            */
/*  Date      Programmer    Defect      Description                           */
/*  --------  ----------    ------      -----------                           */
/*  03/15/03  sykesm        MD19638     Initial creation                      */
/*  05/28/04  ralder        MD20096     Add try-catch around recovery events  */
/* ************************************************************************** */
package com.ibm.ws.recoverylog.spi;

import java.util.ArrayList;
import java.util.List;

import com.ibm.tx.util.logging.Tr;
import com.ibm.tx.util.logging.TraceComponent;

public class RegisteredRecoveryEventListeners implements RecoveryEventListener
{
    private final static TraceComponent tc = Tr.register(
            RegisteredRecoveryEventListeners.class,
            TraceConstants.TRACE_GROUP, null);

    private final static RegisteredRecoveryEventListeners _instance =
        new RegisteredRecoveryEventListeners();

    private List _listeners = null;

    public static RegisteredRecoveryEventListeners instance()
    {
        return _instance;
    }

    private RegisteredRecoveryEventListeners()
    {
        if (tc.isEntryEnabled()) Tr.entry(tc, "<init>");
        if (tc.isEntryEnabled()) Tr.exit(tc, "<init>");
    }

    public synchronized void add(RecoveryEventListener rel)
    {
        if (tc.isEntryEnabled()) Tr.entry(tc, "add", rel);

        if (_listeners == null) _listeners = new ArrayList();

        _listeners.add(rel);

        if (tc.isEntryEnabled()) Tr.exit(tc, "add");
    }

    public void failureOccurred(FailureScope fs)
    {
        if (_listeners == null) return;

        if (tc.isEntryEnabled()) Tr.entry(tc, "failureOccurred", fs);

        for (int i = 0; i < _listeners.size(); i++)
        {
            RecoveryEventListener rel = (RecoveryEventListener) _listeners.get(i);

            if (tc.isDebugEnabled()) Tr.debug(tc, "Notifying " + rel);

            try
            {
                rel.failureOccurred(fs);
            }
            catch (Throwable t) 
            {
                if (tc.isEventEnabled()) Tr.event(tc, "Exception notifying " + rel, t);
            }
        }

        if (tc.isEntryEnabled()) Tr.exit(tc, "failureOccurred");
    }

    public void clientRecoveryInitiated(FailureScope fs, int clientId)
    {
        if (_listeners == null) return;

        if (tc.isEntryEnabled())
            Tr.entry(tc, "clientRecoveryInitiated", new Object[] {
                fs, 
                new Integer(clientId) });

        for (int i = 0; i < _listeners.size(); i++)
        {
            RecoveryEventListener rel = (RecoveryEventListener) _listeners.get(i);

            if (tc.isDebugEnabled()) Tr.debug(tc, "Notifying " + rel);

            try
            {
                rel.clientRecoveryInitiated(fs, clientId);
            }
            catch (Throwable t) 
            {
                if (tc.isEventEnabled()) Tr.event(tc, "Exception notifying " + rel, t);
            }
        }

        if (tc.isEntryEnabled()) Tr.exit(tc, "clientRecoveryInitiated");
    }

    public void clientRecoveryComplete(FailureScope fs, int clientId)
    {
        if (_listeners == null) return;

        if (tc.isEntryEnabled())
            Tr.entry(tc, "clientRecoveryComplete", new Object[] {
                fs, 
                new Integer(clientId) });

        for (int i = 0; i < _listeners.size(); i++)
        {
            RecoveryEventListener rel = (RecoveryEventListener) _listeners.get(i);

            if (tc.isDebugEnabled()) Tr.debug(tc, "Notifying " + rel);

            try
            {
                rel.clientRecoveryComplete(fs, clientId);
            }
            catch (Throwable t) 
            {
                if (tc.isEventEnabled()) Tr.event(tc, "Exception notifying " + rel, t);
            }
        }

        if (tc.isEntryEnabled()) Tr.exit(tc, "clientRecoveryComplete");
    }

    public void recoveryComplete(FailureScope fs)
    {
        if (_listeners == null) return;

        if (tc.isEntryEnabled()) Tr.entry(tc, "recoveryComplete", fs);

        for (int i = 0; i < _listeners.size(); i++)
        {
            RecoveryEventListener rel = (RecoveryEventListener) _listeners.get(i);

            if (tc.isDebugEnabled()) Tr.debug(tc, "Notifying " + rel);

            try
            {
                rel.recoveryComplete(fs);
            }
            catch (Throwable t) 
            {
                if (tc.isEventEnabled()) Tr.event(tc, "Exception notifying " + rel, t);
            }
        }

        if (tc.isEntryEnabled()) Tr.exit(tc, "recoveryComplete");
    }
}
