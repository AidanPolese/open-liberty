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
package com.ibm.ws.cdi.web.impl;

import java.util.List;

import javax.enterprise.inject.spi.BeanManager;

import org.jboss.weld.bean.builtin.BeanManagerProxy;
import org.jboss.weld.manager.BeanManagerImpl;
import org.jboss.weld.servlet.ConversationFilter;
import org.jboss.weld.servlet.WeldInitialListener;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Reference;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.cdi.web.interfaces.CDIWebConstants;
import com.ibm.ws.cdi.web.interfaces.CDIWebRuntime;
import com.ibm.ws.cdi.web.interfaces.PreEventListenerProvider;
import com.ibm.ws.runtime.metadata.ModuleMetaData;
import com.ibm.ws.webcontainer.async.AsyncContextImpl;
import com.ibm.wsspi.kernel.service.utils.AtomicServiceReference;
import com.ibm.wsspi.webcontainer.filter.IFilterConfig;
import com.ibm.wsspi.webcontainer.filter.IFilterMapping;
import com.ibm.wsspi.webcontainer.servlet.IServletContext;

/**
 * Register WeldInitalListener on the servlet context. This listener needs to be the first listener.
 */
public abstract class AbstractInitialListenerRegistration implements PreEventListenerProvider {

    public static final String CONVERSATION_FILTER_REGISTERED = ConversationFilter.class.getName() + ".registered";
// because we use a package-info.java for trace options, just need this to register our group and message file
    private static final TraceComponent tc = Tr.register(AbstractInitialListenerRegistration.class);

    public static final String WELD_INITIAL_LISTENER_ATTRIBUTE = "org.jboss.weld.servlet.WeldInitialListener";

    private final AtomicServiceReference<CDIWebRuntime> cdiWebRuntimeRef = new AtomicServiceReference<CDIWebRuntime>(
                    "cdiWebRuntime");

    protected void activate(ComponentContext context) {
        cdiWebRuntimeRef.activate(context);
    }

    protected void deactivate(ComponentContext context) {
        cdiWebRuntimeRef.deactivate(context);
    }

    @Reference(name = "cdiWebRuntime", service = CDIWebRuntime.class)
    protected void setCdiWebRuntime(ServiceReference<CDIWebRuntime> ref) {
        cdiWebRuntimeRef.setReference(ref);
    }

    protected void unsetCdiWebRuntime(ServiceReference<CDIWebRuntime> ref) {
        cdiWebRuntimeRef.unsetReference(ref);
    }

    protected abstract ModuleMetaData getModuleMetaData(IServletContext isc);

    /** {@inheritDoc} */
    @Override
    public void registerListener(IServletContext isc) {

        CDIWebRuntime cdiWebRuntime = cdiWebRuntimeRef.getService();
        if (cdiWebRuntime != null && cdiWebRuntime.isCdiEnabled(isc)) {
            ModuleMetaData moduleMetaData = getModuleMetaData(isc);
            BeanManager beanManager = cdiWebRuntime.getModuleBeanManager(moduleMetaData);
            if (beanManager != null) {
                // check to see if the ConversationFilter is mapped.  If so we need to set a context init property
                // to prevent WeldInitialListener from doing conversation activation
                List<IFilterMapping> filterMappings = isc.getWebAppConfig().getFilterMappings();
                for (IFilterMapping filterMapping : filterMappings) {
                    IFilterConfig filterConfig = filterMapping.getFilterConfig();
                    if (CDIWebConstants.CDI_CONVERSATION_FILTER.equals(filterConfig.getFilterName())) {
                        isc.setInitParameter(CONVERSATION_FILTER_REGISTERED, Boolean.TRUE.toString());
                    }
                }
                /*
                 * Workaround jira https://issues.jboss.org/browse/WELD-1874
                 * To make sure that the WeldInitialListener has the correct beanManager we
                 * have to pass a BeanManagerImpl into the constructor, however we do not
                 * know if we have a BeanManagerImpl or a BeanManagerProxy.
                 */
                BeanManagerImpl beanManagerImpl = null;

                if (beanManager instanceof BeanManagerProxy) {
                    BeanManagerProxy proxy = (BeanManagerProxy) beanManager;
                    beanManagerImpl = proxy.delegate();
                } else if (beanManager instanceof BeanManagerImpl) {
                    beanManagerImpl = (BeanManagerImpl) beanManager;
                } else {
                    throw new RuntimeException("Unexpected beanManager instance.");
                }
                WeldInitialListener weldInitialListener = new WeldInitialListener(beanManagerImpl);
                isc.addListener(weldInitialListener);
                isc.setAttribute(WELD_INITIAL_LISTENER_ATTRIBUTE, weldInitialListener);
                //End of workaround

                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                    Tr.debug(tc, "added WeldInitialListener to the servlet context");

                //Put bean manager on the servlet context
                isc.setAttribute("javax.enterprise.inject.spi.BeanManager", beanManager);
            }

        }

    }

    /*
     * This method is called just before the first AsyncListener is registered for
     * an Async Servlet Request. It registers an async listener which will be run before
     * any application AsyncListenes.
     */
    @Override
    public void registerListener(IServletContext isc, AsyncContextImpl ac) {
        Object obj = isc.getAttribute(WELD_INITIAL_LISTENER_ATTRIBUTE);
        if (obj != null) {
            WeldInitialListener wl = (WeldInitialListener) obj;
            WeldInitialAsyncListener asyncListener = new WeldInitialAsyncListener(wl, isc);
            ac.addListener(asyncListener, ac.getIExtendedRequest(), ac.getIExtendedResponse());
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                Tr.debug(tc, "added WeldInitialAsyncListener to the asyncContext");
        }
    }

}
