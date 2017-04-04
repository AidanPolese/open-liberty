/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corporation 2011, 2013
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */

package com.ibm.wsspi.anno.service;

public interface AnnotationService_Logging {
    // Root logging constant.
    public static final String ANNO_LOGGER = "com.ibm.ws.anno";

    // Function categories ... these match entire packages.
    public static final String ANNO_LOGGER_SERVICE = ANNO_LOGGER + ".service";

    public static final String ANNO_LOGGER_TARGETS = ANNO_LOGGER + ".target";
    public static final String ANNO_LOGGER_TARGETS_VISITOR = ANNO_LOGGER_TARGETS + ".visitor";

    public static final String ANNO_LOGGER_SOURCE = ANNO_LOGGER + ".source";
    public static final String ANNO_LOGGER_UTIL = ANNO_LOGGER + ".util";
    public static final String ANNO_LOGGER_INFO = ANNO_LOGGER + ".info";

    // Detail categories ... these cross the boundaries of the function categories.
    public static final String ANNO_LOGGER_STATE = ANNO_LOGGER + ".state";
    public static final String ANNO_LOGGER_SCAN = ANNO_LOGGER + ".scan";
}
