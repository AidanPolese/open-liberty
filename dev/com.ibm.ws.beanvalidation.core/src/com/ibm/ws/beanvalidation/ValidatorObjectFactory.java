/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2010, 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.beanvalidation;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.spi.ObjectFactory;
import javax.validation.Validator;

import com.ibm.ejs.ras.Tr;
import com.ibm.ejs.ras.TraceComponent;
import com.ibm.ejs.util.Util;

public class ValidatorObjectFactory implements ObjectFactory
{
    private static final TraceComponent tc = Tr.register(ValidatorObjectFactory.class,
                                                         "BeanValidation",
                                                         BVNLSConstants.BV_RESOURCE_BUNDLE);

    @Override
    public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment) throws Exception
    {
        final boolean isTraceOn = TraceComponent.isAnyTracingEnabled();
        if (isTraceOn && tc.isEntryEnabled())
            Tr.entry(tc, "getObjectInstance : " + Util.identity(obj));

        Validator validator = AbstractBeanValidation.getValidatorFactory().getValidator();

        if (isTraceOn && tc.isEntryEnabled())
            Tr.exit(tc, "getObjectInstance: " + Util.identity(validator));

        return validator;
    }
}
