/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.collective.security;

import java.util.UUID;

/**
 * Service interface to determine the collective's UUID and name.
 * <p>
 * The collective's UUID and name are stored on disk and will be retrieved automatically.
 * When the service is registered, the collective UUID is present.
 */
public interface CollectiveUUID {

    /**
     * Get the collective's UUID.
     * 
     * @return the collective UUID. {@code null} is not returned.
     */
    UUID getCollectiveUUID();

    /**
     * Get the collective's name.
     * 
     * @return the collective name. {@code null} is not returned.
     */
    String getCollectiveName();

}
