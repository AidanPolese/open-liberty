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
package com.ibm.ws.ras.instrument.internal.introspect;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.ibm.ws.ras.instrument.internal.model.PackageInfo;
import com.ibm.ws.ras.instrument.internal.model.TraceOptionsData;

public class TraceConfigPackageVisitor extends ClassVisitor {

    protected final static Type TRACE_OPTIONS_TYPE = Type.getObjectType("com/ibm/websphere/ras/annotation/TraceOptions");
    protected final static Type TRIVIAL_TYPE = Type.getObjectType("com/ibm/websphere/ras/annotation/Trivial");

    protected String internalPackageName;
    protected boolean trivialPackage;
    protected TraceOptionsAnnotationVisitor traceOptionsAnnotationVisitor;

    public TraceConfigPackageVisitor() {
        super(Opcodes.ASM4);
    }

    public TraceConfigPackageVisitor(ClassVisitor visitor) {
        super(Opcodes.ASM4, visitor);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.internalPackageName = name.replaceAll("/[^/]+$", "");
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        AnnotationVisitor av = super.visitAnnotation(desc, visible);
        if (TRIVIAL_TYPE.getDescriptor().equals(desc)) {
            trivialPackage = true;
        } else if (TRACE_OPTIONS_TYPE.getDescriptor().equals(desc)) {
            traceOptionsAnnotationVisitor = new TraceOptionsAnnotationVisitor(av);
            av = traceOptionsAnnotationVisitor;
        }
        return av;
    }

    public PackageInfo getPackageInfo() {
        TraceOptionsData traceOptionsData = null;
        if (traceOptionsAnnotationVisitor != null) {
            traceOptionsData = traceOptionsAnnotationVisitor.getTraceOptionsData();
        }
        return new PackageInfo(internalPackageName, trivialPackage, traceOptionsData);
    }
}
