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

import com.ibm.ws.container.service.naming.JavaColonNamespaceBindings;
import com.ibm.wsspi.injectionengine.InjectionBinding;

public class InjectionBindingClassNameProvider implements JavaColonNamespaceBindings.ClassNameProvider<InjectionBinding<?>> {
    public static final JavaColonNamespaceBindings.ClassNameProvider<InjectionBinding<?>> instance = new InjectionBindingClassNameProvider();

    @Override
    public String getBindingClassName(InjectionBinding<?> binding) {
        return binding.getInjectionClassTypeName();
    }
}
