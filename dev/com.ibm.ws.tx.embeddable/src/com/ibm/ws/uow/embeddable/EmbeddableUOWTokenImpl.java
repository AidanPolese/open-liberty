/* ********************************************************************************* */
/* COMPONENT_NAME: WAS.transactions                                                  */
/*                                                                                   */
/* ORIGINS: 27                                                                       */
/*                                                                                   */
/* IBM Confidential OCO Source Material                                              */
/* 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2004, 2011 */
/* The source code for this program is not published or otherwise divested           */
/* of its trade secrets, irrespective of what has been deposited with the            */
/* U.S. Copyright Office.                                                            */
/*                                                                                   */
/* @(#) 1.3 SERV1/ws/code/was.transaction.impl/src/com/ibm/ws/uow/UOWTokenImpl.java, WAS.transactions, WASX.SERV1, xx0940.06 5/24/08 06:02:45 [10/7/09 16:55:10]                                                     */
/*                                                                                   */
/*  DESCRIPTION:                                                                     */
/*                                                                                   */
/*  Change History:                                                                  */
/*                                                                                   */
/*  YY-MM-DD  Programmer  Defect  Description                                        */
/*  --------  ----------  ------  -----------                                        */
/*  04-05-07  awilkins    200172  Creation                                           */
/*  08-05-24  johawkes    522569  Perf trace                                         */
/*  11-11-24  johawkes    723423  Repackaging                                        */
/* ********************************************************************************* */

package com.ibm.ws.uow.embeddable;

import javax.transaction.Transaction;

import com.ibm.ejs.ras.Tr;
import com.ibm.ejs.ras.TraceComponent;
import com.ibm.tx.TranConstants;
import com.ibm.ws.LocalTransaction.LocalTransactionCoordinator;

public class EmbeddableUOWTokenImpl implements UOWToken
{
    private static final TraceComponent tc = Tr.register(UOWToken.class, TranConstants.TRACE_GROUP, null);

    protected Transaction _transaction;
    protected LocalTransactionCoordinator _localTranCoord;
    
    protected EmbeddableUOWTokenImpl(Transaction transaction, LocalTransactionCoordinator localTranCoord)
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) Tr.entry(tc, "UOWToken", new Object[]{transaction, localTranCoord});

        _transaction = transaction;
        _localTranCoord = localTranCoord;

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) Tr.exit(tc, "UOWToken", this);
    }

    public final Transaction getTransaction()
    {        
        return _transaction;
    }

    public final LocalTransactionCoordinator getLocalTransactionCoordinator()
    {       
        return _localTranCoord;
    }

    public String toString()
    {
        final StringBuffer buffer = new StringBuffer();
    
        buffer.append("UOWToken [ ");
        buffer.append("Transaction: ");
        buffer.append(_transaction);
        buffer.append(", ");
        buffer.append("LocalTranCoord: ");
        buffer.append(_localTranCoord);
        buffer.append(" ]");
    
        return buffer.toString();
    }
}
