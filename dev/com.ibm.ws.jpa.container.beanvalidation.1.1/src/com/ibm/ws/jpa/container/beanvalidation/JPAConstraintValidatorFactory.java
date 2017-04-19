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
// Module  :  JPAConstraintValidatorFactory.java
//
// Change Activity:
//
// Reason    Version   Date     Userid    Change Description
// --------- --------- -------- --------- -----------------------------------------
// F743-30405 WAS80    20100715 darveaux : Defer actual bean validation work as long as possible
// d727932   WAS85     20120208 xuhaih   : refactor for bean validation access
// RTC200353  WAS90    20160107 nmittles : Adding placeholder methods to support bval 1.1 API updates
// RTC202465  WAS90    20160126 njr      : Implement bean validation 1.1 methods.
// --------- --------- -------- --------- -----------------------------------------

package com.ibm.ws.jpa.container.beanvalidation;

import static com.ibm.ws.jpa.management.JPAConstants.JPA_RESOURCE_BUNDLE_NAME;
import static com.ibm.ws.jpa.management.JPAConstants.JPA_TRACE_GROUP;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorFactory;
import javax.validation.ValidatorFactory;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;

/**
 * The purpose of this class is to delay obtaining obtaining the
 * javax.validation.ValidatorFactory instance for as long as possible.
 * 
 * This is only used for JPA providers that are doing bean validation,
 * and the point is to delay doing real bean validation work until after
 * the classes have been transformed and the MMD has been placed on the thread.
 */
public class JPAConstraintValidatorFactory implements ConstraintValidatorFactory
{
    private static final TraceComponent tc = Tr.register(JPAConstraintValidatorFactory.class,
                                                         JPA_TRACE_GROUP,
                                                         JPA_RESOURCE_BUNDLE_NAME);

    /**
     * The real ConstraintValidatorFactory instance.
     * 
     * A real javax.validation.ValidatorFactory instance has a one-to-one association
     * with a module. A real javax.validation.ValidatorFactory always hands back the
     * same javax.validation.ConstraintValidatorFactory instance. Thus, the
     * javax.validation.ConstraintValidatorFactory instance also has a one-to-one
     * association with a module.
     * 
     * Our wrappers mimic this relationship structure. The JPAValidatorFactory wrapper
     * stands-in for the javax.validation.ValidatorFactory instance, and thus has a
     * one-to-one relationship with a module. The JPAValidatorFactory wrapper always
     * hands back the same instance of this JPAConstraintValidatorFactory, which
     * in turn always uses the same javax.validation.ConstraintValidatorFactory instance.
     * Thus, the javax.validation.ConstraintValidatorFactory instance has a one-to-one
     * association with a module in the wrapper scenario as well.
     */
    private ConstraintValidatorFactory ivConstraintValidatorFactory = null;
    private final ValidatorFactoryLocator ivValidatorFactoryLocator;

    public JPAConstraintValidatorFactory(ValidatorFactoryLocator locator)
    {
        ivValidatorFactoryLocator = locator;
    }

    @Override
    public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key)
    {
        final boolean isTraceOn = TraceComponent.isAnyTracingEnabled();

        if (ivConstraintValidatorFactory == null)
        {
            ValidatorFactory validatorFactory = ivValidatorFactoryLocator.getValidatorFactory();
            ivConstraintValidatorFactory = validatorFactory.getConstraintValidatorFactory();

            if (isTraceOn && tc.isDebugEnabled())
            {
                Tr.debug(tc, "Obtained the ConstraintValidatorFactory: " + ivConstraintValidatorFactory);
            }
        }

        // Each invocation of this method could specify a different input Class,
        // which presumably maps to a different ConstraintValidator instance,
        // so we can't cache the ConstraintValidator instance itself.
        return ivConstraintValidatorFactory.getInstance(key);
    }

    @Override
    public void releaseInstance(ConstraintValidator<?, ?> instance) {
        if (ivConstraintValidatorFactory == null) {
            ValidatorFactory validatorFactory = ivValidatorFactoryLocator.getValidatorFactory();
            ivConstraintValidatorFactory = validatorFactory.getConstraintValidatorFactory();

            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                Tr.debug(tc, "Obtained the ConstraintValidatorFactory: " + ivConstraintValidatorFactory);
        }

        ivConstraintValidatorFactory.releaseInstance(instance);
    }
}
