//-------------------------------------------------------------------------------
//IBM Confidential OCO Source Material
//5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2003, 2006, 2007
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.
//
//Change History:
//
//Change ID     Author    Abstract
//---------     --------  -------------------------------------------------------
//315734        clanzen   Add property for endPointName.
//-------------------------------------------------------------------------------
package com.ibm.wsspi.udpchannel;

import com.ibm.ws.channelfw.internal.ChannelFrameworkConstants;

/**
 * This interface describes the constants used for configuring a UDP Channel.
 * 
 */
public interface UDPConfigConstants {

    /**
     * Host name that an inbound channel will use for listening
     */
    String HOST_NAME = ChannelFrameworkConstants.HOST_NAME;

    /**
     * port on which the inbound channel will listen
     */
    String PORT = ChannelFrameworkConstants.PORT;

    String ADDR_EXC_LIST = "addressExcludeList";

    String ADDR_INC_LIST = "addressIncludeList";

    /**
     * The size of the udp socket sending buffer
     */
    String SEND_BUFF_SIZE = "sendBufferSizeSocket";

    /**
     * The size of the udp socket receiving buffer
     */
    String RCV_BUFF_SIZE = "receiveBufferSizeSocket";

    /**
     * The size of the UDP buffer to be sent up the channel chain.
     */
    String CHANNEL_RCV_BUFF_SIZE = "receiveBufferSizeChannel";

    /**
     * Value used to determine if every conn link gets its own worker thread instance.
     */
    String CHANNEL_FACTORY_UNIQUE_WORKER_THREADS = "uniqueWorkerThreads";

    /**
     * Value used to store the name of the WAS end point in the channel properties.
     */
    String ENDPOINT_NAME = "endPointName";

    /**
     * Minimum Port Value
     */
    int PORT_MIN = 0;

    /**
     * Maximum Port Value
     */
    int PORT_MAX = 65535;

    /**
     * Maximum UDP Packet Size
     */
    int MAX_UDP_PACKET_SIZE = 65535;

    /**
     * Minimum UDP receive buffer size
     */
    int RECEIVE_BUFFER_SIZE_MIN = 4;

    /**
     * Maximum UDP receive buffer size
     */
    int RECEIVE_BUFFER_SIZE_MAX = 16777216; // 16 Meg

    /**
     * Minimum UDP send buffer size
     */
    int SEND_BUFFER_SIZE_MIN = 4;

    /**
     * Maximum UDP send buffer size
     */
    int SEND_BUFFER_SIZE_MAX = 16777216; // 16 Meg

    /**
     * The configured host interface. This is used in the connection ready callback to
     * identify which configured UDP channel chain this is. This is retrieved from the vc statemap.
     */
    String CONFIGURED_HOST_INTERFACE_VC_MAP = "UDPConfiguredListeningHost";

    /**
     * The configured port. This is used in the connection ready callback to
     * identify which configured UDP channel chain this is. This is retrieved from the vc statemap.
     */
    String CONFIGURED_PORT_VC_MAP = "UDPConfiguredListeningPort";

}
