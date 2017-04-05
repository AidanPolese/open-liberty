/*
* IBM Confidential
*
* OCO Source Materials
*
* WLP Copyright IBM Corp. 2016
*
* The source code for this program is not published or otherwise divested
* of its trade secrets, irrespective of what has been deposited with the
* U.S. Copyright Office.
*/
package com.ibm.wsspi.stackManager;

/**
 * A stack package represents a package that is included in a StackGroup
 */
public interface Package {

    /**
     * Returns the name of this StackPackage.
     *
     * @return String
     */
    public abstract String getName();

    /**
     * Sets the name for this StackPackage.
     *
     * @param name
     */
    public abstract void setName(String name);

    /**
     * Returns the clusterName of this StackPackage.
     *
     * @return String
     */
    public abstract String getClusterName();

    /**
     * Sets the clusterName for this StackPackage.
     *
     * @param name
     */
    public abstract void setClusterName(String name);

}
