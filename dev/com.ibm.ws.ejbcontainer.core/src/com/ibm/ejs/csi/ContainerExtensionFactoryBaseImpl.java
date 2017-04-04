/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2002, 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */

/**
 *  A <code>ContainerExtensionFactoryBaseImpl</code> constructs implementations
 *  of container collaborators, strategies, and policies classes for the base server.
 */

package com.ibm.ejs.csi;

import java.util.List;

import javax.transaction.UserTransaction;

import com.ibm.ejs.container.BeanMetaData;
import com.ibm.ejs.container.activator.ActivationStrategy;
import com.ibm.ejs.container.activator.Activator;
import com.ibm.ejs.ras.Tr;
import com.ibm.ejs.ras.TraceComponent;
import com.ibm.websphere.csi.ContainerExtensionFactory;
import com.ibm.websphere.csi.PassivationPolicy;
import com.ibm.ws.ejbcontainer.failover.SfFailoverCache;

public class ContainerExtensionFactoryBaseImpl implements ContainerExtensionFactory {

    private static final TraceComponent tc =
                    Tr.register(ContainerExtensionFactoryBaseImpl.class, "EJBContainer",
                                "com.ibm.ejs.container.container");

    /**
     * Return a <code>UOWControl</code>. <p>
     */
    public UOWControl getUOWControl(UserTransaction userTx) //LIDB4171-35.03 F84120
    {
        final boolean entryEnabled =
                        TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled();
        if (entryEnabled) {
            Tr.entry(tc, "getUOWControl");
        }
        UOWControl uowCtrl = new TransactionControlImpl(userTx); //LIDB4171-35.03 F84120
        if (entryEnabled) {
            Tr.exit(tc, "getUOWControl");
        }
        return uowCtrl;
    }

    /**
     * Return an <code>ActivationStrategy</code>. <p>
     */
    public ActivationStrategy getActivationStrategy(int type,
                                                    Activator activator,
                                                    PassivationPolicy passivationPolicy,
                                                    SfFailoverCache failoverCache) //LIDB2018-1
    {
        ActivationStrategy as = null;
        if (type == Activator.ENTITY_SESSIONAL_TRAN_ACTIVATION_STRATEGY) {
            as = activator.getActivationStrategy(Activator.OPTC_ENTITY_ACTIVATION_STRATEGY); // d127328
        }
        else if (type == Activator.STATEFUL_ACTIVATE_SESSION_ACTIVATION_STRATEGY) {
            as = activator.getActivationStrategy(Activator.STATEFUL_ACTIVATE_TRAN_ACTIVATION_STRATEGY); // d127328
        }
        return as;
    }

    /**
     * Return a boolean indicating whether the ejb has Bean-Managed Activitiy Sessions. <p>
     */
    public boolean isActivitySessionBeanManaged(boolean usesBeanManagedTx) // d126204.2
    {
        // return false since no activity session attributes are defined on the base.
        return false;
    }

    /**
     * Return an <code>ActivationStrategy</code>. <p>
     */
    public List<ActivitySessionMethod> getActivitySessionAttributes(BeanMetaData bmd) throws Exception // F743-24095
    {
        // return null since no activity session attributes are defined on the base.
        return null;
    }

}
