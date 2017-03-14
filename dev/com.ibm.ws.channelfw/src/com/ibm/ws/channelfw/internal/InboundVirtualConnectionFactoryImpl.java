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
//@(#) 1.4 CF/ws/code/channelfw.impl/src/com/ibm/ws/channel/framework/impl/InboundVirtualConnectionFactoryImpl.java, WAS.channelfw, CCX.CF 5/10/04 22:25:00 [5/11/05 12:35:20]

package com.ibm.ws.channelfw.internal;

import com.ibm.websphere.channelfw.FlowType;
import com.ibm.wsspi.channelfw.InboundVirtualConnectionFactory;
import com.ibm.wsspi.channelfw.VirtualConnection;

/**
 * Inbound VirtualConnectionFactory implementation.
 * 
 */
public class InboundVirtualConnectionFactoryImpl implements InboundVirtualConnectionFactory {

    /**
     * Constructor for Inbound types.
     */
    public InboundVirtualConnectionFactoryImpl() {
        // Nothing needed here at this time.
    }

    /*
     * @see
     * com.ibm.wsspi.channelfw.InboundVirtualConnectionFactory#createConnection()
     */
    public VirtualConnection createConnection() {
        VirtualConnectionImpl vc = new InboundVirtualConnectionImpl();
        vc.init();

        return vc;
    }

    /*
     * @see com.ibm.wsspi.channelfw.VirtualConnectionFactory#getType()
     */
    public FlowType getType() {
        return FlowType.INBOUND;
    }

    /*
     * @see com.ibm.wsspi.channelfw.VirtualConnectionFactory#getName()
     */
    public String getName() {
        return "inbound";
    }

    /*
     * @see com.ibm.wsspi.channelfw.VirtualConnectionFactory#destroy()
     */
    public void destroy() {
        // Nothing needed here at this time.
    }

}
