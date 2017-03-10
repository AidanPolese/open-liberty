/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2010
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */

package com.ibm.websphere.ras.annotation;

import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * The <code>TraceOptions</code> annotation can be used to declare which trace
 * group a class (or classes in a package) should be asociated with. The
 * annotation can also be used to declare whether or not debug traces should be
 * cut when exceptions are explicitly thrown or caught.
 * <p>
 * For example:<br>
 * 
 * <pre>
 * &#064;TraceOptions(traceGroup = &quot;MyTraceGroup&quot;, traceExceptionThrow = true)
 * public class Foo
 * {}
 * </pre>
 * 
 * will associate the class <code>Foo</code> with the <code>MyTraceGroup</code>
 * trace group and will cause debug traces to be added whenever an exception is
 * explicitly thrown. <br>
 * or:<br>
 * 
 * <pre>
 * &#064;TraceOptions(traceGroups = { &quot;BarGroup&quot;, &quot;FooGroup&quot; }, messageBundle = &quot;com.ibm.bar&quot;)
 * public class Bar
 * {}
 * </pre>
 * 
 * will associate the class <code>Bar</code> with the trace groups
 * <code>BarGroup</code> and <code>FooGroup</code> if the underlying trace
 * runtime supports multiple groups. If not, only the first trace group listed
 * will be used. The message bundle "com.ibm.bar" will be used for messages.
 */
@Retention(RUNTIME)
@Target({ TYPE, PACKAGE })
public @interface TraceOptions {
    String traceGroup() default "";

    String[] traceGroups() default {};

    String messageBundle() default "";

    boolean traceExceptionThrow() default false;

    boolean traceExceptionHandling() default false;
}
