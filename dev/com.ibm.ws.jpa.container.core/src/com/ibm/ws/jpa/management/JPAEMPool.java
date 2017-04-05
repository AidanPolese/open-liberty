// /I/ /W/ /G/ /U/   <-- CMVC Keywords, replace / with %
// %I% %W% %G% %U%
//
// IBM Confidential OCO Source Material
// 5639-D57,5630-A36,5630-A37,5724-D18 (C) COPYRIGHT International Business Machines Corp. 2008, 2013
//
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Module  :  JPAEMPool.java
//
// Source File Description:
//
//     Provides a bounded thread-safe pool of EntityManager instances based
//     on linked nodes.
//
// Change Activity:
//
// Reason    Version   Date     Userid    Change Description
// --------- --------- -------- --------- -----------------------------------------
// d510184   WAS70     20080505 tkb      : Create seperate EMF for each java:comp
// d638095.1 WAS70BE   20100308 jckrueg  : Call providers prepareForPooling() when possible.
// F61057    WAS85     20120224 bkail    : Use JPAEMPoolHelper
// d731877   WAS8501   20120518 jrbauer  : Enable pool access to WsJpa provider via lightweight EMF wrapper
// d743325   WAS855    20130121 jrbauer  : store jpa component reference in the pool
// RTC112113 WAS90     20131023 leealber : Add new JPA 2.1 APIs
// d152713   RTC       20150126 jgrassel : Support @PersistenceContext Tx-Sync Feature
// --------- --------- -------- --------- -----------------------------------------
package com.ibm.ws.jpa.management;

import static com.ibm.ws.jpa.management.JPAConstants.JPA_RESOURCE_BUNDLE_NAME;
import static com.ibm.ws.jpa.management.JPAConstants.JPA_TRACE_GROUP;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.persistence.Cache;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnitUtil;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.metamodel.Metamodel;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;

/**
 * Provides a bounded thread-safe pool of EntityManager instances based
 * on linked nodes. <p>
 * 
 * This pool orders elements FIFO (first-in-first-out) and utilizes a
 * ConcurrentLinkedQueue to maximize multi-thread access. <p>
 */
final class JPAEMPool implements EntityManagerFactory
{
    // This class implements EntityManagerFactory for use by the
    // WSOPENJPA_EMF_POOL_PROPERTY_NAME property.  Only the createEntityManager
    // method can be meaningfully used, so we do not bother to create a
    // version-specific wrapper for JPA 2.1+.

    private static final TraceComponent tc = Tr.register(JPAEMPool.class,
                                                         JPA_TRACE_GROUP,
                                                         JPA_RESOURCE_BUNDLE_NAME);

    /**
     * The EntityManagerFactory associated with the persistence context reference.
     **/
    protected final EntityManagerFactory ivFactory;

    /**
     * Properties declared in @PersistenceContext annotation and/or
     * <persistence-context-ref> xml.
     **/
    private final Map<?, ?> ivProperties;

    /** Pool capacity. -1 and 0 indicate no pooling. **/
    private int ivPoolCapacity;

    /** Current number of EntityManager instances in the pool. **/
    private int ivPoolSize = 0;

    /** PersistenceUnit Information object associated with this pool **/
    private JPAPUnitInfo ivPUnitInfo = null; //d638095.1

    /** The real pool of EntityManager instances. **/
    private final ConcurrentLinkedQueue<EntityManager> ivPool = new ConcurrentLinkedQueue<EntityManager>();

    private final AbstractJPAComponent ivAbstractJpaComponent;

    /**
     * Constructor.
     * 
     * @param factory
     *            the factory used to create new instances when the pool is empty
     * @param properties
     *            the persistence context reference properties used to create
     *            new instances when the pool is empty.
     * @param capacity
     *            the maximium number of EntityManager instances stored in the pool.
     * @param jpaPUnitInfo
     *            the object that stores information about the associated persistence unit.
     */
    @SuppressWarnings("unchecked")
    JPAEMPool(EntityManagerFactory factory, Map<?, ?> properties, int capacity, JPAPUnitInfo jpaPUnitInfo, AbstractJPAComponent jpaComponent)
    {
        final boolean isTraceOn = TraceComponent.isAnyTracingEnabled();

        if (isTraceOn && tc.isDebugEnabled())
            Tr.debug(tc, "JPAEMPool : " + factory + ", " + properties +
                         ", capacity = " + capacity);
        ivFactory = factory;

        // If pooling is enabled, include a reference to the pool as a wsjpa persistence property
        if (capacity > 0) {
            Map<String, Object> props = null;
            if (properties == null || properties.isEmpty()) {
                props = new HashMap<String, Object>();
            } else {
                props = new HashMap<String, Object>((Map<String, Object>) properties);
            }
            if (isTraceOn && tc.isEntryEnabled()) {
                Tr.exit(tc, "getEntityManager : adding pool to em properties: " + this);
            }

            // We need to store the EMPool in the properties map as delayed collections are pool
            // aware.
            props.put(JPAConstants.WSOPENJPA_EMF_POOL_PROPERTY_NAME, this);
            ivProperties = props;
        } else {
            ivProperties = properties;
        }

        ivPoolCapacity = capacity;
        ivPUnitInfo = jpaPUnitInfo; // d638095.1
        ivAbstractJpaComponent = jpaComponent;
    }

    /**
     * Returns an EntityManager instance from the pool, or a newly created
     * instance if the pool is empty. <p>
     * 
     * If a global JTA transaction is present, the EntityManager will have
     * joined that transaction. <p>
     * 
     * @param jtaTxExists
     *            true if a global jta transaction exists; otherwise false.
     * @param unsynchronized
     *            true if SynchronizationType.UNSYNCHRONIZED is requested, false if not.
     **/
    public EntityManager getEntityManager(boolean jtaTxExists, boolean unsynchronized)
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.entry(tc, "getEntityManager : [" + ivPoolSize + "] tx = " +
                         jtaTxExists + " unsynchronized = " + unsynchronized);

