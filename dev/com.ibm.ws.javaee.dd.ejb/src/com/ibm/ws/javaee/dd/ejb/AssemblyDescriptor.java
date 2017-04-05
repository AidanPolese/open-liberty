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
package com.ibm.ws.javaee.dd.ejb;

import java.util.List;

import com.ibm.ws.javaee.dd.common.MessageDestination;
import com.ibm.ws.javaee.dd.common.SecurityRole;

/**
 * Represents &lt;assembly-descriptor>.
 */
public interface AssemblyDescriptor
{
    /**
     * @return &lt;security-role> as a read-only list
     */
    List<SecurityRole> getSecurityRoles();

    /**
     * @return &lt;method-permission> as a read-only list
     */
    List<MethodPermission> getMethodPermissions();

    /**
     * @return &lt;container-transaction> as a read-only list
     */
    List<ContainerTransaction> getContainerTransactions();

    /**
     * @return &lt;interceptor-binding> as a read-only list
     */
    List<InterceptorBinding> getInterceptorBinding();

    /**
     * @return &lt;message-destination> as a read-only list
     */
    List<MessageDestination> getMessageDestinations();

    /**
     * @return &lt;exclude-list>, or null if unspecified
     */
    ExcludeList getExcludeList();

    /**
     * @return &lt;application-exception> as a read-only list
     */
    List<ApplicationException> getApplicationExceptionList();
}
