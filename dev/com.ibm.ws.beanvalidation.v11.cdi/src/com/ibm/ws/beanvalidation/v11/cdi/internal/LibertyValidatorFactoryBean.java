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
package com.ibm.ws.beanvalidation.v11.cdi.internal;

import javax.enterprise.context.spi.CreationalContext;
import javax.validation.ValidatorFactory;

import org.apache.bval.cdi.ValidatorFactoryBean;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.beanvalidation.BVNLSConstants;

/**
 * This class is used to extend the Apache ValidatorFactoryBean for the sole purpose
 * of overriding the create method. Instead of passing in the ValidatorFactory object
 * when the bean is initialized, we delay the creation of the ValidatorFactory until
 * create is called. The delay is needed since the server thread doesn't have its
 * metadata and context initialized to a point where creating the ValidatorFactory will succeed.
 * 
 */
public class LibertyValidatorFactoryBean extends ValidatorFactoryBean {
    private static final TraceComponent tc = Tr.register(LibertyValidatorFactoryBean.class, "BeanValidation", BVNLSConstants.BV_RESOURCE_BUNDLE);

    protected String id = null;

    public LibertyValidatorFactoryBean() {
        super(null);

    }

    @Override
    public ValidatorFactory create(CreationalContext<ValidatorFactory> context) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.entry(tc, "create");
        ValidatorFactory vf = ValidationExtensionService.instance().getDefaultValidatorFactory();

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.exit(tc, "create", new Object[] { vf });
        return vf;
    }

    /*
     * Override this method so that a LibertyValidatorFactoryBean is stored in the WELD
     * Bean Store keyed on its classname. This allows an injected ValidatorFactory Bean to
     * be retrieved in both local and server failover scenarios as per defect 774504.
     */
    @Override
    public String getId() {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.entry(tc, "getId");
        if (id == null)
        {
            // Set id to the class name
            id = this.getClass().getName();
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.exit(tc, "getId", new Object[] { id });
        return id;
    }

}
