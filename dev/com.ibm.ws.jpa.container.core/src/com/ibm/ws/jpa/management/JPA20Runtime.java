/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jpa.management;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import com.ibm.websphere.csi.J2EEName;
import com.ibm.ws.Transaction.UOWCoordinator;
import com.ibm.ws.jpa.JPAPuId;

public class JPA20Runtime implements JPARuntime {
    @Override
    public JPAVersion getJPARuntimeVersion() {
        return JPAVersion.JPA20;
    }

    /**
     * Returns true if this is the default wrapper factory. Callers can use this
     * to avoid unnecessarily recreating wrappers during deserialization.
     */
    @Override
    public boolean isDefault() {
        return true;
    }

    @Override
    public EntityManagerFactory createJPAEMFactory(JPAPuId puId, J2EEName j2eeName, EntityManagerFactory emf) {
        return new JPAEMFactory(puId, j2eeName, emf);
    }

    @Override
    public JPATxEntityManager createJPATxEntityManager(JPAPuId puRefId, JPAPUnitInfo puInfo, J2EEName j2eeName, String refName, Map<?, ?> properties,
                                                       boolean isUnsynchronized, AbstractJPAComponent jpaComponent) {
        return new JPATxEntityManager(puRefId, puInfo, j2eeName, refName, properties, isUnsynchronized, jpaComponent);
    }

    @Override
    public JPAExEntityManager createJPAExEntityManager(JPAPuId puRefId, JPAPUnitInfo puInfo, J2EEName j2eeName, String refName, Map<?, ?> properties,
                                                       boolean isUnsynchronized, AbstractJPAComponent jpaComponent) {
        return new JPAExEntityManager(puRefId, puInfo, j2eeName, refName, properties, isUnsynchronized, jpaComponent);
    }

    @Override
    public JPAExEmInvocation createExEmInvocation(UOWCoordinator uowCoord, EntityManager em, boolean txIsUnsynchronized) {
        return new JPAExEmInvocation(uowCoord, em, null, txIsUnsynchronized);
    }

    @Override
    public EntityManager createEntityManagerInstance(EntityManagerFactory emf, boolean unsynchronized) {
        if (emf == null) {
            return null;
        }

        return emf.createEntityManager();
    }

    @Override
    public EntityManager createEntityManagerInstance(EntityManagerFactory emf, Map<?, ?> propMap, boolean unsynchronized) {
        if (emf == null) {
            return null;
        }

        return emf.createEntityManager(propMap);
    }

    @Override
    public boolean isIgnoreDataSourceErrors(Boolean ignoreDataSource) {
        return ignoreDataSource != null ? ignoreDataSource : true;
    }

    @Override
    public String processJEE7JTADataSource(String jtaDataSource, String nonJtaDataSource) {
        return jtaDataSource;
    }

}
