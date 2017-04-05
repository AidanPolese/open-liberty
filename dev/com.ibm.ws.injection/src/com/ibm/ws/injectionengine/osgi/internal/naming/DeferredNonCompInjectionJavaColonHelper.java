/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012, 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.injectionengine.osgi.internal.naming;

import javax.naming.NamingException;

import org.osgi.service.component.annotations.Component;

import com.ibm.ws.container.service.naming.JavaColonNamingHelper;
import com.ibm.ws.container.service.naming.NamingConstants;
import com.ibm.ws.injectionengine.osgi.internal.OSGiInjectionScopeData;

/**
 * A naming helper that initializes deferred reference data in case doing so
 * will cause non-java:comp references to become available. This service is
 * registered with a lower-than-default service.ranking to ensure that all
 * deferred reference data is not unnecessarily processed eagerly.
 */
@Component(service = JavaColonNamingHelper.class,
           property = { "service.vendor=IBM", "service.ranking:Integer=-1" })
public class DeferredNonCompInjectionJavaColonHelper extends InjectionJavaColonHelper {
    @Override
    protected OSGiInjectionScopeData getInjectionScopeData(NamingConstants.JavaColonNamespace namespace) throws NamingException {
        // If the namespace is java:comp or there was no new reference data to
        // process, then no-op: the standard InjectionJavaColonHelper already
        // had a chance.
        if (!namespace.isComp()) {
            OSGiInjectionScopeData scopeData = super.getInjectionScopeData(namespace);
            if (scopeData.processDeferredReferenceData()) {
                return scopeData;
            }
        }
        return null;
    }
}
