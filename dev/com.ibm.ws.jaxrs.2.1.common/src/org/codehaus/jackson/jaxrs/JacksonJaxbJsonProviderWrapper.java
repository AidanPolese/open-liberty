/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2016
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package org.codehaus.jackson.jaxrs;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.ext.Provider;

import org.codehaus.jackson.map.ObjectMapper;

/**
 * this provider is a wrapper of the codehaus jackson provider
 * to make the customized the fastxml jackson with high priority.
 */
@Provider
@Consumes({ "*/*" })
@Produces({ "*/*" })
public class JacksonJaxbJsonProviderWrapper extends JacksonJaxbJsonProvider {

    public JacksonJaxbJsonProviderWrapper()
    {
        this(null, DEFAULT_ANNOTATIONS);
    }

    public JacksonJaxbJsonProviderWrapper(Annotations[] annotationsToUse)
    {
        this(null, annotationsToUse);
    }

    public JacksonJaxbJsonProviderWrapper(ObjectMapper mapper, Annotations[] annotationsToUse)
    {
        super(mapper, annotationsToUse);
    }

}
