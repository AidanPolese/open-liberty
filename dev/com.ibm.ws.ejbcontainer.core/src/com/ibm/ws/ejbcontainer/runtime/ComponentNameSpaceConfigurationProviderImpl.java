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
package com.ibm.ws.ejbcontainer.runtime;

import com.ibm.ejs.container.BeanMetaData;
import com.ibm.ejs.container.ContainerException;
import com.ibm.ejs.container.EJBConfigurationException;
import com.ibm.wsspi.injectionengine.ComponentNameSpaceConfiguration;
import com.ibm.wsspi.injectionengine.ComponentNameSpaceConfigurationProvider;
import com.ibm.wsspi.injectionengine.InjectionException;

public class ComponentNameSpaceConfigurationProviderImpl
                implements ComponentNameSpaceConfigurationProvider
{
    private final BeanMetaData ivBMD;
    private final AbstractEJBRuntime ivEJBRuntime;

    public ComponentNameSpaceConfigurationProviderImpl(BeanMetaData bmd, AbstractEJBRuntime ejbRuntime)
    {
        ivBMD = bmd;
        ivEJBRuntime = ejbRuntime;
    }

    @Override
    public String toString()
    {
        return super.toString() + '[' + ivBMD.j2eeName + ']';
    }

    public ComponentNameSpaceConfiguration getComponentNameSpaceConfiguration()
                    throws InjectionException
    {
        try
        {
            return ivEJBRuntime.finishBMDInitForReferenceContext(ivBMD);
        } catch (ContainerException ex)
        {
            throw new InjectionException(ex);
        } catch (EJBConfigurationException ex)
        {
            throw new InjectionException(ex);
        }
    }
}
