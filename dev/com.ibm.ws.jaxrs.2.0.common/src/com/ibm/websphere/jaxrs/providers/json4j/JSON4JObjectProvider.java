/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.websphere.jaxrs.providers.json4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import com.ibm.ws.jaxrs20.providers.json4j.utils.ProviderUtils;
import com.ibm.ws.jaxrs20.utils.ReflectUtil;

//import com.ibm.json.java.JSONObject;

@SuppressWarnings("rawtypes")
@Provider
@Consumes({ "application/json", "application/javascript" })
@Produces({ "application/json", "application/javascript" })
public class JSON4JObjectProvider implements MessageBodyWriter,
                MessageBodyReader {
    @Override
    public boolean isReadable(Class clazz, Type type,
                              Annotation[] annotations, MediaType mediaType) {
        return ProviderUtils.getJSON4JClass("com.ibm.json.java.JSONObject") == clazz;
    }

    @Override
    public Object readFrom(Class clazz, Type type,
                           Annotation[] annotations, MediaType mediaType,
                           MultivaluedMap headers, InputStream is)
                    throws IOException, WebApplicationException {

        try {

            Method m = ProviderUtils.getMethod("com.ibm.json.java.JSONObject", "parse", new Class<?>[] { Reader.class });

            return ReflectUtil.invoke(m, null, new Object[] { new InputStreamReader(is, ProviderUtils
                            .getCharset(mediaType)) });
        } catch (Throwable e) {

        }

        return null;
    }

    @Override
    public long getSize(Object obj, Class clazz, Type type,
                        Annotation[] annotations, MediaType mediaType) {
        return -1L;
    }

    @Override
    public boolean isWriteable(Class clazz, Type type,
                               Annotation[] annotations, MediaType mediaType) {
        return ProviderUtils.getJSON4JClass("com.ibm.json.java.JSONObject").isAssignableFrom(clazz);
    }

    @Override
    public void writeTo(Object obj, Class clazz, Type type,
                        Annotation[] annotations, MediaType mediaType,
                        MultivaluedMap headers, OutputStream os)
                    throws IOException, WebApplicationException {
        // mediaType =
        // MediaTypeUtils.setDefaultCharsetOnMediaTypeHeader(headers,
        // mediaType);
        OutputStreamWriter writer = new OutputStreamWriter(os,
                        ProviderUtils.getCharset(mediaType));

        try {

            Method m = ProviderUtils.getMethod("com.ibm.json.java.JSONObject", "serialize", new Class<?>[] { Writer.class });

            ReflectUtil.invoke(m, obj, new Object[] { writer });
        } catch (Throwable e) {

        }

        //obj.serialize(writer);
    }
}