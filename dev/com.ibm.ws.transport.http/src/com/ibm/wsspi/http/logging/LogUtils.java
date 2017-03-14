// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70. (C) COPYRIGHT International Business Machines Corp. 2004, 2009
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.wsspi.http.logging;

/**
 * Utilities related to the log files.
 * 
 */
public class LogUtils {

    /**
     * Private constructor.
     * 
     */
    private LogUtils() {
        // nothing
    }

    /**
     * Convert the input string into the appropriate Format setting.
     * 
     * @param name
     * @return AccessLog.Format
     */
    public static AccessLog.Format convertNCSAFormat(String name) {
        if (null == name) {
            return AccessLog.Format.COMMON;
        }
        AccessLog.Format format = AccessLog.Format.COMMON;
        String test = name.trim().toLowerCase();
        if ("combined".equals(test)) {
            format = AccessLog.Format.COMBINED;
        }
        return format;
    }

    /**
     * Convert the input string into the appropriate debug log level setting.
     * 
     * @param name
     * @return DebugLog.Level
     */
    public static DebugLog.Level convertDebugLevel(String name) {
        if (null == name) {
            return DebugLog.Level.NONE;
        }
        // WAS panels have expanded names for some of these so we're doing
        // a series of checks instead of the enum.valueOf() option

        // default to the WARNING level
        DebugLog.Level level = DebugLog.Level.WARN;
        String test = name.trim().toLowerCase();
        if ("none".equals(test)) {
            level = DebugLog.Level.NONE;
        } else if ("error".equals(test)) {
            level = DebugLog.Level.ERROR;
        } else if ("debug".equals(test)) {
            level = DebugLog.Level.DEBUG;
        } else if ("crit".equals(test) || "critical".equals(test)) {
            level = DebugLog.Level.CRIT;
        } else if ("info".equals(test) || "information".equals(test)) {
            level = DebugLog.Level.INFO;
        }
        return level;
    }
}
