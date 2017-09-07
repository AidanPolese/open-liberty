/*
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2017
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.security.mp.jwt.impl;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.websphere.ssl.JSSEHelper;
import com.ibm.ws.ffdc.annotation.FFDCIgnore;
import com.ibm.ws.security.common.config.CommonConfigUtils;
import com.ibm.ws.security.common.jwk.impl.JWKSet;
import com.ibm.ws.security.jwt.config.ConsumerUtils;
import com.ibm.ws.security.jwt.config.JwtConsumerConfig;
import com.ibm.ws.security.mp.jwt.MicroProfileJwtConfig;
import com.ibm.ws.security.mp.jwt.MicroProfileJwtService;
import com.ibm.ws.security.mp.jwt.SslRefInfo;
import com.ibm.ws.security.mp.jwt.TraceConstants;
import com.ibm.ws.security.mp.jwt.error.MpJwtProcessingException;
import com.ibm.wsspi.kernel.service.utils.AtomicServiceReference;
import com.ibm.wsspi.ssl.SSLSupport;

/**
 *
 */
@Component(name = "com.ibm.ws.security.mp.jwt", configurationPid = "com.ibm.ws.security.mp.jwt", configurationPolicy = ConfigurationPolicy.REQUIRE, immediate = true, service = { MicroProfileJwtConfig.class, JwtConsumerConfig.class }, property = { "service.vendor=IBM", "type=microProfileJwtConfig" })
public class MicroProfileJwtConfigImpl implements MicroProfileJwtConfig, JwtConsumerConfig {

    private static TraceComponent tc = Tr.register(MicroProfileJwtConfigImpl.class, TraceConstants.TRACE_GROUP, TraceConstants.MESSAGE_BUNDLE);
    protected final boolean IS_REQUIRED = true;
    protected final boolean IS_NOT_REQUIRED = false;

    protected static final String KEY_UNIQUE_ID = "id";
    protected String uniqueId = null;

    protected SSLContext sslContext = null;
    protected SSLSocketFactory sslSocketFactory = null;
    public static final String KEY_sslRef = "sslRef";
    protected String sslRef;
    protected SslRefInfo sslRefInfo = null;

    public static final String KEY_jwksUri = "jwksUri";
    protected String jwksUri = null;

    static final String KEY_MP_JWT_SERVICE = "microProfileJwtService";
    final AtomicServiceReference<MicroProfileJwtService> mpJwtServiceRef = new AtomicServiceReference<MicroProfileJwtService>(KEY_MP_JWT_SERVICE);

    ConsumerUtils consumerUtils = null; // lazy init
    JWKSet jwkSet = null;

    public static final String KEY_ISSUER = "issuer";
    String issuer = null;

    public static final String KEY_AUDIENCE = "audiences";
    String[] audience = null;

    //    public static final String KEY_SIGNATURE_ALGORITHM = "signatureAlgorithm";
    String signatureAlgorithm = "RS256";

    public static final String CFG_KEY_HOST_NAME_VERIFICATION_ENABLED = "hostNameVerificationEnabled";
    protected boolean hostNameVerificationEnabled = false;

    public static final String KEY_TRUSTED_ALIAS = "keyName";
    private String trustAliasName = null;

    public static final String KEY_userNameAttribute = "userNameAttribute";
    protected String userNameAttribute = null;

    public static final String KEY_groupNameAttribute = "groupNameAttribute";
    protected String groupNameAttribute = null;

    public static final String CFG_KEY_TOKEN_REUSE = "tokenReuse";
    protected boolean tokenReuse = true;

    public static final String CFG_KEY_CLOCK_SKEW = "clockSkew";
    private long clockSkewMilliSeconds;

    @Reference(service = MicroProfileJwtService.class, name = KEY_MP_JWT_SERVICE, cardinality = ReferenceCardinality.MANDATORY)
    protected void setMicroProfileJwtService(ServiceReference<MicroProfileJwtService> ref) {
        this.mpJwtServiceRef.setReference(ref);
    }

    protected void unsetMicroProfileJwtService(ServiceReference<MicroProfileJwtService> ref) {
        this.mpJwtServiceRef.unsetReference(ref);
    }

