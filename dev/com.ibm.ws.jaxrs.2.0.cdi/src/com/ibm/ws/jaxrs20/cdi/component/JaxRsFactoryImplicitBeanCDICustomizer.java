/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014, 2017
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.jaxrs20.cdi.component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;

import org.apache.cxf.Bus;
import org.apache.cxf.jaxrs.lifecycle.PerRequestResourceProvider;
import org.apache.cxf.jaxrs.utils.AnnotationUtils;
import org.apache.cxf.message.Message;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.ffdc.annotation.FFDCIgnore;
import com.ibm.ws.jaxrs20.JaxRsConstants;
import com.ibm.ws.jaxrs20.api.JaxRsFactoryBeanCustomizer;
import com.ibm.ws.jaxrs20.cdi.JAXRSCDIConstants;
import com.ibm.ws.jaxrs20.metadata.CXFJaxRsProviderResourceHolder;
import com.ibm.ws.jaxrs20.metadata.EndpointInfo;
import com.ibm.ws.jaxrs20.metadata.JaxRsModuleMetaData;
import com.ibm.ws.jaxrs20.metadata.ProviderResourceInfo;
import com.ibm.ws.jaxrs20.metadata.ProviderResourceInfo.RuntimeType;
import com.ibm.ws.jaxrs20.utils.JaxRsUtils;
import com.ibm.ws.managedobject.ManagedObject;
import com.ibm.ws.managedobject.ManagedObjectException;
import com.ibm.ws.managedobject.ManagedObjectFactory;
import com.ibm.ws.managedobject.ManagedObjectService;
import com.ibm.ws.runtime.metadata.ModuleMetaData;
import com.ibm.wsspi.adaptable.module.Container;
import com.ibm.wsspi.kernel.service.utils.AtomicServiceReference;

/**
 * CDI customizer : responsible for CDI life cycle management if the Restful Application/Resource/Provider is a CDI managed bean
 * Priority is higher than EJB by default
 */
@Component(name = "com.ibm.ws.jaxrs20.cdi.component.JaxRsFactoryImplicitBeanCDICustomizer", immediate = true, property = { "service.vendor=IBM" })
public class JaxRsFactoryImplicitBeanCDICustomizer implements JaxRsFactoryBeanCustomizer {

    private static final TraceComponent tc = Tr.register(JaxRsFactoryImplicitBeanCDICustomizer.class);
    Container containerContext;
    private final AtomicServiceReference<ManagedObjectService> managedObjectServiceRef = new AtomicServiceReference<ManagedObjectService>("managedObjectService");

