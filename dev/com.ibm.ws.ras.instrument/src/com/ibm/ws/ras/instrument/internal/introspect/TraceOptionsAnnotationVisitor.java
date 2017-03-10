//------------------------------------------------------------------------------
//%Z% %I% %W% %G% %U% [%H% %T%]

//COMPONENT_NAME: WAS.sca.ras

//IBM Confidential OCO Source Material
//5724-J08, 5724-I63, 5724-H88, 5655-N01, 5733-W61 (C) COPYRIGHT International Business Machines Corp. 2007
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.

//Change History:

//Defect/Feature  Date      CMVC ID   Description
//--------------  --------  --------- -----------------------------------------
//410408          20070919  sykesm    Initial implementation
//------------------------------------------------------------------------------
package com.ibm.ws.ras.instrument.internal.introspect;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;

import com.ibm.ws.ras.instrument.internal.model.TraceOptionsData;

public class TraceOptionsAnnotationVisitor extends AnnotationVisitor {

    protected List<String> traceGroups = new ArrayList<String>();
    protected String messageBundle;
    protected boolean traceExceptionThrow;
    protected boolean traceExceptionHandling;

    public TraceOptionsAnnotationVisitor() {
        super(Opcodes.ASM4);
    }

    public TraceOptionsAnnotationVisitor(AnnotationVisitor av) {
        super(Opcodes.ASM4, av);
    }

    @Override
    public void visit(String name, Object value) {
        if ("traceGroup".equals(name)) {
            String traceGroup = String.class.cast(value);
            if (!traceGroups.contains(traceGroup)) {
                traceGroups.add(traceGroup);
            }
        } else if ("messageBundle".equals(name)) {
            messageBundle = String.class.cast(value);
        } else if ("traceExceptionThrow".equals(name)) {
            traceExceptionThrow = Boolean.class.cast(value).booleanValue();
        } else if ("traceExceptionHandling".equals(name)) {
            traceExceptionHandling = Boolean.class.cast(value).booleanValue();
        }
        super.visit(name, value);
    }

    @Override
    public AnnotationVisitor visitArray(String name) {
        AnnotationVisitor av = super.visitArray(name);
        if ("traceGroups".equals(name)) {
            av = new TraceGroupsValueArrayVisitor(av);
        }
        return av;
    }

    private final class TraceGroupsValueArrayVisitor extends AnnotationVisitor {

        private TraceGroupsValueArrayVisitor(AnnotationVisitor av) {
            super(Opcodes.ASM4, av);
        }

        @Override
        public void visit(String name, Object value) {
            String traceGroup = String.class.cast(value);
            if (!"".equals(traceGroup) && !traceGroups.contains(traceGroup)) {
                traceGroups.add(traceGroup);
            }
            super.visit(name, value);
        }
    }

    public TraceOptionsData getTraceOptionsData() {
        return new TraceOptionsData(traceGroups, messageBundle, traceExceptionThrow, traceExceptionHandling);
    }
}
