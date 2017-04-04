/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015, 2016
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.cdi.liberty;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import javax.enterprise.inject.spi.CDIProvider;

import org.jboss.weld.ejb.spi.EjbServices;
import org.jboss.weld.security.spi.SecurityServices;
import org.jboss.weld.serialization.spi.ProxyServices;
import org.jboss.weld.transaction.spi.TransactionServices;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.ibm.ejs.util.Util;
import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.cdi.CDIException;
import com.ibm.ws.cdi.CDIService;
import com.ibm.ws.cdi.extension.WebSphereCDIExtension;
import com.ibm.ws.cdi.impl.AbstractCDIRuntime;
import com.ibm.ws.cdi.interfaces.Application;
import com.ibm.ws.cdi.interfaces.CDIArchive;
import com.ibm.ws.cdi.interfaces.CDIRuntime;
import com.ibm.ws.cdi.interfaces.EjbEndpointService;
import com.ibm.ws.cdi.interfaces.ExtensionArchive;
import com.ibm.ws.container.service.app.deploy.ApplicationInfo;
import com.ibm.ws.container.service.metadata.MetaDataSlotService;
import com.ibm.ws.container.service.state.ApplicationStateListener;
import com.ibm.ws.container.service.state.StateChangeException;
import com.ibm.ws.kernel.LibertyProcess;
import com.ibm.ws.resource.ResourceRefConfigFactory;
import com.ibm.ws.runtime.metadata.ApplicationMetaData;
import com.ibm.ws.runtime.metadata.MetaData;
import com.ibm.ws.runtime.metadata.MetaDataSlot;
import com.ibm.ws.runtime.metadata.ModuleMetaData;
import com.ibm.ws.threadContext.ComponentMetaDataAccessorImpl;
import com.ibm.wsspi.adaptable.module.AdaptableModuleFactory;
import com.ibm.wsspi.artifact.factory.ArtifactContainerFactory;
import com.ibm.wsspi.classloading.ClassLoadingService;
import com.ibm.wsspi.injectionengine.InjectionEngine;
import com.ibm.wsspi.kernel.service.utils.AtomicServiceReference;
import com.ibm.wsspi.kernel.service.utils.ConcurrentServiceReferenceSet;
import com.ibm.wsspi.kernel.service.utils.ServiceAndServiceReferencePair;

/**
 * This class is to get hold all necessary services.
 */
@Component(name = "com.ibm.ws.cdi.liberty.CDIRuntimeImpl", service = { ApplicationStateListener.class, CDIService.class }, property = { "service.vendor=IBM" })
public class CDIRuntimeImpl extends AbstractCDIRuntime implements ApplicationStateListener, CDIService, CDIRuntime, CDIProvider {
    private static final TraceComponent tc = Tr.register(CDIRuntimeImpl.class);

    private final AtomicServiceReference<MetaDataSlotService> metaDataSlotServiceSR = new AtomicServiceReference<MetaDataSlotService>("metaDataSlotService");
    private final AtomicServiceReference<EjbEndpointService> ejbEndpointServiceSR = new AtomicServiceReference<EjbEndpointService>("ejbEndpointService");

    /** Reference for delayed activation of ClassLoadingService */
    private final AtomicServiceReference<ClassLoadingService> classLoadingSRRef = new AtomicServiceReference<ClassLoadingService>("classLoadingService");

    private final AtomicServiceReference<EjbServices> ejbServices = new AtomicServiceReference<EjbServices>("ejbServices");
    private final AtomicServiceReference<TransactionServices> transactionServices = new AtomicServiceReference<TransactionServices>("transactionServices");
    private final AtomicServiceReference<SecurityServices> securityServices = new AtomicServiceReference<SecurityServices>("securityServices");

    /** Reference for all portal extensions **/
    private final ConcurrentServiceReferenceSet<WebSphereCDIExtension> extensionsSR = new ConcurrentServiceReferenceSet<WebSphereCDIExtension>("extensionService");

