/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.security.authentication.tai;

import org.osgi.framework.ServiceReference;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.wsspi.kernel.service.utils.ConcurrentServiceReferenceMap;
import com.ibm.wsspi.security.tai.TrustAssociationInterceptor;

/**
 * This class process the attribute invokeBeforeSSO and invokeAfterSSO attributes
 * from the inerceptor user features
 */
public class TAIUtil {
    private static final TraceComponent tc = Tr.register(TAIUtil.class);

    private boolean invokeBeforeSSO = false;
    private boolean invokeAfterSSO = false;

    /**
     * This method will process the TAI user feature properties and determine
     * whether this TAI will be invoked before or after the SSO authentication. Because
     * we do not have a direct access to the TAI user feature configuration if any.
     * 
     * @param interceptorServiceRef
     * @param interceptorId
     */
    public void processTAIUserFeatureProps(ConcurrentServiceReferenceMap<String, TrustAssociationInterceptor> interceptorServiceRef,
                                           String interceptorId) {
        invokeBeforeSSO = false;
        invokeAfterSSO = false;

        ServiceReference<TrustAssociationInterceptor> taiServiceRef = interceptorServiceRef.getReference(interceptorId);
        Object beforeSsoProp = taiServiceRef.getProperty(TAIConfig.KEY_INVOKE_BEFORE_SSO);
        Object afterSsoProp = taiServiceRef.getProperty(TAIConfig.KEY_INVOKE_AFTER_SSO);
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "user feature have properties,  beforeSsoProp=" + beforeSsoProp + " invokeAfterSSO=" + afterSsoProp);
        }

        /*
         * The interceptor user defined feature may have the following:
         * 1) Do not specified invokeBeforeSSO and invokeAfterSSO attributes.
         * 2) Specified only invokeAfterSSO attribute.
         * 3) Specified only invokeBeforeSSO attribute.
         * 4) Specified both invokeBeforeSSO and invokeAfterSSO attributes.
         */
        if (beforeSsoProp == null && afterSsoProp == null) {
            invokeAfterSSO = true;
        } else if (beforeSsoProp == null && afterSsoProp != null) {
            resolveOnlyInvokeAfterSSOSpecified(afterSsoProp);
        } else if (beforeSsoProp != null && afterSsoProp == null) {
            resolveOnlyInvokeBeforeSSOSpecified(beforeSsoProp);
        } else if (beforeSsoProp != null && afterSsoProp != null) {
            invokeBeforeSSO = (Boolean) beforeSsoProp;
            invokeAfterSSO = (Boolean) afterSsoProp;
        }
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "resolve user feature have,  invokeBeforeSSO=" + invokeBeforeSSO + " invokeAfterSSO=" + invokeAfterSSO);
        }
    }

    /**
     * If inbokeBeforeSSO is false, then this TAI will be invoked after SSO authentication
     * 
     * @param beforeSSO
     * @return
     */
    protected void resolveOnlyInvokeBeforeSSOSpecified(Object beforeSSO) {
        invokeBeforeSSO = (Boolean) beforeSSO;
        if (!invokeBeforeSSO) {
            invokeAfterSSO = true;
        }
    }

    /**
     * If invokeAfterSSO is false, then this TAI will be invoked before SSO authentication.
     * 
     * @param afterSSO
     * @return
     */
    protected void resolveOnlyInvokeAfterSSOSpecified(Object afterSSO) {
        invokeAfterSSO = (Boolean) afterSSO;
        if (!invokeAfterSSO) {
            invokeBeforeSSO = true;
        }
    }

    public boolean isInvokeBeforeSSO() {
        return invokeBeforeSSO;
    }

    public boolean isInvokeAfterSSO() {
        return invokeAfterSSO;
    }
}
