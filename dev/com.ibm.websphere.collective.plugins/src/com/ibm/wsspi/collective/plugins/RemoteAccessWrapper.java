/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2014,2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.collective.plugins;


/**
 * Wraps a remote access connection object, to hide the underlying implementation details of the connection.
 * 
 * @ibm-spi
 */
public interface RemoteAccessWrapper {

    /**
     * Returns the actual remote access object.
     */
    public Object getRemoteAccessObject();

    /**
     * Ends the session of this remote access wrapper. The remote access object cannot be used after its session has ended.
     */
    public void endSession();

    /**
     * The target host name. This value cannot be null.
     */
    public String getHostName();

    /**
     * The target Liberty user directory. This value may be null, if we're connecting at a host-level.
     */
    public String getUserDir();

    /**
     * The target Liberty server name. This value may be null, if we're connecting at a host-level.
     */
    public String getServerName();

}
