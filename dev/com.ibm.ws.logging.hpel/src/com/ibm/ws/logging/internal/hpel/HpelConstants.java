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
package com.ibm.ws.logging.internal.hpel;

/**
 *
 */
public interface HpelConstants {
    // Common prefix for all HPEL properties
    String PROP_PREFIX = "com.ibm.hpel.";

    // Extra prefixes to separate log, trace, and console properties
    String LOG_PREFIX = "log.";
    String TRACE_PREFIX = "trace.";
    String CONSOLE_PREFIX = "text.";

    // Default values are marked by '*'
    // Properties common to log and trace
    String DATA_DIRECTORY = "dataDirectory"; // String (logLocation by default)
    String PURGE_MAXSIZE = "purgeMaxSize"; // MB or -1* to disable
    String PURGE_MINTIME = "purgeMinTime"; // Hours or -1* to disable
    String BUFFERING = "bufferingEnabled"; // Boolean (true is default)
    String OUTOFSPACE_ACTION = "outOfSpaceAction"; // HpelTraceServiceConfig.OutOfSpaceAction
    String FILESWITCH_TIME = "fileSwitchTime"; // Hour 0*-23 or -1 to disable

    // Trace specific properties
    String MEMORYBUFFER_SIZE = "memoryBufferSize"; // MB or -1* to use directory

    // Console specific properties
    // String CONSOLE_TYPE = "type"; // STDOUT*/STDERR/none
    String INCLUDE_TRACE = "includeTrace"; // Boolean (false is default)
    String OUTPUT_FORMAT = "outputFormat"; // HpelTraceServiceConfig.OutputFormat

    String TEXT_LOG = "textLog"; // Sub-element of logging holding "text." attributes.

    //BootstrapConstants.INTERNAL_SERVER_NAME
    String INTERNAL_SERVER_NAME = "wlp.server.name";
    //BootstrapConstants.BOOTPROP_PRODUCT_INFO
    String BOOTPROP_PRODUCT_INFO = "websphere.product.info";
}
