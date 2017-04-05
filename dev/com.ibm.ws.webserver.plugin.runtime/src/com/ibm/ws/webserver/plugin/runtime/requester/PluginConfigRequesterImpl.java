/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2016
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.webserver.plugin.runtime.requester;

import java.io.File;
import java.util.HashMap;

import javax.management.StandardMBean;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.ffdc.FFDCFilter;
import com.ibm.ws.webserver.plugin.runtime.PluginRuntimeConstants;
import com.ibm.ws.webserver.plugin.runtime.interfaces.PluginConfigRequester;
import com.ibm.ws.webserver.plugin.runtime.interfaces.PluginUtilityConfigGenerator;
import com.ibm.wsspi.kernel.service.location.WsLocationAdmin;
import com.ibm.wsspi.kernel.service.location.WsResource;

/**
 *
 */
@Component(service = { PluginConfigRequester.class },
                immediate = true,
                property = { "service.vendor=IBM", "jmx.objectname=" + PluginConfigRequester.OBJECT_NAME, })
public class PluginConfigRequesterImpl extends StandardMBean implements PluginConfigRequester {

    private static final TraceComponent tc = Tr.register(PluginConfigRequesterImpl.class, PluginRuntimeConstants.TR_GROUP, PluginRuntimeConstants.NLS_PROPS);

    protected WsLocationAdmin locMgr;

    private final HashMap<PluginUtilityConfigGenerator.Types, PluginUtilityConfigGenerator> pluginConfigMbeans;

    private File writeDirectory;
    private BundleContext bundleContext = null;

    public PluginConfigRequesterImpl() {
        super(PluginConfigRequester.class, false);
        pluginConfigMbeans = new HashMap<PluginUtilityConfigGenerator.Types, PluginUtilityConfigGenerator>();
    }

    /**
     * @return the bundleContext
     */
    public BundleContext getBundleContext() {
        return bundleContext;
    }

    @Activate
    protected void activate(BundleContext bc) {
        bundleContext = bc;
    }

    @Deactivate
    protected void deactivate() {}

    @Reference(service = PluginUtilityConfigGenerator.class,
                    cardinality = ReferenceCardinality.MULTIPLE,
                    policy = ReferencePolicy.DYNAMIC)
    protected void setPluginUtilityConfigGenerator(PluginUtilityConfigGenerator mb) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            Tr.debug(this, tc, "PluginConfigRequesterImpl: setPluginUtilityConfigGenerator :" + mb.getPluginConfigType() + " : " + mb.getClass().getSimpleName());
        this.pluginConfigMbeans.put(mb.getPluginConfigType(), mb);
    }

    protected void unsetPluginUtilityConfigGenerator(PluginUtilityConfigGenerator mb) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            Tr.debug(this, tc, "PluginConfigRequesterImpl: unsetGeneratePluginConfig :" + mb.getPluginConfigType() + " : " + mb.getClass().getSimpleName());
        this.pluginConfigMbeans.remove(mb.getPluginConfigType());
    }

    @Reference(service = WsLocationAdmin.class,
                    cardinality = ReferenceCardinality.MANDATORY)
    protected void setLocationAdmin(WsLocationAdmin locRef) {
        this.locMgr = locRef;
        WsResource outFile = null;
        outFile = locMgr.getServerOutputResource("logs" + File.separatorChar + "state" + File.separatorChar);
        writeDirectory = outFile.asFile();
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            Tr.debug(this, tc, "PluginConfigRequesterImpl.setLocationAdmin : write directory : " + writeDirectory.getPath());
    }

    protected void unsetLocationAdmin(ServiceReference<WsLocationAdmin> ref) {
        this.locMgr = null;
    }

    /** {@inheritDoc} */
    @Override
    public boolean generateClusterPlugin(String cluster) {

        boolean result = false;
        PluginUtilityConfigGenerator mBean = this.pluginConfigMbeans.get(PluginUtilityConfigGenerator.Types.COLLECTIVE);
        if (mBean != null) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                Tr.debug(this, tc, "PluginConfigRequesterImpl.generateClusterPlugin : write directory : " + writeDirectory.getPath());
            try {
                mBean.generatePluginConfig(cluster, writeDirectory);
                result = true;
            } catch (Throwable th) {
                FFDCFilter.processException(th, getClass().getName(), "generateClusterPlugin");
                if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
                    Tr.event(tc, "Error generating cluster plugin-cfg.xml: " + th.getMessage());
                }
            }
        } else {
            result = false;
            Tr.error(tc, "collective.mbean.not.available", cluster);
        }

        return result;
    }

    /** {@inheritDoc} */
    @Override
    public boolean generateAppServerPlugin() {

        boolean result = false;
        PluginUtilityConfigGenerator mBean = this.pluginConfigMbeans.get(PluginUtilityConfigGenerator.Types.WEBCONTAINER);
        if (mBean != null) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                Tr.debug(this, tc, "PluginConfigRequesterImpl.generateAppServerPlugin : write directory : " + writeDirectory.getPath());
            try {
                mBean.generatePluginConfig(null, writeDirectory);
                result = true;
            } catch (Throwable th) {
                FFDCFilter.processException(th, getClass().getName(), "generateAppServerPlugin");
                if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
                    Tr.event(tc, "Error generating app server plugin-cfg.xml xml: " + th.getMessage());
                }
            }
        } else {
            result = false;
            Tr.error(tc, "appserver.mbean.not.available");
        }
        return result;
    }

}
