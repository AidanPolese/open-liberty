package com.ibm.ws.tx.jta.embeddable;

//
// COMPONENT_NAME: WAS.transactions
//
// ORIGINS: 27
//
// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2007, 2009
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// %Z% %I% %W% %G% %U% [%H% %T%]
//
// DESCRIPTION:
//
// Change History:
//
// Date      Programmer    Defect   Description
// --------  ----------    ------   -----------
// 07/08/01  johawkes      451213.1 Creation
// 07/08/30  johawkes      463313   Override TransactionImpl creation in WAS
// 07/09/03  awilkins      464243   Fix ClassCastException in suspend
// 08/02/15  kaczyns       512190   Handle SystemException on begin
// 09/06/15  mallam        596067   package moves
// 11/11/24  johawkes      723423   Repackaging tx.embeddable

import javax.transaction.Transaction;
import javax.transaction.xa.Xid;

import com.ibm.tx.TranConstants;
import com.ibm.tx.jta.embeddable.EmbeddableTransactionManagerFactory;
import com.ibm.tx.jta.embeddable.impl.EmbeddableTransactionImpl;
import com.ibm.tx.jta.impl.JCARecoveryData;
import com.ibm.tx.jta.impl.TranManagerSet;
import com.ibm.tx.ltc.impl.LocalTranCurrentSet;
import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.LocalTransaction.LocalTransactionCoordinator;
import com.ibm.ws.Transaction.UOWCurrent;
import com.ibm.ws.ffdc.FFDCFilter;

public final class EmbeddableJCATranWrapperImpl extends com.ibm.tx.jta.impl.JCATranWrapperImpl
{
    private static final TraceComponent tc = Tr.register(EmbeddableJCATranWrapperImpl.class, TranConstants.TRACE_GROUP, TranConstants.NLS_FILE);

    /**
     * 
     * Create a new transaction wrapper and transaction
     * 
     * @param timeout
     * @param xid
     * @param jcard
     */
    public EmbeddableJCATranWrapperImpl(int timeout, Xid xid, JCARecoveryData jcard)
    {
        _tranManager = (TranManagerSet) EmbeddableTransactionManagerFactory.getTransactionManager();

        suspend(); // suspend and save any LTC before we create the global txn

        _txn = new EmbeddableTransactionImpl(timeout, xid, jcard);

        _prepared = false;

        _associated = true;
    }

    /**
     * Suspend any transaction context off the thread - save the LTC in the wrapper
     */
    @Override
    public void suspend()
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.entry(tc, "suspend");

        _suspendedUOWType = ((UOWCurrent) _tranManager).getUOWType();

        switch (_suspendedUOWType)
        {
            case UOWCurrent.UOW_LOCAL:

                if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
                    Tr.event(tc, "suspending (local)");
                }

                _suspendedUOW = LocalTranCurrentSet.instance().suspend();

                break;

            case UOWCurrent.UOW_GLOBAL:

                if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
                    Tr.event(tc, "suspending (global)");
                }

                _suspendedUOW = _tranManager.suspend();

                break;

            case UOWCurrent.UOW_NONE:

                _suspendedUOW = null;
                break;

            default:
                break;
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.exit(tc, "suspend", _suspendedUOWType);
    }

    /**
     * 
     */
    @Override
    public void resume()
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.entry(tc, "resume");

        switch (_suspendedUOWType)
        {
            case UOWCurrent.UOW_LOCAL:
                LocalTranCurrentSet.instance().resume((LocalTransactionCoordinator) _suspendedUOW);
                break;

            case UOWCurrent.UOW_GLOBAL:
                try
                {
                    _tranManager.resume((Transaction) _suspendedUOW);
                } catch (Exception e) {
                    FFDCFilter.processException(e, "com.ibm.ws.Transaction.JTA.EmbeddableJCATranWrapperImpl.resume", "135", this);
                    if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                        Tr.debug(tc, "Failed to resume", new Object[] { _suspendedUOW, e });
                }
                break;

            default:
                break;
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.exit(tc, "resume");
    }
}