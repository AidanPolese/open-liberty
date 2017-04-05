/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.ejbcontainer.remote.client.internal;

import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import com.ibm.ws.container.service.app.deploy.ModuleInfo;
import com.ibm.ws.container.service.app.deploy.extended.ExtendedModuleInfo;
import com.ibm.ws.container.service.state.ModuleStateListener;
import com.ibm.ws.container.service.state.StateChangeException;
import com.ibm.ws.ejbcontainer.osgi.EJBContainer;
import com.ibm.ws.kernel.LibertyProcess;
import com.ibm.ws.runtime.metadata.ModuleMetaData;
import com.ibm.wsspi.kernel.service.utils.AtomicServiceReference;

@Component(service = ModuleStateListener.class)
public class EJBClientRuntimeImpl implements ModuleStateListener {
    private final AtomicServiceReference<EJBContainer> ejbContainerSR = new AtomicServiceReference<EJBContainer>("ejbContainer");
    private EJBClientMetaDataRuntime ejbClientMetaDataRuntime;

    @Reference(service = LibertyProcess.class, target = "(wlp.process.type=client)")
    protected void setLibertyProcess(ServiceReference<LibertyProcess> reference) {}

    protected void unsetLibertyProcess(ServiceReference<LibertyProcess> reference) {}

    @Reference(name = "ejbContainer", service = EJBContainer.class)
    protected void setEJBContainer(ServiceReference<EJBContainer> reference) {
        ejbContainerSR.setReference(reference);
    }

    protected void unsetEJBContainer(ServiceReference<EJBContainer> reference) {
        ejbContainerSR.unsetReference(reference);
    }

    @Reference
    protected void setEJBClientMetaDataRuntime(EJBClientMetaDataRuntime runtime) {
        this.ejbClientMetaDataRuntime = runtime;
    }

    protected void unsetEJBClientMetaDataRuntime(EJBClientMetaDataRuntime runtime) {
        this.ejbClientMetaDataRuntime = null;
    }

    @Activate
    protected void activate(ComponentContext cc) {
        ejbContainerSR.activate(cc);
    }

    @Deactivate
    protected void deactivate(ComponentContext cc) {
        ejbContainerSR.deactivate(cc);
    }

    private ModuleMetaData getEJBModuleMetaData(ModuleInfo moduleInfo) {
        return ejbClientMetaDataRuntime.getEJBModuleMetaData(((ExtendedModuleInfo) moduleInfo).getMetaData());
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
