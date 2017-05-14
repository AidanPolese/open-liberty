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
import java.io.IOException;
import java.io.PrintWriter;

public class ModelClassGenerator {
    private static String[] splitClassName(String className) {
        int index = className.lastIndexOf('.');
        return new String[] { className.substring(0, index), className.substring(index + 1) };
    }

    protected final File destdir;
    private final String packageName;
    final String simpleName;

    ModelClassGenerator(File destdir, String className) {
        this.destdir = destdir;
        String[] split = splitClassName(className);
        packageName = split[0];
        simpleName = split[1];
    }

    ModelClassGenerator(File destdir, String packageName, String simpleName) {
        this.destdir = destdir;
        this.packageName = packageName;
        this.simpleName = simpleName;
    }

    PrintWriter open() {
        File packageDir = new File(destdir, packageName.replace('.', '/'));

        if (!packageDir.mkdirs() && !packageDir.isDirectory()) {
            throw new IllegalStateException("Unable to create directory: " + packageDir);
        }

        File classFile = new File(packageDir, simpleName + ".java");
        System.out.println("Generating " + classFile);
        try {
            PrintWriter out = new PrintWriter(classFile, "UTF-8");
            out.println("// NOTE: This is a generated file. Do not edit it directly.");
            writePackageAnnotations(out);
            out.append("package ").append(packageName).append(";").println();
            out.println();
            return out;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    protected void writePackageAnnotations(PrintWriter out) {}
}
