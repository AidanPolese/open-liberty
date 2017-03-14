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
// 09/27/04 ejburcka    234899          Pure nonblocking mode for the TCP Channel
// 04/14/05 wigger      267048          make more custom properties available to change
// 01/30/07 bgower      PK36998         Change max connections max value
// 08/20/07 wigger      454453          add/change max connection and inactivity timeout values

package com.ibm.wsspi.tcpchannel;

import com.ibm.ws.channelfw.internal.ChannelFrameworkConstants;

/**
 * This interface describes the constants used for configuring a TCP Channel.
 * 
 * @ibm-spi
 * 
 */
public interface TCPConfigConstants {

    /**
     * Host name that an inbound channel will use for listening
     */
    String HOST_NAME = ChannelFrameworkConstants.HOST_NAME;

    /**
     * port on which the inbound channel will listen
     */
    String PORT = ChannelFrameworkConstants.PORT;

    /**
     * The maximum number of concurrent connections allowed for an inbound channel
     */
    String MAX_CONNS = "maxOpenConnections";

    /**
     * An IPv4/IPv6 address exclusion list that an inbound channel will use when
     * accepting new connections
     */
    String ADDR_EXC_LIST = "addressExcludeList";

    /**
     * A host name exclusion list that an inbound channel will use when accepting
     * new connections
     */
    String NAME_EXC_LIST = "hostNameExcludeList";

    /**
     * An IPv4/IPv6 address inclusion list that an inbound channel will use when
     * accepting new connections
     */
    String ADDR_INC_LIST = "addressIncludeList";

    /**
     * A host name inclusion list that an inbound channel will use when accepting
     * new connections
     */
    String NAME_INC_LIST = "hostNameIncludeList";

    /**
     * The default time out for TCP operations for this channel
     */
    String INACTIVITY_TIMEOUT = "inactivityTimeout";

    /**
     * The size of the tcp socket sending buffer
     */
    String SEND_BUFF_SIZE = "sendBufferSize";

    /**
     * The size of the tcp socket receiving buffer
     */
    String RCV_BUFF_SIZE = "receiveBufferSize";

    /**
     * Minimum Port Value
     */
    int PORT_MIN = 0;

    /**
     * Maximum Port Value
     */
    int PORT_MAX = 65535;

    /**
     * Minimum number of the maximum allowable concurrent connections
     */
    int MAX_CONNECTIONS_MIN = 1;

    /**
     * Maximum number of the maximum allowable concurrent connections
     */
    int MAX_CONNECTIONS_MAX = 1280000;

    /**
     * Maximum number of the maximum allowable concurrent connections
     */
    int MAX_CONNECTIONS_DEFAULT = 128000;

    /**
     * Minimum TCP socket recceve buffer size
     */
    int RECEIVE_BUFFER_SIZE_MIN = 4;

    /**
     * Maximum TCP socket receive buffer size
     */
    int RECEIVE_BUFFER_SIZE_MAX = 16777216; // 16 Meg

    /**
     * Minimum TCP socket send buffer size
     */
    int SEND_BUFFER_SIZE_MIN = 4;

    /**
     * Maximum TCP socket send buffer size
     */
    int SEND_BUFFER_SIZE_MAX = 16777216; // 16 Meg

    /**
     * Minimum timeout for TCP operations. A value of 0 means No Timeout
     */
    int INACTIVITY_TIMEOUT_MIN = 0; // value given in milliseconds

    /**
     * Maximum timeout for TCP operations
     */
    int INACTIVITY_TIMEOUT_MAX = 3600000; // value given in milliseconds

    /**
     * Default timeout for TCP operations in seconds
     */
    int INACTIVITY_TIMEOUT_DEFAULT_SECONDS = 60;

    /**
     * Default timeout for TCP operations in milliseconds
     */
    int INACTIVITY_TIMEOUT_DEFAULT_MSECS = 60000;

    /**
     * Port that the TCPChannel is actually listening on. This property is
     * put back in the property bag after the TCPChannel has started. It should
     * NOT be passed as part of the input configuration.
     */
    String LISTENING_PORT = ChannelFrameworkConstants.LISTENING_PORT;

    /**
     * Determines whether Include/Exclude Access Lists are to be considered as
     * case insensitive.
     */
    String CASE_INSENSITIVE_HOSTNAMES = "caseInsensitiveHostnames";

}
