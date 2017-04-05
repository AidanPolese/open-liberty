/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.clientcontainer.remote.common;

import java.rmi.RemoteException;

/**
 * Provides access to the ClientSupport remote interface of a singleton class bound in CosNaming
 * to allow remote clients (the client container in particular) to access objects bound in the
 * server's namespace.
 */
public interface ClientSupportFactory {

    /**
     * Returns the remote reference (Stub) to the ClientSupport instance on the
     * default server process.
     *
     * @throws RemoteException if the remote ClientSupport reference cannot be obtained
     */
    ClientSupport getRemoteClientSupport() throws RemoteException;

}
