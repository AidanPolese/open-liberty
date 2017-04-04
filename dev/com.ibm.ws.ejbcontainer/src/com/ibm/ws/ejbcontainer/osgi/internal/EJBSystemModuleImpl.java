/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.ejbcontainer.osgi.internal;

import java.util.Map;

import com.ibm.ejs.csi.EJBModuleMetaDataImpl;
import com.ibm.ws.ejbcontainer.EJBReferenceFactory;
import com.ibm.ws.ejbcontainer.osgi.EJBSystemModule;

public class EJBSystemModuleImpl implements EJBSystemModule {
    private final EJBRuntimeImpl runtimeImpl;
    private final EJBModuleMetaDataImpl moduleMetaData;
    private final Map<String, EJBReferenceFactory> referenceFactories;

    public EJBSystemModuleImpl(EJBRuntimeImpl runtimeImpl,
                               EJBModuleMetaDataImpl mmd,
                               Map<String, EJBReferenceFactory> referenceFactories) {
        this.runtimeImpl = runtimeImpl;
        this.moduleMetaData = mmd;
        this.referenceFactories = referenceFactories;
    }

    @Override
    public void stop() {
        runtimeImpl.stopSystemModule(moduleMetaData);
    }

    @Override
    public EJBReferenceFactory getReferenceFactory(String ejbName) {
        EJBReferenceFactory factory = referenceFactories.get(ejbName);
        if (factory == null) {
            throw new IllegalArgumentException(ejbName);
        }
        return factory;
    }
}
