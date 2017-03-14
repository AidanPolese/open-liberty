/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2009
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */
package com.ibm.websphere.channelfw;

import java.util.List;

/**
 * Manager class that holds inbound endpoint definitions. These are
 * host/port combinations that represent inbound listening sockets.
 */
public interface EndPointMgr {

    /**
     * Create a new endpoint definition with the input parameters.
     * 
     * @param name
     * @param host
     * @param port
     * @return EndPointInfo
     * @throws IllegalArgumentException if input values are incorrect
     */
    EndPointInfo defineEndPoint(String name, String host, int port);

    /**
     * Delete the endpoint that matches the provided name.
     * 
     * @param name
     */
    void removeEndPoint(String name);

    /**
     * Query any existing endpoint defined with the input name.
     * 
     * @param name
     * @return EndPointInfo, null if not found
     */
    EndPointInfo getEndPoint(String name);

    /**
     * Query all currently defined end points.
     * 
     * @return List<EndPointInfo>, never null but might be empty
     */
    List<EndPointInfo> getEndsPoints();

    /**
     * Query the possible list of endpoints that match the provided
     * address and port. A wildcard address is * and a wildcard port
     * is 0.
     * 
     * @param address
     * @param port
     * @return List<EndPointInfo>, never null but might be empty
     */
    List<EndPointInfo> getEndPoints(String address, int port);
}
