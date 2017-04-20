// /I/ /W/ /G/ /U/   <-- CMVC Keywords, replace / with %
// %I% %W% %G% %U%
//
// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2012, 2014
//
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Module  :  ValidatorFactoryLocator.java
//
// Change Activity:
//
// Reason    Version   Date     Userid    Change Description
// --------- --------- -------- --------- -----------------------------------------
// d727932   WAS85     20120208 xuhaih   : refactor for bean validation access
// --------- --------- -------- --------- -----------------------------------------
package com.ibm.ws.jpa.container.beanvalidation;

import javax.validation.ValidatorFactory;

/**
 * The ValidatorFactoryLocator makes use of the BeanValidation service to
 * obtain a ValidatorFactory instance. <p>
 */
interface ValidatorFactoryLocator
{
    /**
     * Returns the container managed ValidatorFactory that has been configured
     * for the current Java EE application module. <p>
     */
    public ValidatorFactory getValidatorFactory();
}