    @Activate
    protected void activate(ComponentContext cc, Map<String, Object> props) throws MpJwtProcessingException {
        this.mpJwtServiceRef.activate(cc);
        uniqueId = (String) props.get(KEY_UNIQUE_ID);
        initProps(cc, props);
        Tr.info(tc, "MPJWT_CONFIG_PROCESSED", uniqueId);
    }

    @Modified
    protected void modified(ComponentContext cc, Map<String, Object> props) throws MpJwtProcessingException {
        initProps(cc, props);
        Tr.info(tc, "MPJWT_CONFIG_MODIFIED", uniqueId);
    }

    @Deactivate
    protected void deactivate(ComponentContext cc) {
        this.mpJwtServiceRef.deactivate(cc);
        Tr.info(tc, "MPJWT_CONFIG_DEACTIVATED", uniqueId);
    }

    public void initProps(ComponentContext cc, Map<String, Object> props) throws MpJwtProcessingException {

        this.issuer = getConfigAttribute(props, KEY_ISSUER, true); //required?

        this.audience = trim((String[]) props.get(KEY_AUDIENCE));
        this.jwksUri = getConfigAttribute(props, KEY_jwksUri);

        this.userNameAttribute = getConfigAttribute(props, KEY_userNameAttribute);
        this.groupNameAttribute = getConfigAttribute(props, KEY_groupNameAttribute);

        this.clockSkewMilliSeconds = (Long) props.get(CFG_KEY_CLOCK_SKEW);

        this.sslRef = getConfigAttribute(props, KEY_sslRef);
        this.sslRefInfo = null; // lazy init

        //this.authFilterRef = getConfigAttribute(props, KEY_authFilterRef);
        //this.authFilter = null; // lazy init

        this.sslContext = null;
        this.trustAliasName = getConfigAttribute(props, KEY_TRUSTED_ALIAS);
        if (props.containsKey(CFG_KEY_HOST_NAME_VERIFICATION_ENABLED)) {
            this.hostNameVerificationEnabled = (Boolean) props.get(CFG_KEY_HOST_NAME_VERIFICATION_ENABLED);
        }
        if (props.containsKey(CFG_KEY_TOKEN_REUSE)) {
            this.tokenReuse = (Boolean) props.get(CFG_KEY_TOKEN_REUSE);
        }
        jwkSet = null; // the jwkEndpoint may have been changed during dynamic update
        consumerUtils = null; // the parameters in consumerUtils may have been changed during dynamic changing

        debug();
    }

