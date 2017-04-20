// /I/ /W/ /G/ /U/   <-- CMVC Keywords, replace / with %
// %I% %W% %G% %U%
//
// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2010, 2014
//
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Module  :  JPATraversableResolver.java
//
// Change Activity:
//
// Reason    Version   Date     Userid    Change Description
// --------- --------- -------- --------- -----------------------------------------
// F743-30405 WAS80    20100715 darveaux : Defer actual bean validation work as long as possible
// d727932   WAS85     20120208 xuhaih   : refactor for bean validation access
// --------- --------- -------- --------- -----------------------------------------

package com.ibm.ws.jpa.container.beanvalidation;

import static com.ibm.ws.jpa.management.JPAConstants.JPA_RESOURCE_BUNDLE_NAME;
import static com.ibm.ws.jpa.management.JPAConstants.JPA_TRACE_GROUP;

import java.lang.annotation.ElementType;

import javax.validation.Path;
import javax.validation.TraversableResolver;
import javax.validation.ValidatorFactory;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;

/**
 * The purpose of this class is to delay obtaining obtaining the
 * javax.validation.ValidatorFactory instance for as long as possible.
 * 
 * This is only used for JPA providers that are doing bean validation,
 * and the point is to delay doing real bean validation work until after
 * the classes have been 'transformed' and the MMD has been placed on the thread.
 */
public class JPATraversableResolver implements TraversableResolver
{
    private static final TraceComponent tc = Tr.register(JPATraversableResolver.class,
                                                         JPA_TRACE_GROUP,
                                                         JPA_RESOURCE_BUNDLE_NAME);

    /**
     * The real TraversableResolver instance.
     * 
     * A real javax.validation.ValidatorFactory instance has a one-to-one association
     * with a module. A real javax.validation.ValidatorFactory always hands back the
     * same javax.validation.TraversableResolver instance. Thus, the
     * javax.validation.TraversableResolver instance also has a one-to-one
     * association with a module.
     * 
     * Our wrappers mimic this relationship structure. The JPAValidatorFactory wrapper
     * stands-in for the javax.validation.ValidatorFactory instance, and thus has a
     * one-to-one relationship with a module. The JPAValidatorFactory wrapper always
     * hands back the same instance of this JPATraversableResolver, which
     * in turn always uses the same TraversableResolver instance.
     * Thus, the javax.validation.TraversableResolver instance has a one-to-one
     * association with a module in the wrapper scenario as well.
     */
    private TraversableResolver ivTraversableResolver = null;

    private final ValidatorFactoryLocator ivValidatorFactoryLocator;

    JPATraversableResolver(ValidatorFactoryLocator locator)
    {
        ivValidatorFactoryLocator = locator;
    }

    private void obtainTraversableResolver()
    {
        final boolean isTraceOn = TraceComponent.isAnyTracingEnabled();

        ValidatorFactory validatorFactory = ivValidatorFactoryLocator.getValidatorFactory();
        ivTraversableResolver = validatorFactory.getTraversableResolver();

        if (isTraceOn && tc.isDebugEnabled())
        {
            Tr.debug(tc, "Obtained the TraversableResolver: " + ivTraversableResolver);
        }

    }

    @Override
    public boolean isCascadable(Object traversableObject,
                                Path.Node traversableProperty,
                                Class<?> rootBeanType,
                                Path pathToTraversableObject,
                                ElementType elementType)
    {
        if (ivTraversableResolver == null)
        {
            obtainTraversableResolver();
        }

        return ivTraversableResolver.isCascadable(traversableObject,
                                                  traversableProperty,
                                                  rootBeanType,
                                                  pathToTraversableObject,
                                                  elementType);
    }

    @Override
    public boolean isReachable(Object traversableObject,
                               Path.Node traversableProperty,
                               Class<?> rootBeanType,
                               Path pathToTraversableObject,
                               ElementType elementType)
    {
        if (ivTraversableResolver == null)
        {
            obtainTraversableResolver();
        }

        return ivTraversableResolver.isReachable(traversableObject,
                                                 traversableProperty,
                                                 rootBeanType,
                                                 pathToTraversableObject,
                                                 elementType);
    }
}
