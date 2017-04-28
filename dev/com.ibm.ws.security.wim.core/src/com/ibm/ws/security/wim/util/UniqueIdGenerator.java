/************** Begin Copyright - Do not add comments here **************
 *
 *
 * IBM Confidential OCO Source Material
 * 5724-H88, 5724-J08, 5724-I63, 5655-W65, 5724-H89, 5722-WE2   Copyright IBM Corp., 2012, 2013, 2014
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U. S. Copyright Office.
 *
 */
package com.ibm.ws.security.wim.util;

import java.util.UUID;

/**
 * This class wraps the JDK 1.5 UUID class to generate UUID. JDK's UUID class
 * can directly be used. This class is created to maintain backward compatibility
 * with the code which use JDK 1.4.x.
 */
public class UniqueIdGenerator {

    /**
     * Generates a UniqueId string
     *
     * @return the generated UniqueId
     */
    public static synchronized String newUniqueId() {
        return UUID.randomUUID().toString();
    }
}
