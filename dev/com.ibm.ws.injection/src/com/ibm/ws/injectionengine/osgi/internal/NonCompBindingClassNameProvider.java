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
package com.ibm.ws.injectionengine.osgi.internal;

import com.ibm.websphere.ras.annotation.Trivial;
import com.ibm.ws.container.service.naming.JavaColonNamespaceBindings;

@Trivial
public class NonCompBindingClassNameProvider implements JavaColonNamespaceBindings.ClassNameProvider<NonCompBinding> {
    public static final JavaColonNamespaceBindings.ClassNameProvider<NonCompBinding> instance = new NonCompBindingClassNameProvider();

    @Override
    public String getBindingClassName(NonCompBinding binding) {
        return binding.binding.getInjectionClassTypeName();
    }
}
