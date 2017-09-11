/*******************************************************************************
 * Copyright (c) 2016, 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.security.mp.jwt.tai;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.security.auth.Subject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jose4j.lang.JoseException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.websphere.ras.annotation.Sensitive;
import com.ibm.websphere.security.WebTrustAssociationException;
import com.ibm.websphere.security.WebTrustAssociationFailedException;
import com.ibm.websphere.security.jwt.JwtToken;
import com.ibm.ws.ffdc.annotation.FFDCIgnore;
import com.ibm.ws.security.SecurityService;
import com.ibm.ws.security.common.crypto.HashUtils;
import com.ibm.ws.security.common.jwk.utils.JsonUtils;
import com.ibm.ws.security.mp.jwt.MicroProfileJwtConfig;
import com.ibm.ws.security.mp.jwt.TraceConstants;
import com.ibm.ws.security.mp.jwt.error.ErrorHandlerImpl;
import com.ibm.ws.security.mp.jwt.error.MpJwtProcessingException;
import com.ibm.ws.security.mp.jwt.impl.utils.Cache;
import com.ibm.ws.security.mp.jwt.impl.utils.JwtPrincipalMapping;
import com.ibm.ws.security.mp.jwt.impl.utils.MicroProfileJwtTaiRequest;
import com.ibm.ws.webcontainer.security.ReferrerURLCookieHandler;
import com.ibm.wsspi.kernel.service.utils.AtomicServiceReference;
import com.ibm.wsspi.kernel.service.utils.ConcurrentServiceReferenceMap;
import com.ibm.wsspi.security.tai.TAIResult;
import com.ibm.wsspi.security.tai.TrustAssociationInterceptor;
import com.ibm.wsspi.security.token.AttributeNameConstants;

@Component(service = { TrustAssociationInterceptor.class }, immediate = true, configurationPolicy = ConfigurationPolicy.IGNORE, property = { "service.vendor=IBM", "type=microProfileJwtTAI", "id=MPJwtTAI", "TAIName=MPJwtTAI", "invokeBeforeSSO:Boolean=true" })
public class MicroProfileJwtTAI implements TrustAssociationInterceptor {

    private static TraceComponent tc = Tr.register(MicroProfileJwtTAI.class, TraceConstants.TRACE_GROUP, TraceConstants.MESSAGE_BUNDLE);

    public static final String KEY_SERVICE_PID = "service.pid";
    public static final String KEY_PROVIDER_ID = "id";
    public static final String KEY_ID = "id";
    private final static String KEY_MPJWT_CONFIG = "microProfileJwtConfig";
    public static final String KEY_LOCATION_ADMIN = "locationAdmin";
    public static final String KEY_AUTH_CACHE_SERVICE = "authCacheService";
    public static final String KEY_SECURITY_SERVICE = "securityService";
    public static final String KEY_FILTER = "authFilter";
    public static final String KEY_MP_JWT_CONFIG = "microProfileJwtConfig";
    public static final String ATTRIBUTE_TAI_REQUEST = "MPJwtTaiRequest";
    public static final String JTI_CLAIM = "jti";

    static final AtomicServiceReference<SecurityService> securityServiceRef = new AtomicServiceReference<SecurityService>(KEY_SECURITY_SERVICE);
    //static final ConcurrentServiceReferenceMap<String, AuthenticationFilter> authFilterServiceRef = new ConcurrentServiceReferenceMap<String, AuthenticationFilter>(KEY_FILTER);
    static final ConcurrentServiceReferenceMap<String, MicroProfileJwtConfig> mpJwtConfigRef = new ConcurrentServiceReferenceMap<String, MicroProfileJwtConfig>(KEY_MP_JWT_CONFIG);

    TAIJwtUtils taiJwtUtils = new TAIJwtUtils();
    private static Cache tokenCache;

    ReferrerURLCookieHandler referrerURLCookieHandler = null;
    TAIRequestHelper taiRequestHelper = new TAIRequestHelper();

    public MicroProfileJwtTAI() {
        tokenCache = new Cache(50000, 600000L); // TODO: Determine if cache settings should be configurable.
    }

    @Reference(service = SecurityService.class, name = KEY_SECURITY_SERVICE, policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY)
    public void setSecurityService(ServiceReference<SecurityService> reference) {
        securityServiceRef.setReference(reference);
    }

    public void unsetSecurityService(ServiceReference<SecurityService> reference) {
        securityServiceRef.unsetReference(reference);
    }

    @Reference(service = MicroProfileJwtConfig.class, name = KEY_MPJWT_CONFIG, policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.MULTIPLE, policyOption = ReferencePolicyOption.RELUCTANT)
    protected void setMicroProfileJwtConfig(ServiceReference<MicroProfileJwtConfig> ref) {
        String id = (String) ref.getProperty(KEY_ID);
        synchronized (mpJwtConfigRef) {
            mpJwtConfigRef.putReference(id, ref);
        }

        if (tc.isDebugEnabled()) {
            Tr.debug(tc, " setMicroProfileJwtConfig id:" + id + " Number of references is now: " + mpJwtConfigRef.size() + "service = " + mpJwtConfigRef.getService(id));
        }
    }

    protected void updatedMicroProfileJwtConfig(ServiceReference<MicroProfileJwtConfig> ref) {
        String id = (String) ref.getProperty(KEY_ID);
        synchronized (mpJwtConfigRef) {
            mpJwtConfigRef.putReference(id, ref);
        }

        if (tc.isDebugEnabled()) {
            Tr.debug(tc, " updateMicroProfileJwtConfig id:" + id);
        }
    }

    protected void unsetMicroProfileJwtConfig(ServiceReference<MicroProfileJwtConfig> ref) {
        String id = (String) ref.getProperty(KEY_ID);
        synchronized (mpJwtConfigRef) {
            mpJwtConfigRef.removeReference(id, ref);
        }

        if (tc.isDebugEnabled()) {
            Tr.debug(tc, " unsetMicroProfileJwtConfig id:" + id);
        }
    }

    public static MicroProfileJwtConfig getMicroProfileJwtConfig(String key) {
        // TODO: Use read/write locks to serialize access when the mpJwtConfigRef is being updated.
        return mpJwtConfigRef.getService(key);
    }

    public static Iterator<MicroProfileJwtConfig> getServices() {
        return mpJwtConfigRef.getServices();
    }

    @Activate
    protected void activate(ComponentContext cc, Map<String, Object> props) {
        //        synchronized (authFilterServiceRef) {
        //            authFilterServiceRef.activate(cc);
        //        }

        synchronized (mpJwtConfigRef) {
            mpJwtConfigRef.activate(cc);
        }
        securityServiceRef.activate(cc);
    }

    @Modified
    protected void modified(ComponentContext cc, Map<String, Object> props) {
        // Do nothing for now.

    }

    @Deactivate
    protected void deactivate(ComponentContext cc) {
        //        synchronized (authFilterServiceRef) {
        //            authFilterServiceRef.deactivate(cc);
        //        }
        synchronized (mpJwtConfigRef) {
            // 240443 work around small kernel bug.
            // need to remove all references, because if we changed id param, osgi will not remove old one.
            // it will however add everything back in later, so we can remove everything now.
            Iterator<String> keysIt = mpJwtConfigRef.keySet().iterator();
            while (keysIt.hasNext()) {
                String key = keysIt.next();
                ServiceReference<MicroProfileJwtConfig> configref = mpJwtConfigRef.getReference(key);
                mpJwtConfigRef.removeReference(key, configref);
            }
            mpJwtConfigRef.deactivate(cc);
        }
        securityServiceRef.deactivate(cc);
    }

    @Override
    public boolean isTargetInterceptor(HttpServletRequest request) throws WebTrustAssociationException {

        MicroProfileJwtTaiRequest mpJwtTaiRequest = taiRequestHelper.createSocialTaiRequestAndSetRequestAttribute(request);
        return taiRequestHelper.requestShouldBeHandledByTAI(request, mpJwtTaiRequest);

    }

    @Override
    public TAIResult negotiateValidateandEstablishTrust(HttpServletRequest request, HttpServletResponse response) throws WebTrustAssociationFailedException {
        if (tc.isDebugEnabled()) {
            Tr.debug(tc, "negotiateValidateandEstablishTrust");
        }
        TAIResult taiResult = TAIResult.create(HttpServletResponse.SC_FORBIDDEN);

        MicroProfileJwtTaiRequest mpJwtTaiRequest = (MicroProfileJwtTaiRequest) request.getAttribute(ATTRIBUTE_TAI_REQUEST);
        taiResult = getAssociatedConfigAndHandleRequest(request, response, mpJwtTaiRequest, taiResult);

        return taiResult;
    }

    @FFDCIgnore({ MpJwtProcessingException.class })
    TAIResult getAssociatedConfigAndHandleRequest(HttpServletRequest request, HttpServletResponse response, MicroProfileJwtTaiRequest mpJwtTaiRequest, TAIResult defaultTaiResult) throws WebTrustAssociationFailedException {
        MicroProfileJwtConfig clientConfig = null;
        try {
            clientConfig = mpJwtTaiRequest.getTheOnlyConfig();
        } catch (MpJwtProcessingException e) {
            // did not find unique mpJwt config to serve this request
            if (tc.isDebugEnabled()) {
                Tr.debug(tc, "A unique mpJwt config wasn't found for this request. Exception was " + e.getMessage());
            }
            return sendToErrorPage(response, defaultTaiResult);
        }
        return handleRequestBasedOnJwtConfig(request, response, clientConfig, defaultTaiResult);
    }

    TAIResult handleRequestBasedOnJwtConfig(HttpServletRequest request, HttpServletResponse response, MicroProfileJwtConfig config, TAIResult defaultTaiResult) throws WebTrustAssociationFailedException {
        if (config == null) {
            if (tc.isDebugEnabled()) {
                Tr.debug(tc, "Client config for request could not be found. An error must have occurred initializing this request.");
            }
            return sendToErrorPage(response, defaultTaiResult);
        }
        return handleMicroProfileJwt(request, response, config);
    }

    /** {@inheritDoc} */
    @Override
    public int initialize(Properties props) throws WebTrustAssociationFailedException {
        // Auto-generated method stub
        return 0;
    }

    /** {@inheritDoc} */
    @Override
    public String getVersion() {
        // Auto-generated method stub
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public String getType() {
        // Auto-generated method stub
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void cleanup() {
        // Auto-generated method stub

    }

    TAIResult handleMicroProfileJwt(HttpServletRequest request, HttpServletResponse response, MicroProfileJwtConfig mpJwtConfig) throws WebTrustAssociationFailedException {

        String token = taiRequestHelper.getBearerToken(request, mpJwtConfig);
        if (token != null) {
            return this.handleMicroProfileJwtValidation(request, response, mpJwtConfig, token);
        }
        return sendToErrorPage(response, TAIResult.create(HttpServletResponse.SC_UNAUTHORIZED));

    }

    @FFDCIgnore({ MpJwtProcessingException.class })
    TAIResult handleMicroProfileJwtValidation(HttpServletRequest req, HttpServletResponse res, MicroProfileJwtConfig clientConfig, String token) throws WebTrustAssociationFailedException {

        JwtToken jwtToken = null;
        String decodedPayload = null;

        if (token != null) {
            // Create JWT from access token / id token
            try {
                jwtToken = taiJwtUtils.validateMpJwtToken(token, clientConfig.getUniqueId());

            } catch (MpJwtProcessingException e) {
                //Tr.error(tc, "AUTH_CODE_FAILED_TO_CREATE_JWT", new Object[] { clientConfig.getUniqueId(), e.getLocalizedMessage() });
                return sendToErrorPage(res, TAIResult.create(HttpServletResponse.SC_UNAUTHORIZED));
            }
            String payload = JsonUtils.getPayload(token);
            decodedPayload = JsonUtils.decodeFromBase64String(payload);
        }

        TAIResult authnResult = null;

        try {
            authnResult = createResult(res, clientConfig, jwtToken, decodedPayload);
        } catch (Exception e) {
            Tr.error(tc, "AUTH_CODE_ERROR_CREATING_RESULT", new Object[] { clientConfig.getUniqueId(), e.getLocalizedMessage() });
            return sendToErrorPage(res, TAIResult.create(HttpServletResponse.SC_UNAUTHORIZED));
        }
        return authnResult;
    }

    TAIResult createResult(HttpServletResponse res, MicroProfileJwtConfig clientConfig, @Sensitive JwtToken jwtToken, @Sensitive String decodedPayload) throws WebTrustAssociationFailedException, MpJwtProcessingException {

        String username = null;
        JsonWebToken jwtPrincipal = null;
        Hashtable<String, Object> customProperties = new Hashtable<String, Object>();

        if (decodedPayload != null) {
            JwtPrincipalMapping claimToPrincipalMapping = new JwtPrincipalMapping(decodedPayload, clientConfig.getUserNameAttribute(), clientConfig.getGroupNameAttribute(), false);
            username = claimToPrincipalMapping.getMappedUser();
            if (username == null) {
                Tr.error(tc, "USERNAME_NOT_FOUND", new Object[0]);
                return sendToErrorPage(res, TAIResult.create(HttpServletResponse.SC_UNAUTHORIZED));
            }
            String issuer = null;
            try {
                jwtPrincipal = taiJwtUtils.createJwtPrincipal(username, claimToPrincipalMapping.getMappedGroups(), jwtToken); //TODO
                issuer = (String) JsonUtils.claimFromJsonObject(decodedPayload, "iss");
            } catch (JoseException e) {
                // TODO: we need a better message here
                String msg = Tr.formatMessage(tc, "AUTH_CODE_FAILED_TO_CREATE_JWT", new Object[] { clientConfig.getUniqueId(), e.getMessage() });
                Tr.error(tc, msg);
                return sendToErrorPage(res, TAIResult.create(HttpServletResponse.SC_UNAUTHORIZED));
            }

            TAIResult result = populatePropertiesFromMapping(res, clientConfig, claimToPrincipalMapping, customProperties, issuer);
            if (result != null) {
                // Error message (if any) has already been logged
                return result;
            }

        }

        customProperties.put(AttributeNameConstants.WSCREDENTIAL_SECURITYNAME, username);
        customProperties.put("com.ibm.ws.authentication.internal.json.web.token", jwtPrincipal); //TODO use AuthenticationConstants.INTERNAL_JSON_WEB_TOKEN

        Subject subject = createSubjectFromProperties(clientConfig, customProperties, jwtToken, jwtPrincipal);
        TAIResult authnResult = TAIResult.create(HttpServletResponse.SC_OK, username, subject);

        return authnResult;
    }

    TAIResult populatePropertiesFromMapping(HttpServletResponse res, MicroProfileJwtConfig clientConfig, JwtPrincipalMapping claimToPrincipalMapping, Hashtable<String, Object> customProperties, String issuer) throws WebTrustAssociationFailedException {

        String realm = issuer;
        //        if (realm == null) {
        //            // runtime default
        //            realm = defaultRealm(clientConfig);
        //        }
        //        if (realm == null) {
        //            Tr.error(tc, "REALM_NOT_FOUND", new Object[] {});
        //            return sendToErrorPage(res, TAIResult.create(HttpServletResponse.SC_UNAUTHORIZED));
        //        }
        String uniqueUser = claimToPrincipalMapping.getMappedUser();
        ;
        ArrayList<String> groups = claimToPrincipalMapping.getMappedGroups();
        ArrayList<String> groupswithrealm = new ArrayList<String>();
        if (groups != null && !groups.isEmpty()) {
            Iterator<String> it = groups.iterator();
            while (it.hasNext()) {
                String group = new StringBuffer("group:").append(realm).append("/").append(it.next()).toString();
                groupswithrealm.add(group);
            }
        }
        String uniqueID = new StringBuffer("user:").append(realm).append("/").append(uniqueUser).toString();
        customProperties.put(AttributeNameConstants.WSCREDENTIAL_UNIQUEID, uniqueID);
        if (realm != null && !realm.isEmpty()) {
            customProperties.put(AttributeNameConstants.WSCREDENTIAL_REALM, realm);
        }
        if (!groupswithrealm.isEmpty()) {
            customProperties.put(AttributeNameConstants.WSCREDENTIAL_GROUPS, groupswithrealm);
        }
        return null;
    }

    Subject createSubjectFromProperties(MicroProfileJwtConfig clientConfig, Hashtable<String, Object> customProperties, @Sensitive JwtToken jwt, JsonWebToken jwtPrincipal) throws MpJwtProcessingException {

        Subject subject = new Subject();
        //
        //        if (jwt != null) {
        //            subject.getPrivateCredentials().add(jwt);
        //        }
        subject.getPrivateCredentials().add(jwtPrincipal); //?
        subject.getPrivateCredentials().add(customProperties);

        return subject;
    }

    @Sensitive
    private static String getKey(@Sensitive String mpJwt) {

        String key = HashUtils.digest(mpJwt);
        return key;
    }

    TAIResult sendToErrorPage(HttpServletResponse response, TAIResult taiResult) {
        return ErrorHandlerImpl.getInstance().handleErrorResponse(response, taiResult);
    }

}
