/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2013, 2014
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package test.client;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;

import org.junit.BeforeClass;
import org.junit.Test;

import com.ibm.ws.kernel.boot.cmdline.Utils;
import com.ibm.ws.kernel.boot.internal.KernelUtils;

/**
 *
 */
public class EmbeddedServerTest {

    // Let's find the kernel boot bundle in our install image
    static File bvtImageKernelBundle = findImageKernelBundle();
    static File testServerClasses = new File("build/bvt/classes_embedded");
    static Field bootJarField = null;
    static Field bootLibDirField = null;
    static Field utilsInstallDirField = null;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

        // Set some statics so we can be talking about
        // the same locations the embedded server will be...
        setKernelUtilsBootstrapJar(bvtImageKernelBundle);
        setKernelUtilsBootstrapLibDir(bvtImageKernelBundle.getParentFile());
        setUtilsInstallDir(bvtImageKernelBundle.getParentFile().getParentFile());
        System.out.println("Kernel boot bundle: " + bvtImageKernelBundle.getAbsolutePath());
    }

    @Test
    public void testEmbeddedServer() throws Throwable {
        // This is not exhaustive: just a simple test of the SPI to
        // start and stop a server from an existing JVM.

        // We're going to start by making a nested classloader with limited delegation back
        // to this classloader.. a pain, but ..
        URLClassLoader classloader = new URLClassLoader(new URL[] { bvtImageKernelBundle.toURI().toURL(),
                                                                   testServerClasses.toURI().toURL() },
                                                        this.getClass().getClassLoader()) {
            @Override
            protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
                Class<?> result = null;

                if (name == null || name.length() == 0)
                    return null;

                result = findLoadedClass(name);

                if (result == null && name != null) {
                    try {
                        // Try to load the class from the child classpath first...
                        result = findClass(name);
                    } catch (ClassNotFoundException cnfe) {
                        result = super.loadClass(name, resolve);
                    }
                }

                return result;
            }
        };

        Class<?> driver = classloader.loadClass("test.server.EmbeddedServerDriver");
        System.out.println(driver.getClassLoader());

        Object d = driver.newInstance();
        List<AssertionFailedError> failures = null;

        Method m = driver.getDeclaredMethod("doTest");
        try {
            failures = (List<AssertionFailedError>) m.invoke(d);
        } catch (InvocationTargetException t) {
            throw t.getCause();
        }

        Assert.assertNotNull("List of failures should not be null", failures);

        for (AssertionFailedError error : failures) {
            // This obviously shouldn't even happen, but...
            error.printStackTrace();
        }
        Assert.assertTrue("List of failures should be empty: " + failures, failures.isEmpty());
    }

    @Test
    public void testEmbeddedServerCleanClasspath() throws Throwable {
        // This is not exhaustive: just a simple test of the SPI to
        // start and stop a server from an existing JVM.

        File devJars = new File(System.getProperty("install.dir", "build/bvt/wlp (&+)"), "dev/spi/ibm");
        File[] jars = devJars.listFiles(new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                return pathname.isFile() && pathname.getName().startsWith("com.ibm.websphere.appserver.spi.kernel.embeddable_") && pathname.getName().endsWith(".jar");
            }
        });

        ArrayList<URL> jarURLs = new ArrayList<URL>();
        jarURLs.add(testServerClasses.toURI().toURL());
        jarURLs.add(Test.class.getProtectionDomain().getCodeSource().getLocation());
        if (jars != null) {
            for (File jar : jars) {
                jarURLs.add(jar.toURI().toURL());
            }
        }

        // We're going to start by making a nested classloader with limited delegation back
        // to this classloader.. a pain, but ..
        URLClassLoader classloader = new URLClassLoader(jarURLs.toArray(new URL[0]),
                                                        null) {
            @Override
            protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
                Class<?> result = null;

                if (name == null || name.length() == 0)
                    return null;

                result = findLoadedClass(name);

                if (result == null && name != null) {
                    try {
                        // Try to load the class from the child classpath first...
                        result = findClass(name);
                    } catch (ClassNotFoundException cnfe) {
                        result = super.loadClass(name, resolve);
                    }
                }

                return result;
            }
        };

        Class<?> driver = classloader.loadClass("test.server.EmbeddedServerDriver");
        System.out.println(driver.getClassLoader());

        Object d = driver.newInstance();
        List<AssertionFailedError> failures = null;

        Method m = driver.getDeclaredMethod("doTest", File.class);
        try {
            failures = (List<AssertionFailedError>) m.invoke(d, bvtImageKernelBundle.getParentFile().getParentFile());
        } catch (InvocationTargetException t) {
            throw t.getCause();
        }

        Assert.assertNotNull("List of failures should not be null", failures);

        for (AssertionFailedError error : failures) {
            // This obviously shouldn't even happen, but...
            error.printStackTrace();
        }
        Assert.assertTrue("List of failures should be empty: " + failures, failures.isEmpty());
    }

    public static File findImageKernelBundle() {
        File root = new File(System.getProperty("install.dir", "build/bvt/wlp (&+)"));
        File libDir = new File(root, "lib");

        File fileList[] = libDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.matches("ws-launch.jar");
            }
        });

        if (fileList == null || fileList.length < 1)
            throw new RuntimeException("Unable to find ws-launch.jar in " + libDir.getAbsolutePath());

        return fileList[0];
    }

    public static void setKernelUtilsBootstrapJar(File bootstrapJar) throws Exception {
        if (bootJarField == null) {
            bootJarField = KernelUtils.class.getDeclaredField("launchHome");
            bootJarField.setAccessible(true);
        }
        bootJarField.set(null, bootstrapJar);
    }

    public static void setKernelUtilsBootstrapLibDir(File bootLibDir) throws Exception {
        if (bootLibDirField == null) {
            bootLibDirField = KernelUtils.class.getDeclaredField("libDir");
            bootLibDirField.setAccessible(true);
        }
        bootLibDirField.set(null, bootLibDir);
    }

    public static void setUtilsInstallDir(File installDir) throws Exception {
        if (utilsInstallDirField == null) {
            utilsInstallDirField = Utils.class.getDeclaredField("installDir");
            utilsInstallDirField.setAccessible(true);
        }
        utilsInstallDirField.set(null, installDir);
    }
}
