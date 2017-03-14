/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.websphere.endpoint;

/**
 * EndPointInfoMBean represents a defined endpoint within the channel framework. Use this interface to
 * access the host, name, and port attributes of the channel framework endpoint.
 * <p>
 * MBeans of this type can be queried using the following filter <p>
 * &nbsp;&nbsp;WebSphere:feature=channelfw,type=endpoint,&#42;
 * 
 * @ibm-api
 */
public interface EndPointInfoMBean {

    /**
     * Query the name of this endpoint.
     * 
     * @return String
     */
    public String getName();

    /**
     * Query the host assigned to this endpoint.
     * 
     * @return String
     */
    public String getHost();

    /**
     * Query the port assigned to this endpoint.
     * 
     * @return int
     */
    public int getPort();

}
