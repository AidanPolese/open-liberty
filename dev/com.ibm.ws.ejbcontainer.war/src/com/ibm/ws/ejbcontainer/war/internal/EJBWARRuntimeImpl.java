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
package com.ibm.ws.ejbcontainer.war.internal;

import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;

import com.ibm.ws.container.service.app.deploy.ModuleInfo;
import com.ibm.ws.container.service.app.deploy.extended.ExtendedModuleInfo;
import com.ibm.ws.container.service.state.ModuleStateListener;
import com.ibm.ws.container.service.state.StateChangeException;
import com.ibm.ws.ejbcontainer.osgi.EJBContainer;
import com.ibm.ws.runtime.metadata.ModuleMetaData;
import com.ibm.wsspi.kernel.service.utils.AtomicServiceReference;

public class EJBWARRuntimeImpl implements ModuleStateListener {
    private final AtomicServiceReference<EJBContainer> ejbContainerSR = new AtomicServiceReference<EJBContainer>("ejbContainer");
    private EJBWARMetaDataRuntime ejbWARMetaDataRuntime;

    public void setEJBContainer(ServiceReference<EJBContainer> reference) {
        ejbContainerSR.setReference(reference);
    }

    public void unsetEJBContainer(ServiceReference<EJBContainer> reference) {
        ejbContainerSR.unsetReference(reference);
    }

    public void setEJBWARMetaDataRuntime(EJBWARMetaDataRuntime runtime) {
        this.ejbWARMetaDataRuntime = runtime;
    }

    public void unsetEJBWARMetaDataRuntime(EJBWARMetaDataRuntime runtime) {
        this.ejbWARMetaDataRuntime = null;
    }

    public void activate(ComponentContext cc) {
        ejbContainerSR.activate(cc);
    }

    public void deactivate(ComponentContext cc) {
        ejbContainerSR.deactivate(cc);
    }

    private ModuleMetaData getEJBModuleMetaData(ModuleInfo moduleInfo) {
        return ejbWARMetaDataRuntime.getEJBModuleMetaData(((ExtendedModuleInfo) moduleInfo).getMetaData());
    }

    @Override
    public void moduleStarting(ModuleInfo moduleInfo) throws StateChangeException {
        ModuleMetaData ejbMMD = getEJBModuleMetaData(moduleInfo);
        if (ejbMMD != null) {
            ejbContainerSR.getServiceWithException().startEJBInWARModule(ejbMMD, moduleInfo.getContainer());
        }
    }

    @Override
    public void moduleStarted(ModuleInfo moduleInfo) throws StateChangeException {
        ModuleMetaData ejbMMD = getEJBModuleMetaData(moduleInfo);
        if (ejbMMD != null) {
            ejbContainerSR.getServiceWithException().startedEJBInWARModule(ejbMMD, moduleInfo.getContainer());
        }
    }

    @Override
    public void moduleStopping(ModuleInfo moduleInfo) {}

    @Override
    public void moduleStopped(ModuleInfo moduleInfo) {
        ModuleMetaData ejbMMD = getEJBModuleMetaData(moduleInfo);
        if (ejbMMD != null) {
            ejbContainerSR.getServiceWithException().stopEJBInWARModule(ejbMMD, moduleInfo.getContainer());
        }
    }
}
