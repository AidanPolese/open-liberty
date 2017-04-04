package com.ibm.wsspi.stackManager;

/**
 * An 'Installable' is a representation of a binary file
 * that can be installed and is a pre-requisite for the
 * deployment of a {@link Stack}.
 */
public interface Installable {

    // Installable types
    public static final String WLP_TYPE = "wlp";
    public static final String JRE_TYPE = "jre";
    public static final String OTHER_TYPE = "other";

    /**
     * Returns the file name of this Installable.
     * 
     * @return String
     */
    public abstract String getName();

    /**
     * Returns the type of this Installable.
     * Could be {@code jre}, {@code wlp}, or {@code other}.
     * 
     * @return String
     */
    public abstract String getType();

    /**
     * Returns the expandable setting for this Installable.
     * Could be {@code true}, {@code false} or {@code auto}.
     * 
     * @return String
     */
    public abstract String getExpandable();

    /**
     * Returns the absolute path to the source directory for this Installable.
     * 
     * @return
     */
    public abstract String getSourceDir();

    /**
     * Returns the absolute path to the source file for this Installable.
     * 
     * @return
     */
    public abstract String getSourcePath();

    /**
     * Returns the absolute path to the install directory for this Installable.
     * 
     * @return
     */
    public abstract String getInstallDir();

    /**
     * Returns the extension for this Installable.
     * 
     * @return
     */
    public abstract String getExtension();

    /**
     * Returns the operating system for this Installable.
     * 
     * @return
     */
    public abstract String getOS();

    /**
     * Returns the architecture of the processor for this Installable.
     * 
     * @return
     */
    public abstract String getArch();

    /**
     * Sets the name for this Installable.
     * 
     * @param name
     */
    public abstract void setName(String name);

    /**
     * Sets the type for this Installable.
     * 
     * @param type
     */
    public abstract void setType(String type);

    /**
     * Sets the expandable setting for this Installable.
     * 
     * @param expand
     */
    public abstract void setExpandable(String expand);

    /**
     * Sets the path to the source directory for this Installable.
     * 
     * @param sourceDir
     */
    public abstract void setSourceDir(String sourceDir);

    /**
     * Sets the path to the source file for this Installable.
     * 
     * @param sourcePath
     */
    public abstract void setSourcePath(String sourcePath);

    /**
     * Sets the path to the install directory for this Installable.
     * 
     * @param installDir
     */
    public abstract void setInstallDir(String installDir);

    /**
     * Sets the extension for this Installable.
     * 
     * @param extension
     */
    public abstract void setExtension(String extension);

    /**
     * Sets the operating system for this Installable.
     * 
     * @param os
     */
    public abstract void setOS(String os);

    /**
     * Sets the architecture of the processor for this Installable.
     * 
     * @param arch
     */
    public abstract void setArch(String arch);
}