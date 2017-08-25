/*******************************************************************************
 * Copyright (c) 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.jsf.container.fat;

import static org.junit.Assert.assertTrue;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.ibm.websphere.simplicity.ShrinkHelper;

import componenttest.custom.junit.runner.FATRunner;
import componenttest.topology.impl.LibertyServer;
import componenttest.topology.utils.FATServletClient;

/**
 * A collection of tests for the JSF 2.2 Faces Flows feature
 * All of these tests make use of the programmatic/CDI facilities provided by JSF.
 *
 * Flows features tested include:
 * faces-config.xml configuration
 * *-flow.xml configuration
 * switches
 * parameters, nested flows
 * JAR packaging
 * explicit navigation cases
 *
 * @author Bill Lucy
 */
@RunWith(FATRunner.class)
public class CDIFlowsTests extends FATServletClient {

    private static final String APP_NAME = "JSF22CDIFacesFlows";

    public static LibertyServer server = JSF22FlowsTests.server;

    @BeforeClass
    public static void setup() throws Exception {
        WebArchive app = ShrinkWrap.create(WebArchive.class, APP_NAME + ".war")
//                      .addAsLibrary(new File("publish/files/mojarra/jsf-api-2.2.14.jar"))
//                      .addAsLibrary(new File("publish/files/mojarra/jsf-impl-2.2.14.jar"))
                        .addPackage("jsf.cdi.flow.beans");
        app = (WebArchive) ShrinkHelper.addDirectory(app, "test-applications/" + APP_NAME + "/resources");

        // TODO-6677: Eventually this library will be auto-added by the jsfContainer-2.2 feature
//      app = app.addAsLibrary(new File("publish/files/mojarra/com.ibm.ws.jsfContainer.2.2.jar"));

        ShrinkHelper.exportToServer(server, "dropins", app);

        server.startServer();
    }

    @AfterClass
    public static void testCleanup() throws Exception {
        server.stopServer();
    }

    /**
     * Verify the behavior of a simple flow which is defined via a *-flow.xml configuration,
     * and which utilizes a simple flowScoped bean
     */
    @Test
    public void JSF22Flows_TestSimpleBean() throws Exception {
        JSF22FlowsTests.testSimpleCase("simpleBean", APP_NAME);
    }

    /**
     * Verify the behavior of a simple flow which is defined entirely programmatically
     */
    @Test
    public void JSF22Flows_TestFlowBuilder() throws Exception {
        JSF22FlowsTests.testSimpleCase("simpleFlowBuilder", APP_NAME);
    }

    /**
     * Verify the behavior of nested flow set in which one flow is defined declaratively and another is
     * defined programmatically
     */
    @Test
    public void JSF22Flows_TestMixedConfiguration() throws Exception {
        JSF22FlowsTests.testNestedFlows("mixedNested1", "mixedNested2", "mixedNested", APP_NAME);
    }

    /**
     * Verify that we can define and use a custom navigation handler
     */
    //@Test
    public void JSF22Flows_TestCustomNavigationHandler() throws Exception {
        // Still running into an NPE here; we need to check for that in NavigationHandlerImpl
        JSF22FlowsTests.testSimpleCase("customNavigationHandler", "/JSF22FacesFlowsNavigation/");
    }

    /**
     * Verify the FlowBuilder initializer() and finalizer()
     */
    @Test
    public void JSF22Flows_TestInitializerAndFinalizer() throws Exception {
        /*
         * 1. Navigate to first page
         * 2. verify that <Current testBean.testValue value: test String> is on the page
         * 3. enter something into textbox, navigate to page 2, verify page2 info is the same
         * 4. navigate to return page
         * 5. verify that <Count: 1> is on the page
         * 6. return home
         */
        testInitializerAndFinalizer();
    }

    /**
     * Verify the FlowBuilder initializer() and finalizer()
     */
    @Test
    public void JSF22Flows_TestProgrammaticSwitch() throws Exception {
        JSF22FlowsTests.testFlowSwitch("programmaticSwitch", APP_NAME);
    }

    /**
     * Helper method to test the initializer and finalizer application
     */
    private void testInitializerAndFinalizer() throws Exception {
        // Navigate to the index
        WebClient webClient = JSF22FlowsTests.getWebClient();
        HtmlPage page = JSF22FlowsTests.getIndex(webClient, APP_NAME);
        String flowID = "initializeFinalize";

        /*
         * Navigate to the first page and make sure the initialize() method set testBean correctly
         */
        page = JSF22FlowsTests.findAndClickButton(page, flowID);
        assertTrue("The page doesn't contain the right text: " + page.asText(),
                   page.asText().contains("initialize() and finalize() flow page 1"));
        JSF22FlowsTests.assertInFlow(page, flowID);
        assertTrue("The page doesn't contain the right text: " + page.asText(),
                   page.asText().contains("Current flowscope value: no flowscope value"));
        assertTrue("The page doesn't contain the right text: " + page.asText(),
                   page.asText().contains("Current testBeanInitFinalize.testValue value: test string"));

        // Assign flowscope value
        HtmlInput inputField = (HtmlInput) page.getElementById("inputValue");
        inputField.setValueAttribute("test string");

        // Click submit: we should navigate to page 2, and the bean values should persist
        page = JSF22FlowsTests.findAndClickButton(page, "button1");
        assertTrue("The page doesn't contain the right text: " + page.asText(),
                   page.asText().contains("initialize() and finalize() flow page 2"));
        JSF22FlowsTests.assertInFlow(page, flowID);
        assertTrue("The page doesn't contain the right text: " + page.asText(),
                   page.asText().contains("Current flowscope value: test string"));
        assertTrue("The page doesn't contain the right text: " + page.asText(),
                   page.asText().contains("Current testBeanInitFinalize.testValue value: test string"));

        // Exit flow to the return page;
        // Make sure that the finalize() method was called correctly (the printed count text will update)
        page = JSF22FlowsTests.findAndClickButton(page, "button2");
        JSF22FlowsTests.assertNotInFlow(page);
        assertTrue("The page doesn't contain the right text: " + page.asText(),
                   page.asText().contains("Count: 1"));
    }
}
