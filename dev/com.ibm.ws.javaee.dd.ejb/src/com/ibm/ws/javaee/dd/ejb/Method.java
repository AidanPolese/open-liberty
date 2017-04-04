/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012, 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.javaee.dd.ejb;

import com.ibm.ws.javaee.dd.common.Describable;

/**
 * Represents the methodType type.
 */
public interface Method
                extends Describable,
                BasicMethod
{
    /**
     * Represents an unspecified value for {@link #getInterfaceTypeValue}.
     * 
     * @see org.eclipse.jst.j2ee.ejb.MethodElementKind#UNSPECIFIED
     */
    int INTERFACE_TYPE_UNSPECIFIED = 0;

    /**
     * Represents "Home" for {@link #getInterfaceTypeValue}.
     * 
     * @see org.eclipse.jst.j2ee.ejb.MethodElementKind#HOME
     */
    int INTERFACE_TYPE_HOME = 2;

    /**
     * Represents "Remote" for {@link #getInterfaceTypeValue}.
     * 
     * @see org.eclipse.jst.j2ee.ejb.MethodElementKind#REMOTE
     */
    int INTERFACE_TYPE_REMOTE = 1;

    /**
     * Represents "LocalHome" for {@link #getInterfaceTypeValue}.
     * 
     * @see org.eclipse.jst.j2ee.ejb.MethodElementKind#LOCAL_HOME
     */
    int INTERFACE_TYPE_LOCAL_HOME = 4;

    /**
     * Represents "Local" for {@link #getInterfaceTypeValue}.
     * 
     * @see org.eclipse.jst.j2ee.ejb.MethodElementKind#LOCAL
     */
    int INTERFACE_TYPE_LOCAL = 3;

    /**
     * Represents "ServiceEndpoint" for {@link #getInterfaceTypeValue}.
     * 
     * @see org.eclipse.jst.j2ee.ejb.MethodElementKind#SERVICE_ENDPOINT
     */
    int INTERFACE_TYPE_SERVICE_ENDPOINT = 5;

    /**
     * Represents "Timer" for {@link #getInterfaceTypeValue}.
     * 
     * @see org.eclipse.jst.j2ee.ejb.MethodElementKind#TIMER
     */
    int INTERFACE_TYPE_TIMER = 6;

    /**
     * Represents "MessageEndpoint" for {@link #getInterfaceTypeValue}.
     * 
     * @see org.eclipse.jst.j2ee.ejb.MethodElementKind#MESSAGE_ENDPOINT
     */
    int INTERFACE_TYPE_MESSAGE_ENDPOINT = 7;

    /**
     * Represents "LifecycleCallback" for {@link #getInterfaceTypeValue}.
     * 
     * @see org.eclipse.jst.j2ee.ejb.MethodElementKind#LIFECYCLE_CALLBACK
     */
    int INTERFACE_TYPE_LIFECYCLE_CALLBACK = 8;

    /**
     * @return &lt;ejb-name>
     */
    String getEnterpriseBeanName();

    /**
     * @return &lt;method-intf>
     *         <ul>
     *         <li>{@link #INTERFACE_TYPE_UNSPECIFIED} if unspecified
     *         <li>{@link #INTERFACE_TYPE_HOME} - Home
     *         <li>{@link #INTERFACE_TYPE_REMOTE} - Remote
     *         <li>{@link #INTERFACE_TYPE_LOCAL_HOME} - LocalHome
     *         <li>{@link #INTERFACE_TYPE_LOCAL} - Local
     *         <li>{@link #INTERFACE_TYPE_SERVICE_ENDPOINT} - ServiceEndpoint
     *         <li>{@link #INTERFACE_TYPE_TIMER} - Timer
     *         <li>{@link #INTERFACE_TYPE_MESSAGE_ENDPOINT} - MessageEndpoint
     *         <li>{@link #INTERFACE_TYPE_LIFECYCLE_CALLBACK} - LifecycleCallback
     *         </ul>
     */
    int getInterfaceTypeValue();
}
