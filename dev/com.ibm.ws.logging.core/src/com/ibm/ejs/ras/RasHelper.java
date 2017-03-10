/*
 * ============================================================================
 * @start_prolog@
 * Version: @(#) 1.3 SERV1/ws/code/ras.lite/src/com/ibm/ejs/ras/RasHelper.java, WAS.ras.lite, WAS80.SERV1, kk1041.02 07/08/30 15:32:50 [10/22/10 01:28:54]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 *
 * 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08  (C) Copyright IBM Corp. 2006
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 * @end_prolog@
 *
 * Change activity:
 *
 * Reason          Date   Origin   Description
 * --------------- ------ -------- --------------------------------------------
 * 334159          051219 vaughton Original
 * SIB0048b.cli.1  060922 mnuttall Updated for WAS70.SIB
 *                 061031 vaughton Prep SERV1 version
 * 462724          070830 vaughton Add logger support
 * ============================================================================
 */

package com.ibm.ejs.ras;

import java.util.logging.LogRecord;

import com.ibm.websphere.ras.DataFormatHelper;

public class RasHelper {
    // @start_class_string_prolog@
    public static final String $sccsid = "@(#) 1.3 SERV1/ws/code/ras.lite/src/com/ibm/ejs/ras/RasHelper.java, WAS.ras.lite, WAS80.SERV1, kk1041.02 07/08/30 15:32:50 [10/22/10 01:28:54]";

    // @end_class_string_prolog@

    public static boolean isServer() {
        return false;
    }

    public static String getThreadId() {
        return DataFormatHelper.getThreadId();
    }

    public static String getThreadId(LogRecord logRecord) {
        return DataFormatHelper.getThreadId();
    }

    public static String getVersionId() {
        return "";
    }

    public static String getServerName() {
        return "";
    }

    public static String getProcessId() {
        return "";
    }

    public final static String throwableToString(Throwable t) {
        return DataFormatHelper.throwableToString(t);
    }
}

// End of file