    private final AtomicServiceReference<ArtifactContainerFactory> containerFactorySRRef = new AtomicServiceReference<ArtifactContainerFactory>("containerFactory");
    private final AtomicServiceReference<AdaptableModuleFactory> adaptableModuleFactorySRRef = new AtomicServiceReference<AdaptableModuleFactory>("adaptableModuleFactory");
    private final AtomicServiceReference<InjectionEngine> injectionEngineServiceRef = new AtomicServiceReference<InjectionEngine>("injectionEngine");
    private final AtomicServiceReference<ScheduledExecutorService> scheduledExecutorServiceRef = new AtomicServiceReference<ScheduledExecutorService>("scheduledExecutorService");
    private final AtomicServiceReference<ExecutorService> executorServiceRef = new AtomicServiceReference<ExecutorService>("executorService");

    private final AtomicServiceReference<CDI12ContainerConfig> containerConfigRef = new AtomicServiceReference<CDI12ContainerConfig>("containerConfig");
    private final AtomicServiceReference<ResourceRefConfigFactory> resourceRefConfigFactoryRef = new AtomicServiceReference<ResourceRefConfigFactory>("resourceRefConfigFactory");

    private MetaDataSlot applicationSlot;
    private boolean isClientProcess;
    private RuntimeFactory runtimeFactory;
    private ProxyServicesImpl proxyServices;

    public void activate(ComponentContext cc) {
        containerConfigRef.activate(cc);
        metaDataSlotServiceSR.activate(cc);
        ejbEndpointServiceSR.activate(cc);
        classLoadingSRRef.activate(cc);
        extensionsSR.activate(cc);
        applicationSlot = metaDataSlotServiceSR.getServiceWithException().reserveMetaDataSlot(ApplicationMetaData.class);
        ejbServices.activate(cc);
        securityServices.activate(cc);
        transactionServices.activate(cc);
        containerFactorySRRef.activate(cc);
        scheduledExecutorServiceRef.activate(cc);
        executorServiceRef.activate(cc);
        adaptableModuleFactorySRRef.activate(cc);
        injectionEngineServiceRef.activate(cc);
        resourceRefConfigFactoryRef.activate(cc);

        this.runtimeFactory = new RuntimeFactory(this);
        this.proxyServices = new ProxyServicesImpl();
        start();
    }

    public void deactivate(ComponentContext cc) {
        stop();

        this.runtimeFactory = null;
        this.proxyServices = null;

        metaDataSlotServiceSR.deactivate(cc);
        ejbEndpointServiceSR.deactivate(cc);
        classLoadingSRRef.deactivate(cc);
        ejbServices.deactivate(cc);
        securityServices.deactivate(cc);
        transactionServices.deactivate(cc);
        extensionsSR.deactivate(cc);
        containerFactorySRRef.deactivate(cc);
        scheduledExecutorServiceRef.deactivate(cc);
        executorServiceRef.deactivate(cc);
        adaptableModuleFactorySRRef.deactivate(cc);
        injectionEngineServiceRef.deactivate(cc);
        containerConfigRef.deactivate(cc);
        resourceRefConfigFactoryRef.deactivate(cc);
    }

    @Reference(name = "containerConfig", service = CDI12ContainerConfig.class)
    protected void setContainerConfig(ServiceReference<CDI12ContainerConfig> ref) {
        containerConfigRef.setReference(ref);
    }

    protected void unsetContainerConfig(ServiceReference<CDI12ContainerConfig> ref) {
        containerConfigRef.unsetReference(ref);
    }

    @Reference(name = "executorService", service = ExecutorService.class)
    protected void setExecutorService(ServiceReference<ExecutorService> ref) {
        executorServiceRef.setReference(ref);
    }

    protected void unsetExecutorService(ServiceReference<ExecutorService> ref) {
        executorServiceRef.unsetReference(ref);
    }

    @Reference(name = "scheduledExecutorService", service = ScheduledExecutorService.class, target = "(deferrable=false)")
    protected void setScheduledExecutorService(ServiceReference<ScheduledExecutorService> ref) {
        scheduledExecutorServiceRef.setReference(ref);
    }

    protected void unsetScheduledExecutorService(ServiceReference<ScheduledExecutorService> ref) {
        scheduledExecutorServiceRef.unsetReference(ref);
    }

    @Reference(name = "containerFactory", service = ArtifactContainerFactory.class, target = "(&(category=DIR)(category=JAR)(category=BUNDLE))")
    protected void setContainerFactory(ServiceReference<ArtifactContainerFactory> ref) {
        containerFactorySRRef.setReference(ref);
    }

    protected void unsetContainerFactory(ServiceReference<ArtifactContainerFactory> ref) {
        containerFactorySRRef.unsetReference(ref);
    }

