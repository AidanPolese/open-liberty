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
package com.ibm.ws.cdi.interfaces;

/**
 * The type of an archive
 */
public enum ArchiveType {
    MANIFEST_CLASSPATH,
    WEB_INF_LIB,
    EAR_LIB,
    WEB_MODULE,
    EJB_MODULE,
    CLIENT_MODULE,
    RAR_MODULE,
    JAR_MODULE,
    SHARED_LIB,
    ON_DEMAND_LIB, //hold random classes that needs to be in a bean archive
    RUNTIME_EXTENSION;
}
