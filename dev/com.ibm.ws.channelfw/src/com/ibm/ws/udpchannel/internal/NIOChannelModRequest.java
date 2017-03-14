//-------------------------------------------------------------------------------
//IBM Confidential OCO Source Material
//5724-I63, 5724-H88, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2003, 2005
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.
//
//Change History:
//
//Change ID     Author    Abstract
//---------     --------  -------------------------------------------------------
//d306341		mjohn256  Add RAS logging support to UDP Channel.
//-------------------------------------------------------------------------------
package com.ibm.ws.udpchannel.internal;

import java.nio.channels.DatagramChannel;

/**
 * @author mjohnson
 */
public class NIOChannelModRequest {
    //
    // What is this request?
    //
    public static final int ADD_REQUEST = 1;
    public static final int REMOVE_REQUEST = 2;
    public static final int MODIFY_REQUEST = 3;

    //
    // If it is a modify request, do I need to AND with the mask
    // or OR with the mask?
    //
    public static final int AND_OPERATOR = 1;
    public static final int OR_OPERATOR = 2;

    private int requestType = 0;
    private int interestMask = 0;
    private int interestOperand = 0;
    private DatagramChannel channel = null;
    private UDPNetworkLayer networkLayer = null;

    NIOChannelModRequest(int requestType, DatagramChannel channel, int interestMask, UDPNetworkLayer networkLayer) {
        init(requestType, channel, interestMask, networkLayer);
    }

    NIOChannelModRequest(int requestType, DatagramChannel channel, int interestMask, int interestOperand, UDPNetworkLayer networkLayer) {
        init(requestType, channel, interestMask, networkLayer);

        this.interestOperand = interestOperand;
    }

    private void init(int type, DatagramChannel channel, int mask, UDPNetworkLayer layer) {
        this.requestType = type;
        this.interestMask = mask;
        this.channel = channel;
        this.networkLayer = layer;
    }

    /**
     * @return Returns the channel.
     */
    public DatagramChannel getChannel() {
        return channel;
    }

    /**
     * @return Returns the interestOps.
     */
    public int getInterestMask() {
        return interestMask;
    }

    /**
     * @return Returns the networkLayer.
     */
    public UDPNetworkLayer getNetworkLayer() {
        return networkLayer;
    }

    /**
     * @return Returns the requestType.
     */
    public int getRequestType() {
        return requestType;
    }

    /**
     * @return Returns the interestOperator.
     */
    public int getInterestOperator() {
        return interestOperand;
    }
}
