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
package com.ibm.ejs.container;

import javax.ejb.EnterpriseBean;

/**
 * This class exists for ABI compatibility with ejbdeploy'ed home beans.
 */
public class BeanManagedBeanO
{
    final EntityBeanO ivDelegate;

    BeanManagedBeanO(EntityBeanO delegate)
    {
        ivDelegate = delegate;
    }

    /**
     * Return the underlying bean instance. This method exists for ABI
     * compatibility with ejbdeploy'ed home beans.
     */
    public EnterpriseBean getEnterpriseBean()
    {
        return (EnterpriseBean) ivDelegate.getBeanInstance();
    }
}
