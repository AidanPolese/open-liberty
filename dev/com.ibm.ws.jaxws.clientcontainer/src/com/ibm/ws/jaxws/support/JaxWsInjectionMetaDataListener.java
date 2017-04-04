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
package com.ibm.ws.jaxws.support;

import java.util.List;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.jaxws.metadata.JaxWsModuleMetaData;
import com.ibm.ws.runtime.metadata.ModuleMetaData;
import com.ibm.wsspi.injectionengine.InjectionException;
import com.ibm.wsspi.injectionengine.InjectionMetaData;
import com.ibm.wsspi.injectionengine.InjectionMetaDataListener;

public class JaxWsInjectionMetaDataListener implements InjectionMetaDataListener {

    private static final TraceComponent tc = Tr.register(JaxWsInjectionMetaDataListener.class);

    /**
     * This method will store away the map of classes and injection metadata for later use. It will be stored in the
     * module metadata slot, and it will be used when creating instances of WAR based endpoints.
     */
    @Override
    public void injectionMetaDataCreated(InjectionMetaData injectionMetaData) throws InjectionException {

        // PI22432 Adding the judgement for null
        if (injectionMetaData == null)
            return;

        ModuleMetaData mmd = injectionMetaData.getModuleMetaData();

        if (mmd == null)
            return;

        JaxWsModuleMetaData jaxWsmoduleMetaData = JaxWsMetaDataManager.getJaxWsModuleMetaData(mmd);

        if (jaxWsmoduleMetaData == null) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "Unsupported Module, no JaxWsModuleMetaData is created for " + mmd.getName() + ", Injection Processing for web service is ignored");
            }
            return;
        }

        if (!jaxWsmoduleMetaData.getJ2EEName().equals(mmd.getJ2EEName())) {
            //Only process the injection event for the main module, e.g. EJB WS Router module and EJB module share the same JaxWsModuleMetaData
            //While, we only process the injection event if it is from EJB Module itself
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "Not main module for the JaxWsModuleMetaData {0} or the jaxWsModuleMetaData has been initialized, Injection Processing for web service is ignored",
                         mmd.getName());
            }
            return;
        }

        List<Class<?>> injectionClasses = injectionMetaData.getComponentNameSpaceConfiguration().getInjectionClasses();
        if (injectionClasses != null) {
            for (Class<?> clazz : injectionClasses) {
                jaxWsmoduleMetaData.setReferenceContext(clazz, injectionMetaData.getReferenceContext());
            }
        }

//        try {
//            if (JaxWsUtils.isEJBModule(jaxWsmoduleMetaData.getModuleContainer())) {
//                //only one ReferenceContextInjectionInstanceInterceptor is needed in InstanceManager
//                InstanceInterceptor interceptor = jaxWsmoduleMetaData.getJaxWsInstanceManager().getInterceptor(ReferenceContextInjectionInstanceInterceptor.class.getName());
//                if (interceptor == null) {
//                    jaxWsmoduleMetaData.getJaxWsInstanceManager().addInterceptor(new ReferenceContextInjectionInstanceInterceptor(jaxWsmoduleMetaData.getReferenceContextMap()));
//                }
//            }
//        } catch (UnableToAdaptException e) {
//            throw new InjectionException(e);
//        }

    }
}
