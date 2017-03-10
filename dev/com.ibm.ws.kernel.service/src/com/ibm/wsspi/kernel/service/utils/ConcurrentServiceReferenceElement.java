/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011, 2016
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.kernel.service.utils;

import java.security.AccessController;
import java.security.PrivilegedAction;

import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;

/**
 * Container class for use by the ConcurrentServiceReference containers.
 * Will cache the located service.
 */
class ConcurrentServiceReferenceElement<T> implements Comparable<ConcurrentServiceReferenceElement<T>> {
    private final String referenceName;
    private final ServiceReference<T> serviceRef;
    private final Long serviceId;
    private final Integer serviceRanking;
    private volatile T locatedService = null;

    /**
     * In its current use, all input parameters are guarded by the
     * ConcurrentServiceReferenceMap / Set. Null is not supported for
     * any of these parameters.
     * 
     * @param referenceName ServiceReference for the target service. Service references are
     *            equal if they point to the same service registration, and are ordered by
     *            increasing service.ranking and decreasing service.id. ServiceReferences hold
     *            no service properties: requests/queries for properties are forwarded onto
     *            the backing service registration.
     * @param serviceRef
     */
    ConcurrentServiceReferenceElement(String referenceName, ServiceReference<T> serviceRef) {
        this.referenceName = referenceName;
        this.serviceRef = serviceRef;
        this.serviceId = ServiceReferenceUtils.getId(serviceRef);
        this.serviceRanking = ServiceReferenceUtils.getRanking(serviceRef);
    }

    ServiceReference<T> getReference() {
        return serviceRef;
    }

    Integer getRanking() {
        return serviceRanking;
    }

    @SuppressWarnings("unchecked")
    synchronized T getService(ComponentContext context) {
        T svc = locatedService;
        if (svc == null) {
            SecurityManager sm = System.getSecurityManager();
            if (sm != null) {
                final ComponentContext finalContext = context;
                svc = AccessController.doPrivileged(new PrivilegedAction<T>() {

                    @Override
                    public T run() {
                        return finalContext.locateService(referenceName, serviceRef);
                    }
                });
            } else {
                svc = locatedService = context.locateService(referenceName, serviceRef);
            }
        }
        return svc;
    }

    @Override
    public String toString() {
        return super.toString()
               + "[id=" + serviceId
               + ", ranking=" + serviceRanking
               + "]";
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return serviceId.hashCode();
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (this.getClass() != obj.getClass())
            return false;

        ConcurrentServiceReferenceElement<?> other = (ConcurrentServiceReferenceElement<?>) obj;
        return serviceId.equals(other.serviceId);
    }

    /** {@inheritDoc} */
    @Override
    public int compareTo(ConcurrentServiceReferenceElement<T> that) {
        // Note: that.compareTo(this) will sort highest ranking first.
        int compare = that.serviceRanking.compareTo(this.serviceRanking);
        if (compare == 0) {
            compare = this.serviceId.compareTo(that.serviceId);
        }

        return compare;
    }
}
