/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2010
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */

package com.ibm.ejs.ras;

/**
 * @see com.ibm.websphere.ras.DataFormatHelper
 */
@Deprecated
public class DataFormatHelper {
    /**
     * @see com.ibm.websphere.ras.DataFormatHelper#padHexString(int, int)
     */
    @Deprecated
    public static final String padHexString(int num, int width) {
        return com.ibm.websphere.ras.DataFormatHelper.padHexString(num, width);
    }

    /**
     * @see com.ibm.websphere.ras.DataFormatHelper#throwableToString(Throwable)
     */
    @Deprecated
    public static final String throwableToString(Throwable t) {
        return com.ibm.websphere.ras.DataFormatHelper.throwableToString(t);
    }

    /**
     * @see com.ibm.websphere.ras.DataFormatHelper#escape(String)
     */
    @Deprecated
    public final static String escape(String src) {
        return com.ibm.websphere.ras.DataFormatHelper.escape(src);
    }
}
