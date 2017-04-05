/************** Begin Copyright - Do not add comments here **************
 *
 *  
 * IBM Confidential OCO Source Material
 * 5724-H88, 5724-J08, 5724-I63, 5655-W65, 5724-H89, 5722-WE2   Copyright IBM Corp., 2012, 2013, 2014
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * 
 * Change History:
 * 
 * Tag          Person   Defect/Feature      Comments
 * ----------   ------   --------------      --------------------------------------------------
 * 01/07/2014   rzunzarr     109880          Delete API implementation. Added 2014 copyright.
 */
package com.ibm.websphere.security.wim.copyright;

public interface IBMCopyright
{

    static String lineSep = System.getProperty("line.separator");

    /*
     * See "Description" in this module's file header for defining
     * new Copyright
     */
    static final String LONG_PREFIX = "Licensed Materials - Property of IBM" + lineSep
                                      + "virtual member manager" + lineSep + "(C) Copyright IBM Corp. ";

    static final String LONG_SUFFIX = " All Rights Reserved." + lineSep
                                      + "US Government Users Restricted Rights - Use, duplication or" + lineSep
                                      + "disclosure restricted by GSA ADP Schedule Contract with" + lineSep + "IBM Corp.";

    static final String SHORT_PREFIX = "(c) Copyright International Business Machines Corporation ";

    public static final String COPYRIGHT_NOTICE_LONG_2012 = LONG_PREFIX + "2012" + LONG_SUFFIX;

    public static final String COPYRIGHT_NOTICE_SHORT_2012 = SHORT_PREFIX + "2012";

    public static final String COPYRIGHT_NOTICE_LONG_2014 = LONG_PREFIX + "2014" + LONG_SUFFIX;

    public static final String COPYRIGHT_NOTICE_SHORT_2014 = SHORT_PREFIX + "2014";
}
