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
package com.ibm.ws.beanvalidation.v11.cdi.releasable;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;

import com.ibm.ws.beanvalidation.service.ValidationReleasableFactory;
import com.ibm.ws.beanvalidation.v11.cdi.internal.ReleasableConstraintValidatorFactory;
import com.ibm.ws.managedobject.ManagedObject;

public class ReleasableConstraintValidatorFactoryTest {

    private final Mockery mockery = new JUnit4Mockery();

    final ValidationReleasableFactory releasableFactory = mockery.mock(ValidationReleasableFactory.class);
    final ManagedObject<?> releasable1 = mockery.mock(ManagedObject.class, "releasable1");
    final ManagedObject<?> releasable2 = mockery.mock(ManagedObject.class, "releasable2");

    @Test
    public void testGetInstance() {
        ReleasableConstraintValidatorFactory factory = new ReleasableConstraintValidatorFactory(releasableFactory);
        final TestConstraintValidator1 validator1 = new TestConstraintValidator1();
        final TestConstraintValidator2 validator2 = new TestConstraintValidator2();
        mockery.checking(new Expectations() {
            {
                oneOf(releasableFactory).createValidationReleasable(TestConstraintValidator1.class);
                will(returnValue(releasable1));

                oneOf(releasableFactory).createValidationReleasable(TestConstraintValidator2.class);
                will(returnValue(releasable2));

                oneOf(releasable1).getObject();
                will(returnValue(validator1));

                oneOf(releasable2).getObject();
                will(returnValue(validator2));
            }
        });

        factory.getInstance(TestConstraintValidator1.class);
        factory.getInstance(TestConstraintValidator2.class);
    }

    @Test
    public void testReleaseInstance() {
        ReleasableConstraintValidatorFactory factory = new ReleasableConstraintValidatorFactory(releasableFactory);
        final TestConstraintValidator1 validator1 = new TestConstraintValidator1();
        final TestConstraintValidator2 validator2 = new TestConstraintValidator2();
        mockery.checking(new Expectations() {
            {
                oneOf(releasableFactory).createValidationReleasable(TestConstraintValidator1.class);
                will(returnValue(releasable1));

                oneOf(releasableFactory).createValidationReleasable(TestConstraintValidator2.class);
                will(returnValue(releasable2));

                oneOf(releasable1).getObject();
                will(returnValue(validator1));

                oneOf(releasable2).getObject();
                will(returnValue(validator2));
            }
        });

        factory.getInstance(TestConstraintValidator1.class);
        factory.getInstance(TestConstraintValidator2.class);

        mockery.checking(new Expectations() {
            {
                oneOf(releasable1).release();
                oneOf(releasable2).release();
            }
        });

        factory.releaseInstance(validator1);
        factory.releaseInstance(validator2);
    }

    @Test
    public void testRelease() {
        ReleasableConstraintValidatorFactory factory = new ReleasableConstraintValidatorFactory(releasableFactory);
        final TestConstraintValidator1 validator1 = new TestConstraintValidator1();
        final TestConstraintValidator2 validator2 = new TestConstraintValidator2();
        mockery.checking(new Expectations() {
            {
                oneOf(releasableFactory).createValidationReleasable(TestConstraintValidator1.class);
                will(returnValue(releasable1));

                oneOf(releasableFactory).createValidationReleasable(TestConstraintValidator2.class);
                will(returnValue(releasable2));

                oneOf(releasable1).getObject();
                will(returnValue(validator1));

                oneOf(releasable2).getObject();
                will(returnValue(validator2));
            }
        });

        factory.getInstance(TestConstraintValidator1.class);
        factory.getInstance(TestConstraintValidator2.class);

        mockery.checking(new Expectations() {
            {
                oneOf(releasable1).release();
                oneOf(releasable2).release();
            }
        });

        factory.release();
    }

    @Test
    public void testReleaseInstanceBeforeGetInstance() {
        ReleasableConstraintValidatorFactory factory = new ReleasableConstraintValidatorFactory(releasableFactory);
        final TestConstraintValidator1 validator1 = new TestConstraintValidator1();
        factory.releaseInstance(validator1);
    }

    @Test
    public void testReleaseBeforeGetInstance() {
        ReleasableConstraintValidatorFactory factory = new ReleasableConstraintValidatorFactory(releasableFactory);
        factory.release();
    }

    @Test
    public void testCreateTwoSameConstraintValidators() throws Exception {
        ReleasableConstraintValidatorFactory factory = new ReleasableConstraintValidatorFactory(releasableFactory);

        // Use two of the same type to ensure that the IdentityHashMap uses references 
        // rather than checking if the constraint validator's are equal based on hashCode/equals 
        // combo.
        final TestConstraintValidator1 validator1 = new TestConstraintValidator1();
        final TestConstraintValidator1 validator2 = new TestConstraintValidator1();
        mockery.checking(new Expectations() {
            {
                oneOf(releasableFactory).createValidationReleasable(TestConstraintValidator1.class);
                will(returnValue(releasable1));

                oneOf(releasableFactory).createValidationReleasable(TestConstraintValidator1.class);
                will(returnValue(releasable2));

                oneOf(releasable1).getObject();
                will(returnValue(validator1));

                oneOf(releasable2).getObject();
                will(returnValue(validator2));
            }
        });

        factory.getInstance(TestConstraintValidator1.class);
        factory.getInstance(TestConstraintValidator1.class);

        mockery.checking(new Expectations() {
            {
                oneOf(releasable1).release();
                oneOf(releasable2).release();
            }
        });

        factory.release();
    }

    private static class TestConstraintValidator1 implements ConstraintValidator<TestAnnotation, Object> {

        @Override
        public void initialize(TestAnnotation arg0) {}

        @Override
        public boolean isValid(Object arg0, ConstraintValidatorContext arg1) {
            return false;
        }

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public boolean equals(Object o) {
            return true;
        }

    }

    private static class TestConstraintValidator2 implements ConstraintValidator<TestAnnotation, Object> {

        @Override
        public void initialize(TestAnnotation arg0) {}

        @Override
        public boolean isValid(Object arg0, ConstraintValidatorContext arg1) {
            return false;
        }

    }

    private static @interface TestAnnotation {

    }
}
