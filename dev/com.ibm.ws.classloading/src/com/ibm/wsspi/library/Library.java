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
package com.ibm.wsspi.library;

import java.io.File;
import java.util.Collection;
import java.util.EnumSet;

import com.ibm.wsspi.artifact.ArtifactContainer;
import com.ibm.wsspi.classloading.ApiType;
import com.ibm.wsspi.config.Fileset;

/**
 * A library, configured in server.xml, may contain folders, files (i.e. JARs and native libraries), and filesets.
 * <p>
 * <strong>Do not implement this interface.</strong> Liberty class loaders will only work with the Liberty implementations of this interface.
 */
public interface Library {

    /**
     * The unique identifier for this shared library.
     */
    String id();

    /**
     * This method returns the {@link java.util.Collection} of Filesets
     * 
     * @return a list of contained Filesets
     */
    Collection<Fileset> getFilesets();

    /**
     * Get the single classloader for this shared library.
     * There should be at most one of these in existence at any one time.
     */
    ClassLoader getClassLoader();

    /**
     * Get the allowed API types for this shared library.
     */
    EnumSet<ApiType> getApiTypeVisibility();

    /**
     * This method returns the {@link java.util.Collection} of Files
     * 
     * @return a list of contained Files
     */
    Collection<File> getFiles();

    /**
     * This method returns the {@link java.util.Collection} of Folders
     * 
     * @return a list of contained Folders
     */
    Collection<File> getFolders();

    /**
     * This method returns all the artifact containers from this shared library
     * 
     * @return a collection of contained artifact containers
     */
    Collection<ArtifactContainer> getContainers();

}
