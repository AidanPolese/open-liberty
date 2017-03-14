//* ===========================================================================
//*
//* IBM SDK, Java(tm) 2 Technology Edition, v5.0
//* (C) Copyright IBM Corp. 2005, 2006
//*
//* The source code for this program is not published or otherwise divested of
//* its trade secrets, irrespective of what has been deposited with the U.S.
//* Copyright office.
//*
//* ===========================================================================
//
//@(#) 1.5 CF/ws/code/channelfw.impl/src/com/ibm/ws/channel/framework/impl/OutboundVirtualConnectionImpl.java, WAS.channelfw, CCX.CF 7/27/04 10:11:16 [5/11/05 12:15:27]

package com.ibm.ws.channelfw.internal;

import com.ibm.wsspi.channelfw.ConnectionReadyCallback;
import com.ibm.wsspi.channelfw.OutboundConnectionLink;
import com.ibm.wsspi.channelfw.OutboundVirtualConnection;
import com.ibm.wsspi.channelfw.VirtualConnection;

/**
 * 
 * VirtualOutboundConnection implementation to be used mainly by applications
 * that
 * Applications that are users of the channel framework (not implementing a
 * channel)
 */
public class OutboundVirtualConnectionImpl extends VirtualConnectionImpl implements OutboundVirtualConnection, ConnectionReadyCallback {

    /**
     * top level channel link to delegate many of these requests to
     */
    private OutboundConnectionLink appSideChannelLink = null;

    /**
     * Reference to the application side callback. Used for async connects.
     */
    private ConnectionReadyCallback appCallback = null;

    /**
     * Constructor.
     */
    protected OutboundVirtualConnectionImpl() {
        init();
    }

    /**
     * Set the top connection link to the input value.
     * 
     * @param topLink
     */
    void setConnectionLink(OutboundConnectionLink topLink) {
        this.appSideChannelLink = topLink;
    }

    /*
     * @see com.ibm.wsspi.channelfw.OutboundVirtualConnection#getChannelAccessor()
     */
    public Object getChannelAccessor() {
        return this.appSideChannelLink.getChannelAccessor();
    }

    /*
     * @see
     * com.ibm.wsspi.channelfw.OutboundVirtualConnection#connectAsynch(java.lang
     * .Object, com.ibm.wsspi.channelfw.ConnectionReadyCallback)
     */
    public void connectAsynch(Object address, ConnectionReadyCallback inputAppCallback) throws IllegalStateException {
        this.appCallback = inputAppCallback;
        this.appSideChannelLink.setApplicationCallback(this);
        this.appSideChannelLink.connectAsynch(address);
    }

    /*
     * @see
     * com.ibm.wsspi.channelfw.OutboundVirtualConnection#connect(java.lang.Object)
     */
    public void connect(Object address) throws Exception {
        this.appSideChannelLink.connect(address);
    }

    /*
     * @see com.ibm.ws.channelfw.internal.VirtualConnectionImpl#destroy()
     */
    @Override
    public void destroy() {
        this.appSideChannelLink = null;
        super.destroy();
    }

    /*
     * @see
     * com.ibm.wsspi.channelfw.OutboundVirtualConnection#close(java.lang.Exception
     * )
     */
    public void close(Exception e) {
        if (null != this.appSideChannelLink) {
            this.appSideChannelLink.close(this, e);
        } else {
            throw new RuntimeException("Null Link for outbound connection.");
        }
    }

    /**
     * Get the top level channel link associated with this Connection.
     * 
     * @return OutboundConnectionLink
     */
    public OutboundConnectionLink getApplicationLink() {
        return this.appSideChannelLink;
    }

    // --------------------------------------------
    // Methods used for ConnectionReadyCallback
    // --------------------------------------------

    /**
     * This method is called when the async connect succeeds.
     * It provides an opportunity to free up the reference to the
     * originator of the connectAsync's callback.
     * 
     * @param vc
     */
    public void ready(VirtualConnection vc) {
        this.appCallback.ready(vc);
        this.appCallback = null;
    }

    /**
     * This method is called when there is a problem with the
     * connectAsync call further down the stack. This will just
     * be used as a pass through.
     * 
     * @param e
     */
    public void destroy(Exception e) {
        if (this.appCallback != null) {
            this.appCallback.destroy(e);
            this.appCallback = null;
        }
    }

}
