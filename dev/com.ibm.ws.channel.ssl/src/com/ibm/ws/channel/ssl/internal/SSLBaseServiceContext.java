//IBM Confidential OCO Source Material
//5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70. (C) COPYRIGHT International Business Machines Corp. 2003, 2007
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.
//
//@(#) 1.6 SERV1/ws/code/ssl.channel.impl/src/com/ibm/ws/ssl/channel/impl/SSLBaseServiceContext.java, WAS.channel.ssl, WASX.SERV1, pp0919.25 4/6/07 09:34:52 [5/15/09 18:21:28]
// Change History:
// Date     UserId      Defect          Description
// --------------------------------------------------------------------------------
// 060317   leeja       PK21690         Handle HS_NEED_WRAP after unwrap() call
// 070404   leeja       LIDB2924-15     Remove JSSE2 usage

package com.ibm.ws.channel.ssl.internal;

import com.ibm.wsspi.bytebuffer.WsByteBuffer;
import com.ibm.wsspi.channelfw.VirtualConnection;
import com.ibm.wsspi.tcpchannel.TCPConnectionContext;
import com.ibm.wsspi.tcpchannel.TCPRequestContext;

/**
 * This class will contain the methods common between the SSLReadServiceContext
 * and the SSLWriteServiceContext
 */
public abstract class SSLBaseServiceContext implements TCPRequestContext {

    /** Service context. */
    private SSLConnectionLink connectionLink = null;
    /** Reference to array of byte buffers. */
    private WsByteBuffer[] buffers;
    /** Array used as a place holder and filled in when setBuffer called. */
    private WsByteBuffer[] defaultBuffers = new WsByteBuffer[1];
    /** Reference to the SSL channel configuration. */
    private SSLChannelData config = null;
    /** Reference to the VC. */
    private VirtualConnection myVC = null;
    /** Reference to the VC hash for logging. */
    private int myVCHashCode = 0;

    /**
     * Constructor.
     * 
     * @param connLink
     */
    public SSLBaseServiceContext(SSLConnectionLink connLink) {
        this.connectionLink = connLink;
        this.config = connLink.getChannel().getConfig();
        this.myVC = this.connectionLink.getVirtualConnection();
        this.myVCHashCode = this.myVC.hashCode();
    }

    /**
     * Constructor used for test purposes only.
     */
    public SSLBaseServiceContext() {
        // nothing to do
    }

    /**
     * Return the link.
     * 
     * @return TCPConnectionContext
     */
    public TCPConnectionContext getInterface() {
        return this.connectionLink;
    }

    /*
     * @see com.ibm.wsspi.tcpchannel.TCPRequestContext#clearBuffers()
     */
    public void clearBuffers() {
        if (this.buffers != null) {
            for (int i = 0; i < this.buffers.length; ++i) {
                if (this.buffers[i] != null)
                    this.buffers[i].clear();
            }
        }
    }

    /*
     * @see com.ibm.wsspi.tcpchannel.TCPRequestContext#getBuffers()
     */
    public WsByteBuffer[] getBuffers() {
        return this.buffers;
    }

    /*
     * @see com.ibm.wsspi.tcpchannel.TCPRequestContext#setBuffers(com.ibm.wsspi.bytebuffer.WsByteBuffer[])
     */
    public void setBuffers(WsByteBuffer[] bufs) {
        this.buffers = bufs;
    }

    /*
     * @see com.ibm.wsspi.tcpchannel.TCPRequestContext#getBuffer()
     */
    public WsByteBuffer getBuffer() {
        if (this.buffers == null) {
            return null;
        }
        return (this.buffers[0]);
    }

    /*
     * @see com.ibm.wsspi.tcpchannel.TCPRequestContext#setBuffer(com.ibm.wsspi.bytebuffer.WsByteBuffer)
     */
    public void setBuffer(WsByteBuffer buf) {
        if (buf != null) {
            this.buffers = this.defaultBuffers;
            this.buffers[0] = buf;
        } else {
            this.buffers = null;
        }
    }

    /**
     * Get access to the SSL connection link.
     * 
     * @return SSLConnectionLink
     */
    protected SSLConnectionLink getConnLink() {
        return this.connectionLink;
    }

    /**
     * Get access to the virtual connection object for this connection.
     * 
     * @return VirtualConnection
     */
    protected VirtualConnection getVC() {
        return this.myVC;
    }

    /**
     * Get access to the hash code of this connection's virtual connection.
     * 
     * @return int
     */
    protected int getVCHash() {
        return this.myVCHashCode;
    }

    /**
     * Get access to the channel configuration for this connection.
     * 
     * @return SSLChannelData
     */
    protected SSLChannelData getConfig() {
        return this.config;
    }
}
