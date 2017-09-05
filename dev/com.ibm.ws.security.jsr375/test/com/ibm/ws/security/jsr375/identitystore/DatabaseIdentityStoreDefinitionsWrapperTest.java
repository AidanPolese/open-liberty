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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import javax.security.enterprise.identitystore.DatabaseIdentityStoreDefinition;
import javax.security.enterprise.identitystore.IdentityStore.ValidationType;
import javax.security.enterprise.identitystore.PasswordHash;
import javax.security.enterprise.identitystore.Pbkdf2PasswordHash;

import org.junit.Ignore;
import org.junit.Test;

/**
 * Verify that the {@link DatabaseIdentityStoreDefinitionWrapper} provides proper support for
 * retrieving and evaluating both EL expressions and literal settings as called for in JSR375.
 */
public class DatabaseIdentityStoreDefinitionsWrapperTest {

    /*
     * TODO Need to fix ignored tests.
     * TODO Should really have better EL expressions that would represent how a customer would use them.
     * TODO The hashAlgorithm*_EL tests aren't really using EL. Need to figure out how to do them.
     */

    @Test
    public void callerQuery() {
        DatabaseIdentityStoreDefinition idStoreDefinition = getInstanceofAnnotation(null);
        DatabaseIdentityStoreDefinitionWrapper wrapper = new DatabaseIdentityStoreDefinitionWrapper(idStoreDefinition);

        assertEquals("SELECT * FROM users WHERE name ='%v'", wrapper.getCallerQuery());
    }

    @Test
    public void callerQuery_EL() {
        /*
         * Override the callerQuery setting with an EL expression.
         */
        Map<String, Object> overrides = new HashMap<String, Object>();
        overrides.put("callerQuery", "'blah'.concat('blah')");

        DatabaseIdentityStoreDefinition idStoreDefinition = getInstanceofAnnotation(overrides);
        DatabaseIdentityStoreDefinitionWrapper wrapper = new DatabaseIdentityStoreDefinitionWrapper(idStoreDefinition);

        assertEquals("blahblah", wrapper.getCallerQuery());
    }

    @Test
    public void dataSourceLookup() {
        DatabaseIdentityStoreDefinition idStoreDefinition = getInstanceofAnnotation(null);
        DatabaseIdentityStoreDefinitionWrapper wrapper = new DatabaseIdentityStoreDefinitionWrapper(idStoreDefinition);

        assertEquals("java:comp/DefaultDataSource", wrapper.getDataSourceLookup());
    }

    @Test
    public void dataSourceLookup_EL() {
        /*
         * Override the callerQuery setting with an EL expression.
         */
        Map<String, Object> overrides = new HashMap<String, Object>();
        overrides.put("dataSourceLookup", "'blah'.concat('blah')");

        DatabaseIdentityStoreDefinition idStoreDefinition = getInstanceofAnnotation(overrides);
        DatabaseIdentityStoreDefinitionWrapper wrapper = new DatabaseIdentityStoreDefinitionWrapper(idStoreDefinition);

        assertEquals("blahblah", wrapper.getDataSourceLookup());
    }

    @Test
    public void groupsQuery() {
        DatabaseIdentityStoreDefinition idStoreDefinition = getInstanceofAnnotation(null);
        DatabaseIdentityStoreDefinitionWrapper wrapper = new DatabaseIdentityStoreDefinitionWrapper(idStoreDefinition);

        assertEquals("SELECT * FROM groups WHERE name ='%v'", wrapper.getGroupsQuery());
    }

    @Test
    public void groupsQuery_EL() {
        /*
         * Override the callerQuery setting with an EL expression.
         */
        Map<String, Object> overrides = new HashMap<String, Object>();
        overrides.put("groupsQuery", "'blah'.concat('blah')");

        DatabaseIdentityStoreDefinition idStoreDefinition = getInstanceofAnnotation(overrides);
        DatabaseIdentityStoreDefinitionWrapper wrapper = new DatabaseIdentityStoreDefinitionWrapper(idStoreDefinition);

        assertEquals("blahblah", wrapper.getGroupsQuery());
    }

    @Test
    public void hashAlgorithm() {
        DatabaseIdentityStoreDefinition idStoreDefinition = getInstanceofAnnotation(null);
        DatabaseIdentityStoreDefinitionWrapper wrapper = new DatabaseIdentityStoreDefinitionWrapper(idStoreDefinition);

        assertEquals(Pbkdf2PasswordHash.class, wrapper.getHashAlgorithm());
    }

    @Test
    public void hashAlgorithm_EL() {
        /*
         * Override the callerQuery setting with an EL expression.
         */
        Map<String, Object> overrides = new HashMap<String, Object>();
        overrides.put("hashAlgorithm", TestPasswordHash.class);

        DatabaseIdentityStoreDefinition idStoreDefinition = getInstanceofAnnotation(overrides);
        DatabaseIdentityStoreDefinitionWrapper wrapper = new DatabaseIdentityStoreDefinitionWrapper(idStoreDefinition);

        assertEquals(TestPasswordHash.class, wrapper.getHashAlgorithm());
    }

    @Test
    public void hashAlgorithmParameters() {
        DatabaseIdentityStoreDefinition idStoreDefinition = getInstanceofAnnotation(null);
        DatabaseIdentityStoreDefinitionWrapper wrapper = new DatabaseIdentityStoreDefinitionWrapper(idStoreDefinition);

        assertTrue(wrapper.getHashAlgorithmParameters().isEmpty());
    }

