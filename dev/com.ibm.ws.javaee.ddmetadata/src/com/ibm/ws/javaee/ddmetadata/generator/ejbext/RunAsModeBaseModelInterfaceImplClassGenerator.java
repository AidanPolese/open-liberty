/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.javaee.ddmetadata.generator.ejbext;

import java.io.File;
import java.io.PrintWriter;

import com.ibm.ws.javaee.ddmetadata.generator.ModelInterfaceImplClassGenerator;
import com.ibm.ws.javaee.ddmetadata.model.ModelInterfaceType;

public class RunAsModeBaseModelInterfaceImplClassGenerator extends ModelInterfaceImplClassGenerator {
    public RunAsModeBaseModelInterfaceImplClassGenerator(File destdir, ModelInterfaceType type) {
        super(destdir, type);
    }

    @Override
    protected boolean isTraceComponentNeeded() {
        return true;
    }

    @Override
    protected boolean isFinishExtraNeeded() {
        return true;
    }

    @Override
    protected void writeFinishExtra(PrintWriter out, String indent) {
        out.println();
        out.append(indent).append("        if (specified_identity == null && mode == ModeTypeEnum.SPECIFIED_IDENTITY) {").println();
        out.append(indent).append("            throw new DDParser.ParseException(Tr.formatMessage(tc, \"runasmode.missing.specifiedID.element\", parser.getPath(), parser.getLineNumber()));").println();
        out.append(indent).append("        }").println();
    }

    @Override
    protected boolean isHandleChildExtraNeeded() {
        return true;
    }

    @Override
    protected void writeHandleChildExtra(PrintWriter out, String indent) {
        out.append(indent).append("        if (xmi && \"runAsMode\".equals(localName)) {").println();
        out.append(indent).append("            RunAsModeXMIType runAsMode = new RunAsModeXMIType(this);").println();
        out.append(indent).append("            parser.parse(runAsMode);").println();
        out.append(indent).append("            return true;").println();
        out.append(indent).append("        }").println();
    }
}
