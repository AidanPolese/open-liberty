/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.security.authorization.jacc;

import java.util.List;

public class MethodInfo {
    private final String methodName;
    private final String methodInterfaceName;
    private final List<String> paramList;

    public MethodInfo(String methodName, String methodInterfaceName, List<String> paramList) {
        this.methodName = methodName;
        this.methodInterfaceName = methodInterfaceName;
        this.paramList = paramList;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getMethodInterfaceName() {
        return methodInterfaceName;
    }

    public List<String> getParamList() {
        return paramList;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("method : " ).append(methodName).append(" interface : ").append(methodInterfaceName).append(" parameters : ");
        if (paramList != null) {
            for (String s : paramList) {
                buf.append(s).append(", ");
            }
        } else {
            buf.append("null");
        }
        return buf.toString();
    }
}
