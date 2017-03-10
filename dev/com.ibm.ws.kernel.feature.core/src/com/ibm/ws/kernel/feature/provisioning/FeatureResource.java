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
package com.ibm.ws.kernel.feature.provisioning;

import java.util.List;

import org.osgi.framework.VersionRange;

/**
 *
 */
public interface FeatureResource extends HeaderElementDefinition {

    public VersionRange getVersionRange();

    public String getLocation();

    public SubsystemContentType getType();

    /**
     * @return the raw type attribute, not as an enum value
     */
    public String getRawType();

    /**
     * obtain a list of operating systems this resource is relevant to.
     * 
     * @return null, if for ALL os, or a list of platform names this resource is for.
     */
    public List<String> getOsList();

    /**
     * @return
     */
    public int getStartLevel();

    public String getMatchString();

    public String getBundleRepositoryType();

    /**
     * @param type
     * @return
     */
    public boolean isType(SubsystemContentType type);

    public String getExtendedAttributes();

    public String setExecutablePermission();

    public String getFileEncoding();

    public List<String> getTolerates();
}