        EntityManager em = ivPool.poll();
        if (em != null)
        {
            synchronized (this)
            {
                --ivPoolSize;
            }

            if (jtaTxExists && !unsynchronized)
            {
                em.joinTransaction();
            }
        }
        else
        {
            // createEntityManager will join transaction if present and is SYNCHRONIZED.
            em = ivAbstractJpaComponent.getJPARuntime().createEntityManagerInstance(ivFactory, ivProperties, unsynchronized);
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.exit(tc, "getEntityManager : [" + ivPoolSize + "] " + em);

        return em;
    }

    /**
     * Returns an EntityManager instance to the pool (after clearing), or
     * closes the EntityManager if the pool is full.
     * If the provider supports the "prepareForPooling()" API, then
     * that method will be invoked to clear the Entity Manager and prepare
     * it for pooling. Otherwise we will call clear on the Entity Manager
     * directly. <p>
     * 
     * @param entityManager
     *            EntityManager to place in the pool; or close.
     **/
    void putEntityManager(EntityManager entityManager) {
        if (entityManager != null && entityManager.isOpen()) {
            boolean addToPool = false;

            entityManager.clear();
            synchronized (this) {
                if (ivPoolSize < ivPoolCapacity) {
                    ++ivPoolSize;
                    addToPool = true;
                }
            }

            if (addToPool) {
                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                    Tr.debug(tc, "putEntityManager : [" + ivPoolSize + "] " +
                                 entityManager);
                ivPool.add(entityManager);
            } else {
                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                    Tr.debug(tc, "putEntityManager : close : " + entityManager);
                entityManager.close();
            }
        } else {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                Tr.debug(tc, "putEntityManager : not open : " + entityManager);
        }
    }

    /**
     * Prevents the pool from accepting any further EntityManager instances
     * and closes all EntityManager instances currently in the pool. <p>
     **/
    void shutdown()
    {
        synchronized (this)
        {
            ivPoolCapacity = -1;
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            Tr.debug(tc, "shutdown : " + this);

        EntityManager em = ivPool.poll();

        while (em != null)
        {
            if (em.isOpen())
            {
                em.close();
            }
            em = ivPool.poll();
        }

        synchronized (this)
        {
            ivPoolSize = 0;
        }
    }

    /**
     * Overridden to provide meaningful trace output.
     */
    @Override
    public String toString()
    {
        String identity = Integer.toHexString(System.identityHashCode(this));
        return "JPAEMPool@" + identity + "[" + ivPoolSize + "/" + ivPoolCapacity +
               ", " + ivFactory + "]";
    }

    /**
     * Prohibit closing the emf/pool.
     * 
     * @throws UnsupportedOperationException
     */
    @Override
    public void close()
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            Tr.debug(tc, "close : " + this);
        throw new UnsupportedOperationException("This operation is not supported on a pooling EntityManagerFactory.");
    }

    /**
     * Gets entity manager from pool and wraps it in an invocation type aware,
     * enlistment capable em.
     */
    @Override
    public EntityManager createEntityManager()
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            Tr.debug(tc, "createEntityManager : " + this);

        EntityManager em = getEntityManager(false, false);
        JPAPooledEntityManager pem = new JPAPooledEntityManager(this, em, ivAbstractJpaComponent, true);
        return pem;
    }

    /**
     * Pooled entity managers have their properties already defined. A
     * provider exploiting the pool cannot use this method.
     * 
     * @throws UnsupportedOperationException
     */
    @Override
    public EntityManager createEntityManager(Map arg0)
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            Tr.debug(tc, "createEntityManager : " + this);
        throw new UnsupportedOperationException("This operation is not supported on a pooling EntityManagerFactory.");
    }

    /**
     * Prohibit access to the cache via the pool.
     * 
     * @throws UnsupportedOperationException
     */
    @Override
    public Cache getCache()
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            Tr.debug(tc, "getCache : " + this);
        throw new UnsupportedOperationException("This operation is not supported on a pooling EntityManagerFactory.");
    }

    /**
     * Prohibit access to the criteria builder via the pool.
     * 
     * @throws UnsupportedOperationException
     */
    @Override
    public CriteriaBuilder getCriteriaBuilder()
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            Tr.debug(tc, "getCriteriaBuilder : " + this);
        throw new UnsupportedOperationException("This operation is not supported on a pooling EntityManagerFactory.");
    }

    /**
     * Prohibit access to the metamodel via the pool.
     * 
     * @throws UnsupportedOperationException
     */
    @Override
    public Metamodel getMetamodel()
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            Tr.debug(tc, "getMetamodel : " + this);
        throw new UnsupportedOperationException("This operation is not supported on a pooling EntityManagerFactory.");
    }

    /**
     * Prohibit access to the persistence unit util via the pool.
     * 
     * @throws UnsupportedOperationException
     */
    @Override
    public PersistenceUnitUtil getPersistenceUnitUtil()
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            Tr.debug(tc, "getPersistenceUnitUtil : " + this);
        throw new UnsupportedOperationException("This operation is not supported on a pooling EntityManagerFactory.");
    }

    /**
     * Prohibit access to factory properties via the pool.
     * 
     * @throws UnsupportedOperationException
     */
    @Override
    public Map<String, Object> getProperties()
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            Tr.debug(tc, "getProperties : " + this);
        throw new UnsupportedOperationException("This operation is not supported on a pooling EntityManagerFactory.");
    }

    @Override
    public boolean isOpen()
    {
        return true;
    }
}
