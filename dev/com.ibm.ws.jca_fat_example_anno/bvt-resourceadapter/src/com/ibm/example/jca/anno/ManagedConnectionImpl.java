/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.example.jca.anno;

import java.io.PrintWriter;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.resource.NotSupportedException;
import javax.resource.ResourceException;
import javax.resource.spi.ConnectionEventListener;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.LocalTransaction;
import javax.resource.spi.ManagedConnection;
import javax.resource.spi.ManagedConnectionMetaData;
import javax.security.auth.Subject;
import javax.transaction.xa.XAResource;

import com.ibm.example.jca.anno.ConnectionSpecImpl.ConnectionRequestInfoImpl;

/**
 * Example managed connection.
 */
public class ManagedConnectionImpl implements ManagedConnection {
    final ConcurrentLinkedQueue<ConnectionEventListener> listeners = new ConcurrentLinkedQueue<ConnectionEventListener>();

    @Override
    public void addConnectionEventListener(ConnectionEventListener listener) {
        listeners.add(listener);
    }

    @Override
    public void associateConnection(Object handle) throws ResourceException {
        ((ConnectionImpl) handle).mc = this;
    }

    @Override
    public void cleanup() throws ResourceException {}

    @Override
    public void destroy() throws ResourceException {}

    @Override
    public Object getConnection(Subject subject, ConnectionRequestInfo cri) throws ResourceException {
        return new ConnectionImpl(this, (ConnectionRequestInfoImpl) cri);
    }

    @Override
    public LocalTransaction getLocalTransaction() throws ResourceException {
        throw new NotSupportedException();
    }

    @Override
    public PrintWriter getLogWriter() throws ResourceException {
        return null;
    }

    @Override
    public ManagedConnectionMetaData getMetaData() throws ResourceException {
        throw new NotSupportedException();
    }

    @Override
    public XAResource getXAResource() throws ResourceException {
        throw new NotSupportedException();
    }

    @Override
    public void removeConnectionEventListener(ConnectionEventListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void setLogWriter(PrintWriter logWriter) throws ResourceException {}
}
