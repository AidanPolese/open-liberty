/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.wsoc.injection;

/**
 * Need for keeping track of more than one class dealing with pushing and popping contexts
 */
public class InjectionThings {

    private ClassLoader originalCL = null;
    private boolean appActivateResult = false;

    public ClassLoader getOriginalCL() {
        return originalCL;
    }

    public void setOriginalCL(ClassLoader x) {
        originalCL = x;
    }

    public boolean getAppActivateResult() {
        return appActivateResult;
    }

    public void setAppActivateResult(boolean x) {
        appActivateResult = x;
    }

}
