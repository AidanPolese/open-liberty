/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2010
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */
package com.ibm.ws.webcontainer.osgi.session;

import com.ibm.ws.webcontainer.SessionRegistry;
import com.ibm.ws.webcontainer.httpsession.SessionManager;
import com.ibm.ws.webcontainer.session.impl.SessionContextRegistryImpl;
import com.ibm.ws.webcontainer.session.impl.SessionContextRegistryImplFactory;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * Helper class to encapsulate the session dependency
 */
@Component(service={SessionHelper.class}, property={"service.vendor=IBM"})
public class SessionHelper {

    private SessionManager manager = null;
    private SessionContextRegistryImpl registry = null;
    private SessionContextRegistryImplFactory sessionContextRegistryImplFactory;

    /**
     * @return SessionRegistry the current SessionRegistry, or null if one isn't found
     */
    public synchronized SessionRegistry getRegistry() {
        if (this.registry == null && this.manager != null) {
            this.registry = sessionContextRegistryImplFactory.createSessionContextRegistryImpl(this.manager);
            this.manager.start(this.registry);
        }
        return this.registry;
    }

    /**
     * @param ref the new SessionManager reference
     */
    @Reference(cardinality=ReferenceCardinality.MANDATORY, policy=ReferencePolicy.DYNAMIC)
    protected synchronized void setSessionManager(SessionManager ref) {
        /*-
         * Declarative Services reminder about dynamic required service x:
         *   1) DS will call setx(X1) before activate()
         *   2) If X1 is going to be deactivated, but X2 is available,
         *      then DS will call setx(X2) before unsetx(X1)
         *   3) This allows the dynamic required reference to remain satisfied.
         */
        this.manager = ref;
        this.registry = null; // assume session bundle handles app restart appropriately
    }

    /**
     * @param ref the old SessionManager reference
     */
    protected synchronized void unsetSessionManager(SessionManager ref) {
        if (ref == this.manager) {
            this.manager = null;
            this.registry = null; // assume session bundle handles app restart appropriately
        }
    }
    
    @Reference(cardinality=ReferenceCardinality.MANDATORY, policy=ReferencePolicy.DYNAMIC, policyOption=ReferencePolicyOption.GREEDY)
    protected synchronized void setSessionContextRegistryImplFactory(SessionContextRegistryImplFactory factory) {
        this.sessionContextRegistryImplFactory = factory;
    }
    
    protected synchronized void unsetSessionContextRegistryImplFactory(SessionContextRegistryImplFactory factory) {
        // no-op intended here to avoid SessionContextRegistryImplFactory being null when switching service implementations
    }
    
}
