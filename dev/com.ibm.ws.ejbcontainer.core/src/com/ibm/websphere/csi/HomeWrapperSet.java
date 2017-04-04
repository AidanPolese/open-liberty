/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2004
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.websphere.csi;

import java.rmi.Remote;

public interface HomeWrapperSet {

    /**
     * Returns the Remote reference of the remote home interface of this EJB.
     * This object will be used to bind to the naming service. This method
     * returns null if no remote interface is defined in the bean.
     */
    public Remote getRemote();

    /**
     * Returns the local home interface of this EJB.
     * This object will be used to bind to the naming service. This method
     * returns null if no local interface is defined in the bean.
     */
    public Object getLocal(); //LIDB859-4
}
