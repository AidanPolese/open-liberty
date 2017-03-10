/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2010, 2013
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */
package com.ibm.ws.logging.internal.impl;

public interface LoggingConstants {
    public static enum TraceFormat {
        BASIC, ENHANCED, ADVANCED;
    }

    String DEFAULT_LOG_LEVEL = "AUDIT";
    int DEFAULT_FILE_MAX_SIZE = 20;
    int MAX_DATA_LENGTH = 1024 * 16;
    int DEFAULT_MAX_FILES = 2;

    String DEFAULT_MSG_FILE = "messages.log";
    String DEFAULT_TRACE_FILE = "trace.log";

    String PROP_FFDC_SUMMARY_POLICY = "com.ibm.ws.logging.ffdc.summary.policy";

    String PROP_TRACE_DELEGATE = "com.ibm.ws.logging.trace.delegate";
    String DEFAULT_TRACE_IMPLEMENTATION = "com.ibm.ws.logging.internal.impl.BaseTraceService";
    String JSR47_TRACE_IMPLEMENTATION = "com.ibm.ws.logging.internal.impl.Jsr47TraceService";

    String PROP_FFDC_DELEGATE = "com.ibm.ws.logging.ffdc.delegate";
    String DEFAULT_FFDC_IMPLEMENTATION = "com.ibm.ws.logging.internal.impl.BaseFFDCService";

    String SYSTEM_OUT = "SystemOut";
    String SYSTEM_ERR = "SystemErr";

    String nl = System.getProperty("line.separator");
    int nlen = nl.length();

    enum FFDCSummaryPolicy {
        DEFAULT, IMMEDIATE
    };
}
