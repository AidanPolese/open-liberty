/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011, 2013
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.resource;

import java.util.List;

import com.ibm.wsspi.resource.ResourceConfig;

/**
 * Configuration for a resource reference.
 * <p>
 * This interface is not intended to be implemented by clients.
 */
public interface ResourceRefConfig
                extends ResourceConfig, ResourceRefInfo
{
    /**
     * Sets the binding name.
     *
     * @param bindingName the binding name
     * @see #getBindingName
     */
    void setJNDIName(String bindingName);

    /**
     * Remove all login properties.
     */
    void clearLoginProperties();

    @Override
    List<? extends ResourceRefInfo.Property> getLoginPropertyList();

    /**
     * Merge the binding and extension data from other ResourceRefConfig objects
     * and overwrites the values in this object. If conflicts are found while
     * merging, a {@link MergeConflict} should be added to the conflicts list.
     *
     * @param resRefs an array of resource references with possibly null entries
     * @param conflicts an output array of conflicts found during merging
     */
    void mergeBindingsAndExtensions(ResourceRefConfig[] resRefs, List<MergeConflict> mergeConflicts);

    /**
     * Compare binding and extension data from another ResourceRefConfig object,
     * and compare any values that are not strictly identical even if unset in
     * one of the objects. If any conflicts are returned, this object will be
     * index 0 and the specified object will be index 1.
     *
     * @param resRef the other resource reference
     * @return conflicts the list of conflicts
     */
    List<MergeConflict> compareBindingsAndExtensions(ResourceRefConfig resRef);

    /**
     * The data for a conflict from {@link #mergeBindingsAndExtensions}.
     */
    interface MergeConflict
    {
        /**
         * @return the target resource reference configuration
         */
        ResourceRefConfig getResourceRefConfig();

        /**
         * @return the name of the attribute with a conflict
         */
        String getAttributeName();

        /**
         * @return the first index in the resource reference array that contained
         *         a conflicting value.
         */
        int getIndex1();

        /**
         * @return a string representation of the conflicting attribute value
         *         from {@link #getIndex1}.
         */
        String getValue1();

        /**
         * @return the second index in the resource reference array that
         *         contained a conflicting value.
         */
        int getIndex2();

        /**
         * @return a string representation of the conflicting attribute value
         *         from {@link #getIndex2}.
         */
        String getValue2();
    }
}
