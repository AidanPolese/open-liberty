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

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.ibm.ws.javaee.dd.common.EnvEntry;
import com.ibm.ws.javaee.dd.web.WebApp;
import com.ibm.ws.javaee.ddmodel.DDParser;

/**
 *
 */
public class WebAppTest extends WebAppTestBase {

    @Test
    public void testWeb30() throws Exception {
        parse(webApp30() + "</web-app>");
    }

    @Test
    public void testEnvEntryValueWhitespace() throws Exception {
        WebApp webApp = parse(webApp30() +
                              "<env-entry>" +
                              "<env-entry-name> envName </env-entry-name>" +
                              "<env-entry-value> envValue </env-entry-value>" +
                              "</env-entry>" +
                              "</web-app>");
        List<EnvEntry> envEntries = webApp.getEnvEntries();
        Assert.assertNotNull(envEntries);
        Assert.assertEquals(envEntries.size(), 1);
        EnvEntry envEntry = envEntries.get(0);
        Assert.assertNotNull(envEntry);
        Assert.assertNotNull(envEntry.getName());
        Assert.assertEquals(envEntry.getName(), "envName");
        Assert.assertNotNull(envEntry.getValue());
        Assert.assertEquals(envEntry.getValue(), " envValue ");
    }

    @Test(expected = DDParser.ParseException.class)
    public void testWeb31WithEE6Parser() throws Exception {
        parse(webApp31() + "</web-app>");
    }

    @Test()
    public void testWeb31WithEE7Parser() throws Exception {
        parseWebApp(webApp31() + "</web-app>", WebApp.VERSION_3_1);
    }

    @Test
    public void testAbsoluteOrderingElement() throws Exception {
        parse(webApp30() +
              "<absolute-ordering>" +
              "<name>Fragment1</name>" +
              "<name>Fragment2</name>" +
              "</absolute-ordering>" +
              "</web-app>");
    }

    @Test(expected = DDParser.ParseException.class)
    public void testAbsoluteOrderingDuplicateElements30() throws Exception {
        parseWebApp(webApp30() +
                    "<absolute-ordering>" +
                    "<name>Fragment1</name>" +
                    "<name>Fragment2</name>" +
                    "</absolute-ordering>" +
                    "<absolute-ordering>" +
                    "</absolute-ordering>" +
                    "</web-app>", WebApp.VERSION_3_1);
    }

    @Test(expected = DDParser.ParseException.class)
    public void testAbsoluteOrderingDuplicateElements31() throws Exception {
        parseWebApp(webApp31() +
                    "<absolute-ordering>" +
                    "<name>Fragment1</name>" +
                    "<name>Fragment2</name>" +
                    "</absolute-ordering>" +
                    "<absolute-ordering>" +
                    "</absolute-ordering>" +
                    "</web-app>", WebApp.VERSION_3_1);
    }

    @Test(expected = DDParser.ParseException.class)
    public void testDenyUncoveredHttpMethods30() throws Exception {
        parseWebApp(webApp30() +
                    "<deny-uncovered-http-methods/>" +
                    "</web-app>", WebApp.VERSION_3_0);
    }

    @Test
    public void testDenyUncoveredHttpMethods31() throws Exception {
        parseWebApp(webApp31() +
                    "<deny-uncovered-http-methods/>" +
                    "</web-app>", WebApp.VERSION_3_1);

    }

    @Test(expected = DDParser.ParseException.class)
    public void testDenyUncoveredHttpMethods31NotEmptyType() throws Exception {
        parseWebApp(webApp31() +
                    "<deny-uncovered-http-methods>junk</deny-uncovered-http-methods> +" +
                    "</web-app>", WebApp.VERSION_3_1);

    }
}
