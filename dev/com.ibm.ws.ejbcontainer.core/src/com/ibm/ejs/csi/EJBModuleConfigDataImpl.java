/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2004, 2010
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ejs.csi;

import com.ibm.websphere.csi.EJBModuleConfigData;

/**
 * Wrapper for all the bean config data passed across the CSI.
 */
public class EJBModuleConfigDataImpl
                implements EJBModuleConfigData
{
    private static final long serialVersionUID = -5249702119132194143L;

    private Object ivEJBJar;
    private Object ivBindings;
    private Object ivExtensions;

    public EJBModuleConfigDataImpl(Object ejbJar, Object bindings, Object extensions)
    {
        ivEJBJar = ejbJar;
        ivBindings = bindings;
        ivExtensions = extensions;
    }

    public Object getModule()
    {
        return ivEJBJar;
    }

    public Object getModuleBinding()
    {
        return ivBindings;
    }

    public Object getModuleExtension()
    {
        return ivExtensions;
    }
}
