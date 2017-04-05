/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.resource;

/**
 * Mutable configuration for a resource. The configuration should not be
 * mutated after it is passed to a {@link ResourceFactory}.
 * <p>
 * This interface is not intended to be implemented by clients.
 */
public interface ResourceConfig
                extends ResourceInfo
{
    /**
     * Sets the description.
     *
     * @param description the description
     * @see #getDescription
     */
    void setDescription(String description);

    /**
     * Sets the type class name.
     *
     * @param className the class name
     * @see #getType
     */
    void setType(String className);

    /**
     * Sets the authentication type.
     *
     * @param auth the authentication type
     * @see #getResAuthType
     */
    void setResAuthType(int auth);

    /**
     * Sets the sharing scope.
     *
     * @param sharingScope the sharing scope
     * @see #getSharingScope
     */
    void setSharingScope(int sharingScope);

    /**
     * Sets the login configuration name.
     */
    void setLoginConfigurationName(String name);

    /**
     * Returns the non-null list of login properties.
     */
    void addLoginProperty(String name, String value);

    /**
     * Sets the isolation level.
     */
    void setIsolationLevel(int isolationLevel);

    /**
     * Sets the commit priority.
     */
    void setCommitPriority(int commitPriority);

    /**
     * Sets the branch coupling.
     */
    void setBranchCoupling(int branchCoupling);
}
