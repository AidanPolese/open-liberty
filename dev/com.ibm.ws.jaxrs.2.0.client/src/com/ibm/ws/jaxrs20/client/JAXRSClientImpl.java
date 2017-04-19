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
package com.ibm.ws.jaxrs20.client;

import java.net.URI;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.UriBuilder;

import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.jaxrs.client.ClientConfiguration;
import org.apache.cxf.jaxrs.client.JAXRSClientFactoryBean;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.jaxrs.client.spec.ClientImpl;
import org.apache.cxf.jaxrs.client.spec.TLSConfiguration;
import org.apache.cxf.phase.Phase;

import com.ibm.ws.jaxrs20.bus.LibertyApplicationBus;
import com.ibm.ws.jaxrs20.client.bus.LibertyJAXRSClientBusFactory;
import com.ibm.ws.jaxrs20.client.configuration.LibertyJaxRsClientProxyInterceptor;
import com.ibm.ws.jaxrs20.client.configuration.LibertyJaxRsClientTimeOutInterceptor;
import com.ibm.ws.jaxrs20.client.security.LibertyJaxRsClientSSLOutInterceptor;
import com.ibm.ws.jaxrs20.client.security.ltpa.LibertyJaxRsClientLtpaInterceptor;
import com.ibm.ws.jaxrs20.client.util.JaxRSClientUtil;

/**
 *
 */
public class JAXRSClientImpl extends ClientImpl {

    protected boolean closed;
    protected Set<WebClient> baseClients = new HashSet<WebClient>();
    protected boolean hasSSLConfigInfo = false;
    private TLSConfiguration secConfig = null;
    //Defect 202957 move busCache from ClientMetaData to JAXRSClientImpl
    //Before this change, all the WebTarget has same url in a web module share a bus
    //After this change, all the WebTarget has same url in a JAXRSClientImpl share a bus
    private final Map<String, LibertyApplicationBus> busCache = new ConcurrentHashMap<String, LibertyApplicationBus>();

    /**
     * @param config
     * @param secConfig
     */
    public JAXRSClientImpl(Configuration config, TLSConfiguration secConfig) {
        super(config, secConfig);
        this.secConfig = secConfig;
        /**
         * check if there is any user's programmed SSLContext info
         */
        TLSClientParameters ttClientParams = secConfig.getTlsClientParams();
        if (secConfig.getSslContext() != null
            ||
            ((ttClientParams.getTrustManagers() != null && ttClientParams.getTrustManagers().length > 0) && (ttClientParams.getKeyManagers() != null && ttClientParams.getKeyManagers().length > 0))) {
            hasSSLConfigInfo = true;
        }
    }

    /**
     * override this method, then put our webclient into cxf client API
     */
    @Override
    public WebTarget target(UriBuilder builder) {
        WebTargetImpl wt = (WebTargetImpl) super.target(builder);

        //construct our own webclient
        JAXRSClientFactoryBean bean = new JAXRSClientFactoryBean();
        URI uri = builder.build();
        bean.setAddress(uri.toString());
        WebClient targetClient = bean.createWebClient();

        //get ClientCongfiguration
        ClientConfiguration ccfg = WebClient.getConfig(targetClient);

        //add Liberty Jax-RS Client Timeout Interceptor to configure the timeout
        ccfg.getOutInterceptors().add(new LibertyJaxRsClientTimeOutInterceptor(Phase.PRE_LOGICAL));

        //add Liberty Jax-RS Client Proxy Interceptor to configure the proxy
        ccfg.getOutInterceptors().add(new LibertyJaxRsClientProxyInterceptor(Phase.PRE_LOGICAL));

        //add Liberty Ltpa handler Interceptor to check if is using ltpa token for sso 
        ccfg.getOutInterceptors().add(new LibertyJaxRsClientLtpaInterceptor());

        /**
         * if no any user programmed SSL context info
         * put the LibertyJaxRsClientSSLOutInterceptor into client OUT interceptor chain
         * see if Liberty SSL can help
         */
        if (hasSSLConfigInfo == false) {
            LibertyJaxRsClientSSLOutInterceptor sslOutInterceptor = new LibertyJaxRsClientSSLOutInterceptor(Phase.PRE_LOGICAL);
            sslOutInterceptor.setTLSConfiguration(secConfig);
            ccfg.getOutInterceptors().add(sslOutInterceptor);
        }
        //set bus
        LibertyApplicationBus bus;
        //202957 same url use same bus, add a lock to busCache to ensure only one bus will be created in concurrent mode.
        //ConcurrentHashMap can't ensure that.
        String id = JaxRSClientUtil.convertURItoBusId(uri.toString());
        synchronized (busCache) {
            bus = busCache.get(id);
            if (bus == null) {
                bus = LibertyJAXRSClientBusFactory.getInstance().getClientScopeBus(id);
                busCache.put(id, bus);
            }
        }

        ccfg.setBus(bus);

        //add the webclient to managed set
        this.baseClients.add(targetClient);

        return new WebTargetImpl(wt.getUriBuilder(), wt.getConfiguration(), targetClient);
    }

    /**
     * make the cxf ClientImpl.close works, and we should close all clients too
     */
    @Override
    public void close() {
        super.close();
        for (WebClient wc : baseClients) {
//defec 202957 don't need bus counter any more, since the bus is not shared between jaxrs client any more
            //if one webclient is closed, check if its bus is not used by any other webclient, and reduce counter
//            String id = JaxRSClientUtil.convertURItoBusId(wc.getBaseURI().toString());
//            synchronized (busCache) {
//                LibertyApplicationBus bus = busCache.get(id);
//                if (bus != null) {
//                    AtomicInteger ai = bus.getBusCounter();
//                    if (ai != null) {
//                        //if no webclient uses the bus at this moment, then remove & release it
//                        if (ai.decrementAndGet() == 0) {
//                            busCache.remove(id);
//                            //release bus
//                            bus.shutdown(false);
//                        }
//                    }
//                }
//
//            }

            //close webclient
            wc.close();

        }
        for (LibertyApplicationBus bus : busCache.values()) {
            bus.shutdown(false);
        }
        busCache.clear();
        baseClients = null;
    }

    /**
     * @return busCache
     */
    public Map<String, LibertyApplicationBus> getBusCache() {
        return busCache;
    }
}
