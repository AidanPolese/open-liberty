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

import java.io.IOException;

import com.ibm.wsspi.channelfw.VirtualConnection;

/**
 * A callback object whose methods are called by the UDPChannel
 * upon the completion (or error) of a writeAsynch request.
 */
public interface UDPWriteCompletedCallback {
    /**
     * Called when the request has completeted successfully.
     * 
     * @param vc associated with this request.
     * @param wsc associated with this request.
     */
    void complete(VirtualConnection vc, UDPWriteRequestContext wsc);

    /**
     * Called back if an exception occurrs while processing the request.
     * The implementer of this interface can then decide how to handle this
     * exception.
     * 
     * @param vc associated with this request.
     * @param wsc associated with this request.
     * @param ioe The exception.
     */
    void error(VirtualConnection vc, UDPWriteRequestContext wsc, IOException ioe);

}
