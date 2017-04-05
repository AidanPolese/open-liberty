/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2016
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.security.audit.utils;

import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class AuditConstants {

    static public final String MAX_FILES = "maxFiles";
    static public final String MAX_FILE_SIZE = "maxFileSize";
    static public final String ENCRYPT = "encrypt";
    static public final String SIGN = "sign";
    static public final String ENCRYPT_ALIAS = "encrpytAlias";
    static public final String ENCRYPT_KEYSTORE_REF = "encryptKeyStoreRef";
    static public final String SIGNER_ALIAS = "signerAlias";
    static public final String SIGNER_KEYSTORE_REF = "signerKeyStoreRef";
    static public final String WRAP_BEHAVIOR = "wrapBehavior";
    static public final String LOG_DIRECTORY = "logDirectory";
    static public final String EVENTS = "events";
    static public final String COMPACT = "compact";

    static public final String EVENT_NAME = "eventName";
    static public final String IS_CUSTOM_EVENT = "isCustomEvent";
    static public final String AUDIT_DATA = "auditData";
    static public final String OUTCOME = "outcome";
    static public final String EVENT_SEQUENCE_NUMBER = "eventSequenceNumber";

    static public final String CONFIG_SNAPSHOT = "CONFIG_SNAPSHOT";
    static public final String SECURITY_AUDIT_MGMT = "SECURITY_AUDIT_MGMT";
    static public final String SECURITY_MEMBER_MGMT = "SECURITY_MEMBER_MGMT";
    static public final String SECURITY_SESSION_LOGIN = "SECURITY_SESSION_LOGIN";
    static public final String SECURITY_SESSION_LOGOUT = "SECURITY_SESSION_LOGOUT";
    static public final String SECURITY_SESSION_EXPIRY = "SECURITY_SESSION_EXPIRY";
    static public final String SECURITY_ROLE_MAPPING = "SECURITY_ROLE_MAPPING";
    static public final String SECURITY_AUTHN = "SECURITY_AUTHN";
    static public final String SECURITY_AUTHN_DELEGATION = "SECURITY_AUTHN_DELEGATION";
    static public final String SECURITY_AUTHN_FAILOVER = "SECURITY_AUTHN_FAILOVER";
    static public final String SECURITY_AUTHZ = "SECURITY_AUTHZ";
    static public final String SECURITY_SIGNING = "SECURITY_SIGNING";
    static public final String SECURITY_ENCRYPTION = "SECURITY_ENCRYPTION";
    static public final String SECURITY_RESOURCE_ACCESS = "SECURITY_RESOURCE_ACCESS";
    static public final String SECURITY_MGMT_KEY = "SECURITY_MGMT_KEY";
    static public final String SECURITY_RUNTIME_KEY = "SECURITY_RUNTIME_KEY";
    static public final String JMX_MBEANS = "JMX_MBEANS";
    static public final String JMX_NOTIFICATION = "JMX_NOTIFICATION";
    static public final String JMS = "JMS";
    static public final String CUSTOM = "CUSTOM";
    
    static public final List<String> validEventNamesList = Arrays.asList("CONFIG_SNAPSHOT", "SECURITY_AUDIT_MGMT", "SECURITY_MEMBER_MGMT", "SECURITY_SESSION_LOGIN", 
    		"SECURITY_SESSION_LOGOUT", "SECURITY_SESSION_EXPIRY", "SECURITY_ROLE_MAPPING", "SECURITY_AUTHN", "SECURITY_AUTHN_FAILOVER", "SECURITY_AUTHN_DELEGATION", "SECURITY_AUTHZ", 
    		"SECURITY_SIGNING", "SECURITY_ENCRYPTION", "SECURITY_RESOURCE_ACCESS", "SECURITY_MGMT_KEY", "SECURITY_RUNTIME_KEY", "JMX_MBEANS", "JMX_NOTIFICATION", "JMS"); 

    static public final String SUCCESS = "success";
    static public final String FAILURE = "failure";
    static public final String DENIED = "denied";
    static public final String ERROR = "error";
    static public final String WARNING = "warning";
    static public final String INFO = "info";
    static public final String REDIRECT = "redirect";
    static public final String CHALLENGE = "challenge";

    static public final List<String> validOutcomesList = Arrays.asList("SUCCESS", "FAILURE", "DENIED", "ERROR", "WARNING", "INFO", "REDIRECT", "CHALLENGE");

    static public final String ORIGINAL_AUTH_TYPE = "originalAuthType";
    static public final String FAILOVER_AUTH_TYPE = "failoverAuthType";
    static public final String WEB_CONTAINER = "web";
    static public final String EJB_CONTAINER = "ejb";
}
