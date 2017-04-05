/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.persistence.internal.eclipselink;

import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceUnitInfo;

import org.eclipse.persistence.jpa.PersistenceProvider;

/**
 * An extension of the EclipseLink PersistenceProvider.
 */
public class PsPersistenceProvider extends PersistenceProvider {

     /**
      * This method exists to give us access to the protected method
      * createEntityManagerFactoryImpl(...)
      */
     public EntityManagerFactory createContainerEMF(PersistenceUnitInfo info, Map<?, ?> properties,
          boolean requiresConnection) {
          return super.createContainerEntityManagerFactoryImpl(info, properties, requiresConnection);
     }
}
