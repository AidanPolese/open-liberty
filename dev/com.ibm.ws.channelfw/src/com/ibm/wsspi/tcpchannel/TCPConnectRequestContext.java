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
// Change History:
// Date     UserId      Defect          Description
// --------------------------------------------------------------------------------
// 09/21/04 gilgen      233448          Add copyright statement and change history.

package com.ibm.wsspi.tcpchannel;

import java.net.InetSocketAddress;

/**
 * This is the address passed to TCPChannel when establishing an outbound
 * connection.
 * 
 * @ibm-spi
 */
public interface TCPConnectRequestContext {
    /**
     * Load address to bind this socket to. Can return null in which case
     * the operating system dependent behaviour of binding to the next free
     * local address is assumed.
     * 
     * @return InetSocketAddress
     */
    public InetSocketAddress getLocalAddress();

    /**
     * Remote address to bind socket to. This must return a valid value.
     * 
     * @return InetSocketAddress
     */
    public InetSocketAddress getRemoteAddress();

    /**
     * Amount of time to wait in milliseconds for a connection to complete before
     * timing out.
     * Any exception received from the JDK before the timeout period will be
     * immediately
     * returned without waiting fr the timeout interval.
     * 
     * @return InetSocketAddress
     */
    public int getConnectTimeout();

    /**
     * A special value for the timeout parm used on the connect request calls.
     * Specifying this value will cause the TCPChannel to not timeout this
     * request.
     */
    public int NO_TIMEOUT = -1;
}
