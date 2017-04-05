/*
* IBM Confidential
*
* OCO Source Materials
*
* WLP Copyright IBM Corp. 2016
*
* The source code for this program is not published or otherwise divested
* of its trade secrets, irrespective of what has been deposited with the
* U.S. Copyright Office.
*/
package com.ibm.ws.security.wim;

/**
 *
 */
public class SupportedEntityConfig {

    private final String defaultParent;
    private final String[] rdnProperties;

    /**
     * @param defaultParent
     * @param rdnProperties
     */
    public SupportedEntityConfig(String defaultParent, String[] rdnProperties) {
        this.defaultParent = defaultParent;
        this.rdnProperties = rdnProperties;
    }

    /**
     * @return the defaultParent
     */
    public String getDefaultParent() {
        return defaultParent;
    }

    /**
     * @return the rdnProperties
     */
    public String[] getRdnProperties() {
        return rdnProperties;
    }

}
