/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jpa.container.v21;

import javax.persistence.PersistenceContext;
import javax.persistence.SynchronizationType;

import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;

import com.ibm.ws.jpa.management.JPAPCtxtAttributeAccessor;

@Component(service = JPAPCtxtAttributeAccessor.class,
           name = "com.ibm.ws.jpa.pctxtAttributeAccessor",
           property = Constants.SERVICE_RANKING + ":Integer=21")
public class JPAPCtxtAttributeAccessorV21 extends JPAPCtxtAttributeAccessor {
    @Override
    public boolean isUnsynchronized(PersistenceContext pCtxt) {
        return pCtxt.synchronization() == SynchronizationType.UNSYNCHRONIZED;
    }
}
