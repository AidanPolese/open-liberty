// IBM Confidential OCO Source Material
// Copyright IBM Corp. 2006, 2013
//
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change Activity:
//
// Reason    Version   Date     Userid    Change Description
// --------- --------- -------- --------- -----------------------------------------
// d392996   EJB3      20060930 leealber : Initial Release
// d392996.4 EJB3      20061102 leealber : Process invalid persistence.xml parsing exception.
// d403070   EJB3      20061108 leealber : Temporary add PU property variable expansion for RESOURCE-LOCAL limitation.
// d406735   EJB3      20061120 leealber : Correct <exclude-unlisted-classes> semantics interpretation.
// d406994   EJB3      20071122 leealber : CI:change package to com.ibm.ws.jpa.pxml10
// d406994.2 EJB3      20061120 leealber : CI: exception handling rework
// d412944   EJB3      20070103 leealber : Enable JPA in TEExplorer
// d408408   EJB3      20070123 mikedd   : Close inputStream after parsing persistence,xml
// d416151   EJB3      20070122 leealber : Container managed persistence context part I
// d429219   EJB3      20070328 leealber : Temporarily disable java:comp/env data source support.
// d416151.3 EJB3      20070306 leealber : Extend-scoped support
// d416151.3.7 EJB3    20070501 leealber : Add isAnyTraceEnabled() test
// d416151.3.11 EJB3   20070504 leealber : Code review clean up.
// d416151.3.8 EJB3    20070510 leealber : Replace StringBuffer with StringBuilder.
// d441029   EJB3      20070522 leealber : Correct pu search in EAR scope persistence archive.
// d458689   EJB3      20070831 kjlaw    : Pass archive name to CTOR of JPAPUnitInfo.
// d456716   EJB3      20070906 tkb      : use correct message prefix CWWJP
// d460065   EJB3      20070907 tkb      : improve message replacement parameters
// d473432.1 EJB3      20071011 leealber : Add provider version/revision information in toString.
// d481950   WAS70     20080128 tkb      : move close() to finally block
// d496032   WAS70     20080212 jckrueg  : Put exception out instead of NPE for property syntax error.
// d496032.1 WAS70     20080220 jckrueg  : Fix error message
// d500811   WAS70     20080307 jckrueg  : Validate persistence.xml
// d507361   WAS70     20080324 leealber : Incorrect debug root url text in debug string for loose config
// PK62950   WAS70     20080407 jckrueg  : support <jar-file> in loose config
// d510184   WAS70     20080505 tkb      : Create separate EMF for each java:comp
// F743-954.1 WASX     20090330 leealber : Add additional JPA 2.0 APIs
// F743-8064 WAS80     20090414 jckrueg  : Support 2.0 schema
// F743-8705 WAS80     20090530 leealber : Support caching and validationMode
// d595912   WAS80     20090610 tkb      : do not validate until schema is known
// d600097   WAS80     20090713 leealber : Move up to EA4 level of API definition
// d602618   WASX      20090722 leealber : Update to JPA 2.0 API to EA5 level and temporary
// d618559   WASX      20091014 leealber : Update to JPA 2.0 API to EA9 level - Final Draft
// F743-16027 WAS80    20091029 andymc   : Handling schema file inside embeddable container jar
// F1879-16302
//           WAS80     20091112 tkb      : support 2.0 and 1.0 xml separately
// d681393   WAS80     20101209 tkb      : remove plugin from parent classloader
// d689596   WAS80     20110204 bkail    : Create JPAPUnitInfo with archive JPAPuId
// RTC113511 RWAS90    20131009 bkail    : Use JPAApplInfo.createJPAPUnitInfo
// --------- --------- -------- --------- -----------------------------------------
package com.ibm.ws.jpa.management;

import static com.ibm.ws.jpa.management.JPAConstants.JPA_RESOURCE_BUNDLE_NAME;
import static com.ibm.ws.jpa.management.JPAConstants.JPA_TRACE_GROUP;
import static com.ibm.ws.jpa.management.JPAConstants.PERSISTENCE_XML_RESOURCE_NAME;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.jpa.JPAPuId;

/**
 * This is a data container manages persistence unit information defined in a ejb-jar, war or jar.
 * Each of this object represents a persistenc.xml in an application.
 * 
 * @see com.ibm.ws.jpa.management.JPAApplInfo
 */
