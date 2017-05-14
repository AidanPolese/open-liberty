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
package com.ibm.ws.javaee.ddmetadata.generator;

import java.io.File;
import java.io.PrintWriter;

public class ModelPackageInfoClassGenerator extends ModelClassGenerator {
    public ModelPackageInfoClassGenerator(File destdir, String packageName) {
        super(destdir, packageName, "package-info");
    }

    public void generate() {
        PrintWriter out = open();
        out.println("import com.ibm.websphere.ras.annotation.TraceOptions;");
        out.println("import com.ibm.ws.javaee.ddmodel.DDModelConstants;");
        out.close();
    }

    @Override
    protected void writePackageAnnotations(PrintWriter out) {
        out.println("/**");
        out.println(" * @version 1.0.16");
        out.println(" */");
        out.println("@org.osgi.annotation.versioning.Version(\"1.0.16\")");
        out.println("@TraceOptions(traceGroup = DDModelConstants.TRACE_GROUP, messageBundle = DDModelConstants.TRACE_MESSAGES)");
    }
}
