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

/**
 * %Z% %I% %W% %G% %U% [%H% %T%]
 * COMPONENT_NAME: WAS.beanvalidation
 * FILE_NAME:  BVNLSConstants.java
 *
 *
 * IBM Confidential OCO Source Material
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2010
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U. S. Copyright Office.
 *
 * Change History:
 *
 * D|F Name         Date        User id    Description
 * ------------------------------------------------------------------------------------------------------------------------
 * F000743-26323   05/03/2010  westland   initial drop
 * F000743-28638   06/29/2010  westland   update to add messages when processing the validation.xml inputStream
 * D664527         08/18/2010  westland   Update messages for bean validation when parsing the validation.xml file
 */

package com.ibm.ws.beanvalidation;

/**
 * Defines the constants necessary to NLS conversions. There is a direct relationship between this file and the nlsprops file that this file {@link #NLS_RESOURCE_FILE refers to}
 * .<br>
 * Error message key constants for the Bean Validation localizable message keys. There should be a one to one correspondence between the constant that represents
 * the message key and each message key/message found in the Bean Validation message bundle (i.e., the BVNLSMessages.properties file). Whenever a new message is
 * added to the Bean Validation message bundle, a new message key constant should be add to this file. In other words, there is a tight coupling between these
 * constants and the Bean Validation message bundle. The bean validation code should use these message key constants rather than a String that represents the message key.
 * 
 * @author westland@us.ibm.com
 * @version %I%
 */
public interface BVNLSConstants {

    /**
     * Name of the default resource bundle used by code shipped with BeanValidation
     */
    public static final String BV_RESOURCE_BUNDLE = "com.ibm.ws.beanvalidation.resources.nls.BVNLSMessages";
    public static final String BVKEY_UNABLE_TO_REGISTER_WITH_INJECTION_ENGINE = "BVKEY_UNABLE_TO_REGISTER_WITH_INJECTION_ENGINE";
    public static final String BVKEY_UNABLE_TO_CREATE_VALIDATION_FACTORY = "BVKEY_UNABLE_TO_CREATE_VALIDATION_FACTORY";
    public static final String BVKEY_CLASS_NOT_FOUND = "BVKEY_CLASS_NOT_FOUND";
    public static final String BVKEY_SYNTAX_ERROR_IN_VALIDATION_XML = "BVKEY_SYNTAX_ERROR_IN_VALIDATION_XML";
    public static final String BVKEY_NOT_A_BEAN_VALIDATION_XML = "BVKEY_NOT_A_BEAN_VALIDATION_XML";

}
