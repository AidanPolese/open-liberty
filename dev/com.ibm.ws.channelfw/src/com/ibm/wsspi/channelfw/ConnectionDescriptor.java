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
// 11/27/06 wigger      410109          store connection info in VC
// 02/22/07 wigger      LIDB44463-8     store connection info in VC
// 11/26/07 wigger      457142          Performance: don't access InetAddress methods until necessary                                                                                                                                                                                                   

package com.ibm.wsspi.channelfw;

import java.net.InetAddress;

/**
 * Descriptor class used to pass around information about the local and remote
 * ends of the
 * connection.
 */
public interface ConnectionDescriptor {

    /**
     * Get the name of the host at the remote end of the connection.
     * 
     * @return remote host name
     */
    String getRemoteHostName();

    /**
     * Set the name of the host at the remote end of the connection.
     * 
     * @param s
     *            remote host name
     */
    void setRemoteHostName(String s);

    /**
     * Get the address of the host at the remote end of the connection.
     * 
     * @return remote host address
     */
    String getRemoteHostAddress();

    /**
     * Set the address of the host at the remote end of the connection.
     * 
     * @param s
     *            remote host address
     */
    void setRemoteHostAddress(String s);

    /**
     * Get the name of the host at the local end of the connection.
     * 
     * @return local host name
     */
    String getLocalHostName();

    /**
     * Set the name of the host at the local end of the connection.
     * 
     * @param s
     *            local host name
     */
    void setLocalHostName(String s);

    /**
     * Get the address of the host at the local end of the connection.
     * 
     * @return local host address
     */
    String getLocalHostAddress();

    /**
     * Set the address of the host at the local end of the connection.
     * 
     * @param s
     *            local host address
     */
    void setLocalHostAddress(String s);

    /**
     * Set the remote host name, remote host address, local host name, and
     * local host address for this connnection.
     * 
     * @param s1
     *            remote host name
     * @param s2
     *            remote host address
     * @param s3
     *            local host name
     * @param s4
     *            local host address
     */
    void setAll(String s1, String s2, String s3, String s4);

    /**
     * set the remote InetAddress and local Address (instead of remote host name,
     * remote host address, local host name, and local host address) for this
     * connnection.
     * 
     * @param remote
     *            InetAddress
     * @param local
     *            InetAddress
     */
    void setAddrs(InetAddress remote, InetAddress local);

}
