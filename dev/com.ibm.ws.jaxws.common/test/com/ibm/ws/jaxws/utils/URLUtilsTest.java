/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jaxws.utils;

import junit.framework.Assert;

import org.junit.Test;

/**
 *
 */
public class URLUtilsTest {

    @Test
    public void testIsAbsolutePath() throws Exception {

        String[] absoluteURIs = { "file:/WEB-INF/lib/a.jar", "file:\\WEB-INF\\lib\\b.jar", "jar://c:/test.jar", "http://www.ibm.com/abc.wsdl" };
        for (String absoluteURI : absoluteURIs) {
            Assert.assertTrue("False is returned for a absolute URI from URLUtils.isAbsolutePath(absoluteURI)", URLUtils.isAbsolutePath(absoluteURI));
        }

        String[] relativeURIs = { "WEB-INF/lib/a.jar", "\\WEB-INF\\lib\\b.jar", "WEB-INF\\lib\\a.jar", "/WEB-INF/lib/a.jar" };
        for (String relativeURI : relativeURIs) {
            Assert.assertFalse("True is returned for a absolute URI from URLUtils.isAbsolutePath(relativeURI)", URLUtils.isAbsolutePath(relativeURI));
        }
    }
}
