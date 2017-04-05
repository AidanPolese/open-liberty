/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */

package com.ibm.ws.ejbcontainer.jitdeploy;

import com.ibm.websphere.ras.TrConfigurator;
import com.ibm.websphere.ras.annotation.Trivial;

/**
 * Just In Time Deployment platform specific utility methods. <p>
 * 
 * This is a Liberty platform specific override. <p>
 */
@Trivial
final class JITPlatformHelper {
    /**
     * Returns the path name of the logs directory for the server process. <p>
     * 
     * The platform specific separator character is used and the path does
     * not end with a separator character. <p>
     **/
    static String getLogLocation() {
        return TrConfigurator.getLogLocation();
    }
}
