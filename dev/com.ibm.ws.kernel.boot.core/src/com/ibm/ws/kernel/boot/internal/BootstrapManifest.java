/*******************************************************************************
 * Copyright (c) 2012, 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.kernel.boot.internal;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

import org.osgi.framework.Version;

import com.ibm.ws.kernel.boot.BootstrapConfig;
import com.ibm.ws.kernel.boot.LaunchException;
import com.ibm.ws.kernel.boot.cmdline.Utils;

/**
 * Contain the informations in bootstrap jar's manifest
 */
public class BootstrapManifest {

    static final String BUNDLE_VERSION = "Bundle-Version";

    /** Manifest header for the default kernel version */
    static final String MANIFEST_KERNEL = "WebSphere-DefaultKernel";

    /** Manifest header for the default log provider */
    static final String MANIFEST_LOG_PROVIDER = "WebSphere-DefaultLogProvider";

    /** Manifest header for the default OS Extensions */
    static final String MANIFEST_OS_EXTENSION = "WebSphere-DefaultExtension-";

    /** bootstrap property to override kernel version */
    static final String BOOTPROP_KERNEL = "websphere.kernel";

    /** bootstrap property to override log provider */
    static final String BOOTPROP_LOG_PROVIDER = "websphere.log.provider";

    /** bootstrap property to override os extension */
    static final String BOOTPROP_OS_EXTENSIONS = "websphere.os.extension";

    /** prefix for system-package files */
    static final String SYSTEM_PKG_PREFIX = "OSGI-OPT/websphere/system-packages_";

    /** suffix for system-package files */
    static final String SYSTEM_PKG_SUFFIX = ".properties";

    /**
     * Manifest header designating packages that should be exported into the
     * framework by this jar
     */
    static final String MANIFEST_EXPORT_PACKAGE = "Export-Package";

    private static BootstrapManifest instance = null;

    private final Attributes manifestAttributes;

    public static BootstrapManifest readBootstrapManifest() throws IOException {
        BootstrapManifest manifest = instance;
        if (manifest == null) {
            manifest = instance = new BootstrapManifest();
        }
        return manifest;
    }

    /** Clean up: allow garbage collection to clean up resources we don't need post-bootstrap */
    public static void dispose() {
        instance = null;
    }

    /**
     * @throws IOException
     */
    protected BootstrapManifest() throws IOException {
        JarFile jf = null;
        try {
            jf = new JarFile(KernelUtils.getBootstrapJar());
            Manifest mf = jf.getManifest();

            manifestAttributes = mf.getMainAttributes();
        } catch (IOException e) {
            throw e;
        } finally {
            Utils.tryToClose(jf);
        }
    }

    /**
     * Find and return the name of the core/kernel feature. Look in
     * bootstrap properties first, if not explicitly defined there, get
     * the default from the manifest.
     * 
     * @return the selected kernel version
     */
    public String getKernelDefinition(BootstrapConfig bootProps) {
        String kernelDef = bootProps.get(BOOTPROP_KERNEL);

        if (kernelDef == null)
            kernelDef = manifestAttributes.getValue(MANIFEST_KERNEL);

        if (kernelDef != null)
            bootProps.put(BOOTPROP_KERNEL, kernelDef);

        return kernelDef;
    }

    /**
     * Find and return the name of the log provider. Look in
     * bootstrap properties first, if not explicitly defined there, get
     * the default from the manifest.
     * 
     * @return the selected log provider
     */
    public String getLogProviderDefinition(BootstrapConfig bootProps) {
        String logProvider = bootProps.get(BOOTPROP_LOG_PROVIDER);

        if (logProvider == null)
            logProvider = manifestAttributes.getValue(MANIFEST_LOG_PROVIDER);

        if (logProvider != null)
            bootProps.put(BOOTPROP_LOG_PROVIDER, logProvider);

        return logProvider;
    }

    /**
     * Find and return the name of the os extension. Look in
     * bootstrap properties first, if not explicitly defined there, get
     * the default from the manifest.
     * 
     * @return the selected log provider
     */
    public String getOSExtensionDefinition(BootstrapConfig bootProps) {
        String osExtension = bootProps.get(BOOTPROP_OS_EXTENSIONS);

        if (osExtension == null) {
            String normalizedName = getNormalizedOperatingSystemName(bootProps.get("os.name"));
            osExtension = manifestAttributes.getValue(MANIFEST_OS_EXTENSION + normalizedName);
        }

        if (osExtension != null)
            bootProps.put(BOOTPROP_OS_EXTENSIONS, osExtension);

        return osExtension;
    }

    /**
     * @return the bundleVersion
     */
    public String getBundleVersion() {
        return manifestAttributes.getValue(BUNDLE_VERSION);
    }

