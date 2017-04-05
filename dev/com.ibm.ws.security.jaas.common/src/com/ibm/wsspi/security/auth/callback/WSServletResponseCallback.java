/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.security.auth.callback;

import javax.security.auth.callback.Callback;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * The <code>WSServletResponseCallback</code> allows an HttpServletResponse object to be gathered by
 * <code>CallbackHandler</code> and pass it to the <code>LoginModule</code> stack.
 * </p>
 * 
 * @author IBM Corporation
 * @version 1.0
 * @since 1.0
 * @ibm-spi
 */
public class WSServletResponseCallback implements Callback {

    private HttpServletResponse resp;
    private final String prompt;

    /**
     * <p>
     * Construct a <code>WSServletResponseCallback</code> object with a prompt hint.
     * </p>
     * 
     * @param prompt The prompt hint.
     */
    public WSServletResponseCallback(String prompt) {
        this.prompt = prompt;
    }

    /**
     * <p>
     * Construct a <code>WSServletResponseCallback</code> object with a prompt hint and
     * an HttpServletResponse instance.
     * </p>
     * 
     * @param prompt The prompt hint.
     * @param HttpServletResponse resp
     */
    public WSServletResponseCallback(String prompt, HttpServletResponse resp) {
        this.prompt = prompt;
        this.resp = resp;
    }

    /**
     * <p>
     * Set the HttpServletResponse instance.
     * </p>
     * 
     * @param resp The HttpServletResponse object.
     */
    public void setHttpServletResponse(HttpServletResponse resp) {
        this.resp = resp;
    }

    /**
     * <p>
     * Return the HttpServletResponse. If the HttpServletResponse instance set in
     * Constructor is <code>null</code>, then <code>null</code> is returned.
     * </p>
     * 
     * @return The HttpServletResponse, could be <code>null</code>.
     */
    public HttpServletResponse getHttpServletResponse() {
        return resp;
    }

    /**
     * <p>
     * Return the prompt. If the prompt set in Constructor
     * is <code>null</code>, then <code>null</code> is returned.
     * </p>
     * 
     * @return The prompt, could be <code>null</code>.
     */
    public String getPrompt() {
        return prompt;
    }

    /**
     * <p>
     * Returns the name of the Callback. Typically, it is the name of the class.
     * </p>
     * 
     * @return The name of the Callback.
     */
    @Override
    public String toString() {
        return getClass().getName();
    }

}
