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

package com.ibm.wsspi.channelfw;

/**
 * This interface only needs to be implemented by the first channel (application
 * side)
 * in outbound chains. More specifically, it should be implemented by the object
 * that is returned by OutboundVirtualConnection.getChannelAccessor().
 * <p>
 * The reason to expose the protocol of an outbound chain is to enable
 * appropriate selection of an SSL context from the Security Service.
 * Flexibility exists there for customers to configure SSL repertoires in
 * management groups that can be organized by protocol.
 * 
 */
public interface OutboundProtocol {

    /**
     * If necessary, the user of an OutboundVirtualConnection can override the
     * protocol referenced by the first channel in the chain by setting a property
     * with this name in the VirtualConnection state Map.
     */
    String PROTOCOL = "protocol";

    /**
     * Return a well known protocol string representing this outbound channel
     * interface.
     * A list of well know protocol strings can be found in
     * com.ibm.ssl.core.Constants.
     * 
     * @return String representing the protocol
     */
    String getProtocol();
}
