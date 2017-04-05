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
package com.ibm.ws.injectionengine.osgi.internal;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.spi.ObjectFactory;

import org.osgi.service.component.annotations.Component;

import com.ibm.ws.resource.ResourceFactory;

@Component(service = { ObjectFactory.class, ResourceFactoryObjectFactory.class })
public class ResourceFactoryObjectFactory implements ObjectFactory {
    @Override
    public Object getObjectInstance(Object o, Name n, Context c, Hashtable<?, ?> envmt) throws Exception {
        if (!(o instanceof ResourceFactoryReference)) {
            return null;
        }

        ResourceFactoryReference reference = (ResourceFactoryReference) o;
        ResourceFactory resourceFactory = reference.getResourceFactory();
        return resourceFactory.createResource(null);
    }
}
