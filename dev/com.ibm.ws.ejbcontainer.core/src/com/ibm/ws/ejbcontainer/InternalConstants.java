/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.ejbcontainer;

/**
 * For internal use by EJB container only.
 */
public interface InternalConstants
{
    int TYPE_UNKNOWN = 1;
    int TYPE_SINGLETON_SESSION = 2;
    int TYPE_STATELESS_SESSION = 3;
    int TYPE_STATEFUL_SESSION = 4;
    int TYPE_BEAN_MANAGED_ENTITY = 5;
    int TYPE_CONTAINER_MANAGED_ENTITY = 6;
    int TYPE_MESSAGE_DRIVEN = 7;
    int TYPE_MANAGED_BEAN = 8;

    int TX_NOT_SUPPORTED = 0;
    int TX_BEAN_MANAGED = 1;
    int TX_REQUIRED = 2;
    int TX_SUPPORTS = 3;
    int TX_REQUIRES_NEW = 4;
    int TX_MANDATORY = 5;
    int TX_NEVER = 6;

    int TX_POLICY_BEAN_MANAGED = 1;
    int TX_POLICY_CONTAINER_MANAGED = 2;

    int CMP_VERSION_UNKNOWN = 0;
    int CMP_VERSION_1_X = 1;
    int CMP_VERSION_2_X = 2;

    int METHOD_INTF_REMOTE = 1;
    int METHOD_INTF_HOME = 2;
    int METHOD_INTF_LOCAL = 3;
    int METHOD_INTF_LOCAL_HOME = 4;
    int METHOD_INTF_SERVICE_ENDPOINT = 5;
    int METHOD_INTF_TIMER = 6;
    int METHOD_INTF_MESSAGE_ENDPOINT = 7;
    int METHOD_INTF_LIFECYCLE_INTERCEPTOR = 8;
}
