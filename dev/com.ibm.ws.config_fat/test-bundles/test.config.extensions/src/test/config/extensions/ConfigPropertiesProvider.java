/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011, 2013
 *
 * The source code for this program is not published or otherwise divested of
 * its trade secrets, irrespective of what has been deposited with the U.S.
 * Copyright Office.
 */
package test.config.extensions;

import java.util.Dictionary;

interface ConfigPropertiesProvider {
    /**
     * Waits for a call to the ManagedServiceFactory with
     * the specified config id
     * 
     * 
     * @param pidStartsWith
     * @return the properties that the ManagedServiceFactory was called with
     */
    Dictionary<String, ?> getPropertiesForId(String id);
}
