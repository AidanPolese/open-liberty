/*******************************************************************************
 * Copyright (c) 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.jsonp.fat;

import java.io.InputStream;

import javax.json.spi.JsonProvider;
import javax.json.stream.JsonParser;
import javax.servlet.annotation.WebServlet;

import junit.framework.Assert;

import componenttest.app.FATServlet;

@WebServlet("/CustomJsonProviderServlet")
@SuppressWarnings("serial")
public class CustomLibJSONPServlet extends FATServlet {
    public void testCustomLibJsonProvider() {
        // Verify the custom JSON Provider class is being used.
        JsonProvider dummyProvider = JsonProvider.provider();
        String providerName = dummyProvider.getClass().getName();
        String expectedString1 = "com.ibm.ws.jsonp.lib.provider.JsonProviderImpl";
        Assert.assertTrue("DEBUG: EXPECTED <" + expectedString1 + "> FOUND <" + providerName + ">", expectedString1.equals(providerName));

        // Verify the custom implemented JsonParser class gets loaded and used.
        InputStream dummyInputStream = null;
        JsonParser dummyParser = dummyProvider.createParser(dummyInputStream);
        String parserString = dummyParser.getString();
        String expectedString2 = "Custom JSONP implementation loaded from a shared library";
        Assert.assertTrue("DEBUG: EXPECTED <" + expectedString2 + "> FOUND <" + parserString + ">", expectedString2.equals(parserString));

    }
}