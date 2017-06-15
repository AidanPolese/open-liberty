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

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.stream.JsonParser;
import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;

@WebServlet("/ReadJSONPServlet")
@SuppressWarnings("serial")
public class ReadJSONPServlet extends AbstractJSONPServlet {

    public void testJsonRead() {
        JsonObject jsonData = readJsonFile("/WEB-INF/json_read_test_data.js");
        JsonParser parser = getJsonParser(jsonData);
        parseJson(parser);
        checkJsonData();
    }

    private JsonObject readJsonFile(String fileLocation) {
        ServletContext context = getServletContext();
        InputStream is = context.getResourceAsStream(fileLocation);
        JsonReader reader = Json.createReader(is);
        JsonObject value = reader.readObject();
        return value;
    }
}