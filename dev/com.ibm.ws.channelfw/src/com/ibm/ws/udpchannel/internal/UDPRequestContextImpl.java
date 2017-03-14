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

import com.ibm.wsspi.udpchannel.UDPContext;

/**
 * Basic UDP request context object.
 * 
 * @author mjohnson
 */
public abstract class UDPRequestContextImpl {

    private UDPConnLink udpConnLink = null;
    private WorkQueueManager workQueueMgr = null;

    /**
     * Constructor.
     * 
     * @param udpContext
     * @param wqm
     */
    public UDPRequestContextImpl(UDPConnLink udpContext, WorkQueueManager wqm) {
        this.udpConnLink = udpContext;
        this.workQueueMgr = wqm;
    }

    /**
     * Access the UDP context object.
     * 
     * @return UDPContext
     */
    public UDPContext getInterface() {
        return this.udpConnLink;
    }

    /**
     * Access the UDP connection link object.
     * 
     * @return UDPConnLink
     */
    public UDPConnLink getConnLink() {
        return this.udpConnLink;
    }

    /**
     * Query whether this context is a read or write one.
     * 
     * @return boolean
     */
    public abstract boolean isRead();

    /**
     * Access the work queue manager for this context.
     * 
     * @return WorkQueueManager
     */
    protected WorkQueueManager getWorkQueueManager() {
        return this.workQueueMgr;
    }
}
