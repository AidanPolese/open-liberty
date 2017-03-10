/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012, 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.kernel.boot.internal.commands;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

public abstract class JavaDumper {
    private static JavaDumper instance = createInstance();

    /**
     * Create a dumper for the current JVM.
     */
    private static JavaDumper createInstance() {
        try {
            // Try to find IBM Java dumper class.
            Class<?> dumpClass = Class.forName("com.ibm.jvm.Dump");
            try {
                // Try to find the IBM Java 7.1 dump methods.
                Class<?>[] paramTypes = new Class<?>[] { String.class };
                Method javaDumpToFileMethod = dumpClass.getMethod("javaDumpToFile", paramTypes);
                Method heapDumpToFileMethod = dumpClass.getMethod("heapDumpToFile", paramTypes);
                Method systemDumpToFileMethod = dumpClass.getMethod("systemDumpToFile", paramTypes);
                return new IBMJavaDumperImpl(javaDumpToFileMethod, heapDumpToFileMethod, systemDumpToFileMethod);
            } catch (NoSuchMethodException e) {
                return new IBMLegacyJavaDumperImpl(dumpClass);
            }
        } catch (ClassNotFoundException ex) {
            // Try to find HotSpot MBeans.
            ObjectName diagName;
            ObjectName diagCommandName;
            try {
                diagName = new ObjectName("com.sun.management:type=HotSpotDiagnostic");
                diagCommandName = new ObjectName("com.sun.management:type=DiagnosticCommand");
            } catch (MalformedObjectNameException ex2) {
                throw new IllegalStateException(ex2);
            }

            MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
            if (!mbeanServer.isRegistered(diagName)) {
                diagName = null;
            }
            if (!mbeanServer.isRegistered(diagCommandName)) {
                diagCommandName = null;
            }

            return new HotSpotJavaDumperImpl(mbeanServer, diagName, diagCommandName);
        }
    }

    /**
     * Obtains a dumper for the current JVM.
     */
    public static JavaDumper getInstance() {
        return instance;
    }

    /**
     * Generate a java dump of the current process.
     * 
     * @param action the dump action
     * @param outputDir the server output directory
     * @return the file containing the dump, or null if the action is not supported by this JVM
     * @throws RuntimeException if an error occurs while dumping
     */
    public abstract File dump(JavaDumpAction action, File outputDir);
}
