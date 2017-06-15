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

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.stream.JsonParser;
import javax.servlet.annotation.WebServlet;

@WebServlet("/BuildJSONPServlet")
@SuppressWarnings("serial")
public class BuildJSONPServlet extends AbstractJSONPServlet {

    public void testJsonBuild() {
        JsonObject value = buildJsonObject();
        JsonParser parser = getJsonParser(value);
        parseJson(parser);
        checkJsonData();
    }

    private JsonObject buildJsonObject() {
        JsonObject value = Json.createObjectBuilder()
                        .add("firstName", "Steve")
                        .add("lastName", "Watson")
                        .add("age", 45)
                        .add("phoneNumber", Json.createArrayBuilder()
                                        .add(Json.createObjectBuilder().add("type", "office").add("number", "507-253-1234"))
                                        .add(Json.createObjectBuilder().add("type", "cell").add("number", "507-253-4321")))
                        .build();
        return value;
    }
}