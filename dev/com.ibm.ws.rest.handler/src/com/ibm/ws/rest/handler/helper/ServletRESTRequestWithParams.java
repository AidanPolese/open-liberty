/*
* IBM Confidential
*
* OCO Source Materials
*
* WLP Copyright IBM Corp. 2017
*
* The source code for this program is not published or otherwise divested
* of its trade secrets, irrespective of what has been deposited with the
* U.S. Copyright Office.
*/
package com.ibm.ws.rest.handler.helper;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.security.Principal;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.wsspi.rest.handler.RESTRequest;

/**
 *
 */
public class ServletRESTRequestWithParams implements RESTRequest {
    private final RESTRequest request;
    private final Map<String, String> params;
    public static final TraceComponent tc = Tr.register(ServletRESTRequestWithParams.class);

    public ServletRESTRequestWithParams(RESTRequest req) {
        this.request = req;
        params = new HashMap<String, String>();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.wsspi.rest.handler.RESTRequest#getInput()
     */
    @Override
    public Reader getInput() throws IOException {
        ServletRESTRequestImpl ret = castRequest();
        if (ret != null)
            return ret.getInput();
        return null;

    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.wsspi.rest.handler.RESTRequest#getInputStream()
     */
    @Override
    public InputStream getInputStream() throws IOException {
        ServletRESTRequestImpl ret = castRequest();
        if (ret != null)
            return ret.getInputStream();
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.wsspi.rest.handler.RESTRequest#getHeader(java.lang.String)
     */
    @Override
    public String getHeader(String key) {
        ServletRESTRequestImpl ret = castRequest();
        if (ret != null)
            return ret.getHeader(key);
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.wsspi.rest.handler.RESTRequest#getMethod()
     */
    @Override
    public String getMethod() {
        ServletRESTRequestImpl ret = castRequest();
        if (ret != null)
            return ret.getMethod();
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.wsspi.rest.handler.RESTRequest#getCompleteURL()
     */
    @Override
    public String getCompleteURL() {
        ServletRESTRequestImpl ret = castRequest();
        if (ret != null)
            return ret.getCompleteURL();
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.wsspi.rest.handler.RESTRequest#getURL()
     */
    @Override
    public String getURL() {
        ServletRESTRequestImpl ret = castRequest();
        if (ret != null)
            return ret.getURL();
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.wsspi.rest.handler.RESTRequest#getURI()
     */
    @Override
    public String getURI() {
        ServletRESTRequestImpl ret = castRequest();
        if (ret != null)
            return ret.getURI();
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.wsspi.rest.handler.RESTRequest#getContextPath()
     */
    @Override
    public String getContextPath() {
        ServletRESTRequestImpl ret = castRequest();
        if (ret != null)
            return ret.getContextPath();
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.wsspi.rest.handler.RESTRequest#getPath()
     */
    @Override
    public String getPath() {
        ServletRESTRequestImpl ret = castRequest();
        if (ret != null)
            return ret.getPath();
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.wsspi.rest.handler.RESTRequest#getQueryString()
     */
    @Override
    public String getQueryString() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.wsspi.rest.handler.RESTRequest#getParameter(java.lang.String)
     */
    @Override
    public String getParameter(String name) {
        ServletRESTRequestImpl ret = castRequest();
        if (ret != null)
            return ret.getParameter(name);
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.wsspi.rest.handler.RESTRequest#getParameterValues(java.lang.String)
     */
    @Override
    public String[] getParameterValues(String name) {
        ServletRESTRequestImpl ret = castRequest();
        if (ret != null)
            return ret.getParameterValues(name);
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.wsspi.rest.handler.RESTRequest#getParameterMap()
     */
    @Override
    public Map<String, String[]> getParameterMap() {
        ServletRESTRequestImpl ret = castRequest();
        if (ret != null)
            return ret.getParameterMap();
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.wsspi.rest.handler.RESTRequest#getUserPrincipal()
     */
    @Override
    public Principal getUserPrincipal() {
        ServletRESTRequestImpl ret = castRequest();
        if (ret != null)
            return ret.getUserPrincipal();
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.wsspi.rest.handler.RESTRequest#isUserInRole(java.lang.String)
     */
    @Override
    public boolean isUserInRole(String role) {
        ServletRESTRequestImpl ret = castRequest();
        if (ret != null)
            return ret.isUserInRole(role);
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.wsspi.rest.handler.RESTRequest#getPathVariable(java.lang.String)
     */
    @Override
    public String getPathVariable(String variable) {
        ServletRESTRequestImpl ret = castRequest();
        if (ret != null)
            return ret.getPathVariable(variable);
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.wsspi.rest.handler.RESTRequest#getLocale()
     */
    @Override
    public Locale getLocale() {
        ServletRESTRequestImpl ret = castRequest();
        if (ret != null)
            return ret.getLocale();
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.wsspi.rest.handler.RESTRequest#getLocales()
     */
    @Override
    public Enumeration<Locale> getLocales() {
        ServletRESTRequestImpl ret = castRequest();
        if (ret != null)
            return ret.getLocales();
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.wsspi.rest.handler.RESTRequest#getRemoteAddr()
     */
    @Override
    public String getRemoteAddr() {
        ServletRESTRequestImpl ret = castRequest();
        if (ret != null)
            return ret.getRemoteAddr();
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.wsspi.rest.handler.RESTRequest#getRemoteHost()
     */
    @Override
    public String getRemoteHost() {
        ServletRESTRequestImpl ret = castRequest();
        if (ret != null)
            return ret.getRemoteHost();
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.wsspi.rest.handler.RESTRequest#getRemotePort()
     */
    @Override
    public int getRemotePort() {
        ServletRESTRequestImpl ret = castRequest();
        if (ret != null)
            return ret.getRemotePort();
        return -1;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.wsspi.rest.handler.RESTRequest#getPart(java.lang.String)
     */
    @Override
    public InputStream getPart(String partName) throws IOException {
        ServletRESTRequestImpl ret = castRequest();
        if (ret != null)
            return ret.getPart(partName);
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.wsspi.rest.handler.RESTRequest#isMultiPartRequest()
     */
    @Override
    public boolean isMultiPartRequest() {
        ServletRESTRequestImpl ret = castRequest();
        if (ret != null)
            return ret.isMultiPartRequest();
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.wsspi.rest.handler.RESTRequest#getContentType()
     */
    @Override
    public String getContentType() {
        ServletRESTRequestImpl ret = castRequest();
        if (ret != null)
            return ret.getContentType();
        return null;
    }

    public ServletRESTRequestImpl castRequest() {
        ServletRESTRequestImpl ret = null;
        if (this.request instanceof ServletRESTRequestImpl) {

            ret = (ServletRESTRequestImpl) this.request;
        } else {
            if (tc.isDebugEnabled()) {
                Tr.debug(tc, "********* Could not cast object of type " + this.request.getClass().getName() + " to type " + this.getClass().getName());
            }
        }
        return ret;
    }

    public void addParam(String key, String value) {
        this.params.put(key, value);
    }

    public String getParam(String key) {
        return this.params.get(key);
    }

    public Map<String, String> getAdditionalParamaMap() {
        return this.params;
    }

}
