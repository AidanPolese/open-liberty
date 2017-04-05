/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 1998, 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ejs.container;

import java.rmi.RemoteException;

import com.ibm.ejs.ras.Tr;
import com.ibm.ejs.ras.TraceComponent;

/**
 * Create new <code>BMStatelessBeanO</code>. <p>
 **/
public class BMStatelessBeanO
                extends StatelessBeanO
{
    private static final TraceComponent tc =
                    Tr.register(BMStatelessBeanO.class,
                                "EJBContainer",
                                "com.ibm.ejs.container.container");//121558

    /**
     * Create new <code>BMStatelessBeanO</code>. <p>
     */
    public BMStatelessBeanO(EJSContainer c, EJSHome h)
    {
        super(c, h);
    } // BMStatelessBeanO

    /**
     * setRollbackOnly - This method is illegal for bean managed stateless
     * session beans
     */
    @Override
    public void setRollbackOnly()
    {
        throw new IllegalStateException();
    } // setRollbackOnly

    /**
     * getRollbackOnly - This method is illegal for bean managed stateless
     * session beans
     */
    @Override
    public boolean getRollbackOnly()
    {
        throw new IllegalStateException();
    } // getRollbackOnly

    //d170394
    @Override
    public final void postInvoke(int id, EJSDeployedSupport s)
                    throws RemoteException
    {
        if (state == DESTROYED) {
            return;
        }
        ContainerTx tx = null;
        if (null == ivContainerTx) { //d170394
            tx = ivContainerTx;
        } else {
            tx = container.getCurrentTx(false);
        }

        //167937 - discard bean if BMT was started and is still active.

        if (tx != null && tx.isBmtActive(s.methodInfo))
        {
            // BMT is still active.  Discard bean and let the BeanManaged.postInvoke
            // do the rollback and throwing of the exception.
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                Tr.debug(tc,
                         "Stateless SB method is not allowed to leave a BMT active. " +
                                         "Discarding bean.");
            discard();
        }

        ivContainerTx = null;//d170394
    }

} // BMStatelessBeanO
