package componenttest.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for expressing tests should only run when the java level is above the given level.
 * The argument is a double, such as 1.6 or 1.7.
 * Example
 *
 * @MinimumJavaLevel(javaLevel=1.7)
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface MinimumJavaLevel {

    double javaLevel();

    /**
     * Deprecated: The synthetic test is no longer necessary.
     */
    @Deprecated
    boolean runSyntheticTest() default false;

}
