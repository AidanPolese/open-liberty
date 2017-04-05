/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011, 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.security.tai;

import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.websphere.security.WebTrustAssociationException;
import com.ibm.websphere.security.WebTrustAssociationFailedException;

/**
 * <p>
 * <i>Trust Association</i> interface (<code>com.ibm.wsspi.security.tai.TrustAssociationInterceptor</code>)
 * is a service provider API that enables the integration of third party security service (for example, a reverse
 * Proxy, etc) with WebSphere Application Server.
 * </p>
 * 
 * <p>
 * The idea is during processing the Web request, WebSphere Application Server calls out and pass the <code>
 * HttpServletRequest</code> and <code>HttpServletResponse</code> to the trust association interceptors. The
 * trust association interceptors are considered to be trusted by WebSphere Application Server. The trust
 * association interceptors can inspect the <code>HttpServletRequest</code> to see if it contains security
 * attributes (authentication or authorization attributes) from the third party security service.
 * </p>
 * 
 * <p>
 * The following is the high level flow:
 * <ol>
 * <li>
 * The <code>isTargetInterceptor</code> method of the trust association interceptor is called with
 * <code>HttpServletRequest</code> as parameter. The trust association interceptor inspects to see
 * it can process the request based on information in the <code>HttpServletRequest</code> agreed upon
 * with the third party security service. The implementation should return <code>true</code> if it is
 * the appropriate trust association interceptor, else <code>false</code> should be returned.
 * </li>
 * <li>
 * If appropriate trust association interceptor is selected, the method <code>negotiateValidateandEstablishTrust</code>
 * is called with <code>HttpServletRequest</code> and <code>HttpServletResponse</code>.
 * <ul>
 * <li>
 * If the interceptor finds that the request does not contains the expected authentication data, it can write
 * the protocol specific challenge information in the <code>HttpServletResponse</code> and return status code
 * that is not equal to <code>HttpServletResponse.SC_OK</code> in the <code>TAIResult</code>. WebSphere Application
 * Server security runtime will stop processing the request and send a status code back to the initiator.
 * </li>
 * <li>
 * If the interceptor finds the agreed upon security information in the <code>HttpServletRequest</code>, then
 * it should validate and establish the trust based on the agreed upon protocol with the third party security services,
 * like for example, validate the signature of the security information, decrypt the security information, etc.
 * Once the trust is validated and established, then the trust association interceptor may create a JAAS Subject (optional)
 * populated with the security information (please see security attribute propagation documentation for details on the
 * format) and it should create a <code>TAIResult</code> with the JAAS Subject, the authenticated principal and the
 * status code as <code>HttpServletResponse.SC_OK</code>. If for some reason the validation fail or trust can not be
 * established, the a <code>WebTrustAssociationFailedException</code> should be thrown.
 * </li>
 * </ul>
 * </li>
 * <li>
 * The WebSphere Application Server security runtime then can use the security information in the JAAS Subject (if JAAS
 * Subject is present) and the authenticated principal in the <code>TAIResult</code> to create WebSphere Application
 * Server security credential and proceeds with its normal processing.
 * </li>
 * </ol>
 * </p>
 * 
 * @author International Business Machines Corp.
 * @version 1.0
 * @see javax.servlet.http.HttpServletRequest
 * @see javax.servlet.http.HttpServletResponse
 * @see javax.security.auth.Subject
 * @see com.ibm.websphere.security.WebTrustAssociationFailedException
 * @ibm-spi
 */
public interface TrustAssociationInterceptor {
    /**
     * <p>
     * Every interceptor should know which HTTP requests originate from the third party server that it is supposed
     * to work with.
     * </p>
     * 
     * <p>
     * Given an HTTP request, this method must be used to determine whether or not this interceptor is designed
     * to process the request, in behalf of the trusted server it is designed to interoperate with.
     * </p>
     * 
     * <p>
     * The determination algorithm depends on the specific implementation. But it should be able to unequivocally
     * give either a positive or negative response. If for any reason the implementation encounters a situation
     * where it is not able to give a definite response (such as, not enough information, indeterminate state, remote
     * exception, etc), then the method should throw a WebTrustAssociationException. The caller is left to decide on
     * what to do if an exception is received.
     * </p>
     * 
     * @param req The HTTP Request object (<code>HttpServletRequest</code>)
     * @return If this is the appropriate interceptor to process the request, <code>true</code> should be returned.
     * @exception WebTrustAssociationException
     *                Should be thrown if any reason the implementation encounters a situation where it is not able to give a definite response (such as,
     *                not enough information, indeterminate state, remote exception, etc).
     * @see HttpServletRequest
     */
    public boolean isTargetInterceptor(HttpServletRequest req) throws WebTrustAssociationException;

