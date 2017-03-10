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

package com.ibm.ws.ras.instrument.internal.introspect;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;

public class TraceObjectFieldAnnotationVisitor extends AnnotationVisitor {

    private String fieldName;
    private String fieldDescriptor;

    public TraceObjectFieldAnnotationVisitor() {
        super(Opcodes.ASM4);
    }

    public TraceObjectFieldAnnotationVisitor(AnnotationVisitor av) {
        super(Opcodes.ASM4, av);
    }

    @Override
    public void visit(String name, Object value) {
        super.visit(name, value);
        if ("fieldName".equals(name)) {
            fieldName = String.class.cast(value);
        } else if ("fieldDesc".equals(name)) {
            fieldDescriptor = String.class.cast(value);
        }
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getFieldDescriptor() {
        return fieldDescriptor;
    }
}
