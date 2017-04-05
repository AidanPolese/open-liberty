package com.ibm.tx.jta.embeddable.impl;

/* ***************************************************************************************************** */
/* COMPONENT_NAME: WAS.transactions                                                                      */
/*                                                                                                       */
/*  ORIGINS: 27                                                                                          */
/*                                                                                                       */
/* IBM Confidential OCO Source Material                                                                  */
/* 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2009,2013 */
/* The source code for this program is not published or otherwise divested                               */
/* of its trade secrets, irrespective of what has been deposited with the                                */
/* U.S. Copyright Office.                                                                                */
/*                                                                                                       */
/* %Z% %I% %W% %G% %U% [%H% %T%]                                                                         */
/*                                                                                                       */
/*  DESCRIPTION:                                                                                         */
/*                                                                                                       */
/*  Change History:                                                                                      */
/*                                                                                                       */
/*  Date      Developer  Defect         Description                                                      */
/*  --------  ---------  ------         -----------                                                      */
/*  09-11-03  johawkes   F743-305.1     Creation                                                         */
/*  11-11-24  johawkes   723423         Repackaging                                                      */
/*  13-02-05  johawkes   RTC92698       Get tx timeout from runtime metadata                             */
/* ***************************************************************************************************** */

import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import javax.transaction.TransactionRolledbackException;

import com.ibm.ejs.ras.Tr;
import com.ibm.ejs.ras.TraceComponent;
import com.ibm.tx.TranConstants;
import com.ibm.tx.config.ConfigurationProviderManager;
import com.ibm.tx.jta.impl.TranManagerImpl;
import com.ibm.ws.Transaction.UOWCoordinator;
import com.ibm.ws.ffdc.FFDCFilter;
import com.ibm.wsspi.tx.UOWEventListener;

public class EmbeddableTranManagerImpl extends TranManagerImpl
{
    private static final TraceComponent tc = Tr.register(EmbeddableTranManagerImpl.class, TranConstants.TRACE_GROUP, TranConstants.NLS_FILE);

    @Override
    public void begin() throws NotSupportedException, SystemException
    {
        final boolean traceOn = TraceComponent.isAnyTracingEnabled();

        if (traceOn && tc.isEntryEnabled())
            Tr.entry(tc, "begin (SPI)");

        if (tx != null)
        {
            if (tx.getTxType() != UOWCoordinator.TXTYPE_NONINTEROP_GLOBAL)
            {
                Tr.error(tc, "WTRN0017_UNABLE_TO_BEGIN_NESTED_TRANSACTION");
                final NotSupportedException nse = new NotSupportedException("Nested transactions are not supported.");

                FFDCFilter.processException(nse, "com.ibm.tx.jta.embeddable.impl.EmbeddableTranManagerImpl.begin", "63", this);
                if (traceOn && tc.isEntryEnabled())
                    Tr.exit(tc, "begin (SPI)", nse);
                throw nse;
            }
            else
            {
                if (tc.isDebugEnabled())
                    Tr.debug(tc, "the tx is NONINTEROP_GLOBAL it may safely be treated as null");
            }
        }

        // this is a CMT, so look for Component timeout        
        int timeout = ConfigurationProviderManager.getConfigurationProvider().getRuntimeMetaDataProvider().getTransactionTimeout();
        if (timeout == -1) {
            timeout = txTimeout;
        }

        if (timeout == 0) {
            timeout = ConfigurationProviderManager.getConfigurationProvider().getTotalTransactionLifetimeTimeout();
        }

        tx = createNewTransaction(timeout);

        invokeEventListener(tx, UOWEventListener.POST_BEGIN, null);

        if (traceOn && tc.isEntryEnabled())
            Tr.exit(tc, "begin (SPI)");
    }

    @Override
    protected EmbeddableTransactionImpl createNewTransaction(int timeout) throws SystemException
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            Tr.debug(tc, "createNewTransaction", timeout);

        final EmbeddableTransactionImpl tx = new EmbeddableTransactionImpl(timeout);
        tx.setMostRecentThread(Thread.currentThread());

        return tx;
    }

    /**
     * Complete processing of passive transaction timeout.
     */
    public void completeTxTimeout() throws TransactionRolledbackException
    {
        final boolean traceOn = TraceComponent.isAnyTracingEnabled();

        if (traceOn && tc.isEntryEnabled())
            Tr.entry(tc, "completeTxTimeout");

        if (tx != null && tx.isTimedOut())
        {
            if (traceOn && tc.isEventEnabled())
                Tr.event(tc, "Transaction has timed out. The transaction will be rolled back now");
            Tr.info(tc, "WTRN0041_TXN_ROLLED_BACK", tx.getTranName());
            ((EmbeddableTransactionImpl) tx).rollbackResources();

            final TransactionRolledbackException rbe = new TransactionRolledbackException("Transaction is ended due to timeout");

            FFDCFilter.processException(rbe, "com.ibm.tx.jta.embeddable.impl.EmbeddableTranManagerImpl.completeTxTimeout", "100", this);
            if (traceOn && tc.isEntryEnabled())
                Tr.exit(tc, "completeTxTimeout", rbe);
            throw rbe;
        }

        if (traceOn && tc.isEntryEnabled())
            Tr.exit(tc, "completeTxTimeout");
    }
}