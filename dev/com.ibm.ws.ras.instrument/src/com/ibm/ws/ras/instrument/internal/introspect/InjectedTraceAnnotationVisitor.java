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

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;

import com.ibm.ws.ras.instrument.internal.bci.RasMethodAdapter;

public class InjectedTraceAnnotationVisitor extends AnnotationVisitor {

    private final List<String> methodAdapters = new ArrayList<String>();
    private Class<? extends RasMethodAdapter> currentMethodVisitor;
    private boolean visitedValueArray = false;

    public InjectedTraceAnnotationVisitor() {
        super(Opcodes.ASM4);
    }

    public InjectedTraceAnnotationVisitor(AnnotationVisitor av) {
        super(Opcodes.ASM4, av);
    }

    public <T extends RasMethodAdapter> InjectedTraceAnnotationVisitor(AnnotationVisitor av, Class<T> currentMethodVisitor) {
        super(Opcodes.ASM4, av);
        this.currentMethodVisitor = currentMethodVisitor;
    }

    @Override
    public void visit(String name, Object value) {
        super.visit(name, value);
    }

    @Override
    public AnnotationVisitor visitArray(String name) {
        AnnotationVisitor av = super.visitArray(name);
        if ("value".equals(name)) {
            av = new ValueArrayVisitor(av);
        }
        return av;
    }

    private class ValueArrayVisitor extends AnnotationVisitor {
        private ValueArrayVisitor(AnnotationVisitor av) {
            super(Opcodes.ASM4, av);
        }

        @Override
        public void visit(String name, Object value) {
            visitedValueArray = true;
            String methodAdapter = String.class.cast(value);
            if (!methodAdapters.contains(value)) {
                methodAdapters.add(methodAdapter);
            }
            super.visit(name, value);
        }

        @Override
        public void visitEnd() {
            if (currentMethodVisitor != null) {
                // Force the current visitor into the annotation w/o putting in list
                if (!methodAdapters.contains(currentMethodVisitor.getName())) {
                    super.visit(null, currentMethodVisitor.getName());
                }
            }
            super.visitEnd();
        }
    }

    @Override
    public void visitEnd() {
        if (!visitedValueArray && currentMethodVisitor != null) {
            visitArray("value").visitEnd();
        }
    }

    public List<String> getMethodAdapters() {
        return methodAdapters;
    }
}