    @Reference(name = "adaptableModuleFactory", service = AdaptableModuleFactory.class)
    protected void setAdaptableModuleFactory(ServiceReference<AdaptableModuleFactory> ref) {
        adaptableModuleFactorySRRef.setReference(ref);
    }

    protected void unsetAdaptableModuleFactory(ServiceReference<AdaptableModuleFactory> ref) {
        adaptableModuleFactorySRRef.unsetReference(ref);
    }

    @Reference(name = "extensionService", service = WebSphereCDIExtension.class, policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.MULTIPLE)
    protected void setExtensionService(ServiceReference<WebSphereCDIExtension> reference) {
        extensionsSR.addReference(reference);
    }

    protected void unsetExtensionService(ServiceReference<WebSphereCDIExtension> reference) {
        extensionsSR.removeReference(reference);
    }

    @Reference(name = "transactionServices", service = TransactionServices.class)
    protected void setTransactionServices(ServiceReference<TransactionServices> transactionServices) {
        this.transactionServices.setReference(transactionServices);
    }

    protected void unsetTransactionServices(ServiceReference<TransactionServices> transactionServices) {
        this.transactionServices.unsetReference(transactionServices);
    }

    @Reference(name = "securityServices", service = SecurityServices.class)
    protected void setSecurityServices(ServiceReference<SecurityServices> securityServices) {
        this.securityServices.setReference(securityServices);
    }

    protected void unsetSecurityServices(ServiceReference<SecurityServices> securityServices) {
        this.securityServices.unsetReference(securityServices);
    }

    @Reference(name = "ejbServices", service = EjbServices.class, policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.OPTIONAL)
    protected void setEjbServices(ServiceReference<EjbServices> ejbServices) {
        this.ejbServices.setReference(ejbServices);
    }

    protected void unsetEjbServices(ServiceReference<EjbServices> ejbServices) {
        this.ejbServices.unsetReference(ejbServices);
    }

    @Reference(name = "metaDataSlotService", service = MetaDataSlotService.class)
    protected void setMetaDataSlotService(ServiceReference<MetaDataSlotService> reference) {
        metaDataSlotServiceSR.setReference(reference);
    }

    protected void unsetMetaDataSlotService(ServiceReference<MetaDataSlotService> reference) {
        metaDataSlotServiceSR.unsetReference(reference);
    }

    @Reference(name = "ejbEndpointService", service = EjbEndpointService.class, policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.OPTIONAL)
    protected void setEjbEndpointService(ServiceReference<EjbEndpointService> reference) {
        ejbEndpointServiceSR.setReference(reference);
    }

    protected void unsetEjbEndpointService(ServiceReference<EjbEndpointService> reference) {
        ejbEndpointServiceSR.unsetReference(reference);
    }

    @Reference(name = "resourceRefConfigFactory", service = ResourceRefConfigFactory.class)
    protected void setResourceRefConfigFactory(ServiceReference<ResourceRefConfigFactory> resourceRefConfigFactory) {
        this.resourceRefConfigFactoryRef.setReference(resourceRefConfigFactory);
    }

    protected void unsetResourceRefConfigFactory(ServiceReference<ResourceRefConfigFactory> resourceRefConfigFactory) {
        this.resourceRefConfigFactoryRef.unsetReference(resourceRefConfigFactory);
    }

    /**
     * DS method for setting the class loading service reference.
     *
     * @param service
     */
    @Reference(name = "classLoadingService", service = ClassLoadingService.class)
    protected void setClassLoadingService(ServiceReference<ClassLoadingService> ref) {
        classLoadingSRRef.setReference(ref);
    }

    /**
     * DS method for unsetting the class loading service reference.
     *
     * @param service
     */
    protected void unsetClassLoadingService(ServiceReference<ClassLoadingService> ref) {
        classLoadingSRRef.unsetReference(ref);
    }

    @Reference(name = "injectionEngine", service = InjectionEngine.class)
    protected void setInjectionEngine(ServiceReference<InjectionEngine> ref) {
        injectionEngineServiceRef.setReference(ref);
    }

    protected void unsetInjectionEngine(ServiceReference<InjectionEngine> ref) {
        injectionEngineServiceRef.unsetReference(ref);
    }

