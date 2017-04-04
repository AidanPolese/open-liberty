package com.ibm.websphere.collective.controller;

import java.io.IOException;

/**
 * ControllerConfigMBean defines the administrative interface for managing
 * shared config files across a collective controller replica set.
 * <p>
 * The ObjectName for this MBean is {@value #OBJECT_NAME}.
 * <p>
 * The ControllerConfigMBean supports the creation, deletion, and listing
 * of shared config files. The config files are stored in the
 * ${wlp.user.dir}/<server name>/configDropins/defaults directory.
 * <p>
 * When you store (or delete) a shared config file through one replica, it is
 * automatically replicated to the corresponding directory in all the
 * other replicas in the replica set.
 * 
 * @ibm-api
 */
public interface ControllerConfigMBean {

    /**
     * A String representing the {@link javax.management.ObjectName} that this
     * MBean maps to.
     */
    String OBJECT_NAME = "WebSphere:feature=collectiveController,type=ControllerConfig,name=ControllerConfig";

    /**
     * Add additional config to the replica set through it's shared config directory.
     * Store config in replica's configDropins/defaults directory, which shares it with
     * the other replicas.
     * 
     * If you specify a fileName that already exists in the shared config, it will
     * be overwritten.
     * 
     * @param fileName - file name to create in shared config directory
     * @param config - config to store in file
     * @throws IOException - if something goes wrong in the replica during this operation.
     * @throws IllegalArgumentException - if fileName or config parameters are null.
     */
    public void addSharedConfig(String fileName, String config) throws IOException, IllegalArgumentException;

    /**
     * Remove config from the replica's shared config directory, by deleting
     * the specified file from the replica's configDropins/defaults directory.
     * 
     * If fileName is not found in shared config directory, this operation
     * does nothing.
     * 
     * @param fileName - file name to delete from shared config directory
     * @throws IOException - if something goes wrong in replica during this operation.
     * @throws IllegalArgumentException - if fileName is null.
     */
    public void removeSharedConfig(String fileName) throws IOException, IllegalArgumentException;

    /**
     * List the files in the replica's shared config directory. The array will
     * be empty if the directory is empty. A null is returned if the shared config
     * directory does not exist.
     * 
     * @return array of file names
     * @throws IOException - if something goes wrong in replica during this operation.
     */
    public String[] listSharedConfig() throws IOException;

}
