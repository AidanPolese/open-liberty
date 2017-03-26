/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package componenttest.annotation;

import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Test methods with this annotation must specify the list of passwords
 * that will be searched by the LeakedPasswordChecker in the server's output files.
 * To verify a single password,
 * <pre>
 * &#064;CheckForLeakedPasswords("passwordToVerify")
 * &#064;Test public void myTest(....)
 * </pre>
 * To verify an encoded passwords,
 * <pre>
 * &#064;CheckForLeakedPasswords("\\{xor\\ ")
 * &#064;Test public void myTest(....)
 * </pre>
 * To verify more than one password,
 * <pre>
 * &#064;CheckForLeakedPasswords( { "passwordToVerify", "\\{xor\\}" })
 * &#064;Test public void myTest(....)
 * </pre>
 * 
 */
@Target(METHOD)
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface CheckForLeakedPasswords {
    String[] value();
}
