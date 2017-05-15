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
package com.ibm.ws.javaee.ddmetadata.generator.managedbean;

import java.io.File;
import java.io.PrintWriter;

import com.ibm.ws.javaee.ddmetadata.generator.ModelAdapterClassGenerator;
import com.ibm.ws.javaee.ddmetadata.model.Model;

public class ManagedBeanBndModelAdapterClassGenerator extends ModelAdapterClassGenerator {
    public ManagedBeanBndModelAdapterClassGenerator(File destdir, Model model) {
        super(destdir, model);
    }

    @Override
    protected void writeStatics(PrintWriter out) {
        out.append("    public static final String XML_BND_IN_EJB_MOD_NAME = \"META-INF/ibm-managed-bean-bnd.xml\";").println();
        out.append("    public static final String XML_BND_IN_WEB_MOD_NAME = \"WEB-INF/ibm-managed-bean-bnd.xml\";").println();
        out.println();
    }

    @Override
    protected void writeInitializeEntryName(PrintWriter out, String ddEntryNameVar, String xmiVar) {
        out.append("        String ").append(ddEntryNameVar).append(';').println();
        out.append("        if (rootOverlay.getFromNonPersistentCache(artifactContainer.getPath(), com.ibm.ws.container.service.app.deploy.WebModuleInfo.class) == null) {").println();
        out.append("            ").append(ddEntryNameVar).append(" = XML_BND_IN_EJB_MOD_NAME;").println();
        out.append("        } else {").println();
        out.append("            ").append(ddEntryNameVar).append(" = XML_BND_IN_WEB_MOD_NAME;").println();
        out.append("        }").println();
    }
}
