/************** Begin Copyright - Do not add comments here **************
 *
 * IBM Confidential OCO Source Material
 * Virtual Member Manager (C) COPYRIGHT International Business Machines Corp. 2012
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 */
package com.ibm.ws.security.wim.util;

import com.ibm.websphere.ras.annotation.Trivial;

@Trivial
public class StringUtil {
    /**
     * Returns true iff str endsWith suffix (ignoreCase comparison)
     **/
    public static boolean endsWithIgnoreCase(String str, String suffix) {
        if (str == null || suffix == null)
            return false;

        int strLength = str.length();
        int suffixLength = suffix.length();

        // return false if the string is smaller than the suffix.
        if (strLength < suffixLength)
            return false;

        // perform regionMatch with ignorecase
        if (str.regionMatches(true, strLength - suffixLength, suffix, 0, suffixLength))
            return true;

        return false;
    }

    public static byte[] getBytes(String str) {
        StringBuffer sb = new StringBuffer(str);
        byte[] b = new byte[sb.length()];
        for (int i = 0, len = sb.length(); i < len; i++) {
            b[i] = (byte) sb.charAt(i);
        }
        return b;
    }

    public static String toString(byte[] b) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0, len = b.length; i < len; i++) {
            sb.append((char) (b[i] & 0xff));
        }
        String str = sb.toString();
        return str;
    }
}
