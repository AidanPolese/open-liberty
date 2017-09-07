/*
* IBM Confidential
*
* OCO Source Materials
*
* WLP Copyright IBM Corp. 2017
*
* The source code for this program is not published or otherwise divested
* of its trade secrets, irrespective of what has been deposited with the
* U.S. Copyright Office.
*/
package com.ibm.ws.security.jsr375.cdi;

import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.security.enterprise.credential.BasicAuthenticationCredential;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStore;

import com.ibm.websphere.ras.annotation.Sensitive;

@Default
@ApplicationScoped
public class TestIdentityStore implements IdentityStore {

    public TestIdentityStore() {}

    public CredentialValidationResult validate(@Sensitive BasicAuthenticationCredential cred) {
        return CredentialValidationResult.INVALID_RESULT;
    }

    public CredentialValidationResult validate(@Sensitive UsernamePasswordCredential cred) {
        return CredentialValidationResult.INVALID_RESULT;
    }

    @Override
    public Set<String> getCallerGroups(CredentialValidationResult validationResult) {
        return null;
    }

    @Override
    public int priority() {
        return 100;
    }

    @Override
    public Set<IdentityStore.ValidationType> validationTypes() {
        return IdentityStore.DEFAULT_VALIDATION_TYPES; // Contains VALIDATE and PROVIDE_GROUPS
    }
}