    /**
     * <p>
     * This method is used to determine whether trust association can be
     * established between WebSphere Application Server and the third party security service.
     * In most situations, this involves authenticating the server. All the required information
     * to be able to do this should be available in the HTTP request.
     * </p>
     * 
     * <p>
     * If the third party server failed the validation, or is unable to provide the required
     * information, a WebTrustAssociationFailedException must be thrown.
     * </p>
     * 
     * <p>
     * However, if the interceptor finds that the request does not contains the expected
     * authentication data, it can write the protocol specific challenge information in the
     * response and return <code>TAIResult</code> with status code that is not equal to
     * <code>HttpServletResponse.SC_OK</code>. The WebSphere Application Server security runtime
     * will stop processing the request and send a status code back to the initiator. If the validation
     * is successful and trust is established, the a <code>TAIResult</code> is returned with
     * <code>HttpServletResponse.SC_OK</code> code, the authenticated principal and optionally with a
     * JAAS Subject that contains security information from the third party security services (please
     * refer to security attribute propagation documentation for details on the format). The WebSphere
     * Application Server security runtime will proceed to get the authenticated user from
     * <code>TAIResult.getAuthenticationPrincipal</code> and the security information from the JAAS Subject
     * (if present) to create the necessary credentials and proceeds with its normal processing.
     * </p>
     * 
     * @param req Http Servlet Request object
     * @param res Http Servlet Response Object
     * @return TAIResult, contains the outcome of the negotiation, validation and establishing trust.
     * @exception WebTrustAssociationFailedException
     *                If the third party server failed the validation, or is unable to provide the required
     *                information, a WebTrustAssociationFailedException must be thrown.
     * @see javax.security.auth.Subject
     * @see javax.servlet.http.HttpServletRequest
     * @see javax.servlet.http.HttpServletResponse
     * @see com.ibm.wsspi.security.tai.TAIResult
     */
    public TAIResult negotiateValidateandEstablishTrust(HttpServletRequest req, HttpServletResponse res) throws WebTrustAssociationFailedException;

    /**
     * <p>
     * This method is called to initialize the trust association interceptor.
     * This needs to be implemented by all the trust association interceptor implementations when
     * properties are defined in trust association properties.
     * </p>
     * 
     * <p>
     * The return status is now checked before using the trust association interceptor implementation.
     * A return of <b>0</b> is considered SUCCESS and anything else
     * a FAILURE. If your previous implementation of the TAI returns a
     * different error status you can either change your implementation to match
     * the expectations or do one of the following:
     * <ol>
     * <li>
     * Add the property <b>com.ibm.websphere.security.trustassociation.initStatus</b> in the
     * Trust Association Interceptor custom properties and set it
     * to the value that indicates <b>SUCCESS</b> in your implementation. All
     * other values imply failure. In case of failure, the corresponding
     * TAI implementation will not be used.
     * </li>
     * </ol>
     * 
     * <BR><br><b>OR</b><BR><BR>
     * 
     * <ol start="2">
     * <li>
     * Add the property <b>com.ibm.websphere.security.trustassociation.ignoreInitStatus</b>
     * in the Trust Association Interceptor custom properties and set it to
     * <b>true</b> to indicate to WebSphere to ignore the status of this method.
     * In this case WebSphere will not check the return status from this method
     * just like in the earlier versions.
     * </li>
     * </ol>
     * </p>
     * 
     * @param properties Properties are defined in trust association properties.
     * @return int - By default, <B>0</B> indicates success and anything else a failure. However, to be comparable with earlier
     *         versions, the return type can be set to any value to indicate success by setting the
     *         <b>com.ibm.websphere.security.trustassociation.initStatus</b> property to that value <b>or</b>
     *         one can set the property <b>com.ibm.websphere.security.trustassociation.ignoreInitStatus</b> to
     *         inform WebSphere Application Server to ignore the return status.
     * @exception WebTrustAssociationFailedException
     *                Thrown if unrecoverable error is encounter during initialization.
     */
    public int initialize(Properties props) throws WebTrustAssociationFailedException;

    /**
     * <p>
     * Return the version of the trust association implementation.
     * </p>
     * 
     * @return The version of the trust association interceptor.
     */
    public String getVersion();

    /**
     * <p>
     * The trust association interceptor type.
     * </p>
     * 
     * @return The trust association interceptor type
     */
    public String getType();

    /**
     * <p>
     * This is called during stopping the WebSphere Application Server process, this provides an opportunity for
     * trust association interceptor to perform any necessary clean up. If there is no clean up required, then this
     * method can be no-op.
     * </p>
     */
    public void cleanup();
}
