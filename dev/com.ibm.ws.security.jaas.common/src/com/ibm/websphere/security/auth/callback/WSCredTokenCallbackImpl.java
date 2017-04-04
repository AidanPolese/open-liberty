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
package com.ibm.websphere.security.auth.callback;

import javax.security.auth.callback.Callback;

import com.ibm.websphere.ras.annotation.Sensitive;
import com.ibm.ws.security.jaas.common.callback.AuthenticationHelper;

/**
 * <p>
 * The <code>WSCredTokenCallbackImpl</code> allows credential token to be gathered by
 * <code>CallbackHandler</code> and pass it to the <code>LoginModule</code>.
 * </p>
 * 
 * <p>
 * However, credential token usually is in byte format, it is very error prone and
 * difficult to type it in. It usually pass to the <code>LoginModule</code> programmatically.
 * </p>
 * 
 * @ibm-api
 * @author IBM Corporation
 * @version 1.0
 * @since 1.0
 * @ibm-spi
 */
public class WSCredTokenCallbackImpl implements Callback {

    private byte[] defaultCredToken;
    private byte[] credToken;
    private final String prompt;

    /**
     * <p>
     * Construct a <code>WSCredTokenCallbackImpl</code> object with a prompt hint.
     * </p>
     * 
     * @param prompt The prompt hint.
     */
    public WSCredTokenCallbackImpl(String prompt) {
        this.prompt = prompt;
    }

    /**
     * <p>
     * Construct a <code>WSCredTokenCallbackImpl</code> object with a prompt hint and
     * a default credential token.
     * </p>
     * 
     * @param prompt The prompt hint.
     * @param defaultCredToken
     *            The default credential token.
     */
    public WSCredTokenCallbackImpl(String prompt, @Sensitive byte[] defaultCredToken) {
        this.prompt = prompt;
        this.defaultCredToken = AuthenticationHelper.copyCredToken(defaultCredToken);
    }

    /**
     * <p>
     * Set the credential token.
     * </p>
     * 
     * @param credToken The credential token.
     */
    public void setCredToken(@Sensitive byte[] credToken) {
        this.credToken = AuthenticationHelper.copyCredToken(credToken);
    }

    /**
     * <p>
     * Return the credential token. If the credential token set in
     * <code>WSCredTokenCallbackImpl.setCredToken()</code>
     * is <code>null</code>, the <code>null</code> is returned.
     * </p>
     * 
     * @return The credential token, could be <code>null</code>.
     */
    @Sensitive
    public byte[] getCredToken() {
        return AuthenticationHelper.copyCredToken(credToken);
    }

    /**
     * <p>
     * Return the default credential token. If the credential token set in
     * Constructor is <code>null</code>, then <code>null</code> is returned.
     * </p>
     * 
     * @return The default credential token, could be <code>null</code>.
     */
    @Sensitive
    public byte[] getDefaultCredToken() {
        return AuthenticationHelper.copyCredToken(defaultCredToken);
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
