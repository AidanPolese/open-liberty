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
package com.ibm.ws.wsat.policy;

import org.apache.cxf.Bus;
import org.apache.cxf.ws.policy.AssertionBuilderLoader;
import org.apache.cxf.ws.policy.AssertionBuilderRegistry;
import org.apache.cxf.ws.policy.PolicyInterceptorProviderLoader;
import org.apache.cxf.ws.policy.PolicyInterceptorProviderRegistry;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.jaxws.wsat.Constants;

public final class WSATPolicyLoader implements PolicyInterceptorProviderLoader, AssertionBuilderLoader {

    private final TraceComponent tc = Tr.register(
                                                  WSATPolicyLoader.class, Constants.TRACE_GROUP, null);

    private final Bus bus;

    public WSATPolicyLoader(Bus b) {

        bus = b;
        boolean bOk = true;
        try {
            registerBuilders();
            registerProviders();
        } catch (Throwable t) {
            //probably ATAssertion isn't found or something. We'll ignore this
            //as the policy framework will then not find the providers
            //and error out at that point.  If nothing uses ws-at policy
            //no warnings/errors will display
            bOk = false;
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "error.policy.notloaded");
            }
        }
        if (bOk) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "The WS-AT Policy Loader is invoked successfully.");
            }
        }
    }

    public void registerBuilders() {
        AssertionBuilderRegistry reg = bus.getExtension(AssertionBuilderRegistry.class);
        if (reg == null) {
            return;
        }
        reg.registerBuilder(new WSATAssertionBuilder());

    }

    public void registerProviders() {
        //interceptor providers for all of the above
        PolicyInterceptorProviderRegistry reg = bus.getExtension(PolicyInterceptorProviderRegistry.class);
        if (reg == null) {
            return;
        }
        reg.register(new WSATAssertionPolicyProvider());

    }

}
