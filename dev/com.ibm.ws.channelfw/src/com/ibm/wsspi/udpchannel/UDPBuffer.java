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

import java.net.SocketAddress;

import com.ibm.wsspi.bytebuffer.WsByteBuffer;

/**
 * @author mjohnson
 */
public interface UDPBuffer {
    /**
     * Returns the read WsByteBuffer associated with this read request.
     * 
     * @return WsByteBuffer
     */
    WsByteBuffer getBuffer();

    /**
     * Returns the address of the sending client associated with this read request.
     * 
     * @return SocketAddress
     */
    SocketAddress getAddress();

    /**
     * Returns the object back to the object pool.
     */
    void release();

}
