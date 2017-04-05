/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2010
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.injectionengine;

import java.io.Serializable;

/**
 * Contains the data necessary to perform one client-side injection for the
 * main class of a federated client module.
 */
public class ClientInjection
                implements Serializable
{
    private static final long serialVersionUID = 1032449919194046994L;

    /**
     * The reference name.
     */
    private final String ivRefName;

    /**
     * The type of the object being injected.
     */
    private final String ivInjectionTypeName;

    /**
     * The target class name.
     */
    private final String ivTargetClassName;

    /**
     * The target name in the class. This will either be the field name, or the
     * method name with the "set" prefix removed and lower-cased.
     */
    private final String ivTargetName;

    public ClientInjection(String refName,
                           String injectionTypeName,
                           String targetClassName,
                           String targetName)
    {
        ivRefName = refName;
        ivInjectionTypeName = injectionTypeName;
        ivTargetClassName = targetClassName;
        ivTargetName = targetName;
    }

    @Override
    public String toString()
    {
        return super.toString() +
               "[refName=" + ivRefName +
               ", type=" + ivInjectionTypeName +
               ", targetClass=" + ivTargetClassName +
               ", targetName=" + ivTargetName + ']';
    }

    public String getInjectionTypeName()
    {
        return ivInjectionTypeName;
    }

    public String getTargetClassName()
    {
        return ivTargetClassName;
    }

    public String getTargetName()
    {
        return ivTargetName;
    }

    public String getRefName()
    {
        return ivRefName;
    }
}
