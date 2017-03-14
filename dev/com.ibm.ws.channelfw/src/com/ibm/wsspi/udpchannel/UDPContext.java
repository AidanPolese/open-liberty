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
//-------------------------------------------------------------------------------
package com.ibm.wsspi.udpchannel;

import java.net.InetAddress;

/**
 * A context object encapsulating data related to a UDPChannel.
 * 
 */
public interface UDPContext {

    /**
     * Get the Read Object for this interface
     * 
     * @return UDPReadRequestContext
     */
    UDPReadRequestContext getReadInterface();

    /**
     * Get the Write Object for this interface
     * 
     * @return UDPWriteRequestContext
     */
    UDPWriteRequestContext getWriteInterface();

    /**
     * Get the InetAddress for the locally bound interface.
     * 
     * @return InetAddress
     */
    InetAddress getLocalAddress();

    /**
     * Get the port number for the locally bound interface.
     * 
     * @return int
     */
    int getLocalPort();

}
