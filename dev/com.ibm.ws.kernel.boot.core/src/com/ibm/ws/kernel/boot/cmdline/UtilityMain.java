/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012, 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.kernel.boot.cmdline;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

import org.osgi.framework.Version;
import org.osgi.framework.VersionRange;

import com.ibm.ws.kernel.provisioning.AbstractResourceRepository;
import com.ibm.ws.kernel.provisioning.BundleRepositoryRegistry;
import com.ibm.ws.kernel.provisioning.NameBasedLocalBundleRepository;
import com.ibm.ws.kernel.provisioning.VersionUtility;

/**
 * Utility Main: evaluates the manifest header of the utility jar, and constructs
 * an apprpp
 */
public class UtilityMain {
    private static final String DEFAULT_BUNDLE_VERSION = "0.0.0";

    public static void main(String[] args) {
        try {
            internal_main(args);
        } catch (InvocationTargetException e) {
            Throwable ite = e.getTargetException();
            if (ite != null) {
                ite.printStackTrace();
            } else {
                e.printStackTrace();
            }
            System.exit(ExitCode.ERROR_UNKNOWN_EXCEPTION_CMD);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(ExitCode.ERROR_UNKNOWN_EXCEPTION_CMD);
        }
    }

    /**
     * @param args
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     */
    public static void internal_main(String[] args) throws IOException, ClassNotFoundException,
                    SecurityException, NoSuchMethodException, InstantiationException, IllegalAccessException,
                    IllegalArgumentException, InvocationTargetException {
        // The sole element of the classpath should be the jar that was launched.. 
        String jarName = System.getProperty("java.class.path");

        // Get the Attributes from the jar file
        JarFile jarFile = new JarFile(new File(jarName));
        Attributes a = jarFile.getManifest().getMainAttributes();
        jarFile.close();

        // Look for the attributes we need to find the real launch target: 

        // Command-Class redirects us to the real main
        String commandClass = a.getValue("Command-Class");

        // Require-Bundle tells us what should be on that real main's classpath
        String requiredBundles = a.getValue("Require-Bundle");

        // Some tools do have an assumption about what the parent classloader should be.
        // If set to bootstrap,
        String parentCL = a.getValue("Parent-ClassLoader");

        // A list of packages that will be loaded first by the nested/created PackageDelegateClassLoader, 
        // the value should be like javax.xml.ws, javax.xml.jaxb
        String parentLastPackages = a.getValue("Parent-Last-Package");

        //true is to add the <JAVA_HOME>/lib/tools.jar in the classpath, other values will not
        String requireCompiler = a.getValue("Require-Compiler");

        boolean bootstrapCLParent = false;
        boolean compilerTools = false;

        if ("bootstrap".equals(parentCL)) {
            bootstrapCLParent = true;
        }
        if ("true".equals(requireCompiler)) {
            compilerTools = true;
        }

        // Parse the list of required bundles.
        List<LaunchManifest.RequiredBundle> rbs = LaunchManifest.parseRequireBundle(requiredBundles);

        // Get the repository for bundles in the install
        BundleRepositoryRegistry.initializeDefaults(null, false);
        List<AbstractResourceRepository> repos = getAllRepositories();

        // Collect the list of jars required for the command.. 
        List<URL> urls = new ArrayList<URL>();
        urls = selectResources(repos, rbs, urls);

        // Find extensions.
        String extensions = a.getValue("IBM-RequiredExtensions");
        if (extensions != null && !extensions.isEmpty()) {
            for (String extension : extensions.split(",")) {
                List<LaunchManifest.RequiredBundle> rbsExt = ExtensionUtils.findExtensionBundles(extension.trim());
                if (rbsExt != null && !rbsExt.isEmpty()) {
                    urls = selectResources(repos, rbsExt, urls);
                }
            }
        }

        //if the Require-Compiler is true, add the <JDK_HOME>/lib/tools.jar in the classpath.
        if (compilerTools) {
            File toolsFile = Utils.getJavaTools();
            if (toolsFile != null) {
                urls.add(toolsFile.toURI().toURL());
            } else if (!Utils.hasToolsByDefault()) {
                error("error.sdkRequired", System.getProperty("java.home"));
                System.exit(ExitCode.ERROR_BAD_JAVA_VERSION);
                return;
            }
        }

        Thread currentThread = Thread.currentThread();
        final ClassLoader originalContextLoader = currentThread.getContextClassLoader();
        final URL[] urlArray = urls.toArray(new URL[urls.size()]);
        final List<String> parentLastPackageList = LaunchManifest.parseHeaderList(parentLastPackages);
        final ClassLoader parentLoader = bootstrapCLParent ? null : UtilityMain.class.getClassLoader();
        final ClassLoader cl = new PackageDelegateClassLoader(urlArray, parentLoader, parentLastPackageList);

        currentThread.setContextClassLoader(cl);
        try {
            Class<?> clazz = cl.loadClass(commandClass);
            Method m = clazz.getMethod("main", args.getClass());
            m.invoke(null, (Object) args);
        } finally {
            currentThread.setContextClassLoader(originalContextLoader);
        }
    }

    private static String format(String key, Object... args) {
        String string = Utils.getResourceBundleString(key);
        return args == null || args.length == 0 ? string : MessageFormat.format(string, args);
    }

    private static void error(String key, Object... args) {
        System.err.println(format(key, args));
    }

    private static boolean isValidJavaSpecVersion(LaunchManifest.RequiredBundle rb) {
        String vrString = rb.getAttribute("java.specification.version");
        if (vrString == null) {
            return true;
        }

        VersionRange vr = new VersionRange(vrString);
        Version v = new Version(System.getProperty("java.specification.version"));

        return vr.includes(v);
    }

    private static List<URL> selectResources(List<AbstractResourceRepository> repos, List<LaunchManifest.RequiredBundle> rbs, List<URL> urls) throws MalformedURLException {
        for (LaunchManifest.RequiredBundle rb : rbs) {
            String bundleVersion = rb.getAttribute("version");
            bundleVersion = (null != bundleVersion) ? bundleVersion : DEFAULT_BUNDLE_VERSION;
            if (!isValidJavaSpecVersion(rb)) {
                continue;
            }
            File f = null;
            for (AbstractResourceRepository repo : repos) {
                f = repo.selectResource(rb.getAttribute("location"),
                                        rb.getSymbolicName(),
                                        VersionUtility.stringToVersionRange(bundleVersion));
                if (f != null) {
                    break;
                }
            }
            if (f != null) {
                URL url = f.toURI().toURL();
                // make sure that there is no duplicate url.
                if (!urls.contains(url)) {
                    urls.add(url);
                }
            }
        }
        return urls;
    }

    private static List<AbstractResourceRepository> getAllRepositories() {
        List<AbstractResourceRepository> repos = new ArrayList<AbstractResourceRepository>();
        // default install location.
        repos.add(new NameBasedLocalBundleRepository(Utils.getInstallDir()));
        // add extension directory if it exists.
        List<File> extDirs = ExtensionUtils.listProductExtensionDirectories();
        for (File extDir : extDirs) {
            repos.add(new NameBasedLocalBundleRepository(extDir));
        }
        return repos;
    }
}