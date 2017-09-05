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

package com.ibm.ws.security.jsr375.identitystore;

import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.security.enterprise.credential.Credential;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.DatabaseIdentityStoreDefinition;
import javax.security.enterprise.identitystore.IdentityStore;

/**
 * Liberty's database {@link IdentityStore} implementation.
 */
@Default
@ApplicationScoped
public class DatabaseIdentityStore implements IdentityStore {

    /** The definitions for this IdentityStore. */
    private final DatabaseIdentityStoreDefinitionWrapper idStoreDefinition;

    /**
     * Construct a new {@link DatabaseIdentityStore} instance using the specified definitions.
     *
     * @param idStoreDefinition The definitions to use to configure the {@link IdentityStore}.
     */
    public DatabaseIdentityStore(DatabaseIdentityStoreDefinition idStoreDefinition) {
        this.idStoreDefinition = new DatabaseIdentityStoreDefinitionWrapper(idStoreDefinition);
    }

    @Override
    public Set<String> getCallerGroups(CredentialValidationResult validationResult) {
        // TODO Add group lookup.
        throw new UnsupportedOperationException(getClass().getName() + " does not yet implement the 'getCallerGroups(CredentialValidationResult)' method.");
    }

    @Override
    public int priority() {
        return idStoreDefinition.getPriority();
    }

    @Override
    public CredentialValidationResult validate(Credential credential) {
        /*
         * Only support UserPasswordCredential.
         */
        if (!(credential instanceof UsernamePasswordCredential)) {
            return CredentialValidationResult.NOT_VALIDATED_RESULT;
        }

        // TODO Add validation.
        throw new UnsupportedOperationException(getClass().getName() + " does not yet implement the 'validate(Credential)' method.");
    }

    @Override
    public Set<ValidationType> validationTypes() {
        return idStoreDefinition.getUseFor();
    }
}
