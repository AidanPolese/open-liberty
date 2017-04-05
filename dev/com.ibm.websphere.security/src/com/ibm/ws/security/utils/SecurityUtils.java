/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2016
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.security.utils;

import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.wsspi.kernel.service.utils.AtomicServiceReference;
import com.ibm.wsspi.security.audit.AuditService;

/**
 *
 */
@Component(configurationPolicy = ConfigurationPolicy.IGNORE,
           property = "service.vendor=IBM",
           immediate = true)
public class SecurityUtils {

    private static final TraceComponent tc = Tr.register(SecurityUtils.class);

    private static final String AUDIT_SERVICE = "auditService";
    private static AtomicServiceReference<AuditService> auditServiceRef =
                    new AtomicServiceReference<AuditService>(AUDIT_SERVICE);

    @Reference(name = AUDIT_SERVICE,
               service = AuditService.class,
               policy = ReferencePolicy.DYNAMIC,
               policyOption = ReferencePolicyOption.GREEDY,
               cardinality = ReferenceCardinality.OPTIONAL)
    protected void setAuditService(ServiceReference<AuditService> ref) {
        auditServiceRef.setReference(ref);
    }

    protected void unsetAuditService(ServiceReference<AuditService> ref) {
        auditServiceRef.unsetReference(ref);
    }

    @Activate
    protected void activate(ComponentContext cc) {
        auditServiceRef.activate(cc);
    }

    @Deactivate
    protected void deactivate(ComponentContext cc) {
        auditServiceRef.deactivate(cc);
    }

    public static AuditService getAuditService() {
        return auditServiceRef.getService();
    }

}
