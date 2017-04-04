/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011, 2012
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.injectionengine;

import com.ibm.websphere.csi.J2EEName;
import com.ibm.ws.runtime.metadata.ModuleMetaData;
import com.ibm.wsspi.injectionengine.ComponentNameSpaceConfiguration;
import com.ibm.wsspi.injectionengine.InjectionException;
import com.ibm.wsspi.injectionengine.InjectionMetaData;
import com.ibm.wsspi.injectionengine.InjectionScope;
import com.ibm.wsspi.injectionengine.ReferenceContext;

public class InjectionMetaDataImpl
                implements InjectionMetaData
{
    private final AbstractInjectionEngine ivInjectionEngine;
    private final ComponentNameSpaceConfiguration ivCompNSConfig;
    private final ReferenceContext ivReferenceContext;

    public InjectionMetaDataImpl(AbstractInjectionEngine injectionEngine,
                                 ComponentNameSpaceConfiguration compNSConfig,
                                 ReferenceContext refContext)
    {
        ivInjectionEngine = injectionEngine;
        ivCompNSConfig = compNSConfig;
        ivReferenceContext = refContext;
    }

    @Override
    public String toString()
    {
        return super.toString() + '[' + getComponentNameSpaceConfiguration().getOwningFlow() + ':' + getJ2EEName() + ']';
    }

    public ComponentNameSpaceConfiguration getComponentNameSpaceConfiguration()
    {
        return ivCompNSConfig;
    }

    public ModuleMetaData getModuleMetaData()
    {
        return ivCompNSConfig.getModuleMetaData();
    }

    public J2EEName getJ2EEName()
    {
        return ivCompNSConfig.getJ2EEName();
    }

    public ReferenceContext getReferenceContext()
    {
        return ivReferenceContext;
    }

    public void bindJavaComp(String name, Object bindingObject)
                    throws InjectionException
    {
        ivInjectionEngine.bindJavaNameSpaceObject(ivCompNSConfig, InjectionScope.COMP, name, null, bindingObject);
    }
}
