/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.config.utility.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;

import com.ibm.ws.config.utility.IFileUtility;

/**
 *
 */
public class FileUtility implements IFileUtility {
    static final String SLASH = String.valueOf(File.separatorChar);
    private final String WLP_INSTALL_DIR;
    private final String WLP_USER_DIR;

    /**
     * Construct the FileUtility class based on the values for the various
     * environment variables.
     * <p>
     * The supported environment variables are: WLP_USER_DIR and WLP_OUTPUT_DIR
     * 
     * @param WLP_INSTALL_DIR The value of WLP_INSTALL_DIR environment variable. {@code null} is supported.
     * @param WLP_USER_DIR The value of WLP_USER_DIR environment variable. {@code null} is supported.
     */
    public FileUtility(String WLP_INSTALL_DIR, String WLP_USER_DIR) {
        if (WLP_INSTALL_DIR == null) {
            File remoteAccessUtilityJarFile = getConfigUtilityJar();
            if (remoteAccessUtilityJarFile == null) {
                this.WLP_INSTALL_DIR = System.getProperty("user.dir") + SLASH;
            } else {
                this.WLP_INSTALL_DIR = remoteAccessUtilityJarFile.getParentFile().getParentFile().getAbsolutePath() + SLASH;
            }
        } else {
            if (WLP_INSTALL_DIR.endsWith("/") || WLP_INSTALL_DIR.endsWith("\\")) {
                this.WLP_INSTALL_DIR = WLP_INSTALL_DIR;
            } else {
                this.WLP_INSTALL_DIR = WLP_INSTALL_DIR + SLASH;
            }
        }

        if (WLP_USER_DIR == null) {
            this.WLP_USER_DIR = this.WLP_INSTALL_DIR + "usr" + SLASH;
        } else {
            // Ensure we have a trailing slash
            if (WLP_USER_DIR.endsWith("/") || WLP_USER_DIR.endsWith("\\")) {
                this.WLP_USER_DIR = WLP_USER_DIR;
            } else {
                this.WLP_USER_DIR = WLP_USER_DIR + SLASH;
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public String getInstallDir() {
        return WLP_INSTALL_DIR;
    }

    /** {@inheritDoc} */
    @Override
    public String getUserDir() {
        return WLP_USER_DIR;
    }

    /** {@inheritDoc} */
    @Override
    public boolean createParentDirectory(PrintStream stdout, File file) {
        File parent = file.getParentFile();
        if (parent == null) {
            return true;
        }
        if (!parent.exists()) {
            if (!createParentDirectory(stdout, parent)) {
                return false;
            }
            if (!parent.mkdir()) {
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean exists(File file) {
        return file.exists();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isDirectory(File file) {
        return file.isDirectory();
    }

    /** {@inheritDoc} */
    @Override
    public boolean writeToFile(PrintStream stderr, String toWrite, File outFile) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(outFile);
            fos.write(toWrite.getBytes(Charset.forName("UTF-8")));
            fos.flush();
            return true;
        } catch (FileNotFoundException e) {
            stderr.println(e.getMessage());
        } catch (IOException e) {
            stderr.println(e.getMessage());
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    // Ignored, can not do anything about this
                }
            }
        }
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public StringBuilder readFileToStringBuilder(File file) throws IOException {
        return new StringBuilder(FileUtils.readFileToString(file, "UTF-8"));
    }

    /**
     * A method that translates a URL into a viable File path, taking into
     * account the stupidity of UNC paths and spaces.
     * 
     * @param url
     *            to convert into a file name
     * @return file path derived from URL
     */
    private String getPath(URL url) {
        // Encode the path, this will normalize it to a simple file system path
        String encodedPath = RepositoryPathUtility.getURLEncodedPath(url.toString());

        // Decode to convert back to a base path. Doing this ensures we have
        // consistent path structure between the repository and the script
        return RepositoryPathUtility.decodeURLEncodedDir(encodedPath);
    }

    /**
     * Get the location of com.ibm.ws.config.utility.jar which is under the lib directory
     * 
     * @return File of com.ibm.ws.config.utility.jar
     */
    private File getConfigUtilityJar() {
        File launchHome = null;
        /*
         * The ConfigUtility is located in com.ibm.ws.config.utility.jar so
         * we are using reflection to get the right invocation of where this class
         * is loaded from.
         */
        Class<?> clazz = null;
        try {
            clazz = Class.forName("com.ibm.ws.config.utility.ConfigUtility");
        } catch (Exception e) {
            return null;
        }
        URL home = clazz.getProtectionDomain().getCodeSource().getLocation();

        if (!home.getProtocol().equals("file")) {
            return null;
        }
        String path = getPath(home);
        launchHome = new File(path);
        return launchHome;
    }
}
