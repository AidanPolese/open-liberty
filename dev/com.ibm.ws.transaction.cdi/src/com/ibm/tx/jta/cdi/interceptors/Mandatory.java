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
import javax.transaction.TransactionRequiredException;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;
import javax.transaction.TransactionalException;

import com.ibm.websphere.uow.UOWSynchronizationRegistry;

@Transactional(value = TxType.MANDATORY)
@Priority(Interceptor.Priority.PLATFORM_BEFORE + 200)
@Interceptor
public class Mandatory extends TransactionalInterceptor {

    private static final long serialVersionUID = 1L;

    /**
     * <p>If called outside a transaction context, a TransactionalException with a
     * nested TransactionRequiredException must be thrown.</p>
     * <p>If called inside a transaction context, managed bean method execution will
     * then continue under that context.</p>
     */

    @AroundInvoke
    public Object mandatory(final InvocationContext context) throws Exception {

        if (getUOWM().getUOWType() != UOWSynchronizationRegistry.UOW_TYPE_GLOBAL_TRANSACTION) {
            throw new TransactionalException("global tx required", new TransactionRequiredException());
        }

        return runUnderUOWManagingEnablement(UOWSynchronizationRegistry.UOW_TYPE_GLOBAL_TRANSACTION, true, context, "MANDATORY");
    }
}