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
package com.ibm.ws.javaee.ddmodel.web;

import org.junit.Test;

import com.ibm.ws.javaee.dd.web.WebApp;
import com.ibm.ws.javaee.ddmodel.DDParser;

/**
 *
 */
public class WebFragmentTest extends WebFragmentTestBase {

    @Test
    public void testWebFragment30() throws Exception {
        parseWebFragment(webFragment30() + "</web-fragment>");
    }

    @Test(expected = DDParser.ParseException.class)
    public void testWebFragment31WithEE6Parser() throws Exception {
        parseWebFragment(webFragment31() + "</web-fragment>");
    }

    @Test()
    public void testWebFragment31WithEE7Parser() throws Exception {
        parseWebFragment(webFragment31() + "</web-fragment>", WebApp.VERSION_3_1);
    }

    @Test
    public void testWebFragmentOrderingElement30() throws Exception {
        parseWebFragment(webFragment30() +
                         "<ordering>" +
                         "</ordering>" +
                         "</web-fragment>");
    }

    @Test(expected = DDParser.ParseException.class)
    public void testWebFragmentOrderingDuplicates30() throws Exception {
        parseWebFragment(webFragment30() +
                         "<ordering>" +
                         "</ordering>" +
                         "<ordering>" +
                         "</ordering>" +
                         "</web-fragment>", WebApp.VERSION_3_1);
    }

    @Test(expected = DDParser.ParseException.class)
    public void testWebFragmentOrderingDuplicates31() throws Exception {
        parseWebFragment(webFragment31() +
                         "<ordering>" +
                         "</ordering>" +
                         "<ordering>" +
                         "</ordering>" +
                         "</web-fragment>", WebApp.VERSION_3_1);
    }
}
