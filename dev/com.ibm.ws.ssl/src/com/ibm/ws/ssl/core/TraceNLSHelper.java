/*
 * IBM Confidential OCO Source Material
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2007
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * 
 * @(#) 1.3 SERV1/ws/code/security.crypto/src/com/ibm/ws/ssl/core/TraceNLSHelper.java, WAS.security.crypto, WASX.SERV1, pp0919.25 11/12/07 16:56:26 [5/15/09 18:04:35]
 *
 * Date         Defect        CMVC ID    Description
 *
 * 08/19/05     LIDB3557-1.1  pbirk      3557 Initial Code Drop
 *
 * <p>
 *  Purpose of TraceNLSHelper is to load the ssl bundle once for the component to
 *  use for exception messages, etc.
 * </p>
 */

package com.ibm.ws.ssl.core;

import com.ibm.ejs.ras.TraceNLS;

/**
 * Helper class for interacting with an NLS translation bundle.
 * 
 * @author IBM Corporation
 * @version WAS 7.0
 * @since WAS 7.0
 */
public class TraceNLSHelper {
    private static final TraceNLS tnls = TraceNLS.getTraceNLS(TraceNLSHelper.class, "com.ibm.ws.ssl.resources.ssl");
    private static TraceNLSHelper thisClass = null;

    /**
     * Access the singleton instance of this class.
     * 
     * @return TraceNLSHelper
     */
    public static TraceNLSHelper getInstance() {
        if (thisClass == null) {
            thisClass = new TraceNLSHelper();
        }

        return thisClass;
    }

    private TraceNLSHelper() {
        // do nothing
    }

    /**
     * Look for a translated message using the input key. If it is not found, then
     * the provided default string is returned.
     * 
     * @param key
     * @param defaultString
     * @return String
     */
    public String getString(String key, String defaultString) {
        if (tnls != null)
            return tnls.getString(key, defaultString);

        return defaultString;
    }

    /**
     * Look for a translated message using the input key. If it is not found, then
     * the provided default string is returned.
     * 
     * @param key
     * @param args
     * @param defaultString
     * @return String
     */
    public String getFormattedMessage(String key, Object[] args, String defaultString) {
        if (tnls != null)
            return tnls.getFormattedMessage(key, args, defaultString);

        return defaultString;
    }
}
