/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2010, 2013
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */

package com.ibm.ws.ras.instrument.internal.bci;

import static com.ibm.ws.ras.instrument.internal.main.AlpineTracePreprocessInstrumentation.LOGGER_TYPE;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import com.ibm.ws.ras.instrument.internal.introspect.TraceObjectFieldAnnotationVisitor;

public class AlpineTracePreprocessClassAdapter extends AbstractTracingRasClassAdapter {

    private TraceObjectFieldAnnotationVisitor traceObjectFieldVisitor;
    private final boolean initializeTraceObjectField;

    public AlpineTracePreprocessClassAdapter(ClassVisitor visitor, boolean initializeTraceObjectField) {
        super(visitor, null);
        this.initializeTraceObjectField = initializeTraceObjectField;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String name, boolean visible) {
        AnnotationVisitor av = super.visitAnnotation(name, visible);
        if (name.equals(TRACE_OBJECT_FIELD_TYPE.getDescriptor())) {
            traceObjectFieldVisitor = new TraceObjectFieldAnnotationVisitor(av);
            av = traceObjectFieldVisitor;
        }
        return av;
    }

    @Override
    public RasMethodAdapter createRasMethodAdapter(MethodVisitor delegate, int access, String name, String descriptor, String signature, String[] exceptions) {
        if (LOGGER_TYPE.equals(getTraceObjectFieldType())) {
            return new JSR47TracingMethodAdapter(this, delegate, access, name, descriptor, signature, exceptions);
        } else if (WebSphereTrTracingClassAdapter.TRACE_COMPONENT_TYPE.equals(getTraceObjectFieldType())) {
            return new WebSphereTrTracingMethodAdapter(this, delegate, access, name, descriptor, signature, exceptions);
        } else if (AlpineTracingClassAdapter.TRACE_COMPONENT_TYPE.equals(getTraceObjectFieldType()) && "<clinit>".equals(name)) {
            return new AlpineTracingMethodAdapter(this, false, delegate, access, name, descriptor, signature, exceptions);
        }
        return null;
    }

    @Override
    public String getTraceObjectFieldName() {
        return traceObjectFieldVisitor.getFieldName();
    }

    @Override
    public Type getTraceObjectFieldType() {
        return Type.getType(traceObjectFieldVisitor.getFieldDescriptor());
    }

    @Override
    public boolean isTraceObjectFieldDefinitionRequired() {
        return false;
    }

    @Override
    public boolean isTraceObjectFieldInitializationRequired() {
        return initializeTraceObjectField;
    }
}
