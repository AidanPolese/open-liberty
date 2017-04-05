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
// d392996.3 EJB3      20060930 leealber : Minor bugs/typos
// d406994.2 EJB3      20061120 leealber : CI: exception handling rework
// d406994.4 EJB3      20061120 leealber : CI: miscellanious clean up
// d408412   EJB3      20061129 leealber : Make PUs in ear scope visible to modules by default
// d416151.3.7 EJB3    20070501 leealber : Add isAnyTraceEnabled() test
// d416151.3.3 EJB3    20070507 leealber : Messages/FFDC clean up.
// d416151.3.8 EJB3    20070510 leealber : Replace StringBuffer with StringBuilder.
// d440322   EJB3      20070510 leealber : Enable Loose Config
// d456716   EJB3      20070906 tkb      : use correct message prefix CWWJP
// PK59717   61FEP     20080227 hthomann : Remove pu's when an app is stopped.
// PK62950   WAS70     20080407 jckrueg  : support <jar-file> in loose config
// F743-16027 WAS80    20091029 andymc   : Decoupling from DeployedApplication/Module code for embeddable
// F743-18776
//           WAS80     20100122 bkail    : Use JPAModuleInfo
// F743-27658.1
//           WAS80     20100610 bkail    : Minimize throws clauses
// RTC113511 RWAS90    20131009 bkail    : Move processModulePUs to SharedJPAComponentImpl
// --------- --------- -------- --------- -----------------------------------------
package com.ibm.ws.jpa.management;

import static com.ibm.ws.jpa.management.JPAConstants.EAR_SCOPE_MODULE_NAME;
import static com.ibm.ws.jpa.management.JPAConstants.JPA_RESOURCE_BUNDLE_NAME;
import static com.ibm.ws.jpa.management.JPAConstants.JPA_TRACE_GROUP;

import java.util.HashMap;
import java.util.Map;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.jpa.JPAPuId;

/**
 * This is a top level persistence unit data container. In combination with JPAScopeInfo,
 * JPAPxmlInfo and JPAUnitInfo, it manages persistence unit semantics at the application level. The
 * hierarchy of these data container objects is demonstrated as following:
 * 
 * <quotes> JPAServiceImpl
 * {applName}
 * 1<--------->* JPAApplInfo
 * {scopeName} (archiveName)
 * 1<--------->* JPAScopeInfo
 * {p.xml RootURL}
 * 1<--------->* JPAPxmlInfo
 * {puName}
 * 1<-------->* JPAPUnitInfo
 * {"EAR_Scope_Module"}
 * 1<--------->1 JPAScopeInfo
 * {p.xml RootURL}
 * 1<--------->* JPAPxmlInfo
 * {puName}
 * 1<-------->* JPAPUnitInfo
 * </quotes>
 * 
 */
public abstract class JPAApplInfo
{
    private static final TraceComponent tc = Tr.register(JPAApplInfo.class,
                                                         JPA_TRACE_GROUP,
                                                         JPA_RESOURCE_BUNDLE_NAME);

    private final AbstractJPAComponent ivJPAComponent;

    private final String applName;

    // a Map to keep track of all the p.xml in the same scope, i.e. EJB, Web or EAR.
    // the modJarName is used as the key to identify the collection entry. For EAR scope
    // "EAR_Scope_Module" is used as the unique key.
    private Map<String, JPAScopeInfo> puScopes = null;

    /*
     * Constructor.
     *///F743-16027
    protected JPAApplInfo(AbstractJPAComponent jpaComponent, String applName)
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.entry(tc, "<init>", applName);

