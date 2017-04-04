/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.tx.jta.cdi.interceptors;

import javax.annotation.Priority;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.transaction.InvalidTransactionException;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;
import javax.transaction.TransactionalException;

import com.ibm.websphere.uow.UOWSynchronizationRegistry;

@Transactional(value = TxType.NEVER)
@Priority(Interceptor.Priority.PLATFORM_BEFORE + 200)
@Interceptor
public class Never extends TransactionalInterceptor {
    private static final long serialVersionUID = 1L;

    /**
     * <p>If called outside a transaction context, managed bean method execution
     * must then continue outside a transaction context.</p>
     * <p>If called inside a transaction context, a TransactionalException with
     * a nested InvalidTransactionException must be thrown.</p>
     */

    @AroundInvoke
    public Object never(final InvocationContext context) throws Exception {

        if (getUOWM().getUOWType() == UOWSynchronizationRegistry.UOW_TYPE_GLOBAL_TRANSACTION) {
            throw new TransactionalException("TxType.NEVER method called within a global tx", new InvalidTransactionException());
        }

        return runUnderUOWNoEnablement(UOWSynchronizationRegistry.UOW_TYPE_LOCAL_TRANSACTION, true, context, "NEVER");

    }
}