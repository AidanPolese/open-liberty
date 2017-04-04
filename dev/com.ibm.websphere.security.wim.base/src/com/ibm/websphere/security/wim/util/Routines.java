/************** Begin Copyright - Do not add comments here **************
 *
 *  
 * IBM Confidential OCO Source Material
 * Virtual Member Manager (C) COPYRIGHT International Business Machines Corp. 2012
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * 
 * Change History:
 * 
 * Tag          Person   Defect/Feature      Comments
 * ----------   ------   --------------      --------------------------------------------------
 */
package com.ibm.websphere.security.wim.util;

import com.ibm.websphere.ras.annotation.Trivial;

/**
 * Routines class is used to specify functions which can be used for variety
 * of reasons, mostly consists of helper functions. 
 * 
 * @author Ankit Jain
 */
@Trivial
public class Routines
{
    static final String COPYRIGHT_NOTICE = com.ibm.websphere.security.wim.copyright.IBMCopyright.COPYRIGHT_NOTICE_SHORT_2012;

    private static String newline = System.getProperty("line.separator");

  

    public static Object[] arrayCopy(Object[] inArray)
    {
        Object[] outArray = null;
        if (inArray != null) {
            outArray = new Object[inArray.length];
            System.arraycopy(inArray, 0, outArray, 0, inArray.length);
        }
        return outArray;
    }

}
