/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014,2017
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.jpa.container.eclipselink;

import org.osgi.service.component.annotations.Component;

import com.ibm.ws.jpa.AbstractJPAProviderIntegration;
import com.ibm.ws.jpa.JPAProviderIntegration;

@Component(service = { JPAProviderIntegration.class }, property = { "service.ranking:Integer=20" })
public class EclipseLinkJPAProvider extends AbstractJPAProviderIntegration {
    public EclipseLinkJPAProvider() {
        super();
        providersUsed.add(PROVIDER_ECLIPSELINK); // Avoid 'third party provider' info message when first used
    }

    /**
     * @see com.ibm.ws.jpa.JPAProvider#getDefaultProviderName()
     */
    @Override
    public String getProviderClassName() {
        return PROVIDER_ECLIPSELINK;
    }

    // TODO updatePersistenceUnitProperties -- need to remove an logging properties as logging is handled via Liberty logging

    // TODO supportsEntityManagerPooling -- need to investigate using EntityManager pooling.
}
