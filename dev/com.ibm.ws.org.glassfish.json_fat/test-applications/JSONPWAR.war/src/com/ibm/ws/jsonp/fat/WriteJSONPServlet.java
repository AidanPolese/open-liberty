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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonWriter;
import javax.json.stream.JsonParser;
import javax.servlet.annotation.WebServlet;

@WebServlet("/WriteJSONPServlet")
@SuppressWarnings("serial")
public class WriteJSONPServlet extends AbstractJSONPServlet {

    public void testJsonWrite() {
        InputStream originalInputStream = getServletContext().getResourceAsStream("/WEB-INF/json_read_test_data.js");
        JsonObject originalJsonData = readJsonFile(originalInputStream);

        String outputDir = System.getenv("X_LOG_DIR") + "/json_write_test_data.js";
        writeJsonFile(outputDir, originalJsonData);
        FileInputStream newInputStream = createFileInputStream(outputDir);
        JsonObject newJsonData = readJsonFile(newInputStream);
        JsonParser parser = getJsonParser(newJsonData);
        parseJson(parser);
        checkJsonData();
    }

    private void writeJsonFile(String fileLocation, JsonObject value) {
        FileOutputStream os = createFileOutputStream(fileLocation);
        JsonWriter writer = Json.createWriter(os);
        writer.writeObject(value);
        writer.close();
    }

    private JsonObject readJsonFile(InputStream is) {
        JsonReader reader = Json.createReader(is);
        JsonObject value = reader.readObject();
        return value;
    }
}