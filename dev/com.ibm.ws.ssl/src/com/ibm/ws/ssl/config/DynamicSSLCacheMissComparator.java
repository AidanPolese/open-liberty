/*
 * IBM Confidential OCO Source Material
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2005, 2007
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * @(#) 1.2 SERV1/ws/code/security.crypto/src/com/ibm/ws/ssl/config/DynamicSSLCacheMissComparator.java, WAS.security.crypto, WASX.SERV1, pp0919.25 9/12/07 10:42:28 [5/15/09 18:04:32]
 *
 * Date         Defect        CMVC ID    Description
 *
 * 08/19/05     LIDB3557-1.1  pbirk      3557 Initial Code Drop
 */

package com.ibm.ws.ssl.config;

import java.io.Serializable;

/**
 * DynamicSSLCacheMissComparator instance.
 * <p>
 * This class handles comparing two ConnectionInfo HashMaps for equality
 * in the TreeSet for cache misses during dynamic outbound SSL config decisions.
 * </p>
 *
 * @author IBM Corporation
 * @version WAS 7.0
 * @since WAS 7.0
 */
@SuppressWarnings("rawtypes")
public class DynamicSSLCacheMissComparator implements java.util.Comparator, Serializable {

    private static final long serialVersionUID = -8929150182102517588L;

    /**
     * Constructor.
     */
    public DynamicSSLCacheMissComparator() {
        // do nothing
    }

    /**
     * Compares its two arguments for order.
     **/
    @Override
    public int compare(Object o1, Object o2) {
        return (o1.hashCode() - o2.hashCode());
    }
}
