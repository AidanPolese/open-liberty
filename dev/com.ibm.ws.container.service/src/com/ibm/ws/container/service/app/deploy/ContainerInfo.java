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
package com.ibm.ws.container.service.app.deploy;

import com.ibm.wsspi.adaptable.module.Container;

/**
 * Contains information about a container
 */
public interface ContainerInfo {
    public enum Type {
        MANIFEST_CLASSPATH,
        WEB_INF_CLASSES,
        WEB_INF_LIB,
        EAR_LIB,
        WEB_MODULE,
        EJB_MODULE,
        CLIENT_MODULE,
        RAR_MODULE,
        JAR_MODULE,
        SHARED_LIB
    }

    /**
     * Returns the container type
     */
    public Type getType();

    /**
     * Returns the container name
     */
    public String getName();

    /**
     * Returns the container object
     */
    public Container getContainer();
}
