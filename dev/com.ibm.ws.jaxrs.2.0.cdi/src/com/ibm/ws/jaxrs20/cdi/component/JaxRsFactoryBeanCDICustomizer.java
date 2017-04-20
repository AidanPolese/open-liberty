/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jaxrs20.cdi.component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.context.spi.Context;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.ws.rs.core.Application;

import org.apache.cxf.jaxrs.lifecycle.PerRequestResourceProvider;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import org.apache.cxf.message.Message;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.ffdc.annotation.FFDCIgnore;
import com.ibm.ws.jaxrs20.api.JaxRsFactoryBeanCustomizer;
import com.ibm.ws.jaxrs20.cdi.JAXRSCDIConstants;
import com.ibm.ws.jaxrs20.metadata.CXFJaxRsProviderResourceHolder;
import com.ibm.ws.jaxrs20.metadata.EndpointInfo;
import com.ibm.ws.jaxrs20.metadata.JaxRsModuleMetaData;
import com.ibm.ws.jaxrs20.metadata.ProviderResourceInfo;
import com.ibm.ws.jaxrs20.metadata.ProviderResourceInfo.RuntimeType;

/**
 * CDI customizer : responsible for CDI life cycle management if the Restful Application/Resource/Provider is a CDI managed bean
 * Priority is higher than EJB by default
 */
//@Component(name = "com.ibm.ws.jaxrs20.cdi.component.JaxRsFactoryCDIBeanCustomizer", immediate = true, property = { "service.vendor=IBM" })
public class JaxRsFactoryBeanCDICustomizer implements JaxRsFactoryBeanCustomizer {