    /**
     * DS method for setting the client process - this method will only be invoked
     * in the client Container process (due to the target). There is no need to
     * unset it.
     *
     * @param sr
     */
    @Reference(name = "libertyProcess", service = LibertyProcess.class, target = "(wlp.process.type=client)", cardinality = ReferenceCardinality.OPTIONAL)
    protected void setLibertyProcess(ServiceReference<LibertyProcess> sr) {
        isClientProcess = true;
    }

    @Override
    public ResourceRefConfigFactory getResourceRefConfigFactory() {
        return resourceRefConfigFactoryRef.getService();
    }

    @Override
    public TransactionServices getTransactionServices() {
        return transactionServices.getService();
    }

    @Override
    public SecurityServices getSecurityServices() {
        return securityServices.getService();
    }

    @Override
    public Iterator<ServiceAndServiceReferencePair<WebSphereCDIExtension>> getExtensionServices() {
        return extensionsSR.getServicesWithReferences();
    }

    public ArtifactContainerFactory getArtifactContainerFactory() {
        return containerFactorySRRef.getService();
    }

    public AdaptableModuleFactory getAdaptableModuleFactory() {
        return adaptableModuleFactorySRRef.getService();
    }

    @Override
    public MetaDataSlot getApplicationSlot() {
        return applicationSlot;
    }

    @Override
    public EjbEndpointService getEjbEndpointService() {
        return ejbEndpointServiceSR.getService();
    }

    @Override
    public InjectionEngine getInjectionEngine() {
        return injectionEngineServiceRef.getService();
    }

    @Override
    public ScheduledExecutorService getScheduledExecutorService() {
        return scheduledExecutorServiceRef.getService();
    }

    @Override
    public ExecutorService getExecutorService() {
        return executorServiceRef.getService();
    }

    @Override
    public boolean isClientProcess() {
        return isClientProcess;
    }

    /** {@inheritDoc} */
    @Override
    public void applicationStarting(ApplicationInfo appInfo) throws StateChangeException {
        try {
            Application application = this.runtimeFactory.newApplication(appInfo);
            getCDIContainer().applicationStarting(application);
        } catch (CDIException e) {
            throw new StateChangeException(e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws StateChangeException
     */
    @Override
    public void applicationStopped(ApplicationInfo appInfo) {
        Application application = this.runtimeFactory.removeApplication(appInfo);
        try {
            getCDIContainer().applicationStopped(application);
        } catch (CDIException e) {
            //FFDC and carry on
        }
    }

    @Override
    public void applicationStarted(ApplicationInfo appInfo) throws StateChangeException {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, Util.identity(this), "applicationStarted", appInfo);
        }

    }

    @Override
    public void applicationStopping(ApplicationInfo appInfo) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, Util.identity(this), "applicationStopping", appInfo);
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean isImplicitBeanArchivesScanningDisabled(CDIArchive archive) {
        //TODO check this per archive rather than for the whole server
        return this.containerConfigRef.getService().isImplicitBeanArchivesScanningDisabled();
    }

    /** {@inheritDoc} */
    @Override
    public ExtensionArchive getExtensionArchiveForBundle(Bundle bundle,
                                                         Set<String> extraClasses,
                                                         Set<String> extraAnnotations,
                                                         boolean applicationBDAsVisible,
                                                         boolean extClassesOnly) throws CDIException {

        ExtensionArchive extensionArchive = runtimeFactory.getExtensionArchiveForBundle(bundle, extraClasses, extraAnnotations, applicationBDAsVisible, extClassesOnly);

        return extensionArchive;
    }

    /** {@inheritDoc} */
    @Override
    public ProxyServices getProxyServices() {
        return proxyServices;
    }

    /** {@inheritDoc} */
    @Override
    public void beginContext(CDIArchive archive) throws CDIException {
        JndiHelperComponentMetaData cmd = null;

        MetaData metaData = archive.getMetaData();
        if (archive.isModule()) {
            ModuleMetaData moduleMetaData = (ModuleMetaData) metaData;
            cmd = new JndiHelperComponentMetaData(moduleMetaData);
        } else {
            ApplicationMetaData applicationMetaData = (ApplicationMetaData) metaData;
            cmd = new JndiHelperComponentMetaData(applicationMetaData);
        }

        ComponentMetaDataAccessorImpl accessor = ComponentMetaDataAccessorImpl.getComponentMetaDataAccessor();
        accessor.beginContext(cmd);
    }

}
