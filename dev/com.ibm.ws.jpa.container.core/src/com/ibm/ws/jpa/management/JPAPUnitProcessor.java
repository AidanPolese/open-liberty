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
// d392996.3 EJB3      20061020 leealber : Minor bugs/typos
// d367834.12 EJB3     20061031 schmittm : changed class to fit AnnotationProcessor format
// d403140   EJB3      20061107 schmittm : Fix annotationCollection
// d404167   EJB3      20061115 schmittm : Make Annotation Processors singletons
// d410616   EJB3      20061208 leealber : replace calling DeployedModuleImpl.getJarName() hack with getName()
// d416151   EJB3      20070122 leealber : Container managed persistence context part I
// d416151.2 EJB3      20070220 leealber : Container managed persistence context part 2
// d367834.15 EJB3     20070316 schmittm : Refactor new injection engine Pyxis changes
// d427858   EJB3      20070321 schmittm : make class public for reflection newInstance() call
// d428680   EJB3      20070324 tkb      : use CompNameSpaceConfig for parameters
// d426211   EJB3      20070329 schmittm : move extractAnnotationsFromDD() processing to InjectionEngine
// d428309   EJB3      20070402 schmittm : remove context references and extra methods
// d430982   EJB3      20070405 leealber : add instanceClass for addCookie call in extractAnnotationsFromDD
// d416151.3 EJB3      20070306 leealber : Extend-scoped support
// d416151.3.5 EJB3    20070501 leealber : Rename JPAService to JPAComponent
// d416151.3.7 EJB3    20070501 leealber : Add isAnyTraceEnabled() test
// d416151.3.4 EJB3    20070502 leealber : Clean up resolve error handling
// d416151.3.1 EJB3    20070510 leealber : Inject provider EMF for SLSB
// d432816   EJB3      20070614 schmittm : Use new InjectionEngine framework
// d460065   EJB3      20070907 tkb      : improve messages
//LIDB3294-35.1 WASX   20071212 schmittm : manual override of default processor
// d510184   WAS70     20080505 tkb      : Create separate EMF for each java:comp
// d658856   WAS80     20100629 tkb      : merge PUs from XML
// F743-30682
//           WAS80     20100820 bkail    : Call AbstractJPAInjectionBinding.addRefComponents
// d662814   WAS80     20110115 tkb      : merge must detect conflicts
// F46946.1  WAS85     20110719 bkail    : Use javaee.dd.common
// F50309.1  WAS85     20110829 bkail    : Refine injection API
// F50309.3  WAS85     20110914 bkail    : Update createInjectionBinding signature
// RTC113511 RWAS90    20131009 bkail    : Use AbstractJPAComponent.createPersistenceUnitReference
// --------- --------- -------- --------- -----------------------------------------
package com.ibm.ws.jpa.management;

import static com.ibm.ws.jpa.management.JPAConstants.JPA_RESOURCE_BUNDLE_NAME;
import static com.ibm.ws.jpa.management.JPAConstants.JPA_TRACE_GROUP;
import static com.ibm.wsspi.injectionengine.ComponentNameSpaceConfiguration.ReferenceFlowKind.HYBRID;

import java.lang.reflect.Member;
import java.util.List;

import javax.naming.Reference;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.PersistenceUnits;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.javaee.dd.common.InjectionTarget;
import com.ibm.ws.javaee.dd.common.PersistenceUnitRef;
import com.ibm.ws.jpa.JPAAccessor;
import com.ibm.ws.jpa.JPAPuId;
import com.ibm.wsspi.injectionengine.InjectionBinding;
import com.ibm.wsspi.injectionengine.InjectionException;
import com.ibm.wsspi.injectionengine.InjectionProcessor;

/**
 * A subclass of the InjectionProcessor to handle @PersistenceUnit and
 * 
 * @PersistenceUnits annotations.
 */
public class JPAPUnitProcessor extends InjectionProcessor<PersistenceUnit, PersistenceUnits>
{
    private static final TraceComponent tc = Tr.register(JPAPUnitProcessor.class,
                                                         JPA_TRACE_GROUP,
                                                         JPA_RESOURCE_BUNDLE_NAME);

