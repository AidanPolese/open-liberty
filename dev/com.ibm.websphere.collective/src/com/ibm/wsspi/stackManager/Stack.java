package com.ibm.wsspi.stackManager;

import java.util.List;
import java.util.Map;

/**
 * A 'Stack' is a representation of a server package and
 * a group of {@link Installable} that can be deployed to a host.
 *
 * @author ricogala
 *
 */
public interface Stack {

    /**
     * Gets the image managers
     *
     * @return List of image managers
     */
    public abstract List<String> getImageManagers();

    /**
     * Gets the name for this Stack.
     *
     * @return the name
     */
    public abstract String getName();

    /**
     * Gets the install directory for this Stack.
     *
     * @return the directory
     */
    public abstract String getInstallDir();

    /**
     * Gets the source path for this Stack. This path is the full path including the
     * package file for the stack.
     *
     * @return the directory
     */
    String getSourcePath();

    /**
     * Gets the {@link Installable} object from the Stack
     *
     * @return Installable
     */
    public abstract Installable getInstallable(String installableName);

    /**
     * Gets the {@link Map} of all the {@link Installable} elements for this Stack.
     *
     * @return the installables
     */
    public abstract Map<String, Installable> getInstallables();

    /**
     * Gets the {@link DeployVariable} object from the Stack
     *
     * @return DeployVariable
     */
    public abstract DeployVariable getDeployVariable(String deployVariableName);

    /**
     * Gets the {@link Map} of all the {@link DeployVariable} elements for this Stack.
     *
     * @return
     */
    public abstract Map<String, DeployVariable> getDeployVariables();

    /**
     * Gets the package name for this Stack.
     *
     * @return the package name
     */
    public abstract String getPackageName();

    /**
     * Gets the clusterName for this Stack.
     *
     * @return the clusterName
     */
    public abstract String getClusterName();

    /**
     * Gets the poolSize for this Stack.
     *
     * @return the pool size
     */
    public abstract int getPoolSize();

    /**
     * Gets the transferTimeout for this Stack.
     *
     * @return the transfer timeout in seconds
     */
    public abstract int getTransferTimeout();

    /**
     * Gets the electionPort for this Stack.
     *
     * @return electionPort
     */
    public abstract int getElectionPort();

    /**
     * Gets the wlpInstallDir for this Stack.
     *
     * @return wlpInstallDir
     */
    public abstract String getWlpInstallDir();

    /**
     * Gets the jreInstallDir for this Stack.
     *
     * @return jreInstallDir
     */
    public abstract String getJreInstallDir();

    /**
     * Gets the otherInstallDir for this Stack.
     *
     * @return otherInstallDir
     */
    public abstract String getOtherInstallDir();

    /**
     * Sets the name for this Stack.
     *
     * @param stackName
     */
    public abstract void setName(String stackName);

    /**
     * Sets the install directory for this Stack.
     *
     * @param installDir
     */
    public abstract void setInstallDir(String installDir);

    /**
     * Sets the imageManager for this Stack.
     *
     * @param imageManagers
     */
    public abstract void setImageManagers(String imageManagers);

    /**
     * Sets the poolSize for this Stack.
     *
     * @param poolSize
     */
    public abstract void setPoolSize(int poolSize);

    /**
     * Sets the transferTimeout for this Stack.
     *
     * @param transferTimeout in seconds
     */
    public abstract void setTransferTimeout(int transferTimeout);

    /**
     * Sets the electionPort for this Stack.
     *
     * @param electionPort
     */
    public abstract void setElectionPort(int electionPort);

    /**
     * Sets the wlpInstallDir for this Stack.
     *
     * @param wlpInstallDir
     */
    public abstract void setWlpInstallDir(String wlpInstallDir);

    /**
     * Sets the jreInstallDir for this Stack.
     *
     * @param jreInstallDir
     */
    public abstract void setJreInstallDir(String jreInstallDir);

    /**
     * Sets the otherInstallDir for this Stack.
     *
     * @param otherInstallDir
     */
    public abstract void setOtherInstallDir(String otherInstallDir);

    /**
     * Sets the source path for this Stack. This path is the full path including the
     * package file for the stack.
     *
     * @param sourcePath
     */
    public void setSourcePath(String sourcePath);

    /**
     * Sets the package name for this Stack.
     *
     * @param packageName
     */
    public abstract void setPackageName(String packageName);

    /**
     * Adds a new {@link Installable} to this Stack.
     *
     * @param installable
     * @return {@code true} if successful. {@code false} if the element is already part of this Stack.
     */
    public abstract boolean addInstallable(Installable installable);

    /**
     * Adds a new {@link DeployVariable} to this Stack.
     *
     * @param deployVariable
     * @return {@code true} if successful. {@code false} if the element is already part of this Stack.
     */
    public abstract boolean addDeployVariable(DeployVariable deployVariable);

    /**
     * Checks if input {@link Installable} is already part of this Stack.
     *
     * @param installableName
     * @return {@code true} if the element is in this Stack. {@code false} if it is not.
     */
    public abstract boolean containsInstallable(String installableName);

    /**
     * Checks if input {@link DeployVariable} is already part of this Stack.
     *
     * @param deployVariableName
     * @return {@code true} if the element is in this Stack. {@code false} if it is not.
     */
    public abstract boolean containsDeployVariable(String deployVariableName);

    /**
     * Adds an {@link Installable} to this Stack if it is not already an element of it. <br>
     * Updates the {@link Installable} if the element is contained in the Stack.
     *
     * @param installable
     */
    public abstract boolean updateInstallable(Installable installable);

    /**
     * Adds an {@link Package.cluster} to this Stack if it is not already an element of it. <br>
     * Updates the {@link Package.cluster} if the element is contained in the Stack.
     *
     * @param pack
     */
    public abstract boolean updatePackageCluster(Package pack);

    /**
     * Removes the {@link Installable} from the Stack.
     *
     * @param installable
     * @return {@code true} if successful.
     */
    public abstract boolean removeInstallable(String installableName);

    /**
     * Adds an {@link DeployVariable} to this Stack if it is not already an element of it. <br>
     * Updates the {@link DeployVariable} if the element is contained in the Stack.
     *
     * @param deployVariable
     */
    public abstract boolean updateDeployVariable(DeployVariable deployVariable);

    /**
     * Removes the {@link DeployVariable} from the Stack.
     *
     * @param deployVariable
     * @return {@code true} if successful.
     */
    public abstract boolean removeDeployVariable(String deployVariableName);

}