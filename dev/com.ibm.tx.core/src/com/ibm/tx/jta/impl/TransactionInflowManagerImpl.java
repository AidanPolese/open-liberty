package com.ibm.tx.jta.impl;
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
// 09/06/02  mallam        596067   package move

import javax.resource.spi.XATerminator;
import javax.resource.spi.work.ExecutionContext;
import javax.resource.spi.work.WorkCompletedException;
import javax.transaction.NotSupportedException;

import com.ibm.tx.jta.*;
import com.ibm.tx.util.TMHelper;
import com.ibm.tx.util.logging.FFDCFilter;

public class TransactionInflowManagerImpl implements TransactionInflowManager
{
    private static TransactionInflowManager _instance;

    private TransactionInflowManagerImpl(){}

    public static synchronized TransactionInflowManager instance()
    {
        if (_instance == null)
        {
            _instance = new TransactionInflowManagerImpl();
        }

        return _instance;
    }

    public void associate(ExecutionContext ec, String inflowCoordinatorName) throws WorkCompletedException
    {
        try
        {
            TMHelper.checkTMState();
        }
        catch(NotSupportedException e)
        {
            FFDCFilter.processException(e, "com.ibm.tx.jta.impl.TxExecutionContextHandler.associate", "105", this);
            throw new WorkCompletedException(e);
        }

        TxExecutionContextHandler.instance().associate(ec, inflowCoordinatorName);
    }

    public void dissociate()
    {
        TxExecutionContextHandler.doDissociate();
    }

    public XATerminator getXATerminator(String inflowCoordinatorName)
    {
        return TxXATerminator.instance(inflowCoordinatorName);
    }
}