    @Test
    public void hashAlgorithmParameters_EL() {
        /*
         * TODO Not really an EL test. Not sure how to test EL for a setting that is a String[].
         */

        /*
         * Override the callerQuery setting with an EL expression.
         */
        Map<String, Object> overrides = new HashMap<String, Object>();
        overrides.put("hashAlgorithmParameters", new String[] { "Algorithm.param1=\"value1\"", "Algorithm.param2=32" });

        DatabaseIdentityStoreDefinition idStoreDefinition = getInstanceofAnnotation(overrides);
        DatabaseIdentityStoreDefinitionWrapper wrapper = new DatabaseIdentityStoreDefinitionWrapper(idStoreDefinition);

        assertTrue(wrapper.getHashAlgorithmParameters().contains("Algorithm.param1=\"value1\""));
        assertTrue(wrapper.getHashAlgorithmParameters().contains("Algorithm.param2=32"));
    }

    @Test
    public void priority() {
        DatabaseIdentityStoreDefinition idStoreDefinition = getInstanceofAnnotation(null);
        DatabaseIdentityStoreDefinitionWrapper wrapper = new DatabaseIdentityStoreDefinitionWrapper(idStoreDefinition);

        assertEquals(80, wrapper.getPriority());
    }

    @Test
    public void priority_EL() {
        /*
         * Override the priority with the priorityExpression setting.
         */
        Map<String, Object> overrides = new HashMap<String, Object>();
        overrides.put("priorityExpression", "80/20");

        DatabaseIdentityStoreDefinition idStoreDefinition = getInstanceofAnnotation(overrides);
        DatabaseIdentityStoreDefinitionWrapper wrapper = new DatabaseIdentityStoreDefinitionWrapper(idStoreDefinition);

        assertEquals(4, wrapper.getPriority());
    }

    @Test
    public void useFor() {
        DatabaseIdentityStoreDefinition idStoreDefinition = getInstanceofAnnotation(null);
        DatabaseIdentityStoreDefinitionWrapper wrapper = new DatabaseIdentityStoreDefinitionWrapper(idStoreDefinition);

        assertTrue(wrapper.getUseFor().contains(ValidationType.PROVIDE_GROUPS));
        assertTrue(wrapper.getUseFor().contains(ValidationType.VALIDATE));
    }

    @Test
    @Ignore("This will fail until the EL implementation can handle inner enums.")
    public void useFor_EL() {
        /*
         * Override the useFor with the useForExpression setting.
         */
        Map<String, Object> overrides = new HashMap<String, Object>();
        overrides.put("useForExpression", "{ValidationType.VALIDATE}");

        DatabaseIdentityStoreDefinition idStoreDefinition = getInstanceofAnnotation(overrides);
        DatabaseIdentityStoreDefinitionWrapper wrapper = new DatabaseIdentityStoreDefinitionWrapper(idStoreDefinition);

        assertFalse(wrapper.getUseFor().contains(ValidationType.PROVIDE_GROUPS));
        assertTrue(wrapper.getUseFor().contains(ValidationType.VALIDATE));
    }

    private DatabaseIdentityStoreDefinition getInstanceofAnnotation(final Map<String, Object> overrides) {
        DatabaseIdentityStoreDefinition annotation = new DatabaseIdentityStoreDefinition() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return null;
            }

            @Override
            public String callerQuery() {
                return (overrides != null && overrides.containsKey("callerQuery")) ? (String) overrides.get("callerQuery") : "SELECT * FROM users WHERE name ='%v'";
            }

            @Override
            public String dataSourceLookup() {
                return (overrides != null && overrides.containsKey("dataSourceLookup")) ? (String) overrides.get("dataSourceLookup") : "java:comp/DefaultDataSource";
            }

            @Override
            public String groupsQuery() {
                return (overrides != null && overrides.containsKey("groupsQuery")) ? (String) overrides.get("groupsQuery") : "SELECT * FROM groups WHERE name ='%v'";
            }

            @Override
            public Class<? extends PasswordHash> hashAlgorithm() {
                return (overrides != null && overrides.containsKey("hashAlgorithm")) ? (Class<? extends PasswordHash>) overrides.get("hashAlgorithm") : Pbkdf2PasswordHash.class;
            }

            @Override
            public String[] hashAlgorithmParameters() {
                return (overrides != null && overrides.containsKey("hashAlgorithmParameters")) ? (String[]) overrides.get("hashAlgorithmParameters") : null;
            }

            @Override
            public int priority() {
                return (overrides != null && overrides.containsKey("priority")) ? (Integer) overrides.get("priority") : 80;
            }

            @Override
            public String priorityExpression() {
                return (overrides != null && overrides.containsKey("priorityExpression")) ? (String) overrides.get("priorityExpression") : "";
            }

            @Override
            public ValidationType[] useFor() {
                return (overrides != null && overrides.containsKey("useFor")) ? (ValidationType[]) overrides.get("useFor") : new ValidationType[] { ValidationType.PROVIDE_GROUPS,
                                                                                                                                                    ValidationType.VALIDATE };
            }

            @Override
            public String useForExpression() {
                return (overrides != null && overrides.containsKey("useForExpression")) ? (String) overrides.get("useForExpression") : "";
            }
        };

        return annotation;
    }
}
