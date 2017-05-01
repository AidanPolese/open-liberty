/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.logging.internal;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 *
 */
public class PackageProcessorTest {

    @Test
    public void testExtractPackageFromStackTraceLine() {
        String line = "\tat com.ibm.ws.kernel.boot.Launcher.configAndLaunchPlatform(Launcher.java:311)";
        String packageName = PackageProcessor.extractPackageFromStackTraceLine(line);
        assertEquals("The package name wasn't extracted properly.", "com.ibm.ws.kernel.boot", packageName);

        // Now try with a constructor
        line = "\tat com.ibm.ws.kernel.boot.Launcher.<init>(Launcher.java:311)";
        packageName = PackageProcessor.extractPackageFromStackTraceLine(line);
        assertEquals("The package name wasn't extracted properly.", "com.ibm.ws.kernel.boot", packageName);
    }

    @Test
    public void testExtractPackageFromStackTraceLineInSimplePackage() {
        String line = "\tat simple.Something.doSomething(Whatever.java:19)";
        String packageName = PackageProcessor.extractPackageFromStackTraceLine(line);
        assertEquals("The package name wasn't extracted properly.", "simple", packageName);

        // Now try with a constructor
        line = "\tat simple.Something.<init>(Whatever.java:19)";
        packageName = PackageProcessor.extractPackageFromStackTraceLine(line);
        assertEquals("The package name wasn't extracted properly.", "simple", packageName);

    }

    @Test
    public void testExtractPackageFromStackTraceLineInDefaultPackage() {
        String line = "\tat ClassInDefaultPackage.someMethod(ClassInDefaultPackage.java:19)";
        String packageName = PackageProcessor.extractPackageFromStackTraceLine(line);
        assertEquals("The package name wasn't extracted properly.", null, packageName);

        // Now try with a constructor
        line = "\tat ClassInDefaultPackage.<init>(ClassInDefaultPackage.java:19)";
        packageName = PackageProcessor.extractPackageFromStackTraceLine(line);
        assertEquals("The package name wasn't extracted properly.", null, packageName);

    }

    @Test
    public void testExtractPackageFromStackTraceLineInInnerClass() {
        String line = "\tat com.ibm.ws.kernel5.boot.Launcher$InnerClass.configAndLaunchPlatform(Launcher.java:311)";
        String packageName = PackageProcessor.extractPackageFromStackTraceLine(line);
        assertEquals("The package name wasn't extracted properly.", "com.ibm.ws.kernel5.boot", packageName);

        // Now try with a constructor
        line = "\tat com.ibm.ws.kernel.boot.Launcher$InnerClass.<init>(Launcher.java:311)";
        packageName = PackageProcessor.extractPackageFromStackTraceLine(line);
        assertEquals("The package name wasn't extracted properly.", "com.ibm.ws.kernel.boot", packageName);
    }

    @Test
    public void testExtractPackageFromStackTraceElement() {
        String className = "com.ibm.ws.kernel5.boot.Launcher$InnerClass";
        StackTraceElement element = new StackTraceElement(className, "someMethod", "Launcher.java", 42);
        String packageName = PackageProcessor.extractPackageFromStackTraceElement(element);
        assertEquals("The package name wasn't extracted properly.", "com.ibm.ws.kernel5.boot", packageName);
    }
}
