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
 * Represents the resourceBaseGroup type from the javaee XSD.
 */
public interface ResourceBaseGroup
                extends JNDIEnvironmentRef
{
    /**
     * @return &lt;mapped-name>, or null if unspecified
     */
    String getMappedName();

    /**
     * @return &lt;injection-target> as a read-only list
     */
    List<InjectionTarget> getInjectionTargets();
}
