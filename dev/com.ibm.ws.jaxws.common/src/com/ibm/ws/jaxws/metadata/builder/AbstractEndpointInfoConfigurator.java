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
package com.ibm.ws.jaxws.metadata.builder;

/**
 * Provide basic implementation for the EndpointInfoConfigurator
 */
public abstract class AbstractEndpointInfoConfigurator implements EndpointInfoConfigurator {

    protected Phase phase;

    public AbstractEndpointInfoConfigurator() {
        //if phase if not specified, use PRE_PROCESS_ANNOTATION as default
        this.phase = EndpointInfoConfigurator.Phase.PRE_PROCESS_ANNOTATION;
    }

    public AbstractEndpointInfoConfigurator(Phase phase) {
        this.phase = phase;
    }

    @Override
    public Phase getPhase() {
        return phase;
    }

}
