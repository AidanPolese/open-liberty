// IBM Confidential OCO Source Material
// 5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997, 2004
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.

package com.ibm.ws.monitor.internal.jmx;

public class PmiCollaboratorFactory {
    // return an instance of PmiCollaboratorMBean based on serverType
    public static PmiCollaboratorMBean getPmiCollaborator(String serverType) {
        // return PmiCollaborator for now. Later may have multiple implementations
        // return new PmiCollaborator();
        return PmiCollaborator.getSingletonInstance();
    }

    public static PmiCollaboratorMBean getPmiCollaborator() {
        return PmiCollaborator.getSingletonInstance();
    }
}
