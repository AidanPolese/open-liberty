/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2006, 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.ejbcontainer.injection.factory;

import java.util.Hashtable;

import javax.ejb.EJBContext;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

import com.ibm.ejs.container.BeanMetaData;
import com.ibm.ejs.container.EJSContainer;
import com.ibm.ejs.ras.Tr;
import com.ibm.ejs.ras.TraceComponent;
import com.ibm.ws.runtime.metadata.ComponentMetaData;
import com.ibm.ws.threadContext.ComponentMetaDataAccessorImpl;
import com.ibm.wsspi.injectionengine.InjectionException;
import com.ibm.wsspi.injectionengine.InjectionTargetContext;
import com.ibm.wsspi.injectionengine.factory.InjectionObjectFactory;

/**
 * EJBContext Resolver Factory for injection and lookup. <p>
 * 
 * This class is used as an object factory that returns the EJBContext
 * for the currently active EJB. An InjectionException occurs when the
 * factory is invoked outside the scope of an EJB. <p>
 * 
 * This factory is used when injection occurs or when a component performs
 * a lookup in the java:comp name space for EJBContext subclasses.
 */
public class EJBContextObjectFactory implements InjectionObjectFactory
{
    private static final TraceComponent tc = Tr.register
                    (EJBContextObjectFactory.class,
                     "EJBContainer", "com.ibm.ejs.container.container");

    /**
     * Required default no argument constructor.
     */
    public EJBContextObjectFactory()
    {
        // Default Constructor
    }

    /**
     * (non-Javadoc)
     * 
     * @see ObjectFactory#getObjectInstance
     */
    public Object getObjectInstance(Object obj,
                                    Name name,
                                    Context nameCtx,
                                    Hashtable<?, ?> environment)
                    throws Exception
    {
        final boolean isTraceOn = TraceComponent.isAnyTracingEnabled();
        if (isTraceOn && tc.isEntryEnabled())
            Tr.entry(tc, "getObjectInstance : " + obj);

        // F743-17630CodRv
        // This factory should only be used from inside an ejb.
        //
        // Checking for the lack of EJBContext is good enough to tell us
        // we are not inside an ejb when the code flow was started by some
        // non-ejb component (such as a servlet).  However, in the case where
        // we've got an ejb that calls into a webservice endpoint, then there
        // may be a ContainerTx that has a BeanO, even though we are not
        // allowed to use the factory from inside the webservice endpoint.
        //
        // To account for this, we must also check the ComponentMetaData (CMD) and
        // confirm that it is ejb specific.  In the webservice endpoint case,
        // the CMD will have been switched to some non-ejb CMD, and so this check
        // so stop us.
        EJBContext ejbContext = EJSContainer.getCallbackBeanO(); // d630940
        ComponentMetaData cmd = ComponentMetaDataAccessorImpl.getComponentMetaDataAccessor().getComponentMetaData();
        if (ejbContext == null ||
            !(cmd instanceof BeanMetaData))
        {
            InjectionException iex = new InjectionException
                            ("The EJBContext type may only be injected into an EJB instance " +
                             "or looked up within the context of an EJB.");
            Tr.error(tc, "EJB_CONTEXT_DATA_NOT_AVAILABLE_CNTR0329E",
                     new Object[] { EJBContext.class.getName() });
            if (isTraceOn && tc.isEntryEnabled())
                Tr.exit(tc, "getObjectInstance : ", iex);
            throw iex;
        }

        if (isTraceOn && tc.isEntryEnabled())
            Tr.exit(tc, "getObjectInstance : " + ejbContext);

        return ejbContext;
    }

    @Override
    public Object getInjectionObjectInstance(Reference ref,
                                             Object targetInstance,
                                             InjectionTargetContext targetContext)
                    throws Exception
    {
        if (targetContext != null)
        {
            EJBContext ejbContext = targetContext.getInjectionTargetContextData(EJBContext.class);
            if (ejbContext != null)
            {
                return ejbContext;
            }
        }

        return getObjectInstance(ref, null, null, null);
    }
}