    /**
     * @param bootProps
     * @throws IOException
     */
    public void prepSystemPackages(BootstrapConfig bootProps) {
        // Look for _extra_ system packages
        String packages = bootProps.get(BootstrapConstants.INITPROP_OSGI_EXTRA_PACKAGE);

        // Look for system packages set in bootstrap properties first
        String syspackages = bootProps.get(BootstrapConstants.INITPROP_OSGI_SYSTEM_PACKAGES);

        // Look for exported packages in manifest: append to bootstrap packages
        String mPackages = manifestAttributes.getValue(MANIFEST_EXPORT_PACKAGE);
        if (mPackages != null) {
            packages = (packages == null) ? mPackages : packages + "," + mPackages;

            // save new "extra" packages
            if (packages != null)
                bootProps.put(BootstrapConstants.INITPROP_OSGI_EXTRA_PACKAGE, packages);
        }

        // system packages are replaced, not appended 
        // so we only go look for our list of system packages if it hasn't already been set in bootProps
        // (that's the difference, re: system packages vs. "Extra" packages.. )
        if (syspackages == null) {
            // Look for system packages property file in the jar
            String javaVersion = System.getProperty("java.version", "1.6.0");
            // the java version may have an update modifier in the version string so we need to remove it. 
            int index = javaVersion.indexOf('_');
            index = (index == -1) ? javaVersion.indexOf('-') : index;
            javaVersion = (index == -1) ? javaVersion : javaVersion.substring(0, index);
            String pkgListFileName = SYSTEM_PKG_PREFIX + javaVersion + SYSTEM_PKG_SUFFIX;

            JarFile jarFile = null;
            try {
                jarFile = new JarFile(KernelUtils.getBootstrapJar());

                List<String> systemPackageFileNames = new ArrayList<String>();

                Enumeration<JarEntry> bootstrapJarEntries = jarFile.entries();
                while (bootstrapJarEntries.hasMoreElements()) {
                    JarEntry entry = bootstrapJarEntries.nextElement();
                    if (entry != null && entry.getName().startsWith(SYSTEM_PKG_PREFIX) && entry.getName().endsWith(SYSTEM_PKG_SUFFIX)) {
                        //was one of the system package properties files, add to the list
                        systemPackageFileNames.add(entry.getName());
                    }
                }

                //sort the files by version (high to low)
                Collections.sort(systemPackageFileNames, new Comparator<String>() {
                    @Override
                    public int compare(String name1, String name2) {
                        //elements can't be null because we don't allow
                        //null elements to be added to the list
                        //use OSGi versions so we can cope easily with, for example, java 10
                        Version oneVersion = getVersion(name1);
                        Version twoVersion = getVersion(name2);
                        //!!NOTE reverse the comparison order to get high to low ordering
                        return twoVersion.compareTo(oneVersion);
                    }

                    private Version getVersion(String name) {
                        //remove the prefix
                        String version = name.substring(SYSTEM_PKG_PREFIX.length(), name.length());
                        //remove the suffix
                        version = version.substring(0, version.indexOf(SYSTEM_PKG_SUFFIX, 0));
                        return new Version(version);
                    }
                });

                //if we found any package files then work out the appropriate one
                //otherwise try the default which will produce a nice error message
                if (!systemPackageFileNames.isEmpty()) {
                    //check if we have a package file for the version of Java we are using
                    int indexOfPackageFileToUse = systemPackageFileNames.indexOf(pkgListFileName);
                    //if we don't, then we should use the highest available package list instead
                    //unless there are no files at all, we don't worry about the case of not having
                    //a matching file for a lower version because the minimum execution environment
                    //means we will always be running on the minimum supported level.
                    if (indexOfPackageFileToUse < 0)
                        indexOfPackageFileToUse = 0;
                    //cut down the list to be from the current java version to the oldest version
                    //we will read all the files and append the properties to save maintenance effort on the package lists
                    systemPackageFileNames = systemPackageFileNames.subList(indexOfPackageFileToUse, systemPackageFileNames.size());
                } else {
                    //default system package file name
                    systemPackageFileNames = Arrays.asList(new String[] { pkgListFileName });
                }

                syspackages = getMergedSystemProperties(jarFile, systemPackageFileNames);

                // save new system packages 
                if (syspackages != null)
                    bootProps.put(BootstrapConstants.INITPROP_OSGI_SYSTEM_PACKAGES, syspackages);

            } catch (IOException ioe) {
                throw new LaunchException("Unable to find or read specified properties file; " + pkgListFileName,
                                    MessageFormat.format(BootstrapConstants.messages.getString("error.unknownException"), ioe.toString()),
                                    ioe);
            } finally {
                Utils.tryToClose(jarFile);
            }
        }
    }

    private String getMergedSystemProperties(JarFile jarFile, List<String> pkgListFileNames) throws IOException {
        String packages = null;
        for (String pkgListFileName : pkgListFileNames) {
            ZipEntry propFile = jarFile.getEntry(pkgListFileName);
            if (propFile != null) {
                // read org.osgi.framework.system.packages property value from the file
                Properties properties = new Properties();
                InputStream is = jarFile.getInputStream(propFile);
                try {
                    properties.load(is);
                    String loadedPackages = properties.getProperty(BootstrapConstants.INITPROP_OSGI_SYSTEM_PACKAGES);
                    if (loadedPackages != null) {
                        packages = (packages == null) ? loadedPackages : packages + "," + loadedPackages;
                    }
                } finally {
                    Utils.tryToClose(is);
                }
            } else {
                throw new IOException("Unable to find specified properties file; " + pkgListFileName);
            }
        }
        return packages;
    }

    /**
     * Normalize the value associated with the &quot;os.name&quot; system
     * property by putting it in lower case and removing characters that
     * cannot be used with {@link java.util.jar.Attributes.Name}.
     * 
     * @return the normalized OS name
     */
    static String getNormalizedOperatingSystemName(final String osName) {
        String name = osName.toLowerCase(Locale.ENGLISH);
        name = name.replaceAll("[^0-9a-zA-Z_-]", "");
        return name;
    }
}
