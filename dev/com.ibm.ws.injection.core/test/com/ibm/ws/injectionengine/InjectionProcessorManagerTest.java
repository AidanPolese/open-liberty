/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.injectionengine;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.junit.Assert;
import org.junit.Test;

public class InjectionProcessorManagerTest {
    @Retention(RetentionPolicy.RUNTIME)
    @interface TestPasswordAnnotation {
        String password() default "";
    }

    @Retention(RetentionPolicy.RUNTIME)
    @interface TestPropertiesAnnotation {
        String[] properties() default "";
    }

    @Test
    public void testToString() throws Exception {
        Assert.assertEquals('@' + TestPasswordAnnotation.class.getName() + "(password=)",
                            InjectionProcessorManager.toStringSecure(TestToString.class.getMethod("emptyPassword").getAnnotation(TestPasswordAnnotation.class)));
        Assert.assertEquals('@' + TestPasswordAnnotation.class.getName() + "(password=********)",
                            InjectionProcessorManager.toStringSecure(TestToString.class.getMethod("password").getAnnotation(TestPasswordAnnotation.class)));
        Assert.assertEquals('@' + TestPropertiesAnnotation.class.getName() + "(properties=[])",
                            InjectionProcessorManager.toStringSecure(TestToString.class.getMethod("emptyProperties").getAnnotation(TestPropertiesAnnotation.class)));
        Assert.assertEquals('@' + TestPropertiesAnnotation.class.getName() + "(properties=[a=b, b=c])",
                            InjectionProcessorManager.toStringSecure(TestToString.class.getMethod("properties").getAnnotation(TestPropertiesAnnotation.class)));
    }

    public static class TestToString {
        @TestPasswordAnnotation
        public void emptyPassword() {}

        @TestPasswordAnnotation(password = "abc")
        public void password() {}

        @TestPropertiesAnnotation
        public void emptyProperties() {}

        @TestPropertiesAnnotation(properties = { "a=b", "b=c" })
        public void properties() {}
    }
}
