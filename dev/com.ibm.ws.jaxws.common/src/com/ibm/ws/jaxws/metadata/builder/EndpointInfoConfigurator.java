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
package com.ibm.ws.jaxws.metadata.builder;

import com.ibm.ws.jaxws.metadata.EndpointInfo;
import com.ibm.wsspi.adaptable.module.UnableToAdaptException;

/**
 * Configure the EndpointInfo, the interface provides the abstraction for endpoint configurations from
 * different sources, including annotations, web.xml, webservices.xml, and vendor deployment plan.
 */
public interface EndpointInfoConfigurator {
    /**
     * Prepare for parsing.
     * 
     * @param context
     * @UnableToAdaptException
     */
    void prepare(EndpointInfoBuilderContext context, EndpointInfo endpointInfo) throws UnableToAdaptException;

    /**
     * Configure the EndpointInfo.
     * 
     * @param context
     * @UnableToAdaptException
     */
    void config(EndpointInfoBuilderContext context, EndpointInfo endpointInfo) throws UnableToAdaptException;

    /**
     * Return the phase for the configurator
     * 
     * @return
     */
    Phase getPhase();

    /*
     * Predefine Phases
     */
    public enum Phase {
        PRE_PROCESS_ANNOTATION,
        PROCESS_ANNOTATION,
        POST_PROCESS_ANNOTATION,
        PRE_PROCESS_DESCRIPTOR,
        PROCESS_DESCRIPTOR,
        POST_PROCESS_DESCRIPTOR;
    }

}
