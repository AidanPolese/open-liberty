/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package test.util.kernel.boot;

import java.io.File;
import java.io.IOException;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Block the deactivate method if a known file exists
 */
public class KernelBootTestUtil implements BundleActivator {

    /** {@inheritDoc} */
    @Override
    public void start(BundleContext context) throws Exception {
        System.out.println("BVT BUNDLE: Starting " + this);
    }

    /** {@inheritDoc} */
    @Override
    public void stop(BundleContext context) throws Exception {
        File file = new File("TestBundleDeactivate.txt").getAbsoluteFile();
        System.out.println("BVT BUNDLE: Stopping " + this + " and creating " + file);

        try {
            if (!file.exists() && !file.createNewFile())
                throw new IllegalStateException("Failed to create " + file);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        System.out.println("Created " + file);

        File f = new File("TestBundleDeactivatePrevented.txt").getAbsoluteFile();
        for (int i = 0; i < 60; i++) {
            try {
                if (f.exists()) {
                    Thread.sleep(500);
                    System.out.println("Waiting for file " + f + " to be removed");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
