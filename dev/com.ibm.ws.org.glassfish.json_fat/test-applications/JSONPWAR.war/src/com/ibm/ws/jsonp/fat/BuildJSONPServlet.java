/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
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