    public JPAPUnitProcessor()
    {
        super(PersistenceUnit.class, PersistenceUnits.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.wsspi.injectionengine.InjectionProcessor#createInjectionBinding(java.lang.annotation.Annotation, com.ibm.wsspi.injectionengine.ComponentNameSpaceConfiguration)
     */
    @Override
    public InjectionBinding<PersistenceUnit> createInjectionBinding
                    (PersistenceUnit annotation, Class<?> instanceClass, Member member, String jndiName)
                                    throws InjectionException
    {
        JPAPUnitInjectionBinding injectionBindingResource = new JPAPUnitInjectionBinding(annotation, ivNameSpaceConfig);
        return injectionBindingResource;
    }

    /**
     * (non-Javadoc)
     * 
     * @see com.ibm.wsspi.injectionengine.InjectionProcessor#processXML
     */
    @Override
    public void processXML() throws InjectionException
    {
        final boolean isTraceOn = TraceComponent.isAnyTracingEnabled();
        if (isTraceOn && tc.isEntryEnabled())
            Tr.entry(tc, "processXML : " + ivNameSpaceConfig.getPersistenceUnitRefs());

        // d416151.3 Begins
        List<? extends PersistenceUnitRef> pUnitRefs = ivNameSpaceConfig.getPersistenceUnitRefs();
        if (pUnitRefs != null && pUnitRefs.size() > 0)
        {
            for (PersistenceUnitRef pUnitRef : pUnitRefs)
            {
                if (isTraceOn && tc.isDebugEnabled())
                    Tr.debug(tc, " pUnitRef = " + pUnitRef);

                // If XML has previously been read and an injection binding with the same
                // jndi name has been created, get the current injection binding and merge
                // the new PersistenceUnit Ref into it.
                String jndiName = pUnitRef.getName();
                InjectionBinding<PersistenceUnit> injectionBinding = ivAllAnnotationsCollection.get(jndiName);
                if (injectionBinding != null)
                {
                    ((JPAPUnitInjectionBinding) injectionBinding).merge(pUnitRef);
                }
                else
                {
                    injectionBinding = new JPAPUnitInjectionBinding(pUnitRef, ivNameSpaceConfig); // d662814
                    addInjectionBinding(injectionBinding);
                }

                ((AbstractJPAInjectionBinding<?>) injectionBinding).addRefComponents(jndiName); // F743-30682

                // Process any injection-targets that may be specified.    d429866.1
                // The code takes into account the possibility of duplicate InjectionTargets
                // and will only add if not already present, regardless of whether this is
                // a newly created binding, or a second one being merged.
                List<InjectionTarget> targets = pUnitRef.getInjectionTargets();
                if (targets != null && !targets.isEmpty())
                {
                    for (InjectionTarget target : targets)
                    {
                        String injectionClassName = target.getInjectionTargetClassName();
                        String injectionName = target.getInjectionTargetName();
                        injectionBinding.addInjectionTarget(EntityManagerFactory.class, injectionName, injectionClassName);
                    }
                }
            }
        }
        // d416151.3 Ends
        if (isTraceOn && tc.isEntryEnabled())
            Tr.exit(tc, "processXML : " + ivAllAnnotationsCollection.size());
    }

    @Override
    public void resolve(InjectionBinding<PersistenceUnit> injectionBinding)
                    throws InjectionException
    {
        final boolean isTraceOn = TraceComponent.isAnyTracingEnabled();
        if (isTraceOn && tc.isEntryEnabled())
            Tr.entry(tc, "resolve : " + injectionBinding);
        JPAPUnitInjectionBinding pUnitBinding = (JPAPUnitInjectionBinding) injectionBinding;

        String applName = pUnitBinding.getApplName();
        String modJarName = pUnitBinding.getModJarName();
        String puName = pUnitBinding.getAnnotation().unitName();

        // If this is a WAR module with EJBs, then the isSFSB setting is not reliable,
        // so use a different object factory which knows how to look at the metadata
        // on the thread to determine if running in a stateful bean context.   F743-30682
        boolean isEJBinWar = ivNameSpaceConfig.getOwningFlow() == HYBRID;
        JPAPuId puId = new JPAPuId(applName, modJarName, puName);

        AbstractJPAComponent jpaComponent = (AbstractJPAComponent) JPAAccessor.getJPAComponent();
        Reference ref = jpaComponent.createPersistenceUnitReference
                        (isEJBinWar,
                         puId,
                         ivNameSpaceConfig.getJ2EEName(), // d510184
                         pUnitBinding.getJndiName(), // d510184
                         ivNameSpaceConfig.isSFSB()); // d416151.3.1

        pUnitBinding.setObjects(null, ref);

        if (isTraceOn && tc.isEntryEnabled())
            Tr.exit(tc, "resolve : " + pUnitBinding);
    }

    @Override
    public String getJndiName(PersistenceUnit annotation)
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            Tr.debug(tc, "annotation.name=" + annotation.name());
        return annotation.name();
    }

    @Override
    public PersistenceUnit[] getAnnotations(PersistenceUnits annotation)
    {
        return annotation.value();
    }
}