class JPAPxmlInfo
{
    private static final TraceComponent tc = Tr.register
                    (JPAPxmlInfo.class,
                     JPA_TRACE_GROUP,
                     JPA_RESOURCE_BUNDLE_NAME);

    // Scope information associated to this persistence.xml.
    private JPAScopeInfo ivScopeInfo;

    // Root URL of this persistence.xml.
    private URL ivRootURL;

    // List of persistence units defined in this persistence.xml.
    private Map<String, JPAPUnitInfo> ivPuList;

    /**
     * Constructor.
     * 
     * @param scopeInfo
     * @param rootURL
     */
    JPAPxmlInfo(JPAScopeInfo scopeInfo, URL rootURL)
    {
        super();

        final boolean isTraceOn = TraceComponent.isAnyTracingEnabled();
        if (isTraceOn && tc.isEntryEnabled())
            Tr.entry(tc, "<init>", scopeInfo, rootURL );

        ivScopeInfo = scopeInfo;
        ivRootURL = rootURL;
        ivPuList = new HashMap<String, JPAPUnitInfo>();

        if (isTraceOn && tc.isEntryEnabled())
            Tr.exit(tc, "<init>");
    }

    /**
     * Populates the list of persistence units defined in this persistence.xml.
     * 
     * @param pxml
     * @param looseConfig
     */
    void extractPersistenceUnits(JPAPXml pxml)
    {
        final boolean isTraceOn = TraceComponent.isAnyTracingEnabled();
        if (isTraceOn && tc.isEntryEnabled())
            Tr.entry(tc, "extractPersistenceUnits : " + pxml);

        // Determines the correct schema version, and uses the correct version
        // of JAXB generated classes to parse the persistence.xml file... and
        // the correct version of .xsd to validate against.            F1879-16302
        JaxbPersistence p = JaxbUnmarshaller.unmarshal(pxml);

        List<JaxbPUnit> pus = p.getPersistenceUnit();
        for (JaxbPUnit pu : pus)
        {
            // Guarantee to have a puName from <persistence-unit>
            String puName = pu.getName();
            // Set <persistence-unit>
            JPAApplInfo applInfo = pxml.getApplInfo();
            JPAPuId puId = new JPAPuId(applInfo.getApplName(), pxml.getArchiveName(), puName); // d689596
            JPAPUnitInfo puInfo = applInfo.createJPAPUnitInfo(puId, pxml, ivScopeInfo);

            // set XML Schema version
            puInfo.setPersistenceXMLSchemaVersion(p.getVersion());

            // Must set the root URL first for other puInfo attribute to reference
            // determine the root of the persistence unit.
            puInfo.setPersistenceUnitRootUrl(pxml.getRootURL());

            // JaxbPunit abstraction properly maps the TransactionType from
            // the JAXB generated class to the JPA enum value.          F1879-16302
            puInfo.setTransactionType(pu.getTransactionType());

            // Set <persistence-unit>
            puInfo.setPersistenceUnitDescription(pu.getDescription());

            // Set <provider>
            puInfo.setPersistenceProviderClassName(pu.getProvider());

            // Set <jta-data-source>
            puInfo.setJtaDataSource(pu.getJtaDataSource());

            // Set <nonjta-data-source>
            puInfo.setNonJtaDataSource(pu.getNonJtaDataSource());

            // Set <mapping-file>
            puInfo.setMappingFileNames(pu.getMappingFile());

            // Set <jar-file>
            puInfo.setJarFileUrls(pu.getJarFile(), pxml); //PK62950

            // Set <class>
            puInfo.setManagedClassNames(pu.getClazz());

            // Set <shared-cache-mode> (mapped by JaxbPUnit abstraction) // F743-8705 F1879-16302
            puInfo.setSharedCacheMode(pu.getSharedCacheMode());

            // Set <validataion-mode> (mapped by JaxbPUnit abstraction)  // F743-8705 F1879-16302
            puInfo.setValidationMode(pu.getValidationMode());

            // Set <exclude-unlisted-classes>
            puInfo.setExcludeUnlistedClasses(pu.isExcludeUnlistedClasses());

            // Set <properties> (mapped by JaxbPUnit abstraction)       F1879-16302
            puInfo.setProperties(pu.getProperties());

            if (isTraceOn && tc.isDebugEnabled())
            {
                String rootURLStr = pxml.getRootURL().getFile();
                int earIndex = rootURLStr.indexOf(applInfo.getApplName() + ".ear"); // d507361
                if (earIndex != -1) { // d507361
                    rootURLStr = rootURLStr.substring(earIndex // d507361
                                                      + applInfo.getApplName().length() + 5); // d507361
                } // d507361
                rootURLStr += PERSISTENCE_XML_RESOURCE_NAME; // d507361
                Tr.debug(tc, "extractPersistenceUnits : " + applInfo.getApplName() +
                             "|" + pxml.getArchiveName() + "|" + rootURLStr + "|" +
                             puInfo.getPersistenceUnitName() + "|" +
                             ivScopeInfo.getScopeType() + "|" + puInfo.dump());
            }

            if (getPuInfo(puName) != null) // d441029
            {
                Tr.warning(tc,
                           "DUPLICATE_PERSISTENCE_UNIT_DEFINED_CWWJP0007W",
                           puName, applInfo.getApplName(), pxml.getArchiveName()); // d460065
                puInfo.close(); // d681393
            }
            else
            {
                addPU(puName, puInfo);

                // This getFactory initiates the createEntityManagerFactory call to
                // the persistence provider for this pu. This process will trigger
                // the provider to use the jta/non-jta data source defined in the
                // pu as well as register transformer to the application classloader
                // so that when entity class is loaded, the provider can enhance
                // the entity class.
                //
                // However if the factory is created at this point, the resource
                // ref defined for the associated component has not been initialized
                // yet and hence the java:comp/env scheme of specifying data sources
                // in persistence.xml can not be processed.
                //
                // But, if the factory is NOT created at this point, the provider
                // will not register the transformer, and thus the class will not
                // be transformed when it is loaded.
                //
                // To solve this problem, the factory will be created now, and a
                // 'generic' datasource will be provided, but, the created factory
                // will never be used. When a component finally accesses a factory,
                // (after the component has started), a new factory will be created,
                // one for every java:comp/env name context.  Then, each component
                // will have a factory created with the correct datasource.
                puInfo.initialize(); // d429219 d510184
            }
        }

        if (isTraceOn && tc.isEntryEnabled())
            Tr.exit(tc, "extractPersistenceUnits : # of PU defined = " + ivPuList.size());
    }

