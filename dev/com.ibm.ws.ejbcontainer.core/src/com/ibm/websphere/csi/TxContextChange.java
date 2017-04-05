/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2002, 2003
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.websphere.csi;

import javax.transaction.Transaction; //LIDB1673.2.1.5
import com.ibm.ws.LocalTransaction.LocalTransactionCoordinator;

public class TxContextChange
{

    /**
     * Placeholder for any local tx context which had to be temporarily suspended
     * from the thread in order to establish a different local tx context. <p>
     */

    private LocalTransactionCoordinator suspendedLocalTx = null;

    /**
     * Placeholder for any global tx context which had to be temporarily suspended
     * from the thread in order to establish a local tx context. <p>
     */

    private Transaction suspendedGlobalTx = null; //LIDB1673.2.1.5

    /**
     * Placeholder for local tx context which was established onto the
     * thread. <p>
     */

    private LocalTransactionCoordinator activatedLocalTx = null;

    /**
     * EJBKey of bean instance for which this temporary context change was
     * initiated. <p>
     */

    private EJBKey key = null;

    /**
     * Create a new <code>TxContextChange</code> instance. <p>
     */

    public TxContextChange(LocalTransactionCoordinator suspendedLocalTx,
                           Transaction suspendedGlobalTx, //LIDB1673.2.1.5
                           LocalTransactionCoordinator activatedLocalTx,
                           EJBKey key)
    {
        this.suspendedLocalTx = suspendedLocalTx;
        this.suspendedGlobalTx = suspendedGlobalTx;
        this.activatedLocalTx = activatedLocalTx;
        this.key = key;
    }

    /**
     * Return any local Tx context which was suspended from the
     * thread do to this context change. Returns null if no local
     * tx was suspended.
     */

    public LocalTransactionCoordinator getSuspendedLocalTx()
    {
        return (suspendedLocalTx);
    }

    /**
     * Return any global Tx context which was suspended from the
     * thread do to this context change. Returns null if no global
     * tx was suspended.
     */

    public Transaction getSuspendedGlobalTx() //LIDB1673.2.1.5
    {
        return (suspendedGlobalTx);
    }

    /**
     * Return local Tx context which was established onto the thread
     * by this context change. Returns null if no context change
     * occured.
     */

    public LocalTransactionCoordinator getActivatedLocalTx()
    {
        return (activatedLocalTx);
    }

    /**
     * Return EJB key of bean instance which this context change
     * was initiated for. <p>
     */

    public EJBKey getKey()
    {
        return (key);
    }

} // TxContextChange
