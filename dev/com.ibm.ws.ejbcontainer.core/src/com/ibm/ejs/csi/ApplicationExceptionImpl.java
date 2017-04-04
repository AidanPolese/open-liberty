/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2009, 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ejs.csi;

import java.lang.annotation.Annotation;

import javax.ejb.ApplicationException;

public class ApplicationExceptionImpl
                implements ApplicationException
{
    private final boolean ivRollback;
    private final boolean ivInherited;

    public ApplicationExceptionImpl(boolean rollback, boolean inherited)
    {
        ivRollback = rollback;
        ivInherited = inherited;
    }

    public Class<? extends Annotation> annotationType()
    {
        return ApplicationException.class;
    }

    public boolean rollback()
    {
        return ivRollback;
    }

    public boolean inherited()
    {
        return ivInherited;
    }
}
