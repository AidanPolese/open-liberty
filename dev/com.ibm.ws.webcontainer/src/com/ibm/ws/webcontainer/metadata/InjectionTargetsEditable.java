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
package com.ibm.ws.webcontainer.metadata;

import java.util.List;

import com.ibm.ws.javaee.dd.common.InjectionTarget;

/**
 *
 */
public interface InjectionTargetsEditable {

    public void addInjectionTarget(InjectionTarget injectionTarget);

    public void addInjectionTargets(List<InjectionTarget> injectionTargets);
}
