// IBM Confidential OCO Source Material
// 5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997, 2004
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.

package com.ibm.websphere.pmi.stat;

/**
 * Constants for PMI Stats
 * 
 * @ibm-api
 */
public interface StatConstants {
    /** Represents a set in which all statistics in the server are disabled */
    public static final String STATISTIC_SET_NONE = "none";

    /** Represents a set in which the J2EE 1.4 + some top statistics are enabled */
    public static final String STATISTIC_SET_BASIC = "basic";

    /** Represents a set in which the statistic from Basic set + some important statistics from WebSphere components are enabled */
    public static final String STATISTIC_SET_EXTENDED = "extended";

    /** WebSphere performance statistics can be enabled using sets. Set "ALL" represents a set in which all the statistics are enabled */
    public static final String STATISTIC_SET_ALL = "all";

    /** Represents a customized set that is enabled using fine-grained control */
    public static final String STATISTIC_SET_CUSTOM = "custom";
}
