// /I/ /W/ /G/ /U/   <-- CMVC Keywords, replace / with %
// %I% %W% %G% %U%
//
// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 COPYRIGHT International Business Machines Corp. 2016
//
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Module  :  JPAMessageInterpolator.java
//
// Change Activity:
//
// Reason    Version   Date     Userid    Change Description
// --------- --------- -------- --------- -----------------------------------------
// RTC202465 WAS90     20160126 njr       create
// --------- --------- -------- --------- -----------------------------------------

package com.ibm.ws.jpa.container.beanvalidation;

import static com.ibm.ws.jpa.management.JPAConstants.JPA_RESOURCE_BUNDLE_NAME;
import static com.ibm.ws.jpa.management.JPAConstants.JPA_TRACE_GROUP;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

import javax.validation.ParameterNameProvider;
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
public class JPAParameterNameProvider implements ParameterNameProvider
{
    private static final TraceComponent tc = Tr.register(JPAParameterNameProvider.class,
                                                         JPA_TRACE_GROUP,
                                                         JPA_RESOURCE_BUNDLE_NAME );

    /**
     * The real ParameterNameProvider instance.
     */
    private ParameterNameProvider ivParameterNameProvider;

    private final ValidatorFactoryLocator ivValidatorFactoryLocator;

    JPAParameterNameProvider(ValidatorFactoryLocator locator) {
       ivValidatorFactoryLocator = locator;
    }

    @Override
    public List<String> getParameterNames(Constructor<?> constructor) {
        if (ivParameterNameProvider == null)
            obtainParameterNameProvider();

        return ivParameterNameProvider.getParameterNames(constructor);
    }

    @Override
    public List<String> getParameterNames(Method method) {
        if (ivParameterNameProvider == null)
            obtainParameterNameProvider();

        return ivParameterNameProvider.getParameterNames(method);
    }

    private void obtainParameterNameProvider() {
        ValidatorFactory validatorFactory = ivValidatorFactoryLocator.getValidatorFactory();
        ivParameterNameProvider = validatorFactory.getParameterNameProvider();

        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            Tr.debug(tc, "Obtained the ParameterNameProvider: " + ivParameterNameProvider);
    }
}
