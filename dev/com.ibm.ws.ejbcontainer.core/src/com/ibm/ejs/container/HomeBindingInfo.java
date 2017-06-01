/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2001, 2006
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ejs.container;

import java.rmi.Remote;

import com.ibm.websphere.csi.HomeWrapperSet;
import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;

/**
 * A home interface binding information wrapper for the CSI HomeWrapperSet
 * interface.
 **/
public class HomeBindingInfo implements HomeWrapperSet {
    //d121558
    private static final TraceComponent tc = Tr.register(HomeBindingInfo.class, "EJBContainer", "com.ibm.ejs.container.container");

    private Remote remoteHome;
    private Object localHome; //LIDB859-4

    /**
     * Construct the Local Interface Home Info object for naming service binding.
     */
    public HomeBindingInfo(Remote rHome,
                           Object lHome) //LIDB859-4
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.entry(tc, "<init>");

        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            if (rHome != null)
                Tr.debug(tc, "HomeBinding Remote:" + rHome.getClass().getName());
            if (lHome != null)
                Tr.debug(tc, "HomeBinding Local:" + lHome.getClass().getName());
        }
        remoteHome = rHome;
        localHome = lHome;

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.exit(tc, "<init>");
    }

    /**
     * Returns the Remote reference of the remote home interface of this EJB.
     * This object will be used to bind to the naming service. This method
     * returns null if no remote interface is defined in the bean.
     */
    public Remote getRemote() {
        return remoteHome;
    }

    /**
     * Returns the naming Reference of the local home interface of this EJB.
     * This object will be used to bind to the naming service. This method
     * returns null if no local interface is defined in the bean.
     */
    public Object getLocal() { //LIDB859-4
        return localHome;
    }
}
