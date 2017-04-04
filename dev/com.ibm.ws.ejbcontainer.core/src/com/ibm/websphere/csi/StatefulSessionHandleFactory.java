/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2000
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */

/**
 *  A <code>StatefulSessionHandleFactory</code> constructs Handles
 *  for stateful session beans. <p>
 */

package com.ibm.websphere.csi;

public interface StatefulSessionHandleFactory {

    /**
     * Return a <code>Handle</code> for a stateful session bean. <p>
     */

    public javax.ejb.Handle create(javax.ejb.EJBObject object)
                    throws CSIException;
}
