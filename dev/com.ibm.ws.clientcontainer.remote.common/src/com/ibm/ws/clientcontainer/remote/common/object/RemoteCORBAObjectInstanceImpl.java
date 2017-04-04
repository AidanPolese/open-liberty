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
package com.ibm.ws.clientcontainer.remote.common.object;

import java.rmi.Remote;
import java.security.AccessController;
import java.security.PrivilegedAction;

import javax.rmi.PortableRemoteObject;

import com.ibm.ws.container.service.naming.RemoteObjectInstance;
import com.ibm.ws.container.service.naming.RemoteObjectInstanceException;

/**
 *
 */
public class RemoteCORBAObjectInstanceImpl implements RemoteObjectInstance {
    private static final long serialVersionUID = -5424320741986130299L;

    final Object remoteObject;
    final String interfaceNameToNarrowTo;

    public RemoteCORBAObjectInstanceImpl(Remote remoteObject, String interfaceNameToNarrowTo) {
        this.remoteObject = remoteObject;
        this.interfaceNameToNarrowTo = interfaceNameToNarrowTo;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.clientcontainer.remote.common.object.RemoteObjectInstance#getObject(com.ibm.ws.serialization.SerializationService)
     */
    @Override
    public Object getObject() throws RemoteObjectInstanceException {
        Object object;

        ClassLoader tccl = AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {

            @Override
            public ClassLoader run() {
                return Thread.currentThread().getContextClassLoader();
            }

        });

        try {
            Class<?> interfaceToNarrowTo = Class.forName(interfaceNameToNarrowTo, false, tccl);
            object = PortableRemoteObject.narrow(remoteObject, interfaceToNarrowTo);
        } catch (ClassNotFoundException ex) {
            throw new RemoteObjectInstanceException("Failed to find class " + interfaceNameToNarrowTo + " from classloader: " + tccl, ex);
        } catch (ClassCastException ex) {
            throw new RemoteObjectInstanceException("Unable to narrow remote object to " + interfaceNameToNarrowTo, ex);
        }

        return object;
    }

}
