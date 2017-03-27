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
package com.ibm.ws.kernel.boot.internal;

import java.lang.management.ManagementFactory;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 *
 */
public class TimeoutRule implements TestRule {

    /*
     * (non-Javadoc)
     * 
     * @see org.junit.rules.TestRule#apply(org.junit.runners.model.Statement, org.junit.runner.Description)
     */
    @Override
    public Statement apply(final Statement base, final Description arg1) {
        return new CoreOnTimeoutStatement(base);
    }

    public class CoreOnTimeoutStatement extends Statement {

        private final Statement base;

        /**
         * @param base
         */
        public CoreOnTimeoutStatement(Statement base) {
            this.base = base;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.junit.runners.model.Statement#evaluate()
         */
        @Override
        public void evaluate() throws Throwable {
            try {
                base.evaluate();
            } catch (Throwable ex) {
                // Make sure this is a timeout
                if (ex.getMessage() != null && ex.getMessage().startsWith("test timed out")) {
                    // Run gcore to generate a core dump on platforms where it's available
                    String osName = System.getProperty("os.name");
                    if (osName.startsWith("Linux") || osName.startsWith("Solaris") || osName.startsWith("Sun")) {
                        int pid = getProcessPid();

                        Process p = Runtime.getRuntime().exec("gcore " + pid);
                        p.waitFor();
                    }
                }

                throw ex;
            }

        }

        /**
         * Get the process PID using the RuntimeMXBean. There are no real guarantees about the format,
         * so this could easily break some day.
         */
        private int getProcessPid() throws Exception {
            String pidStr = ManagementFactory.getRuntimeMXBean().getName();

            if (pidStr.contains("@")) {
                return Integer.parseInt(pidStr.split("@")[0]);
            } else {
                return -1;
            }

        }

    }
}
