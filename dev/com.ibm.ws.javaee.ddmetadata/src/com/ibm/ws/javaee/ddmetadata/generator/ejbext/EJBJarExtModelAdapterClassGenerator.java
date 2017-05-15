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

import com.ibm.ws.javaee.ddmetadata.generator.util.AbstractEJBJarBndExtModelAdapterClassGenerator;
import com.ibm.ws.javaee.ddmetadata.model.Model;

public class EJBJarExtModelAdapterClassGenerator extends AbstractEJBJarBndExtModelAdapterClassGenerator {
    public EJBJarExtModelAdapterClassGenerator(File destdir, Model model) {
        super(destdir, model);
    }

    @Override
    protected String getType() {
        return "ext";
    }
}
