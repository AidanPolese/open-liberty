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
// 11/27/06 wigger      410109            store connection info in VC
// 02/22/07 wigger      LIDB4463-8        store connection info in VC
// 11/26/07 wigger      457142            Performance: don't access InetAddress methods until necessary                                                                                                                                                                                                   

package com.ibm.ws.channelfw.internal;

import java.net.InetAddress;

import com.ibm.wsspi.channelfw.ConnectionDescriptor;

/**
 * Implementation of the connection descriptor interface for passing around the
 * information about the local and remote address values of a connection.
 */
public class ConnectionDescriptorImpl implements ConnectionDescriptor {

    private String remoteHostName = null;
    private String localHostName = null;
    private String remoteHostAddress = null;
    private String localHostAddress = null;

    private InetAddress addrLocal = null;
    private InetAddress addrRemote = null;

    /**
     * Constructor.
     */
    public ConnectionDescriptorImpl() {
        // nothing to do
    }

    /**
     * Constructor with string address information.
     * 
     * @param _rhn
     * @param _rha
     * @param _lhn
     * @param _lha
     */
    public ConnectionDescriptorImpl(String _rhn, String _rha, String _lhn, String _lha) {
        this.remoteHostName = _rhn;
        this.remoteHostAddress = _rha;
        this.localHostName = _lhn;
        this.localHostAddress = _lha;
    }

    /**
     * Constructor with InetAddress values.
     * 
     * @param _remote
     * @param _local
     */
    public ConnectionDescriptorImpl(InetAddress _remote, InetAddress _local) {
        this.addrRemote = _remote;
        this.addrLocal = _local;
    }

    /**
     * @see com.ibm.wsspi.channelfw.ConnectionDescriptor#getRemoteHostName()
     */
    public String getRemoteHostName() {
        if ((remoteHostName == null) && (addrRemote != null)) {
            this.remoteHostName = this.addrRemote.getHostName();
        }
        return this.remoteHostName;
    }

    /**
     * @see com.ibm.wsspi.channelfw.ConnectionDescriptor#setRemoteHostName(String)
     */
    public void setRemoteHostName(String _newValue) {
        this.remoteHostName = _newValue;
    }

    /**
     * @see com.ibm.wsspi.channelfw.ConnectionDescriptor#getRemoteHostAddress()
     */
    public String getRemoteHostAddress() {
        if ((remoteHostAddress == null) && (addrRemote != null)) {
            this.remoteHostAddress = this.addrRemote.getHostAddress();
        }
        return this.remoteHostAddress;
    }

    /**
     * @see com.ibm.wsspi.channelfw.ConnectionDescriptor#setRemoteHostAddress(String)
     */
    public void setRemoteHostAddress(String _newValue) {
        this.remoteHostAddress = _newValue;
    }

    /**
     * @see com.ibm.wsspi.channelfw.ConnectionDescriptor#getLocalHostName()
     */
    public String getLocalHostName() {
        if ((localHostName == null) && (addrLocal != null)) {
            this.localHostName = this.addrLocal.getHostName();
        }
        return this.localHostName;
    }

    /**
     * @see com.ibm.wsspi.channelfw.ConnectionDescriptor#setLocalHostName(String)
     */
    public void setLocalHostName(String _newValue) {
        this.localHostName = _newValue;
    }

    /**
     * @see com.ibm.wsspi.channelfw.ConnectionDescriptor#getLocalHostAddress()
     */
    public String getLocalHostAddress() {
        if ((localHostAddress == null) && (addrLocal != null)) {
            this.localHostAddress = this.addrLocal.getHostAddress();
        }
        return this.localHostAddress;
    }

    /**
     * @see com.ibm.wsspi.channelfw.ConnectionDescriptor#setLocalHostAddress(String)
     */
    public void setLocalHostAddress(String _newValue) {
        this.localHostAddress = _newValue;
    }

    /**
     * Set all of the stored information based on the input strings.
     * 
     * @param _rhn
     * @param _rha
     * @param _lhn
     * @param _lha
     */
    public void setAll(String _rhn, String _rha, String _lhn, String _lha) {
        this.remoteHostName = _rhn;
        this.remoteHostAddress = _rha;
        this.localHostName = _lhn;
        this.localHostAddress = _lha;
        this.addrLocal = null;
        this.addrRemote = null;
    }

    /**
     * Set the address information based on the input InetAddress objects.
     * 
     * @param _remote
     * @param _local
     */
    public void setAddrs(InetAddress _remote, InetAddress _local) {
        this.addrRemote = _remote;
        this.addrLocal = _local;
        this.remoteHostName = null;
        this.remoteHostAddress = null;
        this.localHostName = null;
        this.localHostAddress = null;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuilder sb = new StringBuilder(256);
        sb.append("remote host name: ").append(getRemoteHostName());
        sb.append(" remote host address: ").append(getRemoteHostAddress());
        sb.append(" local host name: ").append(getLocalHostName());
        sb.append(" local host address: ").append(getLocalHostAddress());
        return sb.toString();
    }

}
