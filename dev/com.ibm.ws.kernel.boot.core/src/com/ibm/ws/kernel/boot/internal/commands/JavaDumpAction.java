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
package com.ibm.ws.kernel.boot.internal.commands;

public enum JavaDumpAction {
    HEAP("heap"),
    SYSTEM("system"),
    THREAD("thread");
    // WARNING:  z/OS has special operator console support for all valid java dumps, so if a new java dump
    //           is added to this list, please add associated support from the z/OS console (see the
    //           com.ibm.ws.diagnostics.zos project for details)
    //
    //   Existing handlers for java dumps:
    //       heap   = com.ibm.ws.diagnostics.zos.javadump.HeapdumpCommandHandler.java
    //       system = com.ibm.ws.diagnostics.zos.tdump.TdumpCommandHandler.java
    //       thread = com.ibm.ws.diagnostics.zos.javadump.JavacoreCommandHandler.java

    public static JavaDumpAction forDisplayName(String displayName) {
        for (JavaDumpAction action : values()) {
            if (displayName.equals(action.displayName)) {
                return action;
            }
        }
        return null;
    }

    private final String displayName;

    JavaDumpAction(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }
}