    /**
     * Close all the active EntityManagers declared in this persistence.xml.
     */
    void close()
    {
        for (JPAPUnitInfo puInfo : ivPuList.values())
        {
            puInfo.close();
        }
        ivPuList.clear();
    }

    /**
     * Returns the PersistenceUnitInfo object for persistence unit (puName) or null if not
     * defined.
     */
    JPAPUnitInfo getPuInfo(String puName)
    {
        return ivPuList.get(puName);
    }

    /**
     * Adds the puInfo to the collection maintained in this xml info object.
     * 
     * @param puName
     * @param puInfo
     * @return JPAUnitInfo just added.
     */
    JPAPUnitInfo addPU(String puName, JPAPUnitInfo puInfo)
    {
        return ivPuList.put(puName, puInfo);
    }

    /**
     * Returns all persistence unit name defined in this persistence.xml.
     * 
     * @return Persistence unit name collection.
     */
    Set<String> getPuNames()
    {
        return ivPuList.keySet();
    }

    /**
     * Returns the number of persistence unit defined in this persistence.xml.
     * 
     * @return Defined persistence unit count.
     */
    int getPuCount()
    {
        return ivPuList.size();
    }

    /**
     * Dump this persistence.xml data to the input StringBuilder.
     */
    StringBuilder toStringBuilder(StringBuilder sbuf)
    {
        sbuf.append("\n  PxmlInfo: ScopeName=").append(ivScopeInfo.getScopeName())
                        .append("\tRootURL = ").append(ivRootURL).append("\t# PUs = ")
                        .append(ivPuList.size()).append("\t[");
        int index = 0;
        for (JPAPUnitInfo puInfo : ivPuList.values())
        {
            puInfo.toStringBuilder(sbuf);
            if (++index < ivPuList.size())
            {
                sbuf.append(", ");
            }
        }
        sbuf.append(']');
        return sbuf;
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return toStringBuilder(new StringBuilder()).toString();
    }
}
