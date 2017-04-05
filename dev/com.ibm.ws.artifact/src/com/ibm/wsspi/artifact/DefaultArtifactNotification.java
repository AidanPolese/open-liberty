/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.artifact;

import java.util.Collection;

import com.ibm.wsspi.artifact.ArtifactNotifier.ArtifactNotification;

/**
 * Default implementation of the {@link ArtifactNotification} interface
 */
public class DefaultArtifactNotification implements ArtifactNotification {
    private final ArtifactContainer root;
    private final Collection<String> paths;

    /**
     * Constructs an ArtifactNotification. <p>
     * Paths must be absolute, and the container passed must be from the notifier the notification is used for.
     * <p>
     * Paths may be prefixed with '!' to mean 'non recursive' eg.<br>
     * <ul>
     * <li> /WEB-INF <em>(the /WEB-INF directory, and all files/dirs beneath it recursively.)</em>
     * <li> / <em>(all files/dirs in the entire container)</em>
     * <li> !/META-INF <em>(the /META-INF directory and its immediate children)</em>
     * <li> !/ <em>(the container itself, and entries directly on its root.)</em>
     * </ul>
     * 
     * @param root the container to check the paths against. Must not be null.
     * @param paths the collection of paths to check. Must not be null.
     * @throws IllegalArgumentException if either argument is null.
     */
    public DefaultArtifactNotification(ArtifactContainer root, Collection<String> paths) {
        super();
        if (root == null) {
            throw new IllegalArgumentException();
        }
        this.root = root;
        if (paths == null) {
            throw new IllegalArgumentException();
        }
        this.paths = paths;
    }

    /**
     * @return the associated container
     */
    @Override
    public ArtifactContainer getContainer() {
        return root;
    }

    /**
     * @return the paths
     */
    @Override
    public Collection<String> getPaths() {
        return paths;
    }

}
