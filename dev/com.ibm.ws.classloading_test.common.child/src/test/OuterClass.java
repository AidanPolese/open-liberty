/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package test;

/**
 * This class and its member classes are to be loaded by a
 * separate {@link ClassLoader} in the unit tests.
 */
public class OuterClass {
    /**
     * This is a nested class
     */
    public static class NestedClass {}

    /**
     * This is a class that should fail to initialise.
     */
    public static class NestedClassUnloadable {
        static final int NUMBER = 1 / Integer.parseInt("0");
    }

    /**
     * This class should fail to load because its parent failed to initialise.
     */
    public static class NestedClassUnloadableChild extends NestedClassUnloadable {}

}
