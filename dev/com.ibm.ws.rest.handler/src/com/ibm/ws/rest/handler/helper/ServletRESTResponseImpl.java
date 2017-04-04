/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2013, 2016
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.rest.handler.helper;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import javax.servlet.http.HttpServletResponse;

import com.ibm.wsspi.rest.handler.RESTResponse;

/**
 * Implementation of RESTResponse that uses an HttpServletResponse object.
 */
public class ServletRESTResponseImpl implements RESTResponse {

    private final HttpServletResponse response;

    /**
     * Currently, RESTResponseImpl merely wraps a HttpServletResponse.
     * That said, we do not want to expose this object directly as we may
     * replace the underlying mechanism some day to make the handler
     * lighter-weight.
     * 
     * @param response The HttpServletResponse to wrap.
     */
    public ServletRESTResponseImpl(HttpServletResponse response) {
        this.response = response;
    }

    /** {@inheritDoc} */
    @Override
    public Writer getWriter() throws IOException {
        return response.getWriter();
    }

    /** {@inheritDoc} */
    @Override
    public void setResponseHeader(String key, String value) {
        response.setHeader(key, value);
    }

    /** {@inheritDoc} */
    @Override
    public void addResponseHeader(String key, String value) {
        response.addHeader(key, value);
    }

    /** {@inheritDoc} */
    @Override
    public void setStatus(int statusCode) {
        response.setStatus(statusCode);
    }

    /** {@inheritDoc} */
    @Override
    public OutputStream getOutputStream() throws IOException {
        return response.getOutputStream();
    }

    /** {@inheritDoc} */
    @Override
    public void sendError(int statusCode) throws IOException {
        response.sendError(statusCode);
    }

    /** {@inheritDoc} */
    @Override
    public void sendError(int statusCode, String msg) throws IOException {
        response.sendError(statusCode, msg);
    }

    /** {@inheritDoc} */
    @Override
    public void setContentType(String contentType) {
        response.setContentType(contentType);
    }

    /** {@inheritDoc} */
    @Override
    public void setContentLength(int len) {
        response.setContentLength(len);
    }

    /** {@inheritDoc} */
    @Override
    public void setCharacterEncoding(String charset) {
        response.setCharacterEncoding(charset);
    }
}
