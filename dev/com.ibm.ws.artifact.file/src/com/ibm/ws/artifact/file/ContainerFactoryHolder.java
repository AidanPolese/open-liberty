/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.artifact.file;

import org.osgi.framework.BundleContext;

import com.ibm.wsspi.artifact.factory.ArtifactContainerFactory;

/**
 * DS safe way for file artifacts to obtain the container factory
 */
public interface ContainerFactoryHolder {
    /**
     * Get the ContainerFactory from this holder.<p>
     * Because the factory is a service, if someone removes the supplying bundle, without
     * first providing an alternate implementation, then we will explode with an IllegalStateException.<br>
     * It is not expected that this will happen.
     * 
     * @return the containerFactory
     * @throws IllegalStateException if the ContainerFactory has gone away.
     */
    public ArtifactContainerFactory getContainerFactory();

    /**
     * Get the BundleContext from this holder.<p>
     * 
     * @return the containerFactory
     * @throws IllegalStateException if the FileContainerFactory component has been deactivated by DS.
     */
    public BundleContext getBundleContext();
}
