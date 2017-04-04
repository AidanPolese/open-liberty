/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.ssl.internal;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocketFactory;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.websphere.ssl.JSSEHelper;
import com.ibm.websphere.ssl.JSSEProvider;
import com.ibm.ws.ssl.optional.SSLSupportOptional;
import com.ibm.wsspi.ssl.SSLSupport;

/**
 * Wrapper to expose SSLComponent as SSLSupport only when at least one repertoire and keystore are bound.
 */
@Component(service = {},
           configurationPolicy = ConfigurationPolicy.IGNORE,
           property = "service.vendor=IBM")
public class SSLSupportImpl implements SSLSupport {

    private SSLSupportOptional delegate;
    private Map<String, Object> props;
    private volatile ServiceRegistration<SSLSupport> registration;
    private static final TraceComponent tc = Tr.register(SSLSupportImpl.class);

    public SSLSupportImpl() {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
            Tr.event(tc, "<init>");
        }
    }

    @Activate
    protected void activate(BundleContext ctx) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
            Tr.event(tc, "Activated: " + ctx);
        }
        Dictionary<String, Object> props = new Hashtable<String, Object>(this.props);
        registration = ctx.registerService(SSLSupport.class, this, props);
        if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
            Tr.event(tc, "Activated: return registerService: " + registration);
        }
    }

    @Deactivate
    protected void deactivate() {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
            Tr.event(tc, "Deactivated: ");
        }
        registration.unregister();
        registration = null;
    }

    @Reference(target = "(SSLSupport=active)")
    protected void setSSLSupportOptional(SSLSupportOptional delegate, Map<String, Object> props) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
            Tr.event(tc, "setSSLSupportOptional: delegate: " + delegate + " props: " + props);
        }
        this.delegate = delegate;
        this.props = props;
    }

    protected void updatedSSLSupportOptional(SSLSupportOptional delegate, Map<String, Object> props) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
            Tr.event(tc, "updatedSSLSupportOptional: delegate: " + delegate + " props: " + props);
        }
        this.props = props;
        ServiceRegistration<SSLSupport> registration = this.registration;
        if (registration != null) {
            registration.setProperties(new Hashtable<String, Object>(this.props));
        }
    }

    @Override
    public JSSEHelper getJSSEHelper() {
        return delegate.getJSSEHelper();
    }

    @Override
    public JSSEProvider getJSSEProvider() {
        return delegate.getJSSEProvider();
    }

    @Override
    public JSSEProvider getJSSEProvider(String providerName) {
        return delegate.getJSSEProvider(providerName);
    }

    @Override
    public SSLSocketFactory getSSLSocketFactory() {
        return delegate.getSSLSocketFactory();
    }

    @Override
    public SSLSocketFactory getSSLSocketFactory(String sslAlias) throws SSLException {
        return delegate.getSSLSocketFactory(sslAlias);
    }

    @Override
    public SSLSocketFactory getSSLSocketFactory(Properties sslProps) throws SSLException {
        return delegate.getSSLSocketFactory(sslProps);
    }

}
