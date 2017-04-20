/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.security.registry.basic.internal;

import com.ibm.ws.security.registry.UserRegistryIllegalArgumentTemplate;

/**
 * @see UserRegistryIllegalArgumentTemplate
 */
public class BasicRegistryIllegalArgumentTest extends UserRegistryIllegalArgumentTemplate {

    public BasicRegistryIllegalArgumentTest() throws Exception {
        super(basicRegistry());
    }

    static BasicRegistry basicRegistry() {
        BasicRegistry basicRegistry = new BasicRegistry();
        basicRegistry.activate(new BasicRegistryConfig() {

            @Override
            public String realm() {
                return "testRealm";
            }

            @Override
            public boolean ignoreCaseForAuthentication() {
                return false;
            }

            @Override
            public User[] user() {
                return new User[] {};
            }

            @Override
            public Group[] group() {
                return new Group[] {};
            }

            @Override
            public String config_id() {
                return "test-config-id";
            }
        });
        return basicRegistry;
    }
}
