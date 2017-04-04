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
package com.ibm.ws.javaee.dd.common;

/**
 * Represents &lt;resource-ref>.
 */
public interface ResourceRef
                extends ResourceGroup, Describable
{
    /**
     * Represents an unspecified value for {@link #getAuthValue}.
     */
    int AUTH_UNSPECIFIED = -1;

    /**
     * Represents "Container" for {@link #getAuthValue}.
     * 
     * @see org.eclipse.jst.j2ee.common.ResAuthTypeBase#CONTAINER
     */
    int AUTH_CONTAINER = 0;

    /**
     * Represents "Application" for {@link #getAuthValue}.
     * 
     * @see org.eclipse.jst.j2ee.common.ResAuthTypeBase#APPLICATION
     */
    int AUTH_APPLICATION = 1;

    /**
     * Represents an unspecified value for {@link #getSharingScopeValue}.
     */
    int SHARING_SCOPE_UNSPECIFIED = -1;

    /**
     * Represents "Shareable" for {@link #getSharingScopeValue}.
     * 
     * @see org.eclipse.jst.j2ee.common.ResSharingScopeType#SHAREABLE
     */
    int SHARING_SCOPE_SHAREABLE = 0;

    /**
     * Represents "Unshareable" for {@link #getSharingScopeValue}.
     * 
     * @see org.eclipse.jst.j2ee.common.ResSharingScopeType#UNSHAREABLE
     */
    int SHARING_SCOPE_UNSHAREABLE = 1;

    /**
     * @return &lt;res-type>, or null if unspecified
     */
    String getType();

    /**
     * @return &lt;res-auth>
     *         <ul>
     *         <li>{@link #AUTH_UNSPECIFIED} if unspecified
     *         <li>{@link #AUTH_CONTAINER} - Container
     *         <li>{@link #AUTH_APPLICATION} - Application
     *         </ul>
     */
    int getAuthValue();

    /**
     * @return &lt;res-sharing-scope>
     *         <ul>
     *         <li>{@link #SHARING_SCOPE_UNSPECIFIED} if unspecified
     *         <li>{@link #SHARING_SCOPE_SHAREABLE} - Shareable
     *         <li>{@link #SHARING_SCOPE_UNSHAREABLE} - Unshareable
     *         </ul>
     */
    int getSharingScopeValue();
}
