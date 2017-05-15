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

public class EnterpriseBeanModelInterfaceImplClassGenerator extends ModelInterfaceImplClassGenerator {
    public EnterpriseBeanModelInterfaceImplClassGenerator(File destdir, ModelInterfaceType type) {
        super(destdir, type);
    }

    @Override
    protected boolean isHandleChildExtraNeeded() {
        return true;
    }

    @Override
    protected void writeHandleChildExtra(PrintWriter out, String indent) {
        out.append(indent).append("        if (xmi && \"localTran\".equals(localName)) {").println();
        out.append(indent).append("            com.ibm.ws.javaee.ddmodel.commonext.EJBLocalTranXMIType localTran = new com.ibm.ws.javaee.ddmodel.commonext.EJBLocalTranXMIType();").println();
        out.append(indent).append("            parser.parse(localTran);").println();
        out.append(indent).append("            this.local_transaction = localTran;").println();
        out.append(indent).append("            return true;").println();
        out.append(indent).append("        }").println();
    }
}
