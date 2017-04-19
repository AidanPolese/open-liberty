// /I/ /W/ /G/ /U/   <-- CMVC Keywords, replace / with %
// %I% %W% %G% %U%
//
// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2010, 2016
//
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Module  :  JPAValidatorContext.java
//
// Change Activity:
//
// Reason    Version   Date     Userid    Change Description
// --------- --------- -------- --------- -----------------------------------------
// F743-30405 WAS80    20100715 darveaux : Defer actual bean validation work as long as possible
// d727932   WAS85     20120208 xuhaih   : refactor for bean validation access
// RTC200353  WAS90    20160107 nmittles : Adding placeholder methods to support bval 1.1 API updates
// RTC202465  WAS90    20160126 njr      : Implement bean validation 1.1 methods
// --------- --------- -------- --------- -----------------------------------------

package com.ibm.ws.jpa.container.beanvalidation;

import static com.ibm.ws.jpa.management.JPAConstants.JPA_RESOURCE_BUNDLE_NAME;
import static com.ibm.ws.jpa.management.JPAConstants.JPA_TRACE_GROUP;

import javax.validation.ConstraintValidatorFactory;
import javax.validation.MessageInterpolator;
import javax.validation.ParameterNameProvider;
import javax.validation.TraversableResolver;
import javax.validation.Validator;
import javax.validation.ValidatorContext;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;

public class JPAValidatorContext implements ValidatorContext
{
    /**
     * The purpose of this class is to delay obtaining obtaining the
     * javax.validation.ValidatorFactory instance for as long as possible.
     * 
     * This is only used for JPA providers that are doing bean validation,
     * and the point is to delay doing real bean validation work until after
     * the classes have been 'transformed' and the MMD has been placed on the thread.
     * 
     * Instances of this wrapper are NOT cached in the JPAValidatorFactory wrapper.
     * Rather, each time JPAValidatorFactory.usingContext() is invoked, a new instance
     * of this wrapper is created and returned. This matches the behavior of
     * javax.validation.ValidatorFactory, which also creates a new instance
     * of the ValidatorContext each time the .usingContext() method is invoked.
     * 
     */

    private static final TraceComponent tc = Tr.register(JPAValidatorContext.class,
                                                         JPA_TRACE_GROUP,
                                                         JPA_RESOURCE_BUNDLE_NAME);

    // The user can ask the ValidatorContext to create the Validator instance
    // using non-default versions of the TraversableResolver, MessageInterpolator,
    // and ConstraintValidatorFactory.
    //
    // In this case, the specified instances of these objects are cached in the
    // wrapper until the wrapper is ask to create the Validator instance.
    //
    // Of course, we still don't actually create the Validator instance at that
    // time...rather, we just give back the JPAValidator...which means that
    // the JPAValidator needs to be able to create the underlying
    // javax.validation.Validator instance at some point in the future if needed.
    // To make this possible, these objects are passed into the JPAValidator
    // and cached there for future use.
    private TraversableResolver ivSpecifiedTraversableResolver;
    private MessageInterpolator ivSpecifiedMessageInterpolator;
    private ConstraintValidatorFactory ivSpecifiedConstraintValidatorFactory;
    private ParameterNameProvider ivSpecifiedParameterNameProvider;
    private final ValidatorFactoryLocator ivValidatorFactoryLocator;

    JPAValidatorContext(ValidatorFactoryLocator locator)
    {
        ivValidatorFactoryLocator = locator;
    }

    @Override
    public ValidatorContext constraintValidatorFactory(ConstraintValidatorFactory factory)
    {
        final boolean isTraceOn = TraceComponent.isAnyTracingEnabled();
        if (isTraceOn && tc.isDebugEnabled())
        {
            Tr.debug(tc, "Caching specified ConstraintValidatorFactory: " + factory);
        }

        ivSpecifiedConstraintValidatorFactory = factory;

        return this;
    }

    @Override
    public Validator getValidator()
    {
        return new JPAValidator(ivValidatorFactoryLocator,
                        ivSpecifiedTraversableResolver,
                        ivSpecifiedMessageInterpolator,
                        ivSpecifiedConstraintValidatorFactory,
                        ivSpecifiedParameterNameProvider);
    }

    @Override
    public ValidatorContext messageInterpolator(MessageInterpolator messageInterpolator)
    {
        final boolean isTraceOn = TraceComponent.isAnyTracingEnabled();
        if (isTraceOn && tc.isDebugEnabled())
        {
            Tr.debug(tc, "Caching specified message interpolator: " + messageInterpolator);
        }

        ivSpecifiedMessageInterpolator = messageInterpolator;

        return this;
    }

    @Override
    public ValidatorContext traversableResolver(TraversableResolver traversableResolver)
    {
        final boolean isTraceOn = TraceComponent.isAnyTracingEnabled();
        if (isTraceOn && tc.isDebugEnabled())
        {
            Tr.debug(tc, "Caching specified traversable resolver: " + traversableResolver);
        }

        ivSpecifiedTraversableResolver = traversableResolver;

        return this;
    }

   @Override
   public ValidatorContext parameterNameProvider(ParameterNameProvider parameterNameProvider) {
       if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
          Tr.debug(tc, "Caching specified parameter name provider: " + parameterNameProvider);

       ivSpecifiedParameterNameProvider = parameterNameProvider;

       return this;
   }

}
