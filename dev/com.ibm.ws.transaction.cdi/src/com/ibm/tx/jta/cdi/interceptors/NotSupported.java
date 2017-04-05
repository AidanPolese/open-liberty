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
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import com.ibm.websphere.uow.UOWSynchronizationRegistry;

@Transactional(value = TxType.NOT_SUPPORTED)
@Priority(Interceptor.Priority.PLATFORM_BEFORE + 200)
@Interceptor
public class NotSupported extends TransactionalInterceptor {
    private static final long serialVersionUID = 1L;

    /**
     * <p>If called outside a transaction context, managed bean method execution
     * must then continue outside a transaction context.</p>
     * <p>If called inside a transaction context, the current transaction context must
     * be suspended, the managed bean method execution must then continue
     * outside a transaction context, and the previously suspended transaction
     * must be resumed by the interceptor that suspended it after the method
     * execution has completed.</p>
     */

    @AroundInvoke
    public Object notSupported(final InvocationContext context) throws Exception {

        return runUnderUOWNoEnablement(UOWSynchronizationRegistry.UOW_TYPE_LOCAL_TRANSACTION, true, context, "NOT_SUPPORTED");

    }
}