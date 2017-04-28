/************** Begin Copyright - Do not add comments here **************
 *
 *
 * IBM Confidential OCO Source Material
 * Virtual Member Manager (C) COPYRIGHT International Business Machines Corp. 2012
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 */
package com.ibm.ws.security.wim.util;

import java.util.Comparator;

/**
 * The comparator used to compare the length of Strings
 */
public class StringLengthComparator implements Comparator<String> {

    /**
     * @see java.util.Comparator#compare(Object, Object)
     */
    @Override
    public int compare(String s1, String s2) {
        int l1 = s1.length();
        int l2 = s2.length();

        return l2 - l1;
    }
}
