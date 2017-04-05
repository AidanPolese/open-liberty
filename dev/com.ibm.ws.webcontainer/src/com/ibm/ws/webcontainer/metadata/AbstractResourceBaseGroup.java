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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ibm.ws.javaee.dd.common.InjectionTarget;
import com.ibm.ws.javaee.dd.common.ResourceBaseGroup;

/**
 *
 */
public abstract class AbstractResourceBaseGroup implements ResourceBaseGroup, InjectionTargetsEditable {

    private String name;

    private String mappedName;

    private List<InjectionTarget> injectionTargets = new ArrayList<InjectionTarget>();

    public AbstractResourceBaseGroup(ResourceBaseGroup resourceGroup) {
        this.name = resourceGroup.getName();
        this.mappedName = resourceGroup.getMappedName();
        this.injectionTargets.addAll(resourceGroup.getInjectionTargets());
    }

    /** {@inheritDoc} */
    @Override
    public String getMappedName() {
        return mappedName;
    }

    /** {@inheritDoc} */
    @Override
    public List<InjectionTarget> getInjectionTargets() {
        return Collections.unmodifiableList(injectionTargets);
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return name;
    }

    /** {@inheritDoc} */
    @Override
    public void addInjectionTarget(InjectionTarget injectionTarget) {
        injectionTargets.add(injectionTarget);
    }

    /** {@inheritDoc} */
    @Override
    public void addInjectionTargets(List<InjectionTarget> _injectionTargets) {
        injectionTargets.addAll(_injectionTargets);
    }
}