    protected void debug() {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, KEY_ISSUER + ": " + issuer);
            //            Tr.debug(tc, KEY_SIGNATURE_ALGORITHM + ": " + signatureAlgorithm);
            Tr.debug(tc, CFG_KEY_HOST_NAME_VERIFICATION_ENABLED + ": " + hostNameVerificationEnabled);
            Tr.debug(tc, CFG_KEY_TOKEN_REUSE + ": " + tokenReuse);
            Tr.debug(tc, KEY_TRUSTED_ALIAS + ": " + trustAliasName);
            Tr.debug(tc, "jwksUri:" + jwksUri);
            Tr.debug(tc, "userNameAttribute:" + userNameAttribute);
            Tr.debug(tc, "groupNameAttribute:" + groupNameAttribute);
            //Tr.debug(tc, "authFilterRef = " + authFilterRef);
            Tr.debug(tc, "sslRef = " + sslRef);
        }
    }

    protected String getConfigAttribute(Map<String, Object> props, String key) {
        return getConfigAttribute(props, key, IS_NOT_REQUIRED);
    }

    protected String getConfigAttribute(Map<String, Object> props, String key, String defaultValue) {
        return getConfigAttribute(props, key, IS_NOT_REQUIRED, defaultValue);
    }

    protected String getConfigAttribute(Map<String, Object> props, String key, boolean isRequired) {
        return getConfigAttribute(props, key, isRequired, null);
    }

    protected String getConfigAttribute(Map<String, Object> props, String key, boolean isRequired, String defaultValue) {
        String result = trim((String) props.get(key));
        if (key != null && result == null) {
            if (isRequired) {
                Tr.error(tc, "CONFIG_REQUIRED_ATTRIBUTE_NULL", new Object[] { key, uniqueId });
            }
            if (defaultValue != null) {
                result = defaultValue;
            }
        }
        return result;
    }

    public static String[] trim(final String[] originals) {
        return CommonConfigUtils.trim(originals);
    }

    public static String trim(final String original) {
        return CommonConfigUtils.trim(original);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isHostNameVerificationEnabled() {
        return this.hostNameVerificationEnabled;
    }

    /** {@inheritDoc} */
    @Override
    public String getId() {
        return getUniqueId();
    }

    /** {@inheritDoc} */
    @Override
    public String getIssuer() {
        return issuer;
    }

    /** {@inheritDoc} */
    @Override
    public String getSharedKey() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public List<String> getAudiences() {
        if (audience != null) {
            List<String> audiences = new ArrayList<String>();
            for (String aud : audience) {
                audiences.add(aud);
            }
            return audiences;
        } else {
            return null;
        }
    }

    /** {@inheritDoc} */
    @Override
    public String getSignatureAlgorithm() {
        return this.signatureAlgorithm;
    }

    /** {@inheritDoc} */
    @Override
    @FFDCIgnore(MpJwtProcessingException.class)
    public String getTrustStoreRef() {
        if (this.sslRefInfo == null) {
            MicroProfileJwtService service = mpJwtServiceRef.getService();
            if (service == null) {
                if (tc.isDebugEnabled()) {
                    Tr.debug(tc, "mpjwt service is not available");
                }
                return null;
            }
            sslRefInfo = new SslRefInfoImpl(service.getSslSupport(), service.getKeyStoreServiceRef(), sslRef, trustAliasName);
        }
        try {
            return sslRefInfo.getTrustStoreName();
        } catch (MpJwtProcessingException e) {
            // We already logged the error
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public String getTrustedAlias() {
        return trustAliasName;
    }

    /** {@inheritDoc} */
    @Override
    public boolean getJwkEnabled() {
        return jwksUri != null;
    }

    /** {@inheritDoc} */
    @Override
    public String getJwkEndpointUrl() {
        return jwksUri;
    }

    /** {@inheritDoc} */
    @Override
    public ConsumerUtils getConsumerUtils() {
        if (consumerUtils == null) { // lazy init
            MicroProfileJwtService service = mpJwtServiceRef.getService();
            if (service != null) {
                consumerUtils = new ConsumerUtils(service.getKeyStoreServiceRef());
            } else {
                Tr.warning(tc, "SERVICE_NOT_FOUND_JWT_CONSUMER_NOT_AVAILABLE", new Object[] { uniqueId });
            }
        }
        return consumerUtils;
    }

    /** {@inheritDoc} */
    @Override
    public JWKSet getJwkSet() {
        if (jwkSet == null) { // lazy init
            jwkSet = new JWKSet();
        }
        return jwkSet;
    }

    /** {@inheritDoc} */
    @Override
    public String getUniqueId() {
        return uniqueId;
    }

    /** {@inheritDoc} */
    @Override
    public String getSslRef() {
        return this.sslRef;
    }

    //@Override
    public HashMap<String, PublicKey> getPublicKeys() throws MpJwtProcessingException {
        if (this.sslRefInfo == null) {
            MicroProfileJwtService service = mpJwtServiceRef.getService();
            if (service == null) {
                if (tc.isDebugEnabled()) {
                    Tr.debug(tc, "Social login service is not available");
                }
                return null;
            }
            sslRefInfo = new SslRefInfoImpl(service.getSslSupport(), service.getKeyStoreServiceRef(), sslRef, trustAliasName);
        }
        return sslRefInfo.getPublicKeys();
    }

    //@Override
    public SSLContext getSSLContext() throws MpJwtProcessingException {
        if (this.sslContext == null) {
            MicroProfileJwtService service = mpJwtServiceRef.getService();
            if (service == null) {
                if (tc.isDebugEnabled()) {
                    Tr.debug(tc, "Social login service is not available");
                }
                return null;
            }
            SSLSupport sslSupport = service.getSslSupport();
            if (sslSupport == null) {
                if (tc.isDebugEnabled()) {
                    Tr.debug(tc, "SSL support could not be found for microprofile jwt service");
                }
                return null;
            }
            try {
                JSSEHelper jsseHelper = sslSupport.getJSSEHelper();
                if (jsseHelper != null) {
                    sslContext = jsseHelper.getSSLContext(sslRef, null, null, true);
                    if (tc.isDebugEnabled()) {
                        Tr.debug(tc, "sslContext (" + sslRef + ") get: " + sslContext);
                        // Properties sslProps =
                        // jsseHelper.getProperties(sslRef);
                    }
                }
            } catch (Exception e) {
                String msg = Tr.formatMessage(tc, "FAILED_TO_GET_SSL_CONTEXT", new Object[] { uniqueId, e.getLocalizedMessage() });
                throw new MpJwtProcessingException(msg, e);
            }
        }

        return this.sslContext;
    }

    //@Override
    public SSLSocketFactory getSSLSocketFactory() throws MpJwtProcessingException {
        if (this.sslContext == null) {
            MicroProfileJwtService service = mpJwtServiceRef.getService();
            if (service == null) {
                if (tc.isDebugEnabled()) {
                    Tr.debug(tc, "Social login service is not available");
                }
                return null;
            }
            SSLSupport sslSupport = service.getSslSupport();
            if (sslSupport == null) {
                if (tc.isDebugEnabled()) {
                    Tr.debug(tc, "SSL support could not be found for microprofile jwt service");
                }
                return null;
            }
            try {
                sslSocketFactory = sslSupport.getSSLSocketFactory(sslRef);
                JSSEHelper jsseHelper = sslSupport.getJSSEHelper();
                if (tc.isDebugEnabled()) {
                    Tr.debug(tc, "sslSocketFactory (" + sslRef + ") get: " + sslSocketFactory);
                }
            } catch (Exception e) {
                String msg = Tr.formatMessage(tc, "FAILED_TO_GET_SSL_CONTEXT", new Object[] { uniqueId, e.getLocalizedMessage() });
                throw new MpJwtProcessingException(msg, e);
            }
        }

        return this.sslSocketFactory;
    }

    //    /** {@inheritDoc} */
    //    @Override
    //    public String getJwksUri() {
    //        return this.jwksUri;
    //    }

    /** {@inheritDoc} */
    @Override
    public String getUserNameAttribute() {
        return this.userNameAttribute;
    }

    /** {@inheritDoc} */
    @Override
    public String getGroupNameAttribute() {
        return this.groupNameAttribute;
    }

    /**
     * {@inheritDoc}
     *
     * @throws SocialLoginException
     */
    //    @Override
    //    public PublicKey getPublicKey() throws SocialLoginException {
    //        if (this.sslRefInfo == null) {
    //            MicroProfileJwtService service = mpJwtServiceRef.getService();
    //            if (service == null) {
    //                if (tc.isDebugEnabled()) {
    //                    Tr.debug(tc, "Social login service is not available");
    //                }
    //                return null;
    //            }
    //            sslRefInfo = new SslRefInfoImpl(service.getSslSupport(), service.getKeyStoreServiceRef(), sslRef, keyAliasName);
    //        }
    //        return sslRefInfo.getPublicKey();
    //    }

    /**
     * {@inheritDoc}
     *
     * @throws SocialLoginException
     */
    //    @Override
    //    public PrivateKey getPrivateKey() throws SocialLoginException {
    //        if (this.sslRefInfo == null) {
    //            MicroProfileJwtService service = mpJwtServiceRef.getService();
    //            if (service == null) {
    //                if (tc.isDebugEnabled()) {
    //                    Tr.debug(tc, "Social login service is not available");
    //                }
    //                return null;
    //            }
    //            sslRefInfo = new SslRefInfoImpl(service.getSslSupport(), service.getKeyStoreServiceRef(), sslRef, keyAliasName);
    //        }
    //        return sslRefInfo.getPrivateKey();
    //    }

    /** {@inheritDoc} */
    @Override
    public boolean isValidationRequired() {
        // TODO Auto-generated method stub
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public long getClockSkew() {
        return clockSkewMilliSeconds;
    }

    public boolean getTokenReuse() {
        return this.tokenReuse;
    }

    //    /** {@inheritDoc} */
    //    @Override
    //    public SecretKey getSecretKey() throws SocialLoginException {
    //        // TODO Auto-generated method stub
    //        return null;
    //    }

}
