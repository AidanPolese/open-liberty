/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2000, 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.websphere.csi;

/**
 * This interface is internal API and is being maintained for strict API
 * compatibility only. It will be removed in a subsequent release.
 */
@Deprecated
public interface RemoveCollaborator {

    public void remove(EJBKey key);

    public void passivate(EJBKey key);
}
