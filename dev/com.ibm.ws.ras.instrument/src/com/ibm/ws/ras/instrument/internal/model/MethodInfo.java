//------------------------------------------------------------------------------
// %Z% %I% %W% %G% %U% [%H% %T%]
//
// COMPONENT_NAME: WAS.sca.ras
//
// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5655-N01, 5733-W61 (C) COPYRIGHT International Business Machines Corp. 2007
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
//
// Defect/Feature  Date      CMVC ID   Description
// --------------  --------  --------- -----------------------------------------
// 410408          20070919  sykesm    Initial implementation
//------------------------------------------------------------------------------
package com.ibm.ws.ras.instrument.internal.model;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.objectweb.asm.Type;

public class MethodInfo {

    private final String methodName;
    private final String methodDescriptor;
    private boolean trivial;
    private boolean resultSensitive;
    private boolean[] argIsSensitive;
    private Set<Type> ffdcIgnoreExceptions = new LinkedHashSet<Type>();

    public MethodInfo(String name, String descriptor) {
        this.methodName = name;
        this.methodDescriptor = descriptor;
        this.argIsSensitive = new boolean[Type.getArgumentTypes(descriptor).length];
    }

    public String getMethodDescriptor() {
        return methodDescriptor;
    }

    public String getMethodName() {
        return methodName;
    }

    public void addFFDCIgnoreException(Type exceptionType) {
        ffdcIgnoreExceptions.add(exceptionType);
    }

    public Set<Type> getFFDCIgnoreExceptions() {
        return ffdcIgnoreExceptions;
    }

    public boolean isResultSensitive() {
        return resultSensitive;
    }

    public void setResultSensitive(boolean sensitive) {
        this.resultSensitive = sensitive;
    }

    public boolean isTrivial() {
        return trivial;
    }

    public void setTrivial(boolean trivial) {
        this.trivial = trivial;
    }

    public boolean isArgSensitive(int index) {
        if (index < argIsSensitive.length) {
            return argIsSensitive[index];
        }
        return false;
    }

    public void setArgIsSensitive(int index, boolean isSensitive) {
        this.argIsSensitive[index] = isSensitive;
    }

    public void updateDefaultValuesFromClassInfo(ClassInfo classInfo) {
        if (!trivial) {
            trivial = classInfo.isTrivial();
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        sb.append(";methodName=").append(methodName);
        sb.append(",methodDescriptor=").append(methodDescriptor);
        sb.append(",trivial=").append(trivial);
        sb.append(",resultSensitive=").append(resultSensitive);
        sb.append(",argIsSensitive=").append(Arrays.toString(argIsSensitive));
        sb.append(",ffdcIgnoreExceptions=").append(ffdcIgnoreExceptions);
        return sb.toString();
    }
}
