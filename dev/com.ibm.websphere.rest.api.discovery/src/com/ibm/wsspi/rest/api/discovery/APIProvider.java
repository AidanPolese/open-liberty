/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.rest.api.discovery;

/**
 * <p>This interface exposes documentation for a RESTful endpoint.</p>
 * 
 * @ibm-spi
 */
public interface APIProvider {

    public enum DocType {
        Swagger_20_JSON, Swagger_20_YAML, RAML_YAML, API_BLUEPRINT_JSON
    };

    /**
     * This method exposes the documentation for a certain RESTful endpoint. The returned String can be a serialized
     * document, usually JSON or YAML, or a file reference (starting with file:///) or a URL reference (starting with http:// or https://).
     * 
     * @param docType an item from {@link DocType}
     * @return a String representing either the serialized document or a reference to the document, or null if the DocType is not supported.
     */
    public String getDocument(DocType docType);

}
