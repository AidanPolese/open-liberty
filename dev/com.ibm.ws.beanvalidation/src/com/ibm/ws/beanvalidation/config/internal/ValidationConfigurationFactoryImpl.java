/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014, 2016
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.beanvalidation.config.internal;

import javax.validation.Configuration;
import javax.validation.ConstraintValidatorFactory;

import org.osgi.service.component.annotations.Component;

import com.ibm.ws.beanvalidation.config.ValidationConfigurationFactory;
import com.ibm.ws.beanvalidation.config.ValidationConfigurationInterface;
import com.ibm.ws.beanvalidation.config.ValidationConfigurator;
import com.ibm.ws.beanvalidation.service.BeanValidationContext;
import com.ibm.ws.javaee.dd.bval.ValidationConfig;

/**
 * Core implementation of the {@link ValidationConfigurationFactory}. The base case
 * is to return the root {@link ValidationConfigurator} object with no additional
 * behavior. This satisfies the case when beanValidation-1.0 is enabled.
 */
@Component(service = ValidationConfigurationFactory.class)
public class ValidationConfigurationFactoryImpl implements ValidationConfigurationFactory {

    @Override
    public ValidationConfigurationInterface createValidationConfiguration(BeanValidationContext context,
                                                                          ValidationConfig config) {

        return new ValidationConfigurator(context, config);
    }

    @Override
    public ConstraintValidatorFactory getConstraintValidatorFactoryOverride(Configuration<?> config) {
        return null;
    }

}
