/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015, 2016
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.ejbcontainer;

import java.io.Serializable;

import javax.ejb.EJBObject;

/**
 * A client reference to an EJB. For stateless EJBs, this refers to a pool of
 * bean instances. For stateful EJB, this refers to a unique bean identity that
 * was created when the reference was created and must be removed by
 * calling {@link #remove}. For singleton, this refers to the single bean
 * instance.
 */
public interface EJBReference extends Serializable {
    /**
     * Returns an EJB proxy for the component interface. The reference will be
     * narrowed to the specified class.
     *
     * @param klass the client interface
     * @return an EJB proxy for the component interface
     */
    <T extends EJBObject> T getEJBObject(Class<T> klass);

    /**
     * Returns an EJB proxy for the specified business interface.
     *
     * @param klass the business interface class
     * @return an EJB proxy for the business interface class
     * @throws IllegalStateException if the EJB does not have the business interface
     */
    <T> T getBusinessObject(Class<T> klass);

    /**
     * Returns an EJB proxy that implements all local business interfaces
     * declared by the EJB, including the no-interface view if needed. The
     * behavior is undefined if multiple interfaces declare the same method and
     * either getInvokedBusinessInterface is called or the methods on the
     * interfaces declare conflicting application exceptions.
     *
     * @return an EJB proxy for all local business interfaces
     */
    Object getAggregateLocalObject();

    /**
     * Removes the instance if this is a stateful session bean.
     *
     * @throws UnsupportedOperationException if this is not a stateful reference
     */
    void remove();
}
