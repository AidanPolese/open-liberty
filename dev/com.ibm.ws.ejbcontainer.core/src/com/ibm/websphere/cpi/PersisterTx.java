/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 1999, 2001
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.websphere.cpi;

import javax.transaction.*;

/**
 * PersisterTx provides utility methods pertaining to transactions
 * for use by the persisters.
 */

public interface PersisterTx
{

    /**
     * @return a boolean which is true if the transaction is scoped to
     *         the current business method on the bean.
     */
    public boolean beganInThisScope();

    /**
     * Persisters which require synchronization callbacks can register
     * with the transaction using this method.
     * 
     * @param s a Synchronization object on which the tx callbacks are
     *            to be fired.
     * 
     * @exception com.ibm.websphere.csi.CSIException thrown if unable to
     *                enlist for any reason.
     */
    public void registerSynchronization(Synchronization s)
                    throws CPIException;

} // PersisterTx

