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