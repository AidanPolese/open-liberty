// /I/ /W/ /G/ /U/   <-- CMVC Keywords, replace / with %
// %I% %W% %G% %U%
//
// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2007, 2011
//
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Module  :  JPAEMFactory.java
//
// Source File Description:
//
// Change Activity:
//
// Reason    Version   Date     Userid    Change Description
// --------- --------- -------- --------- -----------------------------------------
// d416151   EJB3      20070122 leealber : Initial Release
// d416151.2 EJB3      20070220 leealber : Container managed persistence context part 2
// d416151.3 EJB3      20070306 leealber : Extend-scoped support
// d433963   EJB3      20070420 leealber : Fix NPE in toString.
// d416151.3.5 EJB3    20070501 leealber : Rename JPAService to JPAComponent
// d416151.3.7 EJB3    20070501 leealber : Add isAnyTraceEnabled() test
// d446013   EJB3      20070618 leealber : Injected same instance of JPA?mEntityManager
// d510184   WAS70     20080505 tkb      : Create seperate EMF for each java:comp
// F743-954  WAS80     20090226 leealber : Add JPA 2.0 APIs
// F743-954.1 WASX     20090330 leealber : Add additional JPA 2.0 APIs
// d602618   WASX      20090722 leealber : Update to JPA 2.0 API to EA5 level and temporary 
// d618559   WASX      20091014 leealber : Update to JPA 2.0 API to EA9 level - Final Draft
// F743-16027 WAS80    20091029 andymc   : Removed caching of JPAComponent 
// F743-18776
//           WAS80     20100122 bkail    : Use AbstractJPAComponent, not JPAComponentImpl
// d706751   WAS80     20110609 bkail    : Add wrap; support WSJPAEMFactory and OpenJPAEMFactory
// RTC112113 WAS90     20131023 leealber : Add new JPA 2.1 APIs
// --------- --------- -------- --------- -----------------------------------------
package com.ibm.ws.jpa.management;

import static com.ibm.ws.jpa.management.JPAConstants.JPA_RESOURCE_BUNDLE_NAME;
import static com.ibm.ws.jpa.management.JPAConstants.JPA_TRACE_GROUP;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

import javax.persistence.Cache;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.PersistenceUnitUtil;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.metamodel.Metamodel;

import com.ibm.websphere.csi.J2EEName;
import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.jpa.JPAAccessor;
import com.ibm.ws.jpa.JPAPuId;

/**
 * This class is a proxy/wrapper to enable serialization of
 * EntityManagerFactory. This class is exposed as a public API because the
 * EntityManagerFactory interface does not have an unwrap method. The only
 * supported use of this class is to call {@link #unwrap}.
 * 
 * @ibm-api
 */
public class JPAEMFactory
                implements EntityManagerFactory,
                Serializable
{
    private static final long serialVersionUID = 5790871719838228801L;

    private static final TraceComponent tc = Tr.register(JPAEMFactory.class,
                                                         JPA_TRACE_GROUP,
                                                         JPA_RESOURCE_BUNDLE_NAME);

    private JPAPuId ivPuId;

    private J2EEName ivJ2eeName; // d510184

    protected transient EntityManagerFactory ivFactory;

    protected JPAEMFactory(JPAPuId puId, J2EEName j2eeName, EntityManagerFactory emf) {
        ivPuId = puId;
        ivJ2eeName = j2eeName; // d510184
        ivFactory = emf;
    }

    public JPAEMFactory(JPAEMFactory wrapper) {
        ivPuId = wrapper.ivPuId;
        ivJ2eeName = wrapper.ivJ2eeName;
        ivFactory = wrapper.ivFactory;
    }

    /**
     * Return an object of the specified type to allow access to
     * provider-specific API.
     * 
     * @param cls the class of the object to be returned
     * @return an instance of the specified class
     * @throws PersistenceException if the class is not supported
     */
    public <T> T unwrap(Class<T> cls) // d706751
    {
        if (cls.isInstance(ivFactory))
        {
            return cls.cast(ivFactory);
        }

        throw new PersistenceException(cls.toString());
    }

    @Override
    public void close()
    {
        ivFactory.close();
    }

    @Override
    public EntityManager createEntityManager()
    {
        return ivFactory.createEntityManager();
    }

    @Override
    @SuppressWarnings("unchecked")
    public EntityManager createEntityManager(Map arg0)
    {
        return ivFactory.createEntityManager(arg0);
    }

    @Override
    public boolean isOpen()
    {
        return ivFactory.isOpen();
    }

    @Override
    public String toString()
    {
        return super.toString() + '[' + ivPuId +
               ", " + ivJ2eeName +
               ", " + ivFactory + ']';
    }

    /*
     * Instance serialization.
     */
    private void writeObject(java.io.ObjectOutputStream out) throws IOException
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.entry(tc, "writeObject : " + ivPuId + ", " + ivJ2eeName);

        out.writeObject(ivPuId);
        out.writeObject(ivJ2eeName); // d510184

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.exit(tc, "writeObject");
    }

    /*
     * Instance de-serialization.
     */
    private void readObject(java.io.ObjectInputStream in) throws IOException,
                    ClassNotFoundException
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.entry(tc, "readObject");

        ivPuId = (JPAPuId) in.readObject();
        ivJ2eeName = (J2EEName) in.readObject(); // d510184

        // restore the provider factory from JPA Service via puInfo object.
        //F743-16027 - using JPAAccessor to get JPAComponent, rather than using cached (possibly stale) static reference
        JPAPUnitInfo puInfo = ((AbstractJPAComponent) JPAAccessor.getJPAComponent()).findPersistenceUnitInfo(ivPuId); // d416151.3.5, F743-18776

        ivFactory = puInfo.getEntityManagerFactory(ivJ2eeName); // d510184

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.exit(tc, "readObject : " + this);
    }

    private Object readResolve() // d706751
    {
        // If necessary, create a JPARuntime for an uplevel JPA version.
        JPARuntime jpaRuntime = ((AbstractJPAComponent) JPAAccessor.getJPAComponent()).getJPARuntime();
        Object wrapper = jpaRuntime.isDefault() ? this : jpaRuntime.createJPAEMFactory(ivPuId, ivJ2eeName, ivFactory);

        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            Tr.debug(tc, "readResolve: " + wrapper);
        return wrapper;
    }

    // New JPA 2.0 methods   //F743-954 F743-954.1

    @Override
    public Cache getCache() {
        return ivFactory.getCache();
    }

    @Override
    public Map<String, Object> getProperties() {
        return ivFactory.getProperties();
    }

    @Override
    public CriteriaBuilder getCriteriaBuilder() {
        return ivFactory.getCriteriaBuilder();
    }

    @Override
    public Metamodel getMetamodel() {
        return ivFactory.getMetamodel();
    }

    @Override
    public PersistenceUnitUtil getPersistenceUnitUtil() {
        return ivFactory.getPersistenceUnitUtil();
    }
}
