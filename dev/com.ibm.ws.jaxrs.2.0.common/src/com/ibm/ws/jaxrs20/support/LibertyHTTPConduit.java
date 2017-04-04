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
package com.ibm.ws.jaxrs20.support;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import org.apache.cxf.Bus;
import org.apache.cxf.message.Message;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.transport.http.Address;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.apache.cxf.ws.addressing.EndpointReferenceType;

import com.ibm.ws.util.ThreadContextAccessor;

/**
 * TODO: can we remove this method? The only reference to it is from LibertyHTTPTransportFactory, but that ref is commented out...
 * 
 * LibertyHTTPConduit extends HTTPConduit so that we can set the TCCL when run the handleResponseInternal asynchronously
 */
public class LibertyHTTPConduit extends HTTPConduit {

    //save the bus so that we can get classloder from it.
    private final Bus bus;

    private static final ThreadContextAccessor THREAD_CONTEXT_ACCESSOR = ThreadContextAccessor.getThreadContextAccessor();

    public LibertyHTTPConduit(Bus b, EndpointInfo ei, EndpointReferenceType t) throws IOException {
        super(b, ei, t);
        this.bus = b;
    }

//    @Override
//    protected String getAddress() {
//        return super.getAddress();
//    }
//
//    @Override
//    protected void finalizeConfig() {
//        super.finalizeConfig();
//    }

//    @Override
//    protected OutputStream createOutputStream(Message message, HttpURLConnection connection, boolean needToCacheRequest, boolean isChunking, int chunkThreshold) {
//        return new LibertyWrappedOutputStream(message, connection, needToCacheRequest, isChunking, chunkThreshold, getConduitName());
//    }
//
//    protected class LibertyWrappedOutputStream extends HTTPConduit.WrappedOutputStream {
//
//        protected LibertyWrappedOutputStream(Message outMessage, HttpURLConnection connection, boolean possibleRetransmit, boolean isChunking, int chunkThreshold,
//                                             String conduitName) {
//            super(outMessage, connection, possibleRetransmit, isChunking, chunkThreshold, conduitName);
//        }
//
//        //handleResponse will call handleResponseInternal either synchronously or asynchronously
//        //so if call asynchronously, we set the thread context classloader because liberty executor won't set anything when run the task.
//        @Override
//        protected void handleResponseInternal() throws IOException {
//            if (outMessage == null
//                   || outMessage.getExchange() == null
//                   || outMessage.getExchange().isSynchronous()) {
//                super.handleResponseInternal();
//            } else {
//                ClassLoader oldCl = THREAD_CONTEXT_ACCESSOR.getContextClassLoader(Thread.currentThread());
//                try {
//                    // get the classloader from bus
//                    ClassLoader cl = bus.getExtension(ClassLoader.class);
//                    if (cl != null) {
//                        THREAD_CONTEXT_ACCESSOR.setContextClassLoader(Thread.currentThread(), cl);
//                    }
//                    super.handleResponseInternal();
//                } finally {
//                    THREAD_CONTEXT_ACCESSOR.setContextClassLoader(Thread.currentThread(), oldCl);
//                }
//            }
//        }
//
//    }

    protected void setupConnection(Message message, URI url,
                                   HTTPClientPolicy csPolicy) throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    protected OutputStream createOutputStream(Message message,
                                              boolean needToCacheRequest, boolean isChunking, int chunkThreshold)
                    throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.cxf.transport.http.HTTPConduit#setupConnection(org.apache.cxf.message.Message, org.apache.cxf.transport.http.Address,
     * org.apache.cxf.transports.http.configuration.HTTPClientPolicy)
     */
    @Override
    protected void setupConnection(Message message, Address address, HTTPClientPolicy csPolicy) throws IOException {
        // TODO Auto-generated method stub

    }
}
