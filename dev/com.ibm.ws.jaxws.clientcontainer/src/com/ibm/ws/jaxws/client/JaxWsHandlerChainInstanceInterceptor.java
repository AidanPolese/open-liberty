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
package com.ibm.ws.jaxws.client;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.ws.handler.Handler;

import org.apache.cxf.Bus;
import org.apache.cxf.common.injection.ResourceInjector;
import org.apache.cxf.jaxws.handler.InitParamResourceResolver;
import org.apache.cxf.resource.ResourceManager;
import org.apache.cxf.resource.ResourceResolver;

import com.ibm.ws.jaxws.metadata.HandlerInfo;
import com.ibm.ws.jaxws.metadata.ParamValueInfo;
import com.ibm.ws.jaxws.support.JaxWsInstanceManager.InstanceInterceptor;
import com.ibm.ws.jaxws.support.JaxWsInstanceManager.InterceptException;
import com.ibm.ws.jaxws.support.JaxWsInstanceManager.InterceptorContext;
import com.ibm.ws.jaxws.utils.ResourceUtils;

/**
 *
 */
public class JaxWsHandlerChainInstanceInterceptor implements InstanceInterceptor {

    private final Bus bus;
    private final HandlerInfo handlerInfo;

    public JaxWsHandlerChainInstanceInterceptor(Bus bus, HandlerInfo handlerInfo) {
        this.bus = bus;
        this.handlerInfo = handlerInfo;
    }

    @Override
    public void postNewInstance(InterceptorContext ctx) throws InterceptException {
        Object o = ctx.getInstance();
        configureHandler((Handler<?>) o);

        Method postConstructMethod = ResourceUtils.findPostConstructMethod(o.getClass());
        ResourceUtils.invokeLifeCycleMethod(o, postConstructMethod);

    }

    @Override
    public void postInjectInstance(InterceptorContext ctx) {

    }

    @Override
    public void preDestroyInstance(InterceptorContext ctx) throws InterceptException {

        Object o = ctx.getInstance();
        configureHandler((Handler<?>) o);

        Method preDestroyMethod = ResourceUtils.findPreDestroyMethod(o.getClass());
        ResourceUtils.invokeLifeCycleMethod(o, preDestroyMethod);

    }

    private void configureHandler(Handler<?> handler) {

        if (handlerInfo.getInitParam().size() == 0) {
            return;
        }

        Map<String, String> params = new HashMap<String, String>();

        for (ParamValueInfo param : handlerInfo.getInitParam()) {
            params.put(trimString(param.getParamName() == null ? null : param.getParamName()),
                       trimString(param.getParamValue() == null ? null : param.getParamValue()));
        }

        initializeViaInjection(handler, params);
    }

    private void initializeViaInjection(Handler<?> handler, final Map<String, String> params) {
        if (bus != null) {
            ResourceManager resMgr = bus.getExtension(ResourceManager.class);
            List<ResourceResolver> resolvers = new ArrayList<ResourceResolver>(resMgr.getResourceResolvers());
            resolvers.add(new InitParamResourceResolver(params));
            ResourceInjector resInj = new ResourceInjector(resMgr, resolvers);
            resInj.inject(handler);
        }
    }

    private String trimString(String str) {
        return str != null ? str.trim() : null;
    }
}