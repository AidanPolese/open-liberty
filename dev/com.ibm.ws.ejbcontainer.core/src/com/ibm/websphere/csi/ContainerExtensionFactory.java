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
 *  A <code>ContainerExtensionFactory</code> constructs implementations
 *  of container collaborators, strategies, and policies classes that
 *  have different implementations on the base and extended versions
 *  of the server.
 */

package com.ibm.websphere.csi;

import java.util.List;

import javax.transaction.UserTransaction;

import com.ibm.ejs.container.BeanMetaData;
import com.ibm.ejs.container.ContainerException;
import com.ibm.ejs.container.activator.ActivationStrategy;
import com.ibm.ejs.container.activator.Activator;
import com.ibm.ejs.csi.ActivitySessionMethod;
import com.ibm.ejs.csi.UOWControl;
import com.ibm.ws.ejbcontainer.failover.SfFailoverCache;

public interface ContainerExtensionFactory {

    /**
     * Return an <code>UOWControl</code>. <p>
     */
    public UOWControl getUOWControl(UserTransaction userTx); // LIDB4171-35.03 F84120

    /**
     * Return an <code>ActivationStrategy</code>. <p>
     */
    public ActivationStrategy getActivationStrategy(int type,
                                                    Activator activator,
                                                    PassivationPolicy passivationPolicy,
                                                    SfFailoverCache failoverCache); //LIDB2018-1

    /**
     * Return a boolean indicating whether the ejb has Bean-Managed Activity Sessions. <p>
     */
    public boolean isActivitySessionBeanManaged(boolean usesBeanManagedTx) throws ContainerException; // d126204.2

    /**
     * Return an <code>ActivationStrategy</code>. <p>
     */
    public List<ActivitySessionMethod> getActivitySessionAttributes(BeanMetaData bmd) throws Exception; //d143954 F743-24095
}
