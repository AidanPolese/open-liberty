/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012, 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.collective.repository;

import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.wsspi.kernel.service.utils.AtomicServiceReference;

/**
 * The RepositoryConnectionFactory creates (or retrieves) {@link RepositoryClient} and {@link RepositoryMember}.
 * <p>
 * The {@link RepositoryClient} or {@link RepositoryMember} returned can be
 * held for a reasonable amount of time, and changes to the collective feature
 * configuration will be gracefully handled.
 * <p>
 * The RepositoryConnectionFactory is also exposed as an OSGi service so that
 * consumers of the service can block until the RepositoryConnectionFactory
 * is ready, that is at least the RepositoryClient and RepositoryMember are
 * available.
 * 
 * @ibm-spi
 */
/*
 * (non-Javadoc)
 * Internal implementation notes:
 * 
 * Access flow:
 * RepositoryConnectionFactory -> RepositoryClientDelegate -> [RemoteRepositoryConnection, ...]
 * RepositoryConnectionFactory -> RepositoryMemberDelegate -> [RemoteRepositoryConnection, ...]
 */
@Component(service = { RepositoryConnectionFactory.class },
           immediate = true,
           configurationPolicy = ConfigurationPolicy.IGNORE,
           property = { "service.vendor=IBM" })
public class RepositoryConnectionFactory {
    private static final TraceComponent tc = Tr.register(RepositoryConnectionFactory.class);

    static final String KEY_REPOSITORY_CLIENT = "repositoryClient";
    static final String KEY_REPOSITORY_MEMBER = "repositoryMember";

    // Service references which DS handles, AtomicServiceReference handles caching
    private static final AtomicServiceReference<RepositoryClient> repositoryClientRef = new AtomicServiceReference<RepositoryClient>(KEY_REPOSITORY_CLIENT);
    private static final AtomicServiceReference<RepositoryMember> repositoryMemberRef = new AtomicServiceReference<RepositoryMember>(KEY_REPOSITORY_MEMBER);

    /*
     * (non-Javadoc)
     * Internal implementation notes:
     * The RepositoryClientDelegate will not start unless a real
     * RepositoryClient provider is also available.
     * 
     * We intentionally consume multiple references as we need to know if an
     * unexpected RepositoryClientDelegate is registered, as there should
     * only be one (our internal implementation).
     */
    @Reference(name = KEY_REPOSITORY_CLIENT, service = RepositoryClient.class, policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY,
               target = "(clientType=RepositoryClientDelegate)")
    protected void setRepositoryClient(ServiceReference<RepositoryClient> ref) {
        if (repositoryClientRef.getReference() != null) {
            Tr.error(tc, "REGISTRY_CONFIG_FACTORY_TOO_MANY_CLIENT_SERVICES", repositoryClientRef.getReference().getBundle().getSymbolicName(), ref.getBundle().getSymbolicName());
        }
        repositoryClientRef.setReference(ref);
    }

    protected void unsetRepositoryClient(ServiceReference<RepositoryClient> ref) {
        repositoryClientRef.unsetReference(ref);
    }

    /*
     * (non-Javadoc)
     * Internal implementation notes:
     * The RepositoryMemberDelegate will not start unless a real
     * RepositoryMember provider is also available.
     * 
     * We intentionally consume multiple references as we need to know if an
     * unexpected RepositoryMemberDelegate is registered, as there should
     * only be one (our internal implementation).
     */
    @Reference(name = KEY_REPOSITORY_MEMBER, service = RepositoryMember.class, policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY,
               target = "(memberType=RepositoryMemberDelegate)")
    protected void setRepositoryMember(ServiceReference<RepositoryMember> ref) {
        if (repositoryMemberRef.getReference() != null) {
            Tr.error(tc, "REGISTRY_CONFIG_FACTORY_TOO_MANY_MEMBER_SERVICES", repositoryMemberRef.getReference().getBundle().getSymbolicName(), ref.getBundle().getSymbolicName());
        }
        repositoryMemberRef.setReference(ref);
    }

    protected void unsetRepositoryMember(ServiceReference<RepositoryMember> ref) {
        repositoryMemberRef.unsetReference(ref);
    }

    @Activate
    protected void activate(ComponentContext cc) {
        repositoryClientRef.activate(cc);
        repositoryMemberRef.activate(cc);

        if (tc.isEventEnabled()) {
            Tr.event(tc, "RepositoryConnectionFactory is now active and available as an OSGi service");
        }
    }

    @Deactivate
    protected void deactivate(ComponentContext cc) {
        if (tc.isEventEnabled()) {
            Tr.event(tc, "RepositoryConnectionFactory has been deactived and is no longer available as an OSGi service");
        }

        repositoryClientRef.deactivate(cc);
        repositoryMemberRef.deactivate(cc);
    }

    /**
     * Retrieve a RepositoryClient connection.
     * <p>
     * If no implementation is available, {@code null} will be returned and an error message will be logged.
     * 
     * @return {@link RepositoryClient} implementation if available, {@code null} otherwise.
     */
    public static RepositoryClient getRepositoryClient() {
        RepositoryClient client = repositoryClientRef.getService();
        if (client == null) {
            if (tc.isDebugEnabled()) {
                Tr.debug(tc, "No RepositoryClient service is available.");
            }
        }
        return client;
    }

    /**
     * Instance method used to invoke {@link #getRepositoryClient()}.
     * <p>
     * This method is used for obtaining the RepositoryClient when the
     * RepositoryConnectionFactory is used as a service.
     * 
     * @see #getRepositoryClient()
     */
    public RepositoryClient obtainRepositoryClient() {
        return getRepositoryClient();
    }

    /**
     * Retrieve a RepositoryMember connection.
     * <p>
     * If no implementation is available, {@code null} will be returned and an error message will be logged.
     * 
     * @return {@link RepositoryMember} implementation if available, {@code null} otherwise.
     */
    public static RepositoryMember getRepositoryMember() {
        RepositoryMember member = repositoryMemberRef.getService();
        if (member == null) {
            if (tc.isDebugEnabled()) {
                Tr.debug(tc, "No RepositoryClient service is available.");
            }
        }
        return member;
    }

    /**
     * Instance method used to invoke {@link #getRepositoryMember()}.
     * <p>
     * This method is used for obtaining the RepositoryMember when the
     * RepositoryConnectionFactory is used as a service.
     * 
     * @see #getRepositoryMember()
     */
    public RepositoryMember obtainRepositoryMember() {
        return getRepositoryMember();
    }
}
