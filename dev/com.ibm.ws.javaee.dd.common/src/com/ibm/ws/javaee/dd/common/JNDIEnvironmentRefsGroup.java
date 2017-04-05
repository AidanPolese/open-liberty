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

import java.util.List;

/**
 * Represents the jndiEnvironmentRefsGroup type from the javaee XSD.
 */
public interface JNDIEnvironmentRefsGroup
                extends JNDIEnvironmentRefs
{
    /**
     * @return &lt;post-construct> as a read-only list
     */
    List<LifecycleCallback> getPostConstruct();

    /**
     * @return &lt;pre-destroy> as a read-only list
     */
    List<LifecycleCallback> getPreDestroy();
}
