/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2010
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */

package com.ibm.ws.monitor.internal.bci;

final class MethodInfo {

    private final ProbeInjectionClassAdapter classAdapter;
    private final int accessFlags;
    private final String methodName;
    private final String descriptor;
    private final String signature;
    private final String[] declaredExceptions;

    MethodInfo(ProbeInjectionClassAdapter classAdapter, int accessFlags, String methodName, String descriptor, String signature, String[] exceptions) {
        this.classAdapter = classAdapter;
        this.accessFlags = accessFlags;
        this.methodName = methodName;
        this.descriptor = descriptor;
        this.signature = signature;
        this.declaredExceptions = exceptions;
    }

    ProbeInjectionClassAdapter getClassAdapter() {
        return classAdapter;
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
