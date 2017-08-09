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
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ibm.websphere.simplicity.ShrinkHelper;

import componenttest.annotation.Server;
import componenttest.custom.junit.runner.FATRunner;
import componenttest.topology.impl.LibertyServer;
import componenttest.topology.utils.FATServletClient;
import componenttest.topology.utils.HttpUtils;

@RunWith(FATRunner.class)
public class JSFContainerTest extends FATServletClient {

    public static final String APP_NAME = "jsfApp";
    public static final String APP_RES = "test-applications/" + APP_NAME + "/resources";

    @Server("jsf.container.2.2_fat")
    public static LibertyServer server;

    @BeforeClass
    public static void setUp() throws Exception {
        WebArchive app1 = ShrinkWrap.create(WebArchive.class, APP_NAME + ".war")
                        .addPackages(true, "jsf.container")
                        .addAsLibrary(new File("publish/files/mojarra/jsf-api-2.2.14.jar"))
                        .addAsLibrary(new File("publish/files/mojarra/jsf-impl-2.2.14.jar"))
                        .addAsWebResource(new File(APP_RES + "/TestBean.xhtml"));

        // TODO-6677: Eventually this library will be auto-added by the jsfContainer-2.2 feature
        app1 = app1.addAsLibrary(new File("publish/files/mojarra/com.ibm.ws.jsfContainer.2.2.jar"));

        ShrinkHelper.exportAppToServer(server, app1);
        server.startServer();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        server.stopServer();
    }

    @Test
    public void testCDIBean() throws Exception {
        HttpUtils.findStringInReadyUrl(server, '/' + APP_NAME + "/TestBean.jsf",
                                       "CDI Bean value:",
                                       ":CDIBean::PostConstructCalled:");
    }

    @Test
    public void testJSFBean() throws Exception {
        HttpUtils.findStringInReadyUrl(server, '/' + APP_NAME + "/TestBean.jsf",
                                       "JSF Bean value:",
                                       ":JSFBean::PostConstructCalled:");
    }
}
