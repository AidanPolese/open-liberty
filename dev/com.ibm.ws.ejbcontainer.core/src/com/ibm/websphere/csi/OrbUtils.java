/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 1998, 2002
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */

/**
 *  The <code>OrbUtils</code> provides the EJB container with access
 *  to some ORB specific utilities.
 */

package com.ibm.websphere.csi;

import java.rmi.RemoteException; //d135584

public interface OrbUtils {

    /**
     * Connect the given object with the communications layer (ORB).
     * 
     * @param stub the object to register; will be a stub created by
     *            the orb
     * 
     * @exception CSIException thrown if the connect fails
     * 
     */

    public void connectToOrb(Object stub)
                    throws CSIException;

    /**
     * Map an RMI exception thrown in the container to a transport
     * (ORB) specific exception. For IIOP, this will be mapped to
     * a subclass of org.omg.CORBA.SystemException.
     * 
     * @param e the Exception to be mapped
     * @param message an informational String which may be included
     * @param minorCode a minor code describing the specific problem
     * 
     * @exception CSIException thrown if the mapping fails
     */

    public Exception mapException(RemoteException e, int minorCode) //d135584
    throws CSIException;

    public Exception mapException(RemoteException e) //d135584
    throws CSIException;

}
