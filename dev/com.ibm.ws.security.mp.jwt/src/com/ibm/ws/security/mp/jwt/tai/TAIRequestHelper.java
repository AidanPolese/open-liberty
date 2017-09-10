/*******************************************************************************
 * Copyright (c) 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.security.mp.jwt.tai;

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.security.mp.jwt.MicroProfileJwtConfig;
import com.ibm.ws.security.mp.jwt.TraceConstants;
import com.ibm.ws.security.mp.jwt.error.MpJwtProcessingException;
import com.ibm.ws.security.mp.jwt.impl.utils.ClientConstants;
import com.ibm.ws.security.mp.jwt.impl.utils.MicroProfileJwtTaiRequest;

/**
 *
 */
public class TAIRequestHelper {

    private static TraceComponent tc = Tr.register(TAIRequestHelper.class, TraceConstants.TRACE_GROUP, TraceConstants.MESSAGE_BUNDLE);

    public static final String ATTRIBUTE_TAI_REQUEST = "MPJwtTaiRequest";

    private static final String Authorization_Header = "Authorization";
    public final static String REQ_METHOD_POST = "POST";
    public final static String REQ_CONTENT_TYPE_NAME = "Content-Type";
    public final static String REQ_CONTENT_TYPE_APP_FORM_URLENCODED = "application/x-www-form-urlencoded";
    private static final String ACCESS_TOKEN = "access_token";

    //    static public final String KEY_MP_JWT_CONFIG = "microProfileJwtConfig";
    //    static ConcurrentServiceReferenceMap<String, MicroProfileJwtConfig> microProfileJwtConfigRef = new ConcurrentServiceReferenceMap<String, MicroProfileJwtConfig>(KEY_MP_JWT_CONFIG);
    //
    //    public static void setMicroProfileJwtConfigRef(ConcurrentServiceReferenceMap<String, MicroProfileJwtConfig> configRef) {
    //        microProfileJwtConfigRef = configRef;
    //    }
    //
    //    public MicroProfileJwtConfig getConfig2(String key) {
    //        return microProfileJwtConfigRef.getService(key);
    //    }
    //
    //    public Iterator<MicroProfileJwtConfig> MicroProfileJwtCongetConfigs2() {
    //        return microProfileJwtConfigRef.getServices();
    //    }

    /**
     * Creates a new {@link MicroProfileJwtTaiRequest} object and sets the object as an attribute in the request object provided.
     *
     * @param request
     * @return
     */
    public MicroProfileJwtTaiRequest createMicroProfileJwtTaiRequestAndSetRequestAttribute(HttpServletRequest request) {
        MicroProfileJwtTaiRequest mpJwtTaiRequest = new MicroProfileJwtTaiRequest(request);
        request.setAttribute(ATTRIBUTE_TAI_REQUEST, mpJwtTaiRequest);
        return mpJwtTaiRequest;
    }

    /**
     * Returns whether the provided request should be handled by the microprofile jwt TAI, based on the request path and
     * information
     * in the {@link McroProfileJwtTaiRequest} object provided.
     *
     * @param request
     * @param mpJwtTaiRequest
     * @return
     */
    public boolean requestShouldBeHandledByTAI(HttpServletRequest request, MicroProfileJwtTaiRequest mpJwtTaiRequest) {
        // 241526 don't process jmx requests with this interceptor
        if (isJmxConnectorRequest(request)) {
            return false;
        }
        String loginHint = getLoginHint(request);
        mpJwtTaiRequest = setTaiRequestConfigInfo(request, loginHint, mpJwtTaiRequest);
        return mpJwtTaiRequest.hasServices();
    }

    boolean isJmxConnectorRequest(HttpServletRequest request) {
        String ctxPath = request.getContextPath();
        return "/IBMJMXConnectorREST".equals(ctxPath);
    }

    String getLoginHint(HttpServletRequest request) {
        String specifiedServiceId = getLoginHintFromHeaderOrParameter(request);
        if (specifiedServiceId == null || specifiedServiceId.isEmpty()) {
            // The request did not contain a login hint
            specifiedServiceId = null;
        }
        return specifiedServiceId;
    }

    String getLoginHintFromHeaderOrParameter(HttpServletRequest request) {
        String specifiedServiceId = request.getHeader(ClientConstants.LOGIN_HINT);
        if (tc.isDebugEnabled()) {
            Tr.debug(tc, "specifiedService(h) id:" + specifiedServiceId);
        }
        if (specifiedServiceId == null || specifiedServiceId.isEmpty()) {
            specifiedServiceId = request.getParameter(ClientConstants.LOGIN_HINT);
            if (tc.isDebugEnabled()) {
                Tr.debug(tc, "specifiedService(p) id:" + specifiedServiceId);
            }
        }
        return specifiedServiceId;
    }

    public String getBearerToken(HttpServletRequest req, MicroProfileJwtConfig clientConfig) {
        String token = getBearerTokenFromHeader(req);
        if (token == null) {
            token = getBearerTokenFromParameter(req);
        }
        return token;
    }

    String getBearerTokenFromHeader(HttpServletRequest req) {
        String hdrValue = req.getHeader(Authorization_Header);
        if (tc.isDebugEnabled()) {
            Tr.debug(tc, "Authorization header=", hdrValue);
        }
        String bearerAuthzMethod = "Bearer ";
        if (hdrValue != null && hdrValue.startsWith(bearerAuthzMethod)) {
            hdrValue = hdrValue.substring(bearerAuthzMethod.length());
        }
        return hdrValue;
    }

