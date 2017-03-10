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
package com.ibm.ws.config.utility;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

/**
 *
 */
public interface IFileUtility {

    /**
     * Gets the WLP_INSTALL_DIR.
     * 
     * @return
     */
    String getInstallDir();

    /**
     * Gets the WLP_USER_DIR.
     * 
     * @return
     */
    String getUserDir();

    /**
     * Recursively creates the parent directory for the given File if they do
     * not exist.
     * 
     * @param file
     * @return {@code true} if all parent directories exist or were created, {@code false} otherwise.
     */
    boolean createParentDirectory(PrintStream stdout, File file);

    /**
     * Answers if the File exists.
     * 
     * @param file File whose existence to check for
     * @return {@code true} if the File exists, {@code false} otherwise.
     */
    boolean exists(File file);

    /**
     * Checks if the file is a directory, just like the invocation to {@link File#isDirectory()}.
     * 
     * @param file The file to check.
     * @return {@code true} If the file is a directory.
     */
    boolean isDirectory(File file);

    /**
     * Store the String to the specified File.
     * 
     * @param toWrite
     * @param outFile
     * @return
     */
    boolean writeToFile(PrintStream stderr, String toWrite, File outFile);

    /**
     * Reads the file contents in as a String.
     * 
     * @param file the file to read
     * @throws IOException
     */
    StringBuilder readFileToStringBuilder(File file) throws IOException;
}