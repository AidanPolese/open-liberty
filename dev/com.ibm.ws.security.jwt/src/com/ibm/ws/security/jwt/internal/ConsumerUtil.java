/*******************************************************************************
 * Copyright (c) 2016 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.security.jwt.internal;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.NumericDate;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.InvalidJwtSignatureException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.jwt.consumer.JwtContext;
import org.jose4j.jwx.JsonWebStructure;
import org.jose4j.keys.HmacKey;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.websphere.security.jwt.Claims;
import com.ibm.websphere.security.jwt.InvalidClaimException;
import com.ibm.websphere.security.jwt.InvalidTokenException;
import com.ibm.websphere.security.jwt.JwtToken;
import com.ibm.websphere.security.jwt.KeyException;
import com.ibm.websphere.security.jwt.KeyStoreServiceException;
import com.ibm.ws.security.common.time.TimeUtils;
import com.ibm.ws.security.common.web.WebUtils;
import com.ibm.ws.security.jwt.config.JwtConsumerConfig;
import com.ibm.ws.security.jwt.utils.Constants;
import com.ibm.ws.security.jwt.utils.JtiNonceCache;
import com.ibm.ws.security.jwt.utils.JwtUtils;
import com.ibm.ws.ssl.KeyStoreService;
import com.ibm.wsspi.kernel.service.utils.AtomicServiceReference;

public class ConsumerUtil {
    private static final TraceComponent tc = Tr.register(ConsumerUtil.class);
    private static final Class<?> thisClass = ConsumerUtil.class;

    private AtomicServiceReference<KeyStoreService> keyStoreService = null;

    private static TimeUtils timeUtils = new TimeUtils(TimeUtils.YearMonthDateHourMinSecZone);
    private final JtiNonceCache jtiCache = new JtiNonceCache();

    public ConsumerUtil(AtomicServiceReference<KeyStoreService> kss) {
        keyStoreService = kss;
    }

    public JwtToken parseJwt(String jwtString, JwtConsumerConfig config) throws Exception {

        JwtContext jwtContext = parseJwtWithoutValidation(config.getId(), jwtString, config.getClockSkew());
        if (config.isValidationRequired()) {
            Key signingKey = getSigningKey(config, jwtContext);
            jwtContext = parseJwtWithValidation(jwtString, jwtContext, config.getId(), signingKey,
                    config.getClockSkew(), config.getIssuer(), config.getAudiences(), config.getSignatureAlgorithm());
        }

        JwtTokenConsumerImpl jwtToken = new JwtTokenConsumerImpl(jwtContext);

        // Check if the token has already been processed before
        if (jtiCache.contains(jwtToken)) {
            if (tc.isDebugEnabled()) {
                Tr.debug(tc, "JWT token can only be submitted once. The issuer is " + jwtToken.getClaims().getIssuer()
                        + ", and JTI is " + jwtToken.getClaims().getJwtId());
            }
            String errorMsg = Tr.formatMessage(tc, "JWT_DUP_JTI_ERR",
                    new Object[] { jwtToken.getClaims().getIssuer(), jwtToken.getClaims().getJwtId() });
            throw new InvalidTokenException(errorMsg);
        }

        return jwtToken;
    }

    /**
     * Get the appropriate signing key based on the signature algorithm
     * specified in the config.
     *
     * @param config
     * @return
     * @throws KeyException
     */
    Key getSigningKey(JwtConsumerConfig config, JwtContext jwtContext) throws KeyException {
        Key signingKey = null;
        if (config == null) {
            if (tc.isDebugEnabled()) {
                Tr.debug(tc, "JWT consumer config object is null");
            }
            return null;
        }

        String sigAlg = config.getSignatureAlgorithm();

        if (Constants.SIGNATURE_ALG_HS256.equals(sigAlg)) {
            try {
                signingKey = getSharedSecretKey(config);
            } catch (Exception e) {
                String msg = Tr.formatMessage(tc, "JWT_ERROR_GETTING_SHARED_KEY",
                        new Object[] { e.getLocalizedMessage() });
                throw new KeyException(msg, e);
            }

        } else if (Constants.SIGNATURE_ALG_RS256.equals(sigAlg)) {
            if (config.getJwkEnabled()) {
                try {
                    signingKey = getJwksKey(config, jwtContext);
                } catch (Exception e) {
                    String msg = Tr.formatMessage(tc, "JWT_ERROR_GETTING_JWK_KEY",
                            new Object[] { config.getJwkEndpointUrl(), e.getLocalizedMessage() });
                    throw new KeyException(msg, e);
                }
            } else { // jwks is not enabled
                String trustedAlias = config.getTrustedAlias();
                String trustStoreRef = config.getTrustStoreRef();
                try {
                    signingKey = getPublicKey(trustedAlias, trustStoreRef, Constants.SIGNATURE_ALG_RS256);
                } catch (Exception e) {
                    String msg = Tr.formatMessage(tc, "JWT_ERROR_GETTING_PUBLIC_KEY",
                            new Object[] { trustedAlias, trustStoreRef, e.getLocalizedMessage() });
                    throw new KeyException(msg, e);
                }
            }
        }
        if (signingKey == null) {
            if (tc.isDebugEnabled()) {
                Tr.debug(tc, "A signing key could not be found");
            }
        }

        return signingKey;
    }

    protected Key getJwksKey(JwtConsumerConfig config, JwtContext jwtContext) throws Exception {
        Key signingKey = null;

        List<JsonWebStructure> jsonStructures = jwtContext.getJoseObjects();
        if (jsonStructures == null || jsonStructures.isEmpty()) {
            throw new InvalidJwtException("Invalid JsonWebStructure");
        }
        JsonWebStructure jsonStruct = jsonStructures.get(0);

        // debug statemenets
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "JsonWebStructure class: " + jsonStruct.getClass().getName() + " data:" + jsonStruct);
            if (jsonStruct instanceof JsonWebSignature) {
                JsonWebSignature signature = (JsonWebSignature) jsonStruct;
                Tr.debug(tc, "JsonWebSignature alg: " + signature.getAlgorithmHeaderValue() + " 3rd:'" + signature.getEncodedSignature() + "'");
            }
        }

        String kid = jsonStruct.getKeyIdHeaderValue();
        String jwkEndpoint = config.getJwkEndpointUrl();
        if (!WebUtils.validateUriFormat(jwkEndpoint)) {
            // TODO - NLS message
            if (tc.isDebugEnabled()) {
                Tr.debug(tc, "The JWK endpoint " + jwkEndpoint + " is not a valid URI");
            }
            return null;
        }
        JwKRetriever jwkRetriever = new JwKRetriever(config);
        signingKey = jwkRetriever.getPublicKeyFromJwk(kid, null); // only kid or x5t will work but not both

        return signingKey;
    }

    /**
     * Creates a Key object from the shared key specified in the provided
     * configuration.
     *
     * @param config
     * @return
     * @throws KeyException
     */
    Key getSharedSecretKey(JwtConsumerConfig config) throws KeyException {
        if (config == null) {
            if (tc.isDebugEnabled()) {
                Tr.debug(tc, "JWT consumer config object is null");
            }
            return null;
        }

        String sharedKey = config.getSharedKey();
        if (sharedKey == null || sharedKey.isEmpty()) {
            String msg = Tr.formatMessage(tc, "JWT_MISSING_SHARED_KEY");
            throw new KeyException(msg);
        }

        try {
            return new HmacKey(sharedKey.getBytes(Constants.UTF_8));
        } catch (UnsupportedEncodingException e) {
            // Should not happen - UTF-8 should be supported
            if (tc.isDebugEnabled()) {
                Tr.debug(tc, "Caught exception getting shared key bytes: " + e.getLocalizedMessage());
            }
        }
        return null;
    }

    /**
     * Creates a Key object from the certificate stored in the trust store and
     * alias provided.
     *
     * @param trustedAlias
     * @param trustStoreRef
     * @param signatureAlgorithm
     * @return
     * @throws KeyStoreServiceException
     * @throws KeyException
     */
    Key getPublicKey(String trustedAlias, String trustStoreRef, String signatureAlgorithm)
            throws KeyStoreServiceException, KeyException {
        Key signingKey = null;

        try {
            if (keyStoreService == null) {
                String msg = Tr.formatMessage(tc, "JWT_TRUSTSTORE_SERVICE_NOT_AVAILABLE");
                throw new KeyStoreServiceException(msg);
            }

            signingKey = JwtUtils.getPublicKey(trustedAlias, trustStoreRef, keyStoreService.getService());

        } catch (Exception e) {
            String msg = Tr.formatMessage(tc, "JWT_NULL_SIGNING_KEY_WITH_ERROR",
                    new Object[] { signatureAlgorithm, Constants.SIGNING_KEY_X509, e.getLocalizedMessage() });
            throw new KeyException(msg, e);
        }

        if (tc.isDebugEnabled()) {
            Tr.debug(tc, "Trusted alias: " + trustedAlias + ", Truststore: " + trustStoreRef);
            Tr.debug(tc, "RSAPublicKey: " + (signingKey instanceof RSAPublicKey));
        }
        if (signingKey != null && !(signingKey instanceof RSAPublicKey)) {
            signingKey = null;
        }
        return signingKey;
    }

    // Just parse without validation
    protected JwtContext parseJwtWithoutValidation(String configId, String jwtString, long clockSkewInMilliseconds)
            throws Exception {
        if (jwtString == null || jwtString.isEmpty()) {
            String errorMsg = Tr.formatMessage(tc, "JWT_CONSUMER_NULL_OR_EMPTY_STRING",
                    new Object[] { configId, jwtString });
            throw new InvalidTokenException(errorMsg);
        }

        JwtConsumerBuilder builder = new JwtConsumerBuilder();
        builder.setSkipAllValidators();
        builder.setDisableRequireSignature();
        builder.setSkipSignatureVerification();
        builder.setAllowedClockSkewInSeconds((int) (clockSkewInMilliseconds / 1000));

        JwtConsumer firstPassJwtConsumer = builder.build();

        JwtContext jwtContext = firstPassJwtConsumer.process(jwtString);

        return jwtContext;

    }

    protected JwtContext parseJwtWithValidation(String jwtString, JwtContext jwtContext, String id, Key key,
            long clockSkewInMilliseconds, String issuers, List<String> allowedAudiences, String signatureAlgorithm)
            throws Exception {

        JwtClaims jwtClaims = jwtContext.getJwtClaims();

        if (tc.isDebugEnabled()) {
            Tr.debug(tc, "Key from config: " + key);
        }

        validateIssuer(id, issuers, jwtClaims.getIssuer());

        if (!validateAudience(allowedAudiences, jwtClaims.getAudience())) {
            String msg = Tr.formatMessage(tc, "JWT_AUDIENCE_NOT_TRUSTED", new Object[] { jwtClaims.getAudience(), id, allowedAudiences });
            throw new InvalidClaimException(msg);
        }

        // check azp

        validateIatAndExp(jwtClaims, clockSkewInMilliseconds);

        validateNbf(jwtClaims, clockSkewInMilliseconds);

        validateAlgorithm(jwtContext, signatureAlgorithm);

        if (key == null && signatureAlgorithm != null && !signatureAlgorithm.equalsIgnoreCase("none")) {
            String msg = Tr.formatMessage(tc, "JWT_MISSING_KEY", new Object[] { signatureAlgorithm });
            throw new InvalidClaimException(msg);
        }

        JwtConsumerBuilder consumerBuilder = new JwtConsumerBuilder();

        consumerBuilder.setExpectedIssuer(jwtClaims.getIssuer());
        consumerBuilder.setSkipDefaultAudienceValidation();
        consumerBuilder.setRequireExpirationTime();
        consumerBuilder.setVerificationKey(key);
        consumerBuilder.setRelaxVerificationKeyValidation();
        consumerBuilder.setAllowedClockSkewInSeconds((int) (clockSkewInMilliseconds / 1000));

        JwtConsumer jwtConsumer = consumerBuilder.build();
        JwtContext validatedJwtContext = null;

        try {
            validatedJwtContext = jwtConsumer.process(jwtString);
        } catch (InvalidJwtSignatureException e) {
            String msg = Tr.formatMessage(tc, "JWT_INVALID_SIGNATURE", new Object[] { e.getLocalizedMessage() });
            throw new InvalidTokenException(msg, e);

        } catch (InvalidJwtException e) {

            Throwable cause = getRootCause(e);
            // java.security.InvalidKeyException: No installed provider supports
            // this key: (null)
            if (cause != null && cause instanceof InvalidKeyException) {
                throw e;
            } else {
                // Don't have enough information to output a more useful error
                // message
                throw e;
            }
        }
        return validatedJwtContext;

    }

    /**
     * Verifies that tokenIssuer is one of the values specified in the
     * comma-separated issuers string.
     *
     * @param consumerConfigId
     * @param issuers
     * @param tokenIssuer
     * @return
     * @throws InvalidClaimException
     */
    static boolean validateIssuer(String consumerConfigId, String issuers, String tokenIssuer) throws InvalidClaimException {
        boolean isIssuer = false;
        if (issuers == null || issuers.isEmpty()) {
            String msg = Tr.formatMessage(tc, "JWT_TRUSTED_ISSUERS_NULL", new Object[] { tokenIssuer, consumerConfigId });
            throw new InvalidClaimException(msg);
        }

        StringTokenizer st = new StringTokenizer(issuers, ",");
        while (st.hasMoreTokens()) {
            String iss = st.nextToken().trim();
            if (tc.isDebugEnabled()) {
                Tr.debug(tc, "Trusted issuer: " + iss);
            }
            if (Constants.ALL_ISSUERS.equals(iss) || (tokenIssuer != null && tokenIssuer.equals(iss))) {
                isIssuer = true;
                break;
            }
        }

        if (!isIssuer) {
            String msg = Tr.formatMessage(tc, "JWT_ISSUER_NOT_TRUSTED", new Object[] { tokenIssuer, consumerConfigId, issuers });
            throw new InvalidClaimException(msg);
        }
        return isIssuer;
    }

    /**
     * Verifies that at least one of the values specified in audiences is
     * contained in the allowedAudiences list.
     *
     * @param allowedAudiences
     * @param audiences
     * @return
     */
    static boolean validateAudience(List<String> allowedAudiences, List<String> audiences) {
        boolean valid = false;

        if (allowedAudiences != null && allowedAudiences.contains(Constants.ALL_AUDIENCES)) {
            return true;
        }
        if (allowedAudiences != null && audiences != null) {
            for (String audience : audiences) {
                for (String allowedAud : allowedAudiences) {
                    if (allowedAud.equals(audience)) {
                        valid = true;
                        break;
                    }
                }
            }
        } else if (allowedAudiences == null && (audiences == null || audiences.isEmpty())) {
            valid = true;
        }

        return valid;
    }

    /**
     * Validates the the {@value Claims#ISSUED_AT} and
     * {@value Claims#EXPIRATION} claims are present and properly formed. Also
     * verifies that the {@value Claims#ISSUED_AT} time is after the
     * {@value Claims#EXPIRATION} time.
     *
     * @param jwtClaims
     * @param clockSkewInMilliseconds
     * @throws InvalidClaimException
     */
    static void validateIatAndExp(JwtClaims jwtClaims, long clockSkewInMilliseconds) throws InvalidClaimException {
        if (jwtClaims == null) {
            if (tc.isDebugEnabled()) {
                Tr.debug(tc, "Missing JwtClaims object");
            }
            return;
        }
        String malformedClaim = null;
        NumericDate issueAtClaim = null;
        NumericDate expirationClaim = null;
        try {
            // Get these in a dedicated block just in case either is determined
            // to be malformed
            malformedClaim = Claims.ISSUED_AT;
            issueAtClaim = jwtClaims.getIssuedAt();
            malformedClaim = Claims.EXPIRATION;
            expirationClaim = jwtClaims.getExpirationTime();
        } catch (MalformedClaimException e) {
            String msg = Tr.formatMessage(tc, "JWT_CONSUMER_MALFORMED_CLAIM",
                    new Object[] { malformedClaim, e.getLocalizedMessage() });
            throw new InvalidClaimException(msg, e);
        }

        // Establish the clock skew time range (current time +/- clock skew)
        long now = (new Date()).getTime();
        NumericDate currentTimeMinusSkew = NumericDate.fromMilliseconds(now - clockSkewInMilliseconds);
        NumericDate currentTimePlusSkew = NumericDate.fromMilliseconds(now + clockSkewInMilliseconds);
        if (tc.isDebugEnabled()) {
            Tr.debug(tc, "Checking iat [" + createDateString(issueAtClaim) + "] and exp ["
                    + createDateString(expirationClaim) + "]");
            Tr.debug(tc, "Comparing against current time (minus clock skew of " + (clockSkewInMilliseconds / 1000)
                    + " seconds) [" + createDateString(currentTimeMinusSkew) + "]");
            Tr.debug(tc, "Comparing against current time (plus clock skew of " + (clockSkewInMilliseconds / 1000)
                    + " seconds) [" + createDateString(currentTimePlusSkew) + "]");
        }

        if (issueAtClaim != null && expirationClaim != null) {
            if (issueAtClaim.isAfter(currentTimePlusSkew)) {
                String msg = Tr.formatMessage(tc, "JWT_IAT_AFTER_CURRENT_TIME",
                        new Object[] { createDateString(issueAtClaim), createDateString(currentTimePlusSkew),
                                (clockSkewInMilliseconds / 1000) });
                throw new InvalidClaimException(msg);
            }
            if (issueAtClaim.isOnOrAfter(expirationClaim)) {
                String msg = Tr.formatMessage(tc, "JWT_IAT_AFTER_EXP",
                        new Object[] { createDateString(issueAtClaim), createDateString(expirationClaim) });
                throw new InvalidClaimException(msg);
            }
        } else {
            // TODO - what if one or the other is missing? is that an error
            // condition?
        }

        // Check that expiration claim is in the future, accounting for the
        // clock skew
        if (expirationClaim == null || (!expirationClaim.isAfter(currentTimeMinusSkew))) {
            String msg = Tr.formatMessage(tc, "JWT_TOKEN_EXPIRED", new Object[] { createDateString(expirationClaim),
                    createDateString(currentTimeMinusSkew), (clockSkewInMilliseconds / 1000) });
            throw new InvalidClaimException(msg);
        }
    }

    /**
     * Validates the the {@value Claims#NOT_BEFORE} claim is present and
     * properly formed. Also
     *
     * @param jwtClaims
     * @param clockSkewInMilliseconds
     * @throws InvalidClaimException
     */
    static void validateNbf(JwtClaims jwtClaims, long clockSkewInMilliseconds) throws InvalidClaimException {
        if (jwtClaims == null) {
            if (tc.isDebugEnabled()) {
                Tr.debug(tc, "Missing JwtClaims object");
            }
            return;
        }
        NumericDate nbf = null;
        try {
            nbf = jwtClaims.getNotBefore();
        } catch (MalformedClaimException e) {
            String msg = Tr.formatMessage(tc, "JWT_CONSUMER_MALFORMED_CLAIM",
                    new Object[] { Claims.NOT_BEFORE, e.getLocalizedMessage() });
            throw new InvalidClaimException(msg, e);
        }

        long now = (new Date()).getTime();
        NumericDate currentTimePlusSkew = NumericDate.fromMilliseconds(now + clockSkewInMilliseconds);

        // Check that nbf claim is in the past, accounting for the clock skew
        if (nbf != null && (nbf.isOnOrAfter(currentTimePlusSkew))) {
            String msg = Tr.formatMessage(tc, "JWT_TOKEN_BEFORE_NBF", new Object[] { createDateString(nbf),
                    createDateString(currentTimePlusSkew), (clockSkewInMilliseconds / 1000) });
            throw new InvalidClaimException(msg);
        }
    }

    static void validateAlgorithm(JwtContext jwtContext, String requiredAlg) throws InvalidTokenException {
        if (requiredAlg == null) {
            if (tc.isDebugEnabled()) {
                Tr.debug(tc, "No required signature algorithm was specified");
            }
            return;
        }
        String tokenAlg = getAlgorithmHeader(jwtContext);
        if (tokenAlg == null) {
            if (tc.isDebugEnabled()) {
                Tr.debug(tc, "There is no JWT header");
            }
            String msg = Tr.formatMessage(tc, "JWT_MISSING_ALG_HEADER", new Object[] { requiredAlg });
            throw new InvalidTokenException(msg);
        }
        if (tc.isDebugEnabled()) {
            Tr.debug(tc, "JWT is signed with algorithm: ", tokenAlg);
            Tr.debug(tc, "JWT is required to be signed with algorithm: ", requiredAlg);
        }
        if (!requiredAlg.equals(tokenAlg)) {
            String msg = Tr.formatMessage(tc, "JWT_ALGORITHM_MISMATCH", new Object[] { tokenAlg, requiredAlg });
            throw new InvalidTokenException(msg);
        }
    }

    static String getAlgorithmHeader(JwtContext jwtContext) {
        if (jwtContext == null) {
            if (tc.isDebugEnabled()) {
                Tr.debug(tc, "JwtContext is null");
            }
            return null;
        }
        List<JsonWebStructure> jsonStructures = jwtContext.getJoseObjects();
        if (jsonStructures == null || jsonStructures.isEmpty()) {
            if (tc.isDebugEnabled()) {
                Tr.debug(tc, "There is no JWT header");
            }
            return null;
        }
        JsonWebStructure jsonStruct = jsonStructures.get(0);
        String algHeader = jsonStruct.getAlgorithmHeaderValue();
        if (tc.isDebugEnabled()) {
            Tr.debug(tc, "JWT is signed with algorithm: ", algHeader);
        }
        return algHeader;
    }

    static Throwable getRootCause(Exception e) {
        Throwable rootCause = null;
        Throwable tmpCause = e;
        while (tmpCause != null) {
            rootCause = tmpCause;
            tmpCause = rootCause.getCause();
        }
        return rootCause;
    }

    static String createDateString(NumericDate date) {
        if (date == null) {
            return null;
        }
        // NumericDate.getValue() returns a value in seconds, so convert to
        // milliseconds
        return timeUtils.createDateString(1000 * date.getValue());
    }

}
