/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.config.xml.internal;

import com.ibm.websphere.config.ConfigUpdateException;
import com.ibm.ws.config.admin.ConfigID;

public class ConfigMergeException extends ConfigUpdateException {

    /**  */
    private static final long serialVersionUID = -4706030558184048028L;

    /**
     * @param ex
     */
    public ConfigMergeException(Exception ex) {
        super(ex);
    }

    /**
     * @param in
     */
    public ConfigMergeException(ConfigID id) {
        super("The configuration element " + id + " can not be merged.");
    }

    public ConfigMergeException(ConfigVariable var) {
        super("The configuration variable " + var.getName() + " can not be merged.");
    }

}