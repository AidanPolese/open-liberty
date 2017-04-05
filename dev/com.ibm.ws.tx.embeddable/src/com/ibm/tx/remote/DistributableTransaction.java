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

import javax.transaction.SystemException;
import javax.transaction.xa.Xid;

import com.ibm.tx.jta.embeddable.impl.WSATRecoveryCoordinator;

/**
 *
 */
public interface DistributableTransaction {

    /**
     * 
     */
    void suspendAssociation();

    /**
     * 
     */
    void resumeAssociation();

    /**
     * @return
     */
    String getGlobalId();

    /**
     * 
     */
    void addAssociation();

    /**
     * 
     */
    void removeAssociation();

    /**
     * @return
     */
    int getStatus();

    /**
     * 
     */
    void setRollbackOnly();

    /**
     * @return
     */
    Xid getXid();

    /**
     * @param xaResFactoryFilter
     * @param xaResInfo
     * @param xid
     * @throws SystemException
     */
    void enlistAsyncResource(String xaResFactoryFilter, Serializable xaResInfo, Xid xid) throws SystemException;

    /**
     * @param rc
     */
    void setWSATRecoveryCoordinator(WSATRecoveryCoordinator rc);

    /**
     * 
     */
    void replayCompletion();

}