    private static List<String> validRequestScopeList = new ArrayList<String>();
    private static List<String> validSingletonScopeList = new ArrayList<String>();
    static {
        validRequestScopeList.add(JAXRSCDIConstants.REQUEST_SCOPE);
        validRequestScopeList.add(JAXRSCDIConstants.DEPENDENT_SCOPE);
        validRequestScopeList.add(JAXRSCDIConstants.SESSION_SCOPE);
        validSingletonScopeList.add(JAXRSCDIConstants.DEPENDENT_SCOPE);
        validSingletonScopeList.add(JAXRSCDIConstants.APPLICATION_SCOPE);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ws.jaxrs20.api.JaxRsFactoryBeanCustomizer#getPriority()
     */
    @Override
    public Priority getPriority() {
        return Priority.Higher;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ws.jaxrs20.api.JaxRsFactoryBeanCustomizer#isCustomizableBean(java.lang.Class, java.lang.Object)
     */
    @Override
    public boolean isCustomizableBean(Class<?> clazz, Object context) {
        if (context == null) {
            return false;
        }
        @SuppressWarnings("unchecked")
        Map<Class<?>, ManagedObject<?>> newContext = (Map<Class<?>, ManagedObject<?>>) (context);
        if (newContext.isEmpty()) {
            return false;
        }
        if (newContext.containsKey(clazz)) {
            return true;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ws.jaxrs20.api.JaxRsFactoryBeanCustomizer#onSingletonProviderInit(java.lang.Object, java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T onSingletonProviderInit(T provider, Object context, Message m) {
        if (context == null) {
            return null;
        }
        Map<Class<?>, ManagedObject<?>> newContext = (Map<Class<?>, ManagedObject<?>>) (context);
        if (newContext.isEmpty()) {
            return null;
        }
        ManagedObject<?> managedObject = newContext.get(provider.getClass());
        Object newProviderObject = null;
        if (managedObject == null) {
            newProviderObject = getInstanceFromManagedObject(provider, context);
        } else {
            newProviderObject = managedObject.getObject();
        }

        return (T) newProviderObject;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ws.jaxrs20.api.JaxRsFactoryBeanCustomizer#onSingletonServiceInit(java.lang.Object, java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T onSingletonServiceInit(T service, Object context) {
        if (context == null) {
            return null;
        }
        Map<Class<?>, ManagedObject<?>> newContext = (Map<Class<?>, ManagedObject<?>>) (context);
        if (newContext.isEmpty()) {
            return null;
        }

        ManagedObject<?> managedObject = newContext.get(service.getClass());
        Object newServiceObject = null;
        if (managedObject == null) {
            newServiceObject = getInstanceFromManagedObject(service, context);
        } else {
            newServiceObject = managedObject.getObject();
        }
        return (T) newServiceObject;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ws.jaxrs20.api.JaxRsFactoryBeanCustomizer#beforeServiceInvoke(java.lang.Object, boolean, java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T beforeServiceInvoke(T serviceObject, boolean isSingleton,
                                     Object context) {
        if (isSingleton || context == null) {
            return serviceObject;
        }

        Map<Class<?>, ManagedObject<?>> newContext = (Map<Class<?>, ManagedObject<?>>) (context);
        if (newContext.isEmpty()) {
            return null;
        }

//        ManagedObject<?> managedObject = newContext.get(serviceObject.getClass());
        Object newServiceObject = null;
//        if (managedObject == null) {
        newServiceObject = getInstanceFromManagedObject(serviceObject, context);
//        } else {
//            newServiceObject = managedObject.getObject();
//        }
        return (T) newServiceObject;

    }

    private Object getClassFromCDI(Class<?> clazz) {
        BeanManager manager = getBeanManager();
        Bean<?> bean = getBeanFromCDI(clazz);
        Object obj = null;
        if (bean != null) {
            obj = manager.getReference(bean, clazz,
                                       manager.createCreationalContext(bean));
        }
        return obj;
    }

    @SuppressWarnings("unchecked")
    private <T> T getInstanceFromManagedObject(T serviceObject, Object context) {

        Class<?> clazz = serviceObject.getClass();
        //temp fix for session problem

        Object rtn = getClassFromCDI(clazz);
        if (rtn != null) {
            return (T) rtn;
        }
        //end temp fix
        Map<Class<?>, ManagedObject<?>> newContext = (Map<Class<?>, ManagedObject<?>>) (context);

        ManagedObject<?> newServiceObject = null;

        newServiceObject = getClassFromManagedObject(clazz);
        if (newServiceObject != null) {

            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "Get instance from CDI " + clazz.getName());
            }
            newContext.put(clazz, newServiceObject);

            return (T) newServiceObject.getObject();
        } else {
            newContext.remove(clazz);
        }
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "Get instance from CDI is null , use from rs for " + clazz.getName());
        }
        return serviceObject;
    }

    /**
     * @param clazz
     * @return
     */
    @FFDCIgnore(value = { Exception.class })
    private ManagedObject<?> getClassFromManagedObject(Class<?> clazz) {

        ManagedObjectFactory<?> managedObjectFactory = getManagedObjectFactory(clazz, containerContext);

        ManagedObject<?> bean = null;
        try {
            bean = managedObjectFactory.createManagedObject();
        } catch (Exception e) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "Couldn't create object instance from ManagedObjectFactory for : " + clazz.getName() + ", but ignore the FFDC: " + e.toString());
            }
        }

        if (bean == null) {
            return null;
        }

        return bean;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ws.jaxrs20.api.JaxRsFactoryBeanCustomizer#serviceInvoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[], boolean, java.lang.Object)
     */
    @Override
    public Object serviceInvoke(Object serviceObject, Method m, Object[] params, boolean isSingleton, Object context, Message msg) throws Exception {
        return m.invoke(serviceObject, params);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ws.jaxrs20.api.JaxRsFactoryBeanCustomizer#afterServiceInvoke(java.lang.Object, boolean, java.lang.Object)
     */
    @Override
    public void afterServiceInvoke(Object serviceObject, boolean isSingleton, Object context) {
        @SuppressWarnings("unchecked")
        Map<Class<?>, ManagedObject<?>> newContext = (Map<Class<?>, ManagedObject<?>>) (context);
        ManagedObject<?> mo = newContext.get(serviceObject.getClass());
        if (!isSingleton) {
            if (mo != null) {
                mo.release();
            }
            newContext.put(serviceObject.getClass(), null);
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ws.jaxrs20.api.JaxRsFactoryBeanCustomizer#onApplicationInit(javax.ws.rs.core.Application, com.ibm.ws.jaxrs20.metadata.JaxRsModuleMetaData)
     */
    @Override
    public Application onApplicationInit(Application app, JaxRsModuleMetaData metaData) {

        this.containerContext = metaData.getServerMetaData().getModuleMetaData().getModuleContainer();

        Class<?> clazz = app.getClass();

        if (!shouldHandle(clazz, true)) {
            return null;
        }
        Application newApp = null;

        ManagedObject<?> mo = getClassFromManagedObject(clazz);
        metaData.setManagedAppRef(mo);

        if (mo != null) {
            newApp = (Application) (mo.getObject());
        }

        if (newApp == null) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "App: app is null from CDI , get app from rs for " + app.getClass().getName());
            }
            return app;
        }
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "App: get app from CDI " + app.getClass().getName());
        }
        return newApp;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ws.jaxrs20.api.JaxRsFactoryBeanCustomizer#onPrepareProviderResource(com.ibm.ws.jaxrs20.api.JaxRsFactoryBeanCustomizer.BeanCustomizerContext)
     */
    @Override
    public void onPrepareProviderResource(BeanCustomizerContext context) {

        EndpointInfo endpointInfo = context.getEndpointInfo();
        Set<ProviderResourceInfo> perRequestProviderAndPathInfos = endpointInfo.getPerRequestProviderAndPathInfos();
        Set<ProviderResourceInfo> singletonProviderAndPathInfos = endpointInfo.getSingletonProviderAndPathInfos();
        Map<Class<?>, ManagedObject<?>> resourcesManagedbyCDI = new HashMap<Class<?>, ManagedObject<?>>();

        if (containerContext == null) {
            containerContext = context.getModuleMetaData().getModuleContainer();
        }

        CXFJaxRsProviderResourceHolder cxfPRHolder = context.getCxfRPHolder();
        for (ProviderResourceInfo p : perRequestProviderAndPathInfos) {
            /**
             * CDI customizer only check if the POJO type bean is CDI bean
             * because when EJB priority is higher than CDI, engine will take the bean as EJB but not CDI,
             * that means EJB already processes it, CDI should not process it again,
             * then CDI should not cache the bean's info in resourcesManagedbyCDI
             */
            if (p.getRuntimeType() != RuntimeType.POJO)
                continue;

            Class<?> clazz = p.getProviderResourceClass();
            if (!hasValidConstructor(clazz, false)) {
                continue;
            }

            Bean<?> bean = null;
            try {
                bean = getBeanFromCDI(clazz);
            } catch (Exception e1) {
                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                    Tr.debug(tc, "ManagedObjectFactory failed to create bean", e1);
                }
            }

            if (bean != null) {

                String scopeName = bean.getScope().getName();
                p.setRuntimeType(RuntimeType.CDI);
                resourcesManagedbyCDI.put(p.getProviderResourceClass(), null);

                if (p.isJaxRsProvider()) {
                    //if CDI Scope is APPLICATION_SCOPE or DEPENDENT_SCOPE, report warning and no action: get provider from CDI
                    if (validSingletonScopeList.contains(scopeName)) {
                        Tr.warning(tc, "warning.jaxrs.cdi.provider.mismatch", clazz.getSimpleName(), scopeName, "CDI");
                    }
                    //else report warning, keep using provider from rs: change to use RuntimeType.POJO
                    else {
                        p.setRuntimeType(RuntimeType.POJO);
                        resourcesManagedbyCDI.remove(p.getProviderResourceClass());
                        Tr.warning(tc, "warning.jaxrs.cdi.provider.mismatch", clazz.getSimpleName(), scopeName, "JAXRS");
                    }
                } else {
                    if (!validRequestScopeList.contains(scopeName)) { //means this is @ApplicationScoped in CDI
                        Tr.warning(tc, "warning.jaxrs.cdi.resource.mismatch", clazz.getSimpleName(), "PerRequest", scopeName, "CDI");
                    }

                }
            } else {

                if (shouldHandle(clazz, false)) {
                    p.setRuntimeType(RuntimeType.IMPLICITBEAN);
                    resourcesManagedbyCDI.put(clazz, null);
                }
                continue;

            }

        }
        for (ProviderResourceInfo o : singletonProviderAndPathInfos) {
            /**
             * CDI customizer only check if the POJO type bean is CDI bean
             * because when EJB priority is higher than CDI, engine will take the bean as EJB but not CDI,
             * that means EJB already processes it, CDI should not process it again,
             * then CDI should not cache the bean's info in resourcesManagedbyCDI
             */
            if (o.getRuntimeType() != RuntimeType.POJO)
                continue;

            Class<?> clazz = o.getProviderResourceClass();
            if (!hasValidConstructor(clazz, true)) {
                continue;
            }

            Bean<?> bean = null;
            try {
                bean = getBeanFromCDI(clazz);
            } catch (Exception e1) {
                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                    Tr.debug(tc, "ManagedObjectFactory failed to create bean", e1);
                }
            }

            if (bean != null) {

                String scopeName = bean.getScope().getName();
                o.setRuntimeType(RuntimeType.CDI);
                resourcesManagedbyCDI.put(o.getProviderResourceClass(), null);
                if (o.isJaxRsProvider()) {
                    if (validSingletonScopeList.contains(scopeName)) {
//                        o.putCustomizedProperty("scope", "s");
                        Tr.warning(tc, "warning.jaxrs.cdi.provider.mismatch", clazz.getSimpleName(), scopeName, "CDI");
                    }
                    //else report warning, keep using provider from rs: change to use RuntimeType.POJO
                    else {
                        o.setRuntimeType(RuntimeType.POJO);
                        resourcesManagedbyCDI.remove(clazz);
                        Tr.warning(tc, "warning.jaxrs.cdi.provider.mismatch", clazz.getSimpleName(), scopeName, "JAXRS");
                    }

                    //Old check is this, need verify by using FAT:
                    //if CDI Scope is APPLICATION_SCOPE or DEPENDENT_SCOPE, report warning and no action: get provider from CDI
                    //else report warning and no action: get provider from CDI

                } else {
                    if (!validSingletonScopeList.contains(scopeName)) { // means CDI is per-request, then modify cxfPRHolder to per-request as well.
                        cxfPRHolder.removeResouceProvider(clazz);//remove from original ResourceProvider map and re-add the new one.
                        cxfPRHolder.addResouceProvider(clazz, new PerRequestResourceProvider(clazz));
                        Tr.warning(tc, "warning.jaxrs.cdi.resource.mismatch", clazz.getSimpleName(), "Singleton", scopeName, "CDI");
                    }

                }
            } else {
                if (shouldHandle(clazz, false)) {
                    o.setRuntimeType(RuntimeType.IMPLICITBEAN);
                    resourcesManagedbyCDI.put(clazz, null);
                }
                continue;
            }

        }

        context.setContextObject(resourcesManagedbyCDI);

    }

    private Bean<?> getBeanFromCDI(Class<?> clazz) {
        if (!isCDIEnabled()) {
            return null;
        }
        BeanManager manager = getBeanManager();
        Set<Bean<?>> beans = manager.getBeans(clazz);
        Bean<?> bean = manager.resolve(beans);
        return bean;
    }

    private boolean isCDIEnabled() {

        BeanManager beanManager = getBeanManager();
        return beanManager == null ? false : true;
    }

    @FFDCIgnore(NameNotFoundException.class)
    private BeanManager getBeanManager() {

        BeanManager manager = null;
//        BeanManager manager = JAXRSCDIServiceImplByJndi.getBeanManager();
//
//        if (manager != null)
//        {
//            return manager;
//        }
//        else {
        try {
            InitialContext initialContext = new InitialContext();
            manager = (BeanManager) initialContext.lookup(JAXRSCDIConstants.JDNI_STRING);
            JAXRSCDIServiceImplByJndi.setBeanManager(manager);

            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "Found BeanManager through JNDI lookup: " + JAXRSCDIConstants.JDNI_STRING + ". The manager is: " + manager.toString());
            }
        } catch (NameNotFoundException e) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "Couldn't get BeanManager through JNDI: " + JAXRSCDIConstants.JDNI_STRING + ", but ignore the FFDC: " + e.toString());
            }
        } catch (Exception e) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "Got BeanManager through JNDI failed: " + e.toString());
            }
        }
        return manager;
    }

    /**
     * @param clazz
     * @return
     */
    private boolean shouldHandle(Class<?> clazz, boolean singleton) {
        if (!hasValidConstructor(clazz, singleton)) {
            return false;
        }
        return hasInjectAnnotation(clazz);

    }

    /**
     * @param clazz
     * @return
     */
    private boolean hasValidConstructor(Class<?> clazz, boolean singleton) {
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        if (constructors.length == 0) {
            return true;
        }
        for (Constructor<?> c : constructors) {
            boolean hasInject = c.isAnnotationPresent(Inject.class);
            Class<?>[] params = c.getParameterTypes();
            Annotation[][] anns = c.getParameterAnnotations();
            boolean match = true;
            for (int i = 0; i < params.length; i++) {
                if (singleton) {
                    //annotation is not null and not equals context
                    if (AnnotationUtils.getAnnotation(anns[i], Context.class) == null && !(anns.length == 0 && hasInject)) {
                        match = false;
                        break;
                    }
                } else if ((!AnnotationUtils.isValidParamAnnotations(anns[i])) && !(anns.length == 0 && hasInject)) {
                    match = false;
                    break;
                }
            }

            if (match) {
                return true;
            }

        }
        return false;
    }

    /**
     * @param clazz
     * @return
     */
    private boolean hasInjectAnnotation(Class<?> clazz) {
        if (clazz.isAnnotationPresent(Inject.class)) {
            return true;
        } else {

            Field[] fields = clazz.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                if (fields[i].isAnnotationPresent(Inject.class)) {
                    return true;
                }
            }

            Method[] methods = clazz.getDeclaredMethods();
            for (int i = 0; i < methods.length; i++) {
                if (methods[i].isAnnotationPresent(Inject.class)) {
                    return true;
                }
            }

            Constructor<?>[] c = clazz.getConstructors();
            for (int i = 0; i < c.length; i++) {
                if (c[i].isAnnotationPresent(Inject.class)) {
                    return true;
                }
            }

            Class<?> cls = clazz.getSuperclass();
            if (cls != null) {
                return hasInjectAnnotation(cls);
            } else {
                return false;
            }
        }
    }

    /**
     * CDI doesn't require to wrap proxy on the provider
     */
    @Override
    public <T> T onSetupProviderProxy(T provider, Object contextObject) {
        return null;
    }

    private ManagedObjectFactory<?> getManagedObjectFactory(Class<?> clazz, Container container) {

        if (container == null) {
            return null;
        }

//        if (managedObjectFactoryCache.containsKey(clazz)) {
//            return managedObjectFactoryCache.get(clazz);
//        };

        ManagedObjectFactory<?> mof = null;
        try {
            ManagedObjectService mos = managedObjectServiceRef.getServiceWithException();
            if (mos == null) {
                return null;
            }
            ModuleMetaData mmd = JaxRsUtils.getModuleInfo(container).getMetaData();
            mof = mos.createManagedObjectFactory(mmd, clazz, true);
//            managedObjectFactoryCache.put(clazz, mof);

            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "Successfully to create ManagedObjectFactory for class: " + clazz.getName());
            }
        } catch (ManagedObjectException e) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "Fail to create ManagedObjectFactory for class: " + clazz.getName() + " Exception is: " + e.toString());
            }
        }

        return mof;
    }

    public void activate(ComponentContext compcontext, Map<String, Object> properties) {

        this.managedObjectServiceRef.activate(compcontext);

    }

    public void deactivate(ComponentContext componentContext) {

        this.managedObjectServiceRef.deactivate(componentContext);

    }

    @Reference(name = "managedObjectService",
               service = ManagedObjectService.class,
               policy = ReferencePolicy.DYNAMIC,
               policyOption = ReferencePolicyOption.GREEDY)
    protected void setManagedObjectService(ServiceReference<ManagedObjectService> ref) {
        this.managedObjectServiceRef.setReference(ref);
    }

    protected void unsetManagedObjectService(ServiceReference<ManagedObjectService> ref) {
        this.managedObjectServiceRef.unsetReference(ref);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ws.jaxrs20.api.JaxRsFactoryBeanCustomizer#destroyApplicationScopeResources()
     */
    @Override
    public void destroyApplicationScopeResources(JaxRsModuleMetaData jaxRsModuleMetaData) {

        Bus bus = jaxRsModuleMetaData.getServerMetaData().getServerBus();

        @SuppressWarnings("unchecked")
        Map<String, BeanCustomizerContext> beanCustomizerContexts = (Map<String, BeanCustomizerContext>) bus.getProperty(JaxRsConstants.ENDPOINT_BEANCUSTOMIZER_CONTEXTOBJ);
        if (beanCustomizerContexts == null) {
            return;
        }
        @SuppressWarnings("unchecked")
        Map<Class<?>, ManagedObject<?>> newContext = (Map<Class<?>, ManagedObject<?>>) beanCustomizerContexts.get(Integer.toString(hashCode()));
        if (newContext == null) {
            return;
        }
        Collection<ManagedObject<?>> objects = newContext.values();
        for (ManagedObject<?> mo : objects) {

            if (mo != null) {
                mo.release();
            }
        }

//destroy application
        ManagedObject<?> appObject = (ManagedObject<?>) jaxRsModuleMetaData.getManagedAppRef();
        if (appObject != null) {
            appObject.release();
        }
    }
}
