/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.tx.remote;

import java.io.Serializable;

import javax.transaction.HeuristicCommitException;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;

import com.ibm.ws.Transaction.JTA.HeuristicHazardException;

/**
 *
 */
public interface RemoteTransactionController {
    public boolean importTransaction(String globalId, int expires) throws SystemException;

    public void unimportTransaction(String globalId) throws SystemException;

    public String exportTransaction() throws SystemException;

    public void unexportTransaction(String globalId) throws SystemException;

    public void setRollbackOnly(String globalId) throws SystemException;

    public boolean registerRemoteParticipant(String xaResFactoryFilter, Serializable xaResInfo, String globalId) throws SystemException;

    public boolean registerRecoveryCoordinator(String recoveryCoordinatorFactoryFilter, Serializable recoveryCoordinatorInfo, String globalId) throws SystemException;

    public Vote prepare(String globalId) throws SystemException, HeuristicHazardException, HeuristicMixedException, RollbackException;

    public void commit(String globalId) throws SystemException, HeuristicHazardException, HeuristicRollbackException, HeuristicMixedException;

    public void rollback(String globalId) throws HeuristicHazardException, HeuristicCommitException, HeuristicMixedException, SystemException;

    /**
     * @param globalId
     * @return
     */
    public boolean replayCompletion(String globalId);
}