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

import java.io.File;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.ibm.websphere.simplicity.ShrinkHelper;
import com.ibm.websphere.simplicity.log.Log;

import componenttest.annotation.Server;
import componenttest.annotation.TestServlet;
import componenttest.custom.junit.runner.FATRunner;
import componenttest.topology.impl.LibertyServer;
import componenttest.topology.utils.FATServletClient;
import jsf.beanval.BeanValTestServlet;

/**
 * Tests to execute on the jsf22beanvalServer that use HtmlUnit.
 */
@RunWith(FATRunner.class)
public class JSF22BeanValidationTests extends FATServletClient {

    private static final String APP_NAME = "BeanValidationTests";

    @Server("jsf.container.2.2_fat.beanval")
    @TestServlet(servlet = BeanValTestServlet.class, path = APP_NAME + "/BeanValTestServlet")
    public static LibertyServer server;

    @BeforeClass
    public static void setup() throws Exception {
        WebArchive app = ShrinkWrap.create(WebArchive.class, APP_NAME + ".war")
                        .addAsLibrary(new File("publish/files/mojarra/jsf-api-2.2.14.jar"))
                        .addAsLibrary(new File("publish/files/mojarra/jsf-impl-2.2.14.jar"))
                        .addPackage("jsf.beanval");
        app = (WebArchive) ShrinkHelper.addDirectory(app, "test-applications/" + APP_NAME + "/resources");

        // TODO-6677: Eventually this library will be auto-added by the jsfContainer-2.2 feature
        app = app.addAsLibrary(new File("publish/files/mojarra/com.ibm.ws.jsfContainer.2.2.jar"));

        ShrinkHelper.exportToServer(server, "dropins", app);
        server.addInstalledAppForValidation(APP_NAME);
        server.startServer();
    }

    @AfterClass
    public static void testCleanup() throws Exception {
        server.stopServer();
    }

    /**
     * Execute the BeanTagBinding validation test.
     * This test has two states. First it executes an evaluation with a size greater than the max
     * That test is expected to fail
     * The second test is one that test something at the max length. This test is expected to pass
     *
     * The rest of the bean validation tests are run in com.ibm.ws.jsf_fat_jsf22.JSF20BeanValidation
     * This test was moved out of the above bucket because of a message difference between bean validation
     * 1.0 and 1.1
     */
    @Test
    public void testValidationBeanTagBinding() throws Exception {
        WebClient webClient = new WebClient();

        HtmlPage page = (HtmlPage) webClient.getPage(getAppURL() + "/BeanValidation.jsf");

        Log.info(getClass(), testName.getMethodName(), "Navigating to: /BeanValidationTests/BeanValidation.jsf");
        Log.info(getClass(), testName.getMethodName(), "Attempting to validate with a string greater than max length");
        HtmlTextInput bindingInputText = (HtmlTextInput) page.getElementById("binding");
        bindingInputText.setValueAttribute("aaa");
        page = doClick(page);

        Assert.assertTrue("Sting greater than max did not cause a validation error: \n\n" + page.asText(),
                          page.getElementById("bindingError").getTextContent().equals("binding: Validation Error: Length is greater than allowable maximum of '2'"));

        Log.info(getClass(), testName.getMethodName(), "Navigating to: /BeanValidationTests/BeanValidation.jsf");
        page = (HtmlPage) webClient.getPage(getAppURL() + "/BeanValidation.jsf");

        Log.info(getClass(), testName.getMethodName(), "Attempting to validate with a string of max length");
        bindingInputText = (HtmlTextInput) page.getElementById("binding");
        bindingInputText.setValueAttribute("aa");
        page = doClick(page);

        Assert.assertTrue("Valid input caused a validation error: \n\n" + page.asText(),
                          page.getElementById("success").getTextContent().equals("SUCCESS"));
    }

    private HtmlPage doClick(HtmlPage page) throws Exception {
        HtmlElement button = (HtmlElement) page.getElementById("Validate");
        return button.click();
    }

    private static String getAppURL() {
        return "http://" + server.getHostname() + ":" + server.getHttpDefaultPort() + '/' + APP_NAME;
    }
}