    private static final TraceComponent tc = Tr.register(JaxRsFactoryBeanCDICustomizer.class);
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
        // TODO Auto-generated method stub
        return Priority.High;
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
        Map<Class<?>, Bean<?>> newContext = (Map<Class<?>, Bean<?>>) (context);
        if (newContext.isEmpty())
        {
            return false;
        }
        if (newContext.containsKey(clazz))
        {
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
        if (context == null)
        {
            return null;
        }
        Map<Class<?>, Bean<?>> newContext = (Map<Class<?>, Bean<?>>) (context);
        if (newContext.isEmpty())
        {
            return null;
        }
        T newProvider = null;
        newProvider = (T) getClassFromCDI(provider.getClass(), newContext.get(provider.getClass()));
        if (newProvider != null) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "Provider: get Provider from CDI " + provider.getClass().getName());
            }
            return newProvider;
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "Provider: Provider is null from CDI , use Provider from rs for " + provider.getClass().getName());
        }

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.jaxrs20.api.JaxRsFactoryBeanCustomizer#onSingletonServiceInit(java.lang.Object, java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T onSingletonServiceInit(T service, Object context) {
        if (context == null)
        {
            return service;
        }
        Map<Class<?>, Bean<?>> newContext = (Map<Class<?>, Bean<?>>) (context);
        if (newContext.isEmpty())
        {
            return service;
        }
        T newService = null;

        Bean<?> bean = newContext.get(service.getClass());

        newService = (T) getClassFromCDI(service.getClass(), bean);

        if (newService != null) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "Resource: get Singleton Resource from CDI " + service.getClass().getName());
            }
            //Because this message will duplicate with the message which in method onPrepareProviderResource, so ingore
            //Tr.warning(tc, "warning.jaxrs.cdi.resource", newService.getClass().getSimpleName(), "Singleton", "CDI");
            return newService;
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "Resource: Singleton Resource is null from CDI , use Resource from rs for " + service.getClass().getName());
        }
        return service;
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
        Map<Class<?>, Bean<?>> newContext = (Map<Class<?>, Bean<?>>) (context);
        if (newContext.isEmpty())
        {
            return serviceObject;
        }

        Object newServiceObject = null;
        Class<?> clazz = serviceObject.getClass();

        newServiceObject = getClassFromCDI(clazz, newContext.get(clazz));

        if (newServiceObject != null) {

            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "Resource: get Per Request Resource from CDI " + clazz.getName());
            }
            return (T) newServiceObject;
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "Resource: Per Request Resource is null from CDI , use Resource from rs for " + clazz.getName());
        }
        return serviceObject;
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
        // TODO Auto-generated method stub

    }

    private Bean<?> getBeanFromCDI(Class<?> clazz) {
        if (!isCDIEnabled())
        {
            return null;
        }
        BeanManager manager = getBeanManager();
        Set<Bean<?>> beans = manager.getBeans(clazz);
        Bean<?> bean = manager.resolve(beans);
        return bean;
    }

    @FFDCIgnore(value = { Exception.class, ContextNotActiveException.class })
    private Object getClassFromCDI(Class<?> clazz, Bean<?> bean) {
        BeanManager manager = getBeanManager();
        Object newServiceObject = null;

        if (bean != null) {
            Object obj = null;
            Context cdiContext = null;
            try {
                cdiContext = manager.getContext(bean.getScope());
            } catch (ContextNotActiveException e)
            {
                //do nothing
            } catch (Exception e)
            {
                //do nothing
            }
            if (cdiContext != null)
            {
                obj = cdiContext.get(bean);
            }

            //Because the CDI Interceptor doesn't work, get object from reference directly
            //This condition code should be used for context injection problem, but it has been fixed from framework level
            if (obj == null)
            {
                obj = manager.getReference(bean, clazz,
                                           manager.createCreationalContext(bean));
//                if (bean.getScope().getCanonicalName().equalsIgnoreCase(JAXRSCDIConstants.DEPENDENT_SCOPE))
//                {
                newServiceObject = obj;
//                }
//                else {
//
//                    obj.toString();
//
//                    cdiContext = manager.getContext(bean.getScope());
//                    newServiceObject = cdiContext.get(bean);
//                }
            }
            else {
                newServiceObject = obj;
            }

        }
        return newServiceObject;
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

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.jaxrs20.api.JaxRsFactoryBeanCustomizer#onApplicationInit(javax.ws.rs.core.Application, com.ibm.ws.jaxrs20.metadata.JaxRsModuleMetaData)
     */
    @Override
    public Application onApplicationInit(Application app, JaxRsModuleMetaData metaData) {
        Application newApp = null;

        newApp = (Application) getClassFromCDI(app.getClass());
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
    @FFDCIgnore(value = { IllegalAccessException.class, InstantiationException.class })
    @Override
    public void onPrepareProviderResource(BeanCustomizerContext context) {

        EndpointInfo endpointInfo = context.getEndpointInfo();
        Set<ProviderResourceInfo> perRequestProviderAndPathInfos = endpointInfo.getPerRequestProviderAndPathInfos();
        Set<ProviderResourceInfo> singletonProviderAndPathInfos = endpointInfo.getSingletonProviderAndPathInfos();
        Map<Class<?>, Bean<?>> resourcesManagedbyCDI = new HashMap<Class<?>, Bean<?>>();
//        List<ProviderResourceInfo> list1 = new ArrayList<ProviderResourceInfo>();
//        List<ProviderResourceInfo> list2 = new ArrayList<ProviderResourceInfo>();

        CXFJaxRsProviderResourceHolder cxfPRHolder = context.getCxfRPHolder();
        for (ProviderResourceInfo p : perRequestProviderAndPathInfos)
        {
            /**
             * CDI customizer only check if the POJO type bean is CDI bean
             * because when EJB priority is higher than CDI, engine will take the bean as EJB but not CDI,
             * that means EJB already processes it, CDI should not process it again,
             * then CDI should not cache the bean's info in resourcesManagedbyCDI
             */
            if (p.getRuntimeType() != RuntimeType.POJO)
                continue;

            Bean<?> bean = getBeanFromCDI(p.getProviderResourceClass());
            if (bean != null)
            {

                p.setRuntimeType(RuntimeType.CDI);
                resourcesManagedbyCDI.put(p.getProviderResourceClass(), bean);

                String scopeName = bean.getScope().getCanonicalName();
                if (p.isJaxRsProvider()) {
                    //if CDI Scope is APPLICATION_SCOPE or DEPENDENT_SCOPE, report warning and no action: get provider from CDI
                    if (validSingletonScopeList.contains(scopeName)) {

//                        p.putCustomizedProperty("scope", "s");
                        Tr.warning(tc, "warning.jaxrs.cdi.provider.mismatch", p.getProviderResourceClass().getSimpleName(), bean.getScope().getSimpleName(), "CDI");
                    }
                    //else report warning, keep using provider from rs: change to use RuntimeType.POJO
                    else {
                        p.setRuntimeType(RuntimeType.POJO);
                        resourcesManagedbyCDI.remove(p.getProviderResourceClass());
                        Tr.warning(tc, "warning.jaxrs.cdi.provider.mismatch", p.getProviderResourceClass().getSimpleName(), bean.getScope().getSimpleName(), "JAXRS");
                    }
                } else {
                    if (!validRequestScopeList.contains(scopeName)) //means this is @ApplicationScoped in CDI
                    {
                        String getFrom = "CDI";
                        try {
                            Object po = p.getProviderResourceClass().newInstance();
                            p.setObject(po);
                            cxfPRHolder.removeResouceProvider(p.getProviderResourceClass());//remove from original ResourceProvider map and re-add the new one.
                            cxfPRHolder.addResouceProvider(p.getProviderResourceClass(), new SingletonResourceProvider(po));

                        } catch (IllegalAccessException e) {
                            // report error as this can not be instanced by CDI, need to return back to pojo
                            //todo warning
                            p.setRuntimeType(RuntimeType.POJO);
                            resourcesManagedbyCDI.remove(p.getProviderResourceClass());
                            getFrom = "JAXRS";
                        } catch (InstantiationException e) {
                            // report error as this can not be instanced by CDI, need to return back to pojo
                            //todo warning
                            p.setRuntimeType(RuntimeType.POJO);
                            resourcesManagedbyCDI.remove(p.getProviderResourceClass());
                            getFrom = "JAXRS";
                        }
                        Tr.warning(tc, "warning.jaxrs.cdi.resource.mismatch", p.getProviderResourceClass().getSimpleName(), "PerRequest", bean.getScope().getSimpleName(), getFrom);
                    }

                }
            }

        }
        for (ProviderResourceInfo o : singletonProviderAndPathInfos)
        {
            /**
             * CDI customizer only check if the POJO type bean is CDI bean
             * because when EJB priority is higher than CDI, engine will take the bean as EJB but not CDI,
             * that means EJB already processes it, CDI should not process it again,
             * then CDI should not cache the bean's info in resourcesManagedbyCDI
             */
            if (o.getRuntimeType() != RuntimeType.POJO)
                continue;

            Bean<?> bean = getBeanFromCDI(o.getProviderResourceClass());
            if (bean != null)
            {

                o.setRuntimeType(RuntimeType.CDI);
                resourcesManagedbyCDI.put(o.getProviderResourceClass(), bean);
                String scopeName = bean.getScope().getCanonicalName();
                if (o.isJaxRsProvider()) {
                    if (validSingletonScopeList.contains(scopeName)) {
//                        o.putCustomizedProperty("scope", "s");
                        Tr.warning(tc, "warning.jaxrs.cdi.provider.mismatch", o.getProviderResourceClass().getSimpleName(), bean.getScope().getSimpleName(), "CDI");
                    }
                    //else report warning, keep using provider from rs: change to use RuntimeType.POJO
                    else {
                        o.setRuntimeType(RuntimeType.POJO);
                        resourcesManagedbyCDI.remove(o.getProviderResourceClass());
                        Tr.warning(tc, "warning.jaxrs.cdi.provider.mismatch", o.getProviderResourceClass().getSimpleName(), bean.getScope().getSimpleName(), "JAXRS");
                    }

                    //Old check is this, need verify by using FAT:
                    //if CDI Scope is APPLICATION_SCOPE or DEPENDENT_SCOPE, report warning and no action: get provider from CDI
                    //else report warning and no action: get provider from CDI

                } else {
                    if (!validSingletonScopeList.contains(scopeName)) { // means CDI is per-request, then modify cxfPRHolder to per-request as well.
                        cxfPRHolder.removeResouceProvider(o.getProviderResourceClass());//remove from original ResourceProvider map and re-add the new one.
                        cxfPRHolder.addResouceProvider(o.getProviderResourceClass(), new PerRequestResourceProvider(o.getProviderResourceClass()));
                        Tr.warning(tc, "warning.jaxrs.cdi.resource.mismatch", o.getProviderResourceClass().getSimpleName(), "Singleton", bean.getScope().getSimpleName(), "CDI");
                    }

                }
            }

        }

        context.setContextObject(resourcesManagedbyCDI);

    }

    /**
     * CDI doesn't require to wrap proxy on the provider
     */
    @Override
    public <T> T onSetupProviderProxy(T provider, Object contextObject) {
        return null;
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

//    }

    private boolean isCDIEnabled() {

        BeanManager beanManager = getBeanManager();
        return beanManager == null ? false : true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.jaxrs20.api.JaxRsFactoryBeanCustomizer#destroyApplicationScopeResources(com.ibm.ws.jaxrs20.metadata.JaxRsModuleMetaData)
     */
    @Override
    public void destroyApplicationScopeResources(JaxRsModuleMetaData jaxRsModuleMetaData) {
        // TODO Auto-generated method stub

    }

}
