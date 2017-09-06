/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jaxrs20.providers.customexceptionmapper;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.ExceptionMapper;

import org.osgi.service.component.annotations.Component;

import com.ibm.ws.jaxrs20.providers.api.JaxRsProviderRegister;

/**
 *
 */
@Component(immediate=true)
public class CustomExceptionMapperRegister implements JaxRsProviderRegister {

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.jaxrs20.providers.JaxRsProviderRegister#installProvider(java.util.List, java.util.Set)
     */
    @Override
    public void installProvider(boolean clientSide, List<Object> providers, Set<String> features) {
        if (!clientSide) {
            if (getAddCustomExceptionMapperFlag(providers))
            {
                providers.add(new CustomWebApplicationExceptionMapper());
            }
        }

    }

    private boolean getAddCustomExceptionMapperFlag(List<Object> providers)
    {
        for (Object o : providers)
        {
            Class<?> providerCls = o.getClass();
            boolean flag = getFlagFromClass(providerCls);
            if (!flag)
                return false;
        }
        return true;
    }

    private boolean getFlagFromClass(Class<?> providerCls) {

        if (ExceptionMapper.class.isAssignableFrom(providerCls))
        {

            Type[] types = providerCls.getGenericInterfaces();
            for (Type t : types)
            {
                if (t instanceof ParameterizedType) {
                    ParameterizedType pt = (ParameterizedType) t;
                    if (ExceptionMapper.class.isAssignableFrom((Class<?>) pt.getRawType())) {
                        Type[] args = pt.getActualTypeArguments();
                        Class<?> actualClass = getRawType(args[0]);
                        if (null != actualClass && actualClass.isAssignableFrom(WebApplicationException.class)) {
                            return false;
                        }
                    }
                }
            }
            Class<?> parentClass = providerCls.getSuperclass();
            boolean flag = getFlagFromClass(parentClass);
            return flag;
        }
        return true;
    }

    private Class<?> getRawType(Type genericType) {

        if (genericType instanceof Class) {
            return (Class<?>) genericType;
        } else if (genericType instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType) genericType;
            Type t = paramType.getRawType();
            if (t instanceof Class) {
                return (Class<?>) t;
            }
        } else if (genericType instanceof GenericArrayType) {
            return getRawType(((GenericArrayType) genericType).getGenericComponentType());
        }
        return null;
    }
}