    String getBearerTokenFromParameter(HttpServletRequest req) {
        String reqMethod = req.getMethod();
        if (REQ_METHOD_POST.equalsIgnoreCase(reqMethod)) {
            String contentType = req.getHeader(REQ_CONTENT_TYPE_NAME);
            if (tc.isDebugEnabled()) {
                Tr.debug(tc, "Request content type: " + contentType);
            }
            if (REQ_CONTENT_TYPE_APP_FORM_URLENCODED.equals(contentType)) {
                return req.getParameter(ACCESS_TOKEN);
            }
        }
        return null;
    }

    MicroProfileJwtTaiRequest setTaiRequestConfigInfo(HttpServletRequest request, String specifiedServiceId, MicroProfileJwtTaiRequest mpJwtTaiRequest) {
        if (specifiedServiceId == null) {
            if (tc.isDebugEnabled()) {
                Tr.debug(tc, "Specific config ID not provided, so will set generic config information for MpJwtTaiRequest object");
            }
            return setGenericAndFilteredConfigTaiRequestInfo(request, mpJwtTaiRequest);
        }
        return setSpecificConfigTaiRequestInfo(request, specifiedServiceId, mpJwtTaiRequest);
    }

    MicroProfileJwtTaiRequest setGenericAndFilteredConfigTaiRequestInfo(HttpServletRequest request, MicroProfileJwtTaiRequest mpJwtTaiRequest) {
        if (mpJwtTaiRequest == null) {
            mpJwtTaiRequest = createMicroProfileJwtTaiRequestAndSetRequestAttribute(request);
        }
        Iterator<MicroProfileJwtConfig> services = getConfigServices();
        return setGenericAndFilteredConfigTaiRequestInfoFromConfigServices(request, mpJwtTaiRequest, services);
    }

    MicroProfileJwtTaiRequest setGenericAndFilteredConfigTaiRequestInfoFromConfigServices(HttpServletRequest request, MicroProfileJwtTaiRequest mpJwtTaiRequest, Iterator<MicroProfileJwtConfig> services) {
        if (services == null) {
            return mpJwtTaiRequest;
        }
        if (mpJwtTaiRequest == null) {
            mpJwtTaiRequest = createMicroProfileJwtTaiRequestAndSetRequestAttribute(request);
        }

        while (services.hasNext()) {
            MicroProfileJwtConfig mpJwtConfig = services.next();
            //            AuthenticationFilter authFilter = mpJwtConfig.getAuthFilter();
            //            if (authFilter != null) {
            //                if (authFilter.isAccepted(request)) {
            //                    mpJwtTaiRequest.addFilteredConfig(mpJwtConfig);
            //                }
            //            } else {
            mpJwtTaiRequest.addGenericConfig(mpJwtConfig);
            //            }
        }
        return mpJwtTaiRequest;
    }

    MicroProfileJwtTaiRequest setSpecificConfigTaiRequestInfo(HttpServletRequest request, String configId, MicroProfileJwtTaiRequest mpJwtTaiRequest) {
        if (mpJwtTaiRequest == null) {
            mpJwtTaiRequest = createMicroProfileJwtTaiRequestAndSetRequestAttribute(request);
        }

        MicroProfileJwtConfig config = getConfigAssociatedWithRequestAndId(request, configId);
        if (config == null) {
            mpJwtTaiRequest = handleNoMatchingConfiguration(configId, mpJwtTaiRequest);
        } else {
            mpJwtTaiRequest.setSpecifiedConfig(config);
        }
        return mpJwtTaiRequest;
    }

    MicroProfileJwtTaiRequest handleNoMatchingConfiguration(String configId, MicroProfileJwtTaiRequest mpJwtTaiRequest) {
        String msg = Tr.formatMessage(tc, "MPJWT_NO_SUCH_PROVIDER", new Object[] { configId });
        Tr.error(tc, msg);
        MpJwtProcessingException mpjwtException = new MpJwtProcessingException(msg);
        mpJwtTaiRequest.setTaiException(mpjwtException);
        return mpJwtTaiRequest;
    }

    MicroProfileJwtConfig getConfigAssociatedWithRequestAndId(HttpServletRequest request, String configId) {
        MicroProfileJwtConfig mpJwtConfig = getConfig(configId);
        //        if (!configAuthFilterMatchesRequest(request, mpJwtConfig)) {
        //            // The config with the specified ID isn't configured to service this request
        //            mpJwtConfig = null;
        //        }
        return mpJwtConfig;
    }

    Iterator<MicroProfileJwtConfig> getConfigServices() {
        return MicroProfileJwtTAI.getServices();
    }

    MicroProfileJwtConfig getConfig(String configId) {
        return MicroProfileJwtTAI.getMicroProfileJwtConfig(configId);
    }

    //    boolean configAuthFilterMatchesRequest(HttpServletRequest request, MicroProfileJwtConfig config) {
    //        if (config == null) {
    //            return false;
    //        }
    //        AuthenticationFilter authFilter = config.getAuthFilter();
    //        if (authFilter != null) {
    //            if (!authFilter.isAccepted(request)) {
    //                // Specified configuration is present but its auth filter is not configured to service this request
    //                return false;
    //            }
    //        }
    //        return true;
    //    }

}
