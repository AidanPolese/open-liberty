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
package com.ibm.ws.javaee.ddmetadata.generator.util;

import java.io.File;
import java.io.PrintWriter;

import com.ibm.ws.javaee.ddmetadata.generator.ModelInterfaceImplClassGenerator;
import com.ibm.ws.javaee.ddmetadata.model.ModelInterfaceType;

public abstract class AbstractEJBJarBndExtModelInterfaceImplClassGenerator extends ModelInterfaceImplClassGenerator {
    public AbstractEJBJarBndExtModelInterfaceImplClassGenerator(File destdir, ModelInterfaceType type) {
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
    protected abstract void writeFinishExtra(PrintWriter out, String indent);

    protected void writeCheckUnique(PrintWriter out, String indent,
                                    boolean ejbName, String type, String getter, String elementName, String attributeName,
                                    String iterVar, String collectionExpr, String existingMapVar) {
        out.append(indent).append("        {").println();
        out.append(indent).append("            java.util.Map<String, ").append(type).append("> ").append(existingMapVar)
                        .append(" = new java.util.HashMap<String, ").append(type).append(">(").append(collectionExpr).append(".size());").println();
        out.append(indent).append("            for (").append(type).append(' ').append(iterVar).append(" : ").append(collectionExpr).append(") {").println();
        out.append(indent).append("                ").append(type).append(" existing = ").append(existingMapVar).append(".put(").append(iterVar)
                        .append('.').append(getter).append("(), ").append(iterVar).append(");").println();
        out.append(indent).append("                if (existing != null) {").println();
        out.append(indent).append("                    throw new DDParser.ParseException(Tr.formatMessage(tc, ");
        if (ejbName) {
            out.append("\"found.duplicate.ejbname\", parser.getDeploymentDescriptorPath(), existing.getName()");
        } else {
            out.append("\"found.duplicate.attribute.value\", parser.getDeploymentDescriptorPath(), \"<").append(elementName)
                            .append(">\", \"").append(attributeName).
                            append("\", existing.").append(getter).append("()");
        }
        out.append("));").println();
        out.append(indent).append("                }").println();
        out.append(indent).append("            }").println();
        out.append(indent).append("        }").println();
    }
}
