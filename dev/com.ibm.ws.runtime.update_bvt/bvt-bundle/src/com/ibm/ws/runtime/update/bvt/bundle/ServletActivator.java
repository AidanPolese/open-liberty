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
package com.ibm.ws.runtime.update.bvt.bundle;

import javax.servlet.ServletException;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;

@Component(immediate=true)
public class ServletActivator {
    
    private static final TraceComponent tc = Tr.register(ServletActivator.class);
    private volatile HttpService http;
    
    @Reference(service = HttpService.class)
    protected void setHttp(HttpService http) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
            Tr.event(this, tc, "setHttp " + http);
        }
        this.http = http;
    }

    protected void unsetHttp(HttpService http) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
            Tr.event(this, tc, "unsetHttp " + http);
        }
        http = null;
    }
    
    protected void activate(ComponentContext cc) throws Exception {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
            Tr.event(this, tc, "activate " + cc);
        }
        try {
            http.registerServlet("/runtimeUpdateNotificationMBeanTest", new RuntimeUpdateNotificationMBeanServlet(), null, null);
            if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
                Tr.event(tc, "ServletActivator has completed activation.");
            }
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (NamespaceException e) {
            e.printStackTrace();
        }
    }

    protected void deactivate(ComponentContext cc) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
            Tr.event(this, tc, "deactivate " + cc);
        }
        http.unregister("/runtimeUpdateNotificationMBeanTest");
    }
}
