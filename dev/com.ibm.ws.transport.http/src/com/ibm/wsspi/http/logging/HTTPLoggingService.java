// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70. (C) COPYRIGHT International Business Machines Corp. 2004, 2009
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.wsspi.http.logging;

/**
 * Interface for an HTTP logging service that handles NCSA access logs, debug or
 * error logs, as well as a FRCA access log.
 */
public interface HTTPLoggingService {

    /**
     * Query the NCSA access log file.
     * 
     * @return AccessLog
     */
    AccessLog getAccessLog();

    /**
     * Query the FRCA access log file.
     * 
     * @return AccessLog
     */
    AccessLog getFRCALog();

    /**
     * Query the debug/error log file.
     * 
     * @return DebugLog
     */
    DebugLog getDebugLog();

}
