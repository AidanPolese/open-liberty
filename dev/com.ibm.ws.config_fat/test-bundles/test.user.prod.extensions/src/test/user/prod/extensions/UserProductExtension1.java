/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package test.user.prod.extensions;

/**
 * User product extension service interface.
 */
public interface UserProductExtension1 {

    /**
     * Say Hello.
     * 
     * @param input
     * @return
     */
    public String sayHello(String input);

    /**
     * Retrieves configured attribute 1.
     * 
     * @return A Long value.
     */
    public Long getAttribute1();

    /**
     * Retrieves configured attribute 1.
     * 
     * @return A String value.
     */
    public String getAttribute2();
}
