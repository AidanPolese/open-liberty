/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package javax.management.j2ee;

import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.ejb.EJBHome;

/**
 * The required home interface for the Management EJB component. The
 * interface may be extended by a proprietary interface to include
 * additional create methods that take initialization arguments. A J2EE
 * client must be able to create a compliant session object using the
 * specified create method which takes no arguments.
 */
public interface ManagementHome extends EJBHome {

    /*
     * Throws:
     * javax.ejb.CreateException
     * java.rmi.RemoteException
     * Creates an MEJB session object which provides access to the J2EE
     * Management Model.
     * Returns:
     * An session object which implements
     * javax.management.j2ee.Management.
     */
    public Management create() throws CreateException, RemoteException;
}
