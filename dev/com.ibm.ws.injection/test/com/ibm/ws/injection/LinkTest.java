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
package com.ibm.ws.injection;

import org.junit.Assert;
import org.junit.Test;

import com.ibm.ws.injectionengine.osgi.util.Link;

public class LinkTest {

    @Test
    public void testLinkParser() {
        test("origin.jar", "", null, null, "", true);
        test("origin.jar", "name", null, null, "name", true);
        test("origin.jar", "modname/name", null, "modname", "name", true);
        test("origin.jar", "weirdmodprefix/modname/name", null, "weirdmodprefix/modname", "name", true);
        test("origin.jar", "uri.jar#name", "uri.jar", null, "name", true);
        test("origin.jar", "dir/uri.jar#name", "dir/uri.jar", null, "name", true);
        test("origin.jar", "../uri.jar#name", "uri.jar", null, "name", true);
        test("origin.jar", "../dir/uri.jar#name", "dir/uri.jar", null, "name", true);
        test("origin.jar", "../../uri.jar#name", "uri.jar", null, "name", true);
        test("origin.jar", "../../dir/uri.jar#name", "dir/uri.jar", null, "name", true);
        test("origindir/origin.jar", "uri.jar#name", "origindir/uri.jar", null, "name", true);
        test("origindir/origin.jar", "dir/uri.jar#name", "origindir/dir/uri.jar", null, "name", true);
        test("origindir/origin.jar", "../uri.jar#name", "origindir/uri.jar", null, "name", true);
        test("origindir/origin.jar", "../dir/uri.jar#name", "origindir/dir/uri.jar", null, "name", true);
        test("origindir/origin.jar", "../../uri.jar#name", "uri.jar", null, "name", true);
        test("origindir/origin.jar", "../../dir/uri.jar#name", "dir/uri.jar", null, "name", true);

        test("origin.jar", "jndi/linkname", null, null, "jndi/linkname", false);
        test("origin.jar", "weirdmodprefix/link/name", null, null, "weirdmodprefix/link/name", false);
        test(null, "name", null, null, "name", false);
        test(null, "mod#name", "mod", null, "name", false);

    }

    private static boolean equals(String a, String b) {
        return a == null ? b == null : a.equals(b);
    }

    private static void test(String origin, String input, String expectedModuleURI, String expectedModuleName, String expectedName, boolean allowModule) {
        Link link = Link.parse(origin, input, allowModule);
        Assert.assertTrue("A link did not parse correctly: origin=" + origin + ", input=" + input + ", link=" + link,
                          equals(expectedModuleURI, link.moduleURI) && equals(expectedModuleName, link.moduleName) && equals(expectedName, link.name));
    }

}
