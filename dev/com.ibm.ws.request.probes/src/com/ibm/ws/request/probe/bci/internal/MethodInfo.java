/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 * 
 * 
 * Change activity:
 *
 * Issue       Date        Name     Description
 * ----------- ----------- -------- ------------------------------------
 */

package com.ibm.ws.request.probe.bci.internal;

final class MethodInfo {

    private final int accessFlags;
    private final String methodName;
    private final String descriptor;
    private final String signature;
    private final String[] declaredExceptions;

    MethodInfo(int accessFlags, String methodName, String descriptor, String signature, String[] exceptions) {
        this.accessFlags = accessFlags;
        this.methodName = methodName;
        this.descriptor = descriptor;
        this.signature = signature;
        this.declaredExceptions = exceptions;
    }

    String getMethodName() {
        return methodName;
    }

    int getAccessFlags() {
        return accessFlags;
    }

    String getDescriptor() {
        return descriptor;
    }

    String getSignature() {
        return signature;
    }

    String[] getDeclaredExceptions() {
        return declaredExceptions;
    }
}
