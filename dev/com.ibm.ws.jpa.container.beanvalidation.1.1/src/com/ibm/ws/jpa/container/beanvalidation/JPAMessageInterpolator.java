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
// Module  :  JPAMessageInterpolator.java
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

import java.util.Locale;

import javax.validation.MessageInterpolator;
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
public class JPAMessageInterpolator implements MessageInterpolator
{
    private static final TraceComponent tc = Tr.register(JPAMessageInterpolator.class,
                                                         JPA_TRACE_GROUP,
                                                         JPA_RESOURCE_BUNDLE_NAME);

    /**
     * The real MessageInterpolator instance.
     * 
     * A real javax.validation.ValidatorFactory instance has a one-to-one association
     * with a module. A real javax.validation.ValidatorFactory always hands back the
     * same javax.validation.MessageInterpolator instance. Thus, the
     * javax.validation.MessageInterpolator instance also has a one-to-one
     * association with a module.
     * 
     * Our wrappers mimic this relationship structure. The JPAValidatorFactory wrapper
     * stands-in for the javax.validation.ValidatorFactory instance, and thus has a
     * one-to-one relationship with a module. The JPAValidatorFactory wrapper always
     * hands back the same instance of this JPAMessageInterpolator, which
     * in turn always uses the same MessageInterpolator instance.
     * Thus, the javax.validation.MessageInterpolator instance has a one-to-one
     * association with a module in the wrapper scenario as well.
     */
    private MessageInterpolator ivMessageInterpolator = null;

    private final ValidatorFactoryLocator ivValidatorFactoryLocator;

    JPAMessageInterpolator(ValidatorFactoryLocator locator)
    {
        ivValidatorFactoryLocator = locator;
    }

    private void obtainMessageInterpolator()
    {
        final boolean isTraceOn = TraceComponent.isAnyTracingEnabled();

        ValidatorFactory validatorFactory = ivValidatorFactoryLocator.getValidatorFactory();
        ivMessageInterpolator = validatorFactory.getMessageInterpolator();

        if (isTraceOn && tc.isDebugEnabled())
        {
            Tr.debug(tc, "Obtained the MessageInterpolator: " + ivMessageInterpolator);
        }

    }

    @Override
    public String interpolate(String messageTemplate, MessageInterpolator.Context context)
    {
        if (ivMessageInterpolator == null)
        {
            obtainMessageInterpolator();
        }

        return ivMessageInterpolator.interpolate(messageTemplate, context);
    }

    @Override
    public String interpolate(String messageTemplate, MessageInterpolator.Context context, Locale locale)
    {
        if (ivMessageInterpolator == null)
        {
            obtainMessageInterpolator();
        }

        return ivMessageInterpolator.interpolate(messageTemplate, context, locale);
    }
}
