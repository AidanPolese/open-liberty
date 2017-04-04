/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.injectionengine.factory;

import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

import com.ibm.wsspi.injectionengine.InjectionTargetContext;

/**
 * Extends the Naming ObjectFactory to provides a mechanism to include
 * associated context data along with the Object that is the target of
 * injection. <p>
 *
 * The ability to pass context data to the Object factories is largely
 * for performance, to avoid passing information via thread context
 * data; however, this may also be useful for processing that is
 * unique to injection processing as opposed to a naming lookup. <p>
 *
 * Note that this context data will only be available during injection
 * and will not be available during a Naming lookup. <p>
 */
public interface InjectionObjectFactory extends ObjectFactory
{
    /**
     * Creates an object of the type specified by the Reference parameter,
     * optionally customized by injection target context details. <p>
     *
     * @param ref the reference information that defines the object to
     *            be created.
     * @param targetInstance the object that is the target of the injection.
     * @param targetContext provides access to context data associated with
     *            the target of the injection (e.g. EJBContext). May be null
     *            if not provided by the container, and will be null for a
     *            naming lookup.
     */
    Object getInjectionObjectInstance(Reference ref,
                                      Object targetInstance,
                                      InjectionTargetContext targetContext)
                    throws Exception;
}
