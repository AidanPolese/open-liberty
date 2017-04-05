// IBM Confidential OCO Source Material
// 5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997, 2003
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.

/*
 * @(#)author    Srini Rangaswamy
 * @(#)version   1.1
 * @(#)date      03/15/03
 */

package com.ibm.ws.pmi.server;

// Created this class to hold the module name in PerfLevelSpec.
// In custom PMI path[0] is not always the module name

// We couldn't add module name to the PerfLevelDescriptor to keep the backward compatibility
// between 5.0 and 4.0
public class CustomPerfLevelDescriptor extends PerfLevelDescriptor {
    private static final long serialVersionUID = -1008233654263885657L;
    private String moduleID;

    public CustomPerfLevelDescriptor(String[] path, int level, String moduleID) {
        super(path, level);
        this.moduleID = moduleID;
    }

    public String getModuleName() {
        return moduleID;
    }
}
