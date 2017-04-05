/************** Begin Copyright - Do not add comments here **************
 *
 *  
 * IBM Confidential OCO Source Material
 * 5724-H88, 5724-J08, 5724-I63, 5655-W65, 5724-H89, 5722-WE2   Copyright IBM Corp., 2012, 2013, 2014
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U. S. Copyright Office.
 * 
 * Change History:
 * 
 * Tag          Person              Defect/Feature      Comments
 * -------      ------              --------------      --------------------------------------------------
 */
package com.ibm.ws.security.wim.util;

import java.util.UUID;

import com.ibm.websphere.security.wim.copyright.IBMCopyright;

/**
 * This class wraps the JDK 1.5 UUID class to generate UUID. JDK's UUID class
 * can directly be used. This class is created to maintain backward compatibility
 * with the code which use JDK 1.4.x.
 */
public class UniqueIdGenerator {
    static final String COPYRIGHT_NOTICE = IBMCopyright.COPYRIGHT_NOTICE_SHORT_2014;

    /**
     * Generates a UniqueId string
     * 
     * @return the generated UniqueId
     */
    public static synchronized String newUniqueId()
    {
        return UUID.randomUUID().toString();
    }
}
