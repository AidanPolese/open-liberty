package com.ibm.tx.jta.embeddable.impl;

/* ********************************************************************************* */
/* COMPONENT_NAME: WAS.transactions                                                  */
/*                                                                                   */
/* IBM Confidential OCO Source Material                                              */
/* 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2004, 2008 */
/* The source code for this program is not published or otherwise divested           */
/* of its trade secrets, irrespective of what has been deposited with the            */
/* U.S. Copyright Office.                                                            */
/*                                                                                   */
/* @(#) 1.5.1.1 SERV1/ws/code/was.transaction.impl/src/com/ibm/ws/Transaction/wstx/JTAAsyncResourceBase.java, WAS.transactions, WAS855.SERV1, cf061521.02 4/4/08 11:26:00 [6/12/15 06:28:23]                                                     */
/*                                                                                   */
/*  Change History:                                                                  */
/*                                                                                   */
/*  Date      Programmer    Defect       Description                                 */
/*  --------  ----------    ------       -----------                                 */
/*  04/04/23  dmatthew      LI1922       creation                                    */
/*  04/09/28  johawkes      235214       Fix trace group and imports                 */
/*  04/10/18  awilkins      235214.1     Servicability - improve trace               */
/*  05/11/10  johawkes      322622       Decrement semaphore count before force      */
/*  08/04/04  hursdlg       509776       Heuristics support                          */
/* ********************************************************************************* */

import java.io.Serializable;

import javax.transaction.xa.XAException;

import com.ibm.tx.TranConstants;
import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.Transaction.JTA.JTAResource;
import com.ibm.ws.Transaction.JTA.ResourceWrapper;

public abstract class JTAAsyncResourceBase extends ResourceWrapper implements JTAResource
{
    private static final TraceComponent tc = Tr.register(JTAAsyncResourceBase.class, TranConstants.TRACE_GROUP, TranConstants.NLS_FILE);

    public final static int ASYNC_STATE_ACTIVE = 0;
    public final static int ASYNC_STATE_READONLY = 1;
    public final static int ASYNC_STATE_PREPARED = 2;
    public final static int ASYNC_STATE_ABORTED = 3;
    public final static int ASYNC_STATE_COMMITTED = 4;
    public final static int ASYNC_STATE_HEURROLLBACK = 5;
    public final static int ASYNC_STATE_HEURCOMMIT = 6;
    public final static int ASYNC_STATE_HEURMIXED = 7;
    public final static int ASYNC_STATE_HEURHAZARD = 8;
    public final static int ASYNC_STATE_LAST = 8;
//   
//    protected Semaphore _semaphore;
//
    protected int _asyncState = ASYNC_STATE_ACTIVE;
    protected boolean _stateProcessed;

    final static boolean[][] stateSuperceded = {
                                                /* from to actve rdonly pd rd cd hr hc hm hh */
/* active */                                    { false, true, true, true, false, false, false, true, true },
                                                /* readonly */{ false, false, false, false, false, false, false, false, false },
                                                /* prepared */{ false, false, false, true, true, true, true, true, true },
                                                /* aborted */{ false, false, false, false, false, false, false, false, false },
                                                /* committed */{ false, false, false, false, false, false, false, false, false },
                                                /* heurrollback */{ false, false, false, false, false, false, false, false, false },
                                                /* heurcommit */{ false, false, false, false, false, false, false, false, false },
                                                /* heurmixed */{ false, false, false, false, false, false, false, false, false },
                                                /* heurhazard */{ false, false, false, false, false, false, false, false, false } };

//    public void setResponse(int newState)
//    {
//        if (tc.isEntryEnabled()) Tr.entry(tc, "setResponse", new Object[]{new Integer(newState), this});
//
//        if((newState< ASYNC_STATE_ACTIVE) || (newState > ASYNC_STATE_LAST))
//        {
//            if (tc.isEventEnabled()) Tr.event(tc, "invalid state:"+newState);
//        }
//        else
//            {
//            // Synchronize on Semaphore to avoid possibility of sending messages to this resource for
//            // a different 2PC phase whilst processing this response.
//            synchronized(_semaphore)
//            {
//            	if (tc.isDebugEnabled()) Tr.debug(tc, "_asyncState = " + _asyncState);
//            	
//                if(stateSuperceded[_asyncState][newState])
//                {
//                    _asyncState = newState;
//                    _stateProcessed = false;
//                    // need to change so that decrement only occurs if response is for correct phase.
//                    _semaphore.decrement();
//                }
//            }
//        }
//
//        if ( tc.isEntryEnabled() ) Tr.exit(tc, "setResponse");
//    }
//
//    public void setImmediateResponse(int newState)
//    {
//        if (tc.isEntryEnabled()) Tr.entry(tc, "setImmediateResponse", new Object[]{new Integer(newState), this});
//
//        if((newState< ASYNC_STATE_ACTIVE) || (newState > ASYNC_STATE_LAST))
//        {
//            if ( tc.isEventEnabled() ) Tr.event(tc, "invalid state:"+newState);
//        }
//        else
//        {
//            // Synchronize on Semaphore to avoid possibility of sending messages to this resource for
//            // a different 2PC phase whilst processing this response.
//            synchronized(_semaphore)
//            {
//            	if (tc.isDebugEnabled()) Tr.debug(tc, "_asyncState = " + _asyncState);
//            	
//                if( stateSuperceded[_asyncState][newState] )
//                {
//                    _asyncState = newState;
//                    _stateProcessed = false;
//                    _semaphore.decrement();
//                    _semaphore.force();
//                }
//            }
//        }
//
//        if ( tc.isEntryEnabled() ) Tr.exit(tc, "setImmediateResponse");
//    }
//
//    public void setSemaphore(Semaphore semaphore)
//    {
//        if (tc.isEntryEnabled()) Tr.entry(tc, "setSemaphore", new Object[]{semaphore, this});
//
//        _semaphore = semaphore;
//
//        if (tc.isEntryEnabled()) Tr.exit(tc, "setSemaphore");
//    }
//
//    public Semaphore getSemaphore()
//    {
//        if (tc.isEntryEnabled()) Tr.entry(tc, "getSemaphore", this);
//        if (tc.isEntryEnabled()) Tr.exit(tc, "getSemaphore", _semaphore);
//        return _semaphore;
//    }

    abstract public void sendAsyncPrepare() throws XAException;

    abstract public void sendAsyncCommit() throws XAException;

    abstract public void sendAsyncRollback() throws XAException;

    abstract public Serializable getKey();

}
