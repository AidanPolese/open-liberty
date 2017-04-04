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
package com.ibm.ws.jca.internal;

import java.io.Serializable;
import java.util.Properties;

/**
 * Objects of this class are serialized and used to store the information required to recreate an activation
 * spec during recovery
 */
public class ActivationConfig implements Serializable {

    private static final long serialVersionUID = -3812882135080246095L;

    private Properties activationConfigProps = null;

    private final String destinationRef;

    private String authenticationAlias = null;

    private final String applicationName;

    /**
     * @param activationConfigProps
     * @param destinationRef
     * @param authenticationAlias
     */
    public ActivationConfig(Properties activationConfigProps, String destinationRef, String authenticationAlias, String appName) {
        this.activationConfigProps = activationConfigProps;
        this.destinationRef = destinationRef;
        this.authenticationAlias = authenticationAlias;
        this.applicationName = appName;
    }

    /**
     * @return the activationConfigProps
     */
    public Properties getActivationConfigProps() {
        return activationConfigProps;
    }

    /**
     * @return id of a destination
     */
    public String getDestinationRef() {
        return destinationRef;
    }

    /**
     * @return the authenticationAlias
     */
    public String getAuthenticationAlias() {
        return authenticationAlias;
    }

    /**
     * @return the applicationName
     */
    public String getApplicationName() {
        return applicationName;
    }

}
