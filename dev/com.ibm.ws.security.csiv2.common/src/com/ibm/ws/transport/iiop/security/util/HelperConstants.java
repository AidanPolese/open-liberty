/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.transport.iiop.security.util;

/**
 *
 */
public interface HelperConstants {
    //see ConfigurationEvaluator
    String CONFIG_REFERENCE_TYPE = "config.referenceType";
    /**  */
    String INHERIT = "inherit";

    /**  */
    String STATEFUL = "stateful";

    /**  */
    String REALM = "realm";
    /**  */
    String PRINCIPAL_CLASS = "principal-class";
    /**  */
    String NAME = "name";
    /**  */
    String OID = "oid";
    /**  */
    String DOMAIN = "domain";
    /**  */
    String PASSWORD = "password";
    /**  */
    String USERNAME = "username";
    /**  */
    String REQUIRES = "requires";
    /**  */
    String SUPPORTS = "supports";
    /**  */
    String COMPOUND_SECH_MECH_LIST = "compoundSechMechList";
    /**  */
    String COMPOUND_SEC_MECH_TYPE_LIST = "compoundSecMech";
    /**  */
    String ITT_PRINCIPAL_NAME_DYNAMIC = "ITTPrincipalNameDynamic";
    /**  */
    String ITT_PRINCIPAL_NAME_STATIC = "ITTPrincipalNameStatic";
    /**  */
    String ITT_ANONYMOUS = "ITTAnonymous";
    /**  */
    String ITT_ABSENT = "ITTAbsent";
    /**  */
    String SAS_MECH = "sasMech";
    /**  */
    String GSSUP = "GSSUP";
    /**  */
    String GSSUP_DYNAMIC = "GSSUPDynamic";
    /**  */
    String GSSUP_STATIC = "GSSUPStatic";
    /**  */
    String SECIOP = "SECIOP";
    /**  */
    String SSL_OPTIONS = "csiv2SSLOptions";

    enum AssociationOptions {
        NoProtection, Integrity, Confidentiality, DetectReplay, DetectMisordering, EstablishTrustInTarget,
        EstablishTrustInClient, NoDelegation, SimpleDelegation, CompositeDelegation, IdentityAssertion, DelegationByClient
    };

}