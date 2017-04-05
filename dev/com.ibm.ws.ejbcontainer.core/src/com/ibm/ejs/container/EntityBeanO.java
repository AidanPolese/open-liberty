/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ejs.container;

import java.rmi.RemoteException;

import javax.ejb.EnterpriseBean;

/**
 * This class exists for ABI compatibility with ejbdeploy'ed home beans.
 */
public abstract class EntityBeanO
                extends BeanO
{
    public EntityBeanO(EJSContainer container, EJSHome home)
    {
        super(container, home);
    }

    /**
     * Return the underlying bean instance. This method exists for ABI
     * compatibility with ejbdeploy'ed home beans.
     */
    public abstract EnterpriseBean getEnterpriseBean()
                    throws RemoteException;

    // The remaining methods are for callers in the core container to entities
    // where it's not convenient to refactor entity support out of the callers.

    abstract void postFind()
                    throws RemoteException;

    abstract void postHomeMethodCall();

    abstract void load(ContainerTx containerTx, boolean forUpdate)
                    throws RemoteException;

    abstract void enlistForOptionA(ContainerTx containerTx);

    abstract int getLockMode();
}
