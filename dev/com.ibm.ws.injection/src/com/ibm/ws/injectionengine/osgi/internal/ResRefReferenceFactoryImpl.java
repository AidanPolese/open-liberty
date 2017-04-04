/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011, 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.injectionengine.osgi.internal;

import javax.naming.Reference;

import com.ibm.websphere.ras.annotation.Trivial;
import com.ibm.ws.javaee.dd.common.ResourceRef;
import com.ibm.ws.resource.ResourceRefConfig;
import com.ibm.ws.resource.ResourceRefInfo;
import com.ibm.wsspi.injectionengine.ComponentNameSpaceConfiguration;
import com.ibm.wsspi.injectionengine.InjectionException;
import com.ibm.wsspi.injectionengine.InjectionScope;
import com.ibm.wsspi.injectionengine.factory.ResRefReferenceFactory;

public class ResRefReferenceFactoryImpl implements ResRefReferenceFactory {
    private final ResourceBindingListenerManager resourceBindingListenerManager;

    public ResRefReferenceFactoryImpl(ResourceBindingListenerManager resourceBindingListenerManager) {
        this.resourceBindingListenerManager = resourceBindingListenerManager;
    }

    @Trivial
    @Override
    public Reference createResRefJndiLookup(ComponentNameSpaceConfiguration compNSConfig, InjectionScope scope, ResourceRefInfo resRef) throws InjectionException {
        return createResRefLookupReference(resRef.getName(), (ResourceRefConfig) resRef, false);
    }

    public Reference createResRefLookupReference(String refName, ResourceRefConfig resRef, boolean defaultBinding) {
        String bindingName = resRef.getJNDIName();
        String bindingListenerName = null;
        String type = resRef.getType();

        if (type != null) {
            ResourceBindingImpl binding = resourceBindingListenerManager.binding(refName, bindingName, type, null);
            if (binding != null) {
                bindingName = binding.getBindingName();
                bindingListenerName = binding.getBindingListenerName();
                defaultBinding = false;

                // The binding was set programmatically, so auth-type=Application
                // and login information (including authentication-alias, which is
                // represented as a login property) are no longer meaningful.
                resRef.setResAuthType(ResourceRef.AUTH_CONTAINER);
                resRef.setLoginConfigurationName(null);
                resRef.clearLoginProperties();
            }
        }

        return new IndirectReference(refName, bindingName, resRef.getType(), resRef, bindingListenerName, defaultBinding);
    }
}
