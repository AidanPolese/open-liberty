//IBM Confidential OCO Source Material
//5724-i63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 2003, 2004
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.
// 
//%Z% %I% %W% %G% %U% [%H% %T%]

package com.ibm.websphere.channelfw;

/**
 * The methods of this interface describe requirements needed to
 * determine an appropriate endpoint to connect to. They are used
 * in conjunction with request data to determine the appropriate endpoint.
 * Comparison preferences are chain name first, then virtual host, then
 * factory classes, then channel accessors, then ssl, and finally local.
 * Each one narrows down the list of endpoints.
 */
public interface CFEndPointCriteria {

    /**
     * Access the interface class that will be used to communicate
     * with the first channel of the outbound chain that will be used
     * to connect to the endpoint.
     * 
     * @return interface class of first outbound channel
     */
    Class<?> getChannelAccessor();

    /**
     * This is the name of the inbound chain on the server side that the client
     * wants to talk to.
     * 
     * @return String
     */
    String getChainName();

    /**
     * Access an optional ordered list of channel factories that will
     * be needed to make the connection. This is where things like
     * tunneling and SSL can be specified. If no channel factories
     * must be specified, then null should return.
     * 
     * @return list of channel factories needed for connection
     */
    Class<?>[] getOptionalChannelFactories();

    /**
     * Specifies whether the chain to be searched for must have SSL capabilities.
     * 
     * @return true of Chain must have SSL capabilities
     */
    boolean isSSLRequired();

    /**
     * Query the optional virtual host target of this criteria.
     * 
     * @return String, null if not necessary
     */
    String getVirtualHost();
}
