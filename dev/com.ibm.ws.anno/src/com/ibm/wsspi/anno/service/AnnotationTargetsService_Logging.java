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

/**
 * <p>Logging constants for the annotations service.</p>
 * 
 * <p>All loggers obtain from the root logger, {@link #ANNO_LOGGER}.
 * While this logger may be used to enable all annotations logging,
 * (<code>com.ibm.ws.anno.*=FINER</code>), this is not recommended
 * unless absolutely necessary. In particular, enabling the visitor
 * and scan logging (<code>com.ibm.ws.anno.visitor=FINER</code> and
 * <code>com.ibm.ws.anno.scan=FINER</code>) will produces copious
 * logging output.</p>
 */
public interface AnnotationTargetsService_Logging {

    public static final String ANNO_LOGGER = "com.ibm.ws.anno";

    public static final String ANNO_LOGGER_SERVICE = ANNO_LOGGER + ".service";

    public static final String ANNO_LOGGER_TARGETS = ANNO_LOGGER + ".target";
    public static final String ANNO_LOGGER_TARGETS_VISITOR = ANNO_LOGGER_TARGETS + ".visitor";

    public static final String ANNO_LOGGER_SOURCE = ANNO_LOGGER + ".source";

    public static final String ANNO_LOGGER_UTIL = ANNO_LOGGER + ".util";

    public static final String ANNO_LOGGER_STATE = ANNO_LOGGER + ".state";

    public static final String ANNO_LOGGER_SCAN = ANNO_LOGGER + ".scan";

}
