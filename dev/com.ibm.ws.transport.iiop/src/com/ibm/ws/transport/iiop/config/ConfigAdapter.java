/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
/*
 * Some of the code was derived from code supplied by the Apache Software Foundation licensed under the Apache License, Version 2.0.
 */
package com.ibm.ws.transport.iiop.config;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.omg.CORBA.ORB;

import com.ibm.ws.transport.iiop.spi.IIOPEndpoint;
import com.ibm.ws.transport.iiop.spi.SubsystemFactory;

/**
 * Translates TSS and CSS configurations into CORBA startup args and properties.
 */
public interface ConfigAdapter {
    /**
     * Create an ORB for a CORBABean server context.
     * 
     * @param endpoints TODO
     * @param subsystemFactories TODO
     * @param server The CORBABean that owns this ORB's configuration.
     * 
     * @return An ORB instance configured for the CORBABean.
     * @exception ConfigException
     */
    public ORB createServerORB(Map<String, Object> config, Map<String, Object> extraConfig, List<IIOPEndpoint> endpoints, Collection<SubsystemFactory> subsystemFactories) throws ConfigException;

    /**
     * Create an ORB for a CSSBean client context.
     * 
     * @param client The configured CSSBean used for access.
     * @param subsystemFactories TODO
     * 
     * @return An ORB instance configured for this client access.
     * @exception ConfigException
     */
    public ORB createClientORB(Map<String, Object> clientProperties, Collection<SubsystemFactory> subsystemFactories) throws ConfigException;
}
