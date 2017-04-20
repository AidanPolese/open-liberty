/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.ejbcontainer.osgi.internal.ejb;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;

@MessageDriven(activationConfig = { @ActivationConfigProperty(propertyName = TestActivationConfigProperties.NAME1, propertyValue = TestActivationConfigProperties.VALUE1),
                                   @ActivationConfigProperty(propertyName = TestActivationConfigProperties.NAME2, propertyValue = TestActivationConfigProperties.VALUE2) })
public class TestActivationConfigProperties {
    public static final String NAME1 = "name1";
    public static final String VALUE1 = "value1";
    public static final String NAME2 = "name2";
    public static final String VALUE2 = "value2";
}
