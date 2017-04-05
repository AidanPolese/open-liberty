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
package com.ibm.ws.jca.utils.metagen.internal;

/**
 *
 */
public class InternalConstants {
    public static final String WLP_RA_XML_FILE_NAME = "wlp-ra.xml";
    public static final String RA_XML_FILE_NAME = "ra.xml";
    public static final String METATYPE_XML_FILE_NAME = "metatype.xml";
    public static final String METATYPE_PROPERTIES_FILE_NAME = "metatype.properties";

    public static final String TRACE_LEVEL_DEBUG = "debug";
    public static final String TRACE_LEVEL_WARNING = "warning";
    public static final String TRACE_LEVEL_ENTRY_EXIT = "entry-exit";
    public static final String TRACE_LEVEL_ALL = "all";
    public static final String TRACE_LEVEL_NONE = "none";

    /**
     * Prefix that should be appending at the start of all generated pids
     * to ensure there aren't any conflicting pids with other components
     * or within our own component. This comes as a result of someone
     * using an adapter name of com.ibm.ws.jca on something like a JMS
     * connection factory with would resolve to com.ibm.ws.jca.jmsConnectionFactory
     * which is an established pid within the JCA component.
     */
    public static final String JCA_UNIQUE_PREFIX = "com.ibm.ws.jca";

    public static final String RECOMMEND_AUTH_ALIAS_MSG = "It is recommended to use a container managed authentication alias instead of configuring this property";
}
