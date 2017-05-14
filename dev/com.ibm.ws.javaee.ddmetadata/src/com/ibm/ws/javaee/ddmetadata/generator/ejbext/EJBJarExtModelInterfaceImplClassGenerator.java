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

import com.ibm.ws.javaee.ddmetadata.generator.util.AbstractEJBJarBndExtModelInterfaceImplClassGenerator;
import com.ibm.ws.javaee.ddmetadata.model.ModelInterfaceType;

public class EJBJarExtModelInterfaceImplClassGenerator extends AbstractEJBJarBndExtModelInterfaceImplClassGenerator {
    public EJBJarExtModelInterfaceImplClassGenerator(File destdir, ModelInterfaceType type) {
        super(destdir, type);
    }

    @Override
    protected void writeFinishExtra(PrintWriter out, String indent) {
        out.println();
        writeCheckUnique(out, indent,
                         true, "com.ibm.ws.javaee.dd.ejbext.EnterpriseBean", "getName", null, null,
                         "bean", "getEnterpriseBeans()", "beans");
    }
}
