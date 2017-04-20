/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.beanvalidation.mock;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.validation.Configuration;
import javax.validation.ValidatorFactory;

import org.jmock.Expectations;
import org.jmock.Mockery;

import com.ibm.ws.beanvalidation.config.ValidationConfigurator;
import com.ibm.ws.beanvalidation.service.BeanValidationContext;
import com.ibm.ws.javaee.dd.bval.ValidationConfig;

public class MockValidationConfigurator extends ValidationConfigurator {

    private final Configuration<?> configuration;
    private final ValidatorFactory validatorFactory;

    private boolean released;

    /**
     * Allows us to use the mockery from the test to mock the Configuration and
     * ValidatorFactory to be used for the test.
     */
    public MockValidationConfigurator(BeanValidationContext bvContext,
                                      ValidationConfig config,
                                      Mockery mockery) {
        super(bvContext, config);

        if (mockery != null) {
            this.configuration = mockery.mock(Configuration.class);
            this.validatorFactory = mockery.mock(ValidatorFactory.class);

            mockery.checking(new Expectations() {
                {
                    one(configuration).buildValidatorFactory();
                    will(returnValue(validatorFactory));
                }
            });
        } else {
            configuration = null;
            validatorFactory = null;
        }

    }

    public MockValidationConfigurator(BeanValidationContext bvContext, Mockery mockery) {
        this(bvContext, null, mockery);
    }

    public MockValidationConfigurator(BeanValidationContext bvContext) {
        this(bvContext, null);
    }

    @Override
    public Configuration<?> configure() throws IOException {
        if (configuration != null) {
            return configuration;
        }

        return super.configure();
    }

    @Override
    public void release(ValidatorFactory vf) {
        released = true;
    }

    public void assertReleased() {
        assertTrue("release should have been called on this configurator", released);
    }

    public void assertNotReleased() {
        assertFalse("release shouldn't have been called on this configurator", released);
    }
}
