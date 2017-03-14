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

import java.net.SocketAddress;

import com.ibm.wsspi.bytebuffer.WsByteBuffer;
import com.ibm.wsspi.udpchannel.UDPBuffer;

/**
 * @author mjohnson
 */
public class UDPBufferImpl implements UDPBuffer {
    private WsByteBuffer buffer = null;
    private SocketAddress address = null;
    private UDPBufferFactory udpBufferFactory = null;

    /**
     * Constructor.
     */
    public UDPBufferImpl() {
        // do nothing
    }

    /**
     * Constructor.
     * 
     * @param factory
     */
    public UDPBufferImpl(UDPBufferFactory factory) {
        this.udpBufferFactory = factory;
    }

    /*
     * @see com.ibm.wsspi.udpchannel.UDPBuffer#getBuffer()
     */
    @Override
    public WsByteBuffer getBuffer() {
        return this.buffer;
    }

    /*
     * @see com.ibm.wsspi.udpchannel.UDPBuffer#getAddress()
     */
    @Override
    public SocketAddress getAddress() {
        return this.address;
    }

    protected void set(WsByteBuffer buffer, SocketAddress address) {
        this.buffer = buffer;
        this.address = address;
    }

    /*
     * @see com.ibm.wsspi.udpchannel.UDPBuffer#release()
     */
    @Override
    public void release() {
        if (udpBufferFactory != null) {
            udpBufferFactory.release(this);
        }
    }

    /**
     * Clear the contents of this buffer.
     */
    public void clear() {
        this.buffer = null;
        this.address = null;
    }
}
