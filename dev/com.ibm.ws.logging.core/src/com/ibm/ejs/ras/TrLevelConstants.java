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

package com.ibm.ejs.ras;

import java.util.logging.Level;

import com.ibm.websphere.logging.WsLevel;

public interface TrLevelConstants {

    // Mapping of trace level strings to distinct level groups. The index of the
    // group in the array will be used as an index to the levels array to retrieve
    // the logging Level
    String[][] traceLevels = { { "all", "dump" }, // = 0 0,1
                              { "finest", "debug" }, // = 1 2,3
                              { "finer", "entryExit" }, // = 2 4,5
                              { "fine", "event" }, // = 3 6,7
                              { "detail" }, // = 4 8
                              { "config" }, // = 5 9
                              { "info" }, // = 6 10
                              { "audit" }, // = 7 11
                              { "warning" }, // = 8 12
                              { "severe", "error" }, // = 9 13,14
                              { "fatal" }, // = 10 15
                              { "off" } // = 11 16
    };

    int TRACE_LEVEL_DUMP = 0;
    int TRACE_LEVEL_DEBUG = 1;
    int TRACE_LEVEL_ENTRY_EXIT = 2;
    int TRACE_LEVEL_EVENT = 3;
    int TRACE_LEVEL_DETAIL = 4;
    int TRACE_LEVEL_CONFIG = 5;
    int TRACE_LEVEL_INFO = 6;
    int TRACE_LEVEL_AUDIT = 7;
    int TRACE_LEVEL_WARNING = 8;
    int TRACE_LEVEL_ERROR = 9;
    int TRACE_LEVEL_FATAL = 10;
    int TRACE_LEVEL_OFF = 11;

    int SPEC_TRACE_LEVEL_OFF = 16;

    String TRACE_ENABLED = "enabled",
                    TRACE_DISABLED = "disabled",
                    TRACE_ON = "on",
                    TRACE_OFF = "off";

    Level MIN_LOCALIZATION = WsLevel.DETAIL;

    // An array of distinct trace levels. The trace specification is converted into a 
    // distinct index using the traceLevels array. That index is used to retrieve the actual
    // trace Level from this array. 
    Level[] levels = { Level.ALL, // all, dump
                      Level.FINEST, // finest, debug
                      Level.FINER, // finer, entryexit
                      Level.FINE, // fine, event
                      WsLevel.DETAIL, //detail
                      Level.CONFIG, // config
                      Level.INFO, // info
                      WsLevel.AUDIT, // audit
                      Level.WARNING, // warning
                      Level.SEVERE, // severe, error                    
                      WsLevel.FATAL, // fatal
                      Level.OFF }; // off
}
