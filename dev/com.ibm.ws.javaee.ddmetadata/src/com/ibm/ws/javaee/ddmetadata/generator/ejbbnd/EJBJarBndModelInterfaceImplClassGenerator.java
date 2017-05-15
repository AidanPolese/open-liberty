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
package com.ibm.ws.javaee.ddmetadata.generator.ejbbnd;

import java.io.File;
import java.io.PrintWriter;

import com.ibm.ws.javaee.ddmetadata.generator.util.AbstractEJBJarBndExtModelInterfaceImplClassGenerator;
import com.ibm.ws.javaee.ddmetadata.model.ModelInterfaceType;

public class EJBJarBndModelInterfaceImplClassGenerator extends AbstractEJBJarBndExtModelInterfaceImplClassGenerator {
    public EJBJarBndModelInterfaceImplClassGenerator(File destdir, ModelInterfaceType type) {
        super(destdir, type);
    }

    @Override
    protected void writeFinishExtra(PrintWriter out, String indent) {
        out.println();
        writeCheckUnique(out, indent,
                         true, "com.ibm.ws.javaee.dd.ejbbnd.EnterpriseBean", "getName", null, null,
                         "bean", "getEnterpriseBeans()", "beans");

        out.println();
        writeCheckUnique(out, indent,
                         false, "com.ibm.ws.javaee.dd.commonbnd.Interceptor", "getClassName", "interceptor", "class",
                         "interceptor", "getInterceptors()", "interceptors");

        out.println();
        writeCheckUnique(out, indent,
                         false, "com.ibm.ws.javaee.dd.commonbnd.MessageDestination", "getName", "message-destination", "name",
                         "messageDestination", "getMessageDestinations()", "messageDestinations");
    }
}
