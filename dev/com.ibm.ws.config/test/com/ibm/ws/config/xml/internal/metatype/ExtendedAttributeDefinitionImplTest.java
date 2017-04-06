/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012, 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.config.xml.internal.metatype;

import org.eclipse.equinox.metatype.EquinoxAttributeDefinition;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Test;
import org.osgi.service.metatype.AttributeDefinition;

/**
 *
 */
public class ExtendedAttributeDefinitionImplTest {
    private final Mockery mock = new Mockery();
    private final EquinoxAttributeDefinition delegate = mock.mock(EquinoxAttributeDefinition.class);
    private ExtendedAttributeDefinition ead;

    @After
    public void tearDown() {
        mock.assertIsSatisfied();
    }

    /**
     * Test method for {@link com.ibm.ws.config.xml.internal.metatype.ExtendedAttributeDefinitionImpl#validate(java.lang.String)}.
     */
    @Test
    public void validate_nonString() {
        mock.checking(new Expectations() {
            {
                one(delegate).getExtensionUris();
                will(returnValue(null));
                one(delegate).getType();
                will(returnValue(AttributeDefinition.INTEGER));
                one(delegate).validate("12345");
            }
        });
        ead = new ExtendedAttributeDefinitionImpl(delegate);
        ead.validate("12345");
    }

    /**
     * Test method for {@link com.ibm.ws.config.xml.internal.metatype.ExtendedAttributeDefinitionImpl#validate(java.lang.String)}.
     */
    @Test
    public void validate_multiCardinalityString() {
        mock.checking(new Expectations() {
            {
                one(delegate).getExtensionUris();
                will(returnValue(null));
                one(delegate).getType();
                will(returnValue(AttributeDefinition.STRING));
                one(delegate).validate("value1,value2");
            }
        });
        ead = new ExtendedAttributeDefinitionImpl(delegate);
        ead.validate("value1,value2");
    }

    /**
     * Test method for {@link com.ibm.ws.config.xml.internal.metatype.ExtendedAttributeDefinitionImpl#validate(java.lang.String)}.
     */
    @Test
    public void validate_simpleString() {
        mock.checking(new Expectations() {
            {
                one(delegate).getExtensionUris();
                will(returnValue(null));
                one(delegate).getType();
                will(returnValue(AttributeDefinition.STRING));
                one(delegate).validate("abc");
            }
        });
        ead = new ExtendedAttributeDefinitionImpl(delegate);
        ead.validate("abc");
    }

    /**
     * Test method for {@link com.ibm.ws.config.xml.internal.metatype.ExtendedAttributeDefinitionImpl#validate(java.lang.String)}.
     */
    @Test
    public void validate_escapedString() {
        mock.checking(new Expectations() {
            {
                one(delegate).getExtensionUris();
                will(returnValue(null));
                one(delegate).getType();
                will(returnValue(AttributeDefinition.STRING));
                one(delegate).validate("string\\,with\\,commas");
            }
        });
        ead = new ExtendedAttributeDefinitionImpl(delegate);
        ead.validate("string\\,with\\,commas");
    }
}
