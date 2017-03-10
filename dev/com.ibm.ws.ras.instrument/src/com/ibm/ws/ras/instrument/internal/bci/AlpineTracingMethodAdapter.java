/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2007, 2014
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */
package com.ibm.ws.ras.instrument.internal.bci;

import static com.ibm.ws.ras.instrument.internal.bci.AlpineTracingClassAdapter.TRACE_COMPONENT_TYPE;
import static com.ibm.ws.ras.instrument.internal.bci.AlpineTracingClassAdapter.TR_TYPE;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class AlpineTracingMethodAdapter extends AbstractRasMethodAdapter<AbstractTracingRasClassAdapter> {

    boolean skipTraces = false;

    boolean modifiedMethod = false;

    public AlpineTracingMethodAdapter(AbstractTracingRasClassAdapter classAdapter, MethodVisitor visitor, int access, String methodName, String descriptor, String signature,
                                      String[] exceptions) {
        super(classAdapter, true, visitor, access, methodName, descriptor, signature, exceptions);
    }

    public AlpineTracingMethodAdapter(AbstractTracingRasClassAdapter classAdapter, boolean skipTraces, MethodVisitor visitor, int access, String methodName, String descriptor,
                                      String signature, String[] exceptions) {
        this(classAdapter, visitor, access, methodName, descriptor, signature, exceptions);
        this.skipTraces = skipTraces;
    }

    private void setModifiedMethod(boolean modified) {
        this.modifiedMethod = modified;
    }

    public boolean isModifiedMethod() {
        return modifiedMethod;
    }

    @Override
    public boolean onMethodEntry() {
        if (skipTraces || isTrivial() || isAlreadyTraced()) {
            return false;
        }
        if (isStaticInitializer()) {
            return false;
        }

        Label skipTraceLabel = new Label();
        visitInvokeTraceGuardMethod("isEntryEnabled", skipTraceLabel);

        if (isStatic() || isConstructor() || isStaticInitializer()) {
            visitGetTraceObjectField();
            visitLoadMethodName();
            createTraceArrayForParameters();
            visitMethodInsn(
                            INVOKESTATIC,
                            TR_TYPE.getInternalName(),
                            "entry",
                            Type.getMethodDescriptor(
                                                     Type.VOID_TYPE,
                                                     new Type[] { TRACE_COMPONENT_TYPE, Type.getType(String.class), Type.getType(Object[].class) }));
        } else {
            visitVarInsn(getClassAdapter().getClassType().getOpcode(ILOAD), 0);
            visitGetTraceObjectField();
            visitLoadMethodName();
            createTraceArrayForParameters();
            visitMethodInsn(
                            INVOKESTATIC,
                            TR_TYPE.getInternalName(),
                            "entry",
                            Type.getMethodDescriptor(
                                                     Type.VOID_TYPE,
                                                     new Type[] { Type.getType(Object.class), TRACE_COMPONENT_TYPE, Type.getType(String.class), Type.getType(Object[].class) }));
        }

        visitLabel(skipTraceLabel);
        setModifiedMethod(true);
        return true;
    }

    @Override
    public boolean onMethodReturn() {
        if (skipTraces || isTrivial() || isAlreadyTraced()) {
            return false;
        }
        if (isStaticInitializer()) {
            return false;
        }

        Label skipTraceLabel = new Label();
        visitInvokeTraceGuardMethod("isEntryEnabled", skipTraceLabel);

        boolean traceValueOnStack = setupReturnObjectValueForExitTrace();
        if (traceValueOnStack) {
            if (isStatic()) {
                visitGetTraceObjectField();
                visitInsn(SWAP);
                visitLoadMethodName();
                visitInsn(SWAP);
                visitMethodInsn(
                                INVOKESTATIC,
                                TR_TYPE.getInternalName(),
                                "exit",
                                Type.getMethodDescriptor(
                                                         Type.VOID_TYPE,
                                                         new Type[] { TRACE_COMPONENT_TYPE, Type.getType(String.class), Type.getType(Object.class) }));
            } else {
                visitVarInsn(getClassAdapter().getClassType().getOpcode(ILOAD), 0);
                visitInsn(SWAP);
                visitGetTraceObjectField();
                visitInsn(SWAP);
                visitLoadMethodName();
                visitInsn(SWAP);
                visitMethodInsn(
                                INVOKESTATIC,
                                TR_TYPE.getInternalName(),
                                "exit",
                                Type.getMethodDescriptor(
                                                         Type.VOID_TYPE,
                                                         new Type[] { Type.getType(Object.class), TRACE_COMPONENT_TYPE, Type.getType(String.class), Type.getType(Object.class) }));
            }
        } else {
            if (isStatic()) {
                visitGetTraceObjectField();
                visitLoadMethodName();
                visitMethodInsn(
                                INVOKESTATIC,
                                TR_TYPE.getInternalName(),
                                "exit",
                                Type.getMethodDescriptor(
                                                         Type.VOID_TYPE,
                                                         new Type[] { TRACE_COMPONENT_TYPE, Type.getType(String.class) }));
            } else {
                visitVarInsn(getClassAdapter().getClassType().getOpcode(ILOAD), 0);
                visitGetTraceObjectField();
                visitLoadMethodName();
                visitMethodInsn(
                                INVOKESTATIC,
                                TR_TYPE.getInternalName(),
                                "exit",
                                Type.getMethodDescriptor(
                                                         Type.VOID_TYPE,
                                                         new Type[] { Type.getType(Object.class), TRACE_COMPONENT_TYPE, Type.getType(String.class) }));
            }
        }

        visitLabel(skipTraceLabel);
        setModifiedMethod(true);
        return true;
    }

    @Override
    public boolean onThrowInstruction() {
        if (skipTraces || !getClassAdapter().isTraceExceptionOnThrow() || isAlreadyTraced()) {
            return false;
        }

        Label skipTraceLabel = new Label();
        visitInvokeTraceGuardMethod("isDebugEnabled", skipTraceLabel);

        // The trace will eat the exception the top of the stack so we'll need
        // to duplicate the reference to be sure it still exists after the trace.
        visitInsn(DUP);

        visitGetTraceObjectField();
        visitInsn(SWAP);
        visitLdcInsn(getMethodName() + " is rasing exception");
        visitInsn(SWAP);
        visitLdcInsn(ICONST_1);
        visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/Object"); // tc, method, t, array[1]
        visitInsn(DUP_X1); // tc, method, array[1], t, array[1]
        visitInsn(SWAP); // tc, method, array[1], array[1], t
        visitInsn(ICONST_0); // tc, method, array[1], array[1], t, 0
        visitInsn(SWAP); // tc, method, array[1], array[1], 0, t
        visitInsn(AASTORE); // tc, method, array[1]
        visitMethodInsn(
                        INVOKESTATIC,
                        TR_TYPE.getInternalName(),
                        "debug",
                        Type.getMethodDescriptor(
                                                 Type.VOID_TYPE,
                                                 new Type[] { TRACE_COMPONENT_TYPE, Type.getType(String.class), Type.getType(Object[].class) }));

        visitLabel(skipTraceLabel);
        setModifiedMethod(true);
        return true;
    }

    @Override
    public boolean onExceptionHandlerEntry(Type exception, int var) {
        if (skipTraces || !getClassAdapter().isTraceExceptionOnHandling() || isAlreadyTraced()) {
            return false;
        }

        Label skipTraceLabel = new Label();
        visitInvokeTraceGuardMethod("isDebugEnabled", skipTraceLabel);

        if (var == -1) {
            // The trace will eat the exception the top of the stack so we'll need
            // to duplicate the reference to be sure it still exists after the trace.
            visitInsn(DUP);
        } else {
            visitVarInsn(ALOAD, var);
        }

        visitGetTraceObjectField();
        visitInsn(SWAP);
        visitLdcInsn(getMethodName() + " is handling exception");
        visitInsn(SWAP);
        visitMethodInsn(
                        INVOKESTATIC,
                        TR_TYPE.getInternalName(),
                        "debug",
                        "(Lcom/ibm/websphere/ras/TraceComponent;Ljava/lang/String;Ljava/lang/Object;)V");

        visitLabel(skipTraceLabel);
        setModifiedMethod(true);
        return true;
    }

    @Override
    public void initializeTraceObjectField() {
        if (!getClassAdapter().isTraceObjectFieldInitializationRequired() || isAlreadyTraced()) {
            return;
        }

        visitGetClassForType(Type.getObjectType(getClassAdapter().getClassInternalName()));
        visitMethodInsn(
                        INVOKESTATIC,
                        TR_TYPE.getInternalName(),
                        "register",
                        Type.getMethodDescriptor(
                                                 TRACE_COMPONENT_TYPE,
                                                 new Type[] { Type.getType(Class.class) }));
        visitSetTraceObjectField();
        setModifiedMethod(true);
    }

    private void visitInvokeTraceGuardMethod(String guardMethodName, Label skipTraceLabel) {
        visitMethodInsn(
                        INVOKESTATIC,
                        TRACE_COMPONENT_TYPE.getInternalName(),
                        "isAnyTracingEnabled",
                        Type.getMethodDescriptor(Type.BOOLEAN_TYPE, new Type[0]));
        visitJumpInsn(IFEQ, skipTraceLabel);

        visitGetTraceObjectField();
        visitJumpInsn(IFNULL, skipTraceLabel);

        visitGetTraceObjectField();
        visitMethodInsn(
                        INVOKEVIRTUAL,
                        TRACE_COMPONENT_TYPE.getInternalName(),
                        guardMethodName,
                        Type.getMethodDescriptor(Type.BOOLEAN_TYPE, new Type[0]));
        visitJumpInsn(IFEQ, skipTraceLabel);
    }

    @Override
    protected void boxSensitive(Type type) {
        if (type.getSort() == Type.ARRAY || type.getSort() == Type.OBJECT) {
            visitMethodInsn(Opcodes.INVOKESTATIC,
                            "com/ibm/websphere/ras/DataFormatHelper",
                            "sensitiveToString",
                            "(Ljava/lang/Object;)Ljava/lang/String;");
        } else {
            super.boxSensitive(type);
        }
    }
}
