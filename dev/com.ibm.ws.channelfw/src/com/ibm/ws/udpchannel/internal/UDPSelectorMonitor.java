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

import java.io.IOException;
import java.nio.channels.DatagramChannel;

/**
 * @author mjohnson
 */
public interface UDPSelectorMonitor {
    void setChannel(DatagramChannel channel, UDPNetworkLayer udpPort) throws IOException;

    void removeChannel(DatagramChannel channel);

}