        this.ivJPAComponent = jpaComponent;
        this.applName = applName;
        this.puScopes = new HashMap<String, JPAScopeInfo>();

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.exit(tc, "<init>");
    }

    AbstractJPAComponent getJPAComponent()
    {
        return ivJPAComponent;
    }

    /*
     * Application name getter method.
     * 
     * @return Application name this object represents.
     */
    public String getApplName()
    {
        return applName;
    }

    /**
     * Adds all of the Persistence Units defined in the specified
     * persistence.xml file to the application, sorting by scope
     * and archive name. <p>
     * 
     * @param pxml provides access to a persistence.xml file as well
     *            as the archive name and scope.
     */
    public void addPersistenceUnits(JPAPXml pxml)
    {
        final boolean isTraceOn = TraceComponent.isAnyTracingEnabled();
        if (isTraceOn && tc.isDebugEnabled())
            Tr.debug(tc, "addPersistenceUnits", pxml);

        JPAPuScope puScope = pxml.getPuScope();
        String scopeKey = (puScope == JPAPuScope.EAR_Scope) ? EAR_SCOPE_MODULE_NAME : pxml.getArchiveName();

        JPAScopeInfo scopeInfo = puScopes.get(scopeKey);
        if (scopeInfo == null)
        {
            scopeInfo = new JPAScopeInfo(scopeKey, puScope);
            puScopes.put(scopeKey, scopeInfo);
        }

        // delegate to scopeInfo to read in the persistence-unit data
        scopeInfo.processPersistenceUnit(pxml, this);
    }

    protected abstract JPAPUnitInfo createJPAPUnitInfo(JPAPuId puId, JPAPXml pxml, JPAScopeInfo scopeInfo);

    /*
     * Close all the EntityManagerFactory instances associated to the persistence unit(s) of the
     * input module.
     * 
     * @param modName Module name.
     */
    private final void close(String modName, boolean remove) //PK59717
    {
        JPAScopeInfo scopeInfo = puScopes.get(modName);
        if (scopeInfo != null)
        {
            scopeInfo.close();
            if (remove) //PK59717
                puScopes.remove(modName);
        }
    }

    /*
     * Close all EntityManagerFactory in the input module.
     * 
     * @param dMod
     */
    void closeModule(String modName)
    {
        close(modName, true); //PK59717
    }

    /*
     * Close all EntityManagerFactories in all scopes in this application.
     */
    void closeAllScopeModules()
    {
        for (String module : puScopes.keySet())
        {
            close(module, false); //PK59717
        }
        puScopes.clear();
    }

    /**
     * Returns the persistence unit info object assoicated to the input module and persistence unit.
     * <ul>
     * <li>If no input puName is specified, attempt to locate any unique PU in the module first and
     * then in the ear scope.
     * <li>If pu is defined, look for the specific PU in the module, then in the ear scope.
     * <li>If relative PU name is specified, e.g. "../lib/util.jar#myPU", look for the PU in the
     * specified persistence archive jar.
     * </ul>
     * If no pu is found, null is returned.
     * 
     * @param modName
     * @param puName
     * @return The PU Info object associated to the input parameters.
     */
    // d408412 Begins
    JPAPUnitInfo getPersistenceUnitInfo(String modName, String puName)
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.entry(tc, "getPersistenceUnitInfo", modName, puName, this );

        JPAPUnitInfo rtnVal = null;
        JPAScopeInfo scopeInfo = null;
        if (puName == null || puName.length() == 0)
        {
            // if no pu name is specified, e.g.
            // @PersistenceUnit EntityManagerFactory emf;
            // try to find a unique pu in the module, then in the archive jars in the ear scope.
            // If there is more than one pu defined, it is an error, return null and log an error
            // entry.
            rtnVal = getUniquePU(modName);
        } else
        // test if pu name has "../" prefix, only if true, then look into the global scope for the
        // pu. Per JPA Spec 6.2.2.
        if (puName.startsWith("../"))
        {
            scopeInfo = puScopes.get(EAR_SCOPE_MODULE_NAME);
            if (scopeInfo != null)
            {
                String earPuName = puName.substring("../".length());
                rtnVal = scopeInfo.getPuInfo(earPuName);
            }
        } else
        // look for specific PU, first in the same module, then ear scope if not found.
        {
            scopeInfo = puScopes.get(modName);
            if (scopeInfo != null)
            {
                // if scopeInfo exists, there is at least one PU defined in the scope
                // see if there is a match.
                rtnVal = scopeInfo.getPuInfo(puName);
            }
            if (rtnVal == null)
            {
                // no PU define in the same scope, need to look for PU in the ear scope
                scopeInfo = puScopes.get(EAR_SCOPE_MODULE_NAME);
                if (scopeInfo != null)
                {
                    rtnVal = scopeInfo.getPuInfo(puName);
                }
            }
        }
        if (rtnVal == null)
        {
            Tr.error(tc, "PU_NOT_FOUND_CWWJP0029E", applName, modName, 
                     puName == null || puName.length() == 0 ? "<default>" : puName );
        }
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.exit(tc, "getPersistenceUnitInfo", rtnVal != null ? rtnVal.getPersistenceUnitName()
                            : "PU not found");
        return rtnVal;
    }

    /*
     * Found a unique persistence unit from the application, starting from the module and if not
     * found, look for one in the ear scope. Returns null if no unqiue PU is found.
     */
    private JPAPUnitInfo getUniquePU(String modName)
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.entry(tc, "getUniquePU", modName);
        JPAPUnitInfo rtnVal = null;

        JPAScopeInfo scopeInfo = puScopes.get(modName);
        int totalPus = (scopeInfo != null) ? scopeInfo.getAllPuCount() : 0;

        switch (totalPus) {
            case 1: // found a unique PU in the module.
                rtnVal = scopeInfo.getUniquePuInfo();
                break;

            case 0: // no PU in module scope, look for unique PU in ear scope
                scopeInfo = puScopes.get(EAR_SCOPE_MODULE_NAME);
                totalPus = (scopeInfo != null) ? scopeInfo.getAllPuCount() : 0;
                if (totalPus == 1)
                {
                    // found only one, use it.
                    rtnVal = scopeInfo.getUniquePuInfo();
                    break;
                }
                // fall through to indicate no unique PU is found.
            default:
                // otherwise, can not determine what to use.
                Tr.error(tc, "PU_NOT_SPECIFIED_AND_NO_UNIQUE_PU_FOUND_CWWJP0012E", applName, modName );
        }
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.exit(tc, "getUniquePU", rtnVal);
        return rtnVal;
    }

    // d408412 Ends

    /*
     * @return Number of scope in this application.
     */
    int getScopeSize()
    {
        return puScopes.size();
    }

    @Override
    public String toString()
    {
        StringBuilder sbuf = new StringBuilder();
        sbuf.append("  ApplInfo: Appl = ").append(applName);
        for (JPAScopeInfo scopeInfo : puScopes.values())
        {
            scopeInfo.toStringBuilder(sbuf);
        }
        return sbuf.toString();
    }
}
