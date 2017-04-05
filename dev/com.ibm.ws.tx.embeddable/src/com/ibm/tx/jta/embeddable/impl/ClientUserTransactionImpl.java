package com.ibm.tx.jta.embeddable.impl;

/* ***************************************************************************************************** */
/* COMPONENT_NAME: WAS.transactions                                                                      */
/*                                                                                                       */
/*  ORIGINS: 27                                                                                          */
/*                                                                                                       */
/* IBM Confidential OCO Source Material                                                                  */
/* 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2009,2011 */
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
/* ***************************************************************************************************** */

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;

import com.ibm.tx.jta.impl.UserTransactionImpl;
import com.ibm.ws.tx.embeddable.EmbeddableWebSphereUserTransaction;
import com.ibm.ws.uow.UOWScopeCallback;

public class ClientUserTransactionImpl extends UserTransactionImpl implements EmbeddableWebSphereUserTransaction
{
    private static final EmbeddableWebSphereUserTransaction _instance = new ClientUserTransactionImpl();

    public static EmbeddableWebSphereUserTransaction newOne() {
        return _instance;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.transaction.UserTransaction#begin()
     */
    @Override
    public void begin() throws NotSupportedException, SystemException {
        throw new SystemException();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.transaction.UserTransaction#commit()
     */
    @Override
    public void commit() throws HeuristicMixedException, HeuristicRollbackException, IllegalStateException, RollbackException, SecurityException, SystemException {
        throw new SystemException();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.transaction.UserTransaction#getStatus()
     */
    @Override
    public int getStatus() throws SystemException {
        throw new SystemException();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.transaction.UserTransaction#rollback()
     */
    @Override
    public void rollback() throws IllegalStateException, SecurityException, SystemException {
        throw new SystemException();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.transaction.UserTransaction#setRollbackOnly()
     */
    @Override
    public void setRollbackOnly() throws IllegalStateException, SystemException {
        throw new SystemException();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.transaction.UserTransaction#setTransactionTimeout(int)
     */
    @Override
    public void setTransactionTimeout(int arg0) throws SystemException {
        throw new SystemException();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.tx.embeddable.EmbeddableWebSphereUserTransaction#registerCallback(com.ibm.ws.uow.UOWScopeCallback)
     */
    @Override
    public void registerCallback(UOWScopeCallback callback) {
        throw new IllegalStateException();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.tx.embeddable.EmbeddableWebSphereUserTransaction#unregisterCallback(com.ibm.ws.uow.UOWScopeCallback)
     */
    @Override
    public void unregisterCallback(UOWScopeCallback callback) {
        throw new IllegalStateException();
    }
}