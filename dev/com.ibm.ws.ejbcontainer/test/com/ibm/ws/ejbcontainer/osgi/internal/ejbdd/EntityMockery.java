/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.ejbcontainer.osgi.internal.ejbdd;

import org.jmock.Expectations;
import org.jmock.Mockery;

import com.ibm.ws.javaee.dd.ejb.EnterpriseBean;
import com.ibm.ws.javaee.dd.ejb.Entity;

public class EntityMockery extends ComponentViewableBeanMockery<EntityMockery> {
    private final int persistenceType;
    private int cmpVersion = Entity.CMP_VERSION_UNSPECIFIED;

    EntityMockery(Mockery mockery, String name, int persistenceType) {
        super(mockery, name, EnterpriseBean.KIND_ENTITY);
        this.persistenceType = persistenceType;
    }

    public EntityMockery cmpVersion(int cmpVersion) {
        this.cmpVersion = cmpVersion;
        return this;
    }

    public Entity mock() {
        final Entity entity = mockComponentViewableBean(Entity.class);
        mockery.checking(new Expectations() {
            {
                allowing(entity).getPersistenceTypeValue();
                will(returnValue(persistenceType));

                allowing(entity).getCMPVersionValue();
                will(returnValue(cmpVersion));
            }
        });
        return entity;
    }
}
