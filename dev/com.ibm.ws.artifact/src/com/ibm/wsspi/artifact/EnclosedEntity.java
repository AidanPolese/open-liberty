/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2010
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */
package com.ibm.wsspi.artifact;

/**
 * Represents something enclosed by a Container.
 */
public interface EnclosedEntity extends Entity {

    /**
     * Get the Container that encloses this entity.
     * 
     * @return container enclosing this entity, or null if there is none.
     */
    public ArtifactContainer getEnclosingContainer();

    /**
     * Get the path of this Entity within it's parents. The returned path is
     * guaranteed to not include a trailing path separator character.
     * 
     * @return path of container.
     */
    public String getPath();

    /**
     * Get the Container within this local root hierarchy, that would report isRoot=true.<p>
     * Calling this on a Container that returns isRoot=true, will return that same Container<br>
     * Calling this say, on an entry in a jar, will return the container representing the jar.<br>
     * Calling this on the container representing the jar, will return itself.<br>
     */
    public ArtifactContainer getRoot();

    /**
     * <p>
     * Get path for this Entity. Not all implementations of this interface need to support this method and should return <code>null</code> if they do not support them.
     * </p>
     * <p>
     * If the entry is container within an archive file such as a JAR or ZIP then this will return <code>null</code>.
     * </p>
     * <p>
     * For directories that have more than one physical location mapped to them then this will return the first mapped resource.
     * </p>
     * 
     * @return String representing physical path on disk for this entity.. null if there is none. null is very possible.
     * @deprecated added purely to support getRealPath on ServletContext .. post alpha this will need replacing.
     */
    @Deprecated
    public String getPhysicalPath();
}
