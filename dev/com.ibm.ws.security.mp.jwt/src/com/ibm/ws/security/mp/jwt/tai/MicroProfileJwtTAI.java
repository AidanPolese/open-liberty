/*
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2016, 2017
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
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
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.websphere.ras.annotation.Sensitive;
import com.ibm.websphere.security.WebTrustAssociationException;
import com.ibm.websphere.security.WebTrustAssociationFailedException;
import com.ibm.websphere.security.jwt.JwtToken;
import com.ibm.ws.ffdc.annotation.FFDCIgnore;
import com.ibm.ws.security.SecurityService;
import com.ibm.ws.security.authentication.cache.AuthCacheService;
import com.ibm.ws.security.authentication.utility.SubjectHelper;
import com.ibm.ws.security.common.crypto.HashUtils;
import com.ibm.ws.security.common.jwk.utils.JsonUtils;
import com.ibm.ws.security.mp.jwt.MicroProfileJwtConfig;
import com.ibm.ws.security.mp.jwt.TraceConstants;
import com.ibm.ws.security.mp.jwt.error.ErrorHandlerImpl;
import com.ibm.ws.security.mp.jwt.error.MpJwtProcessingException;
import com.ibm.ws.security.mp.jwt.impl.utils.Cache;
import com.ibm.ws.security.mp.jwt.impl.utils.JwtPrincipalMapping;
import com.ibm.ws.security.mp.jwt.impl.utils.MicroProfileJwtTaiRequest;
import com.ibm.ws.webcontainer.security.PostParameterHelper;
import com.ibm.ws.webcontainer.security.ReferrerURLCookieHandler;
import com.ibm.ws.webcontainer.security.UnprotectedResourceService;
import com.ibm.ws.webcontainer.security.WebProviderAuthenticatorHelper;
import com.ibm.ws.webcontainer.srt.SRTServletRequest;
import com.ibm.wsspi.kernel.service.location.WsLocationAdmin;
import com.ibm.wsspi.kernel.service.utils.AtomicServiceReference;
import com.ibm.wsspi.kernel.service.utils.ConcurrentServiceReferenceMap;
import com.ibm.wsspi.security.tai.TAIResult;
import com.ibm.wsspi.security.tai.TrustAssociationInterceptor;
import com.ibm.wsspi.security.token.AttributeNameConstants;

public class MicroProfileJwtTAI implements TrustAssociationInterceptor, UnprotectedResourceService {

    public static final TraceComponent tc = Tr.register(MicroProfileJwtTAI.class, TraceConstants.TRACE_GROUP, TraceConstants.MESSAGE_BUNDLE);

    public static final String KEY_SERVICE_PID = "service.pid";
    public static final String KEY_PROVIDER_ID = "id";
    public static final String KEY_ID = "id";
    public static final String KEY_LOCATION_ADMIN = "locationAdmin";
    public static final String KEY_AUTH_CACHE_SERVICE = "authCacheService";
    public static final String KEY_SECURITY_SERVICE = "securityService";
    public static final String KEY_FILTER = "authFilter";
    public static final String KEY_MP_JWT_CONFIG = "microProfileJwtConfig";
    public static final String ATTRIBUTE_TAI_REQUEST = "MPJwtTaiRequest";
    public static final String JTI_CLAIM = "jti";

    static final AtomicServiceReference<WsLocationAdmin> locationAdminRef = new AtomicServiceReference<WsLocationAdmin>(KEY_LOCATION_ADMIN);
    static final AtomicServiceReference<AuthCacheService> authCacheServiceRef = new AtomicServiceReference<AuthCacheService>(KEY_AUTH_CACHE_SERVICE);
    static final AtomicServiceReference<SecurityService> securityServiceRef = new AtomicServiceReference<SecurityService>(KEY_SECURITY_SERVICE);
    //static final ConcurrentServiceReferenceMap<String, AuthenticationFilter> authFilterServiceRef = new ConcurrentServiceReferenceMap<String, AuthenticationFilter>(KEY_FILTER);
    static final ConcurrentServiceReferenceMap<String, MicroProfileJwtConfig> mpJwtConfigRef = new ConcurrentServiceReferenceMap<String, MicroProfileJwtConfig>(KEY_MP_JWT_CONFIG);

    static SubjectHelper subjectHelper = new SubjectHelper();
    static WebProviderAuthenticatorHelper authHelper;
    static TAIJwtUtils taiJwtUtils = new TAIJwtUtils();
    private static Cache tokenCache;

    ReferrerURLCookieHandler referrerURLCookieHandler = null;
    TAIRequestHelper taiRequestHelper = new TAIRequestHelper();

    public MicroProfileJwtTAI() {
        tokenCache = new Cache(50000, 600000L); // TODO: Determine if cache settings should be configurable.
    }

    public void setSecurityService(ServiceReference<SecurityService> reference) {
        securityServiceRef.setReference(reference);
    }

    public void unsetSecurityService(ServiceReference<SecurityService> reference) {
        securityServiceRef.unsetReference(reference);
    }

    //    protected void setAuthFilter(ServiceReference<AuthenticationFilter> ref) {
    //        String pid = (String) ref.getProperty(KEY_SERVICE_PID);
    //        synchronized (authFilterServiceRef) {
    //            authFilterServiceRef.putReference(pid, ref);
    //        }
    //        if (tc.isDebugEnabled()) {
    //            Tr.debug(tc, " setFilter pid:" + pid);
    //        }
    //    }
    //
    //    protected void updatedAuthFilter(ServiceReference<AuthenticationFilter> ref) {
    //        String pid = (String) ref.getProperty(KEY_SERVICE_PID);
    //        synchronized (authFilterServiceRef) {
    //            authFilterServiceRef.putReference(pid, ref);
    //        }
    //        if (tc.isDebugEnabled()) {
    //            Tr.debug(tc, " setFilter pid:" + pid);
    //        }
    //    }
    //
    //    protected void unsetAuthFilter(ServiceReference<AuthenticationFilter> ref) {
    //        String pid = (String) ref.getProperty(KEY_SERVICE_PID);
    //        synchronized (authFilterServiceRef) {
    //            authFilterServiceRef.removeReference(pid, ref);
    //        }
    //        if (tc.isDebugEnabled()) {
    //            Tr.debug(tc, " unsetFilter pid:" + pid);
    //        }
    //    }

    // Method for unit testing.
    //    static public AuthenticationFilter getAuthFilter(String pid) {
    //        return authFilterServiceRef.getService(pid);
    //    }

    protected void setMicroProfileJwtConfig(ServiceReference<MicroProfileJwtConfig> ref) {
        String id = (String) ref.getProperty(KEY_ID);
        synchronized (mpJwtConfigRef) {
            mpJwtConfigRef.putReference(id, ref);
        }

        //mpJwtConfigRef.get

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

    protected void setLocationAdmin(ServiceReference<WsLocationAdmin> ref) {
        locationAdminRef.setReference(ref);
    }

    protected void unsetLocationAdmin(ServiceReference<WsLocationAdmin> ref) {
        locationAdminRef.unsetReference(ref);
    }

    protected void setAuthCacheService(ServiceReference<AuthCacheService> reference) {
        authCacheServiceRef.setReference(reference);
    }

    protected void unsetAuthCacheService(ServiceReference<AuthCacheService> reference) {
        authCacheServiceRef.unsetReference(reference);
    }

    @Activate
    protected void activate(ComponentContext cc, Map<String, Object> props) {
        //        synchronized (authFilterServiceRef) {
        //            authFilterServiceRef.activate(cc);
        //        }

        synchronized (mpJwtConfigRef) {
            mpJwtConfigRef.activate(cc);
        }
        locationAdminRef.activate(cc);
        // TODO The cache service maybe disabled in
        // /com.ibm.ws.security.authentication.builtin/src/com/ibm/ws/security/authentication/internal/AuthenticationServiceImpl.java
        authCacheServiceRef.activate(cc);
        securityServiceRef.activate(cc);
        authHelper = new WebProviderAuthenticatorHelper(securityServiceRef);

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
        locationAdminRef.deactivate(cc);
        authCacheServiceRef.deactivate(cc);
        securityServiceRef.deactivate(cc);
    }

    @Override
    public boolean isTargetInterceptor(HttpServletRequest request) throws WebTrustAssociationException {

        MicroProfileJwtTaiRequest mpJwtTaiRequest = taiRequestHelper.createSocialTaiRequestAndSetRequestAttribute(request);
        savePostParameters(request);
        return taiRequestHelper.requestShouldBeHandledByTAI(request, mpJwtTaiRequest);

    }

    @Override
    public TAIResult negotiateValidateandEstablishTrust(HttpServletRequest request, HttpServletResponse response) throws WebTrustAssociationFailedException {
        TAIResult taiResult = TAIResult.create(HttpServletResponse.SC_FORBIDDEN);

        MicroProfileJwtTaiRequest mpJwtTaiRequest = (MicroProfileJwtTaiRequest) request.getAttribute(ATTRIBUTE_TAI_REQUEST);
        taiResult = getAssociatedConfigAndHandleRequest(request, response, mpJwtTaiRequest, taiResult);
        restorePostParameters(request);

        return taiResult;
    }

    @FFDCIgnore({ MpJwtProcessingException.class })
    TAIResult getAssociatedConfigAndHandleRequest(HttpServletRequest request, HttpServletResponse response, MicroProfileJwtTaiRequest mpJwtTaiRequest, TAIResult defaultTaiResult) throws WebTrustAssociationFailedException {
        MicroProfileJwtConfig clientConfig = null;
        try {
            clientConfig = mpJwtTaiRequest.getTheOnlyConfig();
        } catch (MpJwtProcessingException e) {
            // Couldn't find a unique mpJwt config to serve this request
            if (tc.isDebugEnabled()) {
                Tr.debug(tc, "A unique mpJwt config wasn't found for this request. Exception was " + e.getMessage());
            }
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

    /** {@inheritDoc} */
    @Override
    public boolean isAuthenticationRequired(HttpServletRequest request) {
        String ctxPath = request.getContextPath();
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "Context path:" + ctxPath);
        }
        // return !(KnownSocialLoginUrl.SOCIAL_LOGIN_CONTEXT_PATH.equals(ctxPath));
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean logout(HttpServletRequest request, HttpServletResponse response, String userName) {
        boolean bSetSubject = false;
        if (tc.isDebugEnabled()) {
            Tr.debug(tc, "logout() userName:" + userName);
        }

        // Search all service and
        // 1) setSubject if subject match
        // 2) remove the cookie and its cached subject
        synchronized (this.mpJwtConfigRef) {
            Iterator<MicroProfileJwtConfig> services = this.mpJwtConfigRef.getServices();
            MicroProfileJwtConfig mpJwtConfig = null;
            while (services.hasNext()) {
                mpJwtConfig = services.next();
                // TODO remove all the cookies of the subject
            }
        }
        return bSetSubject;
    }

    TAIResult handleMicroProfileJwt(HttpServletRequest request, HttpServletResponse response, MicroProfileJwtConfig mpJwtConfig) throws WebTrustAssociationFailedException {

        String token = taiRequestHelper.getBearerToken(request, mpJwtConfig);
        if (token != null) {
            //if tokenReuse is false, then check whether the given token exists in the cache. If it does, then return error
            if (!mpJwtConfig.getTokenReuse()) {
                String payload = JsonUtils.getPayload(token);
                String decodedPayload = JsonUtils.decodeFromBase64String(payload);
                try {
                    String key = (String) JsonUtils.claimFromJsonObject(decodedPayload, JTI_CLAIM);
                    if (tokenCache.get(key) == null) {
                        //cache miss, proceed with token validation
                        return this.handleMicroProfileJwtValidation(request, response, mpJwtConfig, token);
                    }
                } catch (JoseException e) {

                }
            } else {
                //re-use the token
                return this.handleMicroProfileJwtValidation(request, response, mpJwtConfig, token);
            }
        }
        return sendToErrorPage(response, TAIResult.create(HttpServletResponse.SC_UNAUTHORIZED));

    }

    //    @Sensitive
    //    public static String getAndClearCookie(HttpServletRequest request, HttpServletResponse response, String cookieName) {
    //        Cookie[] cookies = request.getCookies();
    //        String value = CookieHelper.getCookieValue(cookies, cookieName);
    //        CookieHelper.clearCookie(request, response, cookieName, cookies);
    //        return value;
    //    }

    @FFDCIgnore({ MpJwtProcessingException.class })
    TAIResult handleMicroProfileJwtValidation(HttpServletRequest req, HttpServletResponse res, MicroProfileJwtConfig clientConfig, String token) throws WebTrustAssociationFailedException {

        JwtToken jwtToken = null;
        String decodedPayload = null;

        if (token != null) {
            // Create JWT from access token / id token
            try {
                jwtToken = taiJwtUtils.validateMpJwtToken(token, clientConfig.getUniqueId());

            } catch (MpJwtProcessingException e) {
                Tr.error(tc, "AUTH_CODE_FAILED_TO_CREATE_JWT", new Object[] { clientConfig.getUniqueId(), e.getLocalizedMessage() });
                return sendToErrorPage(res, TAIResult.create(HttpServletResponse.SC_UNAUTHORIZED));
            } catch (Exception e) {
                Tr.error(tc, "AUTH_CODE_FAILED_TO_CREATE_JWT", new Object[] { clientConfig.getUniqueId(), e.getLocalizedMessage() });
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
                jwtPrincipal = TAIJwtUtils.createJwtPrincipal(username, claimToPrincipalMapping.getMappedGroups(), jwtToken);
                issuer = (String) JsonUtils.claimFromJsonObject(decodedPayload, "iss");
            } catch (JoseException e) {
                // Tr.error(tc, "", new Object[] {});
                return sendToErrorPage(res, TAIResult.create(HttpServletResponse.SC_UNAUTHORIZED));
            }

            TAIResult result = populatePropertiesFromMapping(res, clientConfig, claimToPrincipalMapping, customProperties, issuer);
            if (result != null) {
                // Error message (if any) has already been logged
                return result;
            }

        }

        //create JsonWebToken
        //org.eclipse.microprofile.jwt.JsonWebToken jwt =
        customProperties.put(AttributeNameConstants.WSCREDENTIAL_SECURITYNAME, username);
        customProperties.put("com.ibm.ws.authentication.internal.json.web.token", jwtPrincipal); //TODO use AuthenticationConstants.INTERNAL_JSON_WEB_TOKEN

        Subject subject = createSubjectFromProperties(clientConfig, customProperties, jwtToken, jwtPrincipal);
        TAIResult authnResult = TAIResult.create(HttpServletResponse.SC_OK, username, subject);

        try {
            if (!clientConfig.getTokenReuse() && JsonUtils.claimFromJsonObject(decodedPayload, JTI_CLAIM) != null) {
                String key = (String) JsonUtils.claimFromJsonObject(decodedPayload, JTI_CLAIM);
                tokenCache.put(key, jwtToken);
            }
        } catch (JoseException e) {

        }

        return authnResult;
    }

    TAIResult populatePropertiesFromMapping(HttpServletResponse res, MicroProfileJwtConfig clientConfig, JwtPrincipalMapping claimToPrincipalMapping, Hashtable<String, Object> customProperties, String issuer) throws WebTrustAssociationFailedException {
        //String realm = claimToPrincipalMapping.getMappedRealm();
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

        if (jwt != null) {
            subject.getPrivateCredentials().add(jwt);
        }
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

    void savePostParameters(HttpServletRequest request) {
        PostParameterHelper.savePostParams((SRTServletRequest) request);
    }

    void restorePostParameters(HttpServletRequest request) {
        PostParameterHelper.restorePostParams((SRTServletRequest) request);
    }

}
