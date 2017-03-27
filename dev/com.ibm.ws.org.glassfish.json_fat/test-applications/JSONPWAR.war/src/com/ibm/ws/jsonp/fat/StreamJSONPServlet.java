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

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;
import javax.json.stream.JsonParser;
import javax.servlet.annotation.WebServlet;

@WebServlet("/StreamJSONPServlet")
@SuppressWarnings("serial")
public class StreamJSONPServlet extends AbstractJSONPServlet {

    public void testJsonStream() {
        String outputDir = System.getenv("X_LOG_DIR") + "/json_stream_test_data.js";
        generateJSON(outputDir);
        JsonParser parser = getJsonParser(outputDir);
        parseJson(parser);
        checkJsonData();
    }

    private void generateJSON(String fileLocation) {
        FileOutputStream os = createFileOutputStream(fileLocation);
        Map<String, Object> props = new HashMap<String, Object>();
        props.put(JsonGenerator.PRETTY_PRINTING, new Object());
        JsonGeneratorFactory factory = Json.createGeneratorFactory(props);
        JsonGenerator generator = factory.createGenerator(os);
        generator.writeStartObject()
                        .write("firstName", "Steve")
                        .write("lastName", "Watson")
                        .write("age", 45)
                        .writeStartArray("phoneNumber")
                        .writeStartObject()
                        .write("type", "office")
                        .write("number", "507-253-1234")
                        .writeEnd()
                        .writeStartObject()
                        .write("type", "cell")
                        .write("number", "507-253-4321")
                        .writeEnd()
                        .writeEnd()
                        .writeEnd();
        generator.close();
    }
}