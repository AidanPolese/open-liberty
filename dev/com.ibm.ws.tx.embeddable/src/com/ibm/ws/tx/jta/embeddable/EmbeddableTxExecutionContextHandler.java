package com.ibm.ws.tx.jta.embeddable;

//
// COMPONENT_NAME: WAS.transactions
//
// ORIGINS: 27
//
// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2007,2010
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
// 07/08/06  johawkes      451213.1 Creation
// 07/08/30  johawkes      463313   Override TransactionImpl creation in WAS
// 08/02/15  kaczyns       512190   Handle SystemException on begin
// 09/06/15  mallam        596067   package moves
// 10/06/15  johawkes      575302   setRollbackOnly in stoppingProvider to avoid multi-threading issues

import javax.resource.spi.work.ExecutionContext;
import javax.resource.spi.work.WorkCompletedException;
import javax.resource.spi.work.WorkException;
import javax.transaction.SystemException;
import javax.transaction.xa.Xid;

import com.ibm.tx.jta.embeddable.EmbeddableTransactionManagerFactory;
import com.ibm.tx.jta.impl.JCARecoveryData;
import com.ibm.tx.jta.impl.JCATranWrapper;
import com.ibm.ws.ffdc.FFDCFilter;
import com.ibm.ws.tx.embeddable.EmbeddableWebSphereTransactionManager;

public class EmbeddableTxExecutionContextHandler extends com.ibm.tx.jta.impl.TxExecutionContextHandler
{
    private static EmbeddableWebSphereTransactionManager _tm = EmbeddableTransactionManagerFactory.getTransactionManager();

    private static EmbeddableTxExecutionContextHandler _instance;

    @Override
    public void associate(ExecutionContext ec, String providerId) throws WorkCompletedException
    {
        try {
            if (_tm.getTransaction() != null) {
                // There's already a global tx on this thread
                final WorkCompletedException wce = new WorkCompletedException("Already associated", WorkException.TX_RECREATE_FAILED);
                throw wce;
            }
        } catch (SystemException e) {
            FFDCFilter.processException(e, "com.ibm.ws.tx.jta.embeddable.EmbeddableTxExecutionContextHandler", "54", this);
        }

        super.associate(ec, providerId);
    }

    @Override
    protected JCATranWrapper createWrapper(int timeout, Xid xid, JCARecoveryData jcard) throws WorkCompletedException /* @512190C */
    {
        return new EmbeddableJCATranWrapperImpl(timeout, xid, jcard);
    }

    public static EmbeddableTxExecutionContextHandler instance()
    {
        if (_instance == null) {
            _instance = new EmbeddableTxExecutionContextHandler();
        }
        return _instance;
    }
}
