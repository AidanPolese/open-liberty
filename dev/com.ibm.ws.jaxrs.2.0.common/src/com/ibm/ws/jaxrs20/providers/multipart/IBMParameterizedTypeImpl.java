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
package com.ibm.ws.jaxrs20.providers.multipart;

import java.lang.reflect.MalformedParameterizedTypeException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

public class IBMParameterizedTypeImpl implements ParameterizedType {
    private final Type[] actualTypeArguments;
    private final Class<?> rawType;
    private Type ownerType;

    private IBMParameterizedTypeImpl(Class<?> rawType,
                                     Type[] actualTypeArguments,
                                     Type ownerType) {
        this.actualTypeArguments = actualTypeArguments;
        this.rawType = rawType;
        if (ownerType != null) {
            this.ownerType = ownerType;
        } else {
            this.ownerType = rawType.getDeclaringClass();
        }

        if (rawType.getTypeParameters().length != actualTypeArguments.length) {
            throw new MalformedParameterizedTypeException();
        }
    }

    public static IBMParameterizedTypeImpl make(Class<?> rawType,
                                                Type[] actualTypeArguments,
                                                Type ownerType) {
        return new IBMParameterizedTypeImpl(rawType, actualTypeArguments, ownerType);
    }

    @Override
    public boolean equals(Object o) {
        boolean ret = false;
        if (o instanceof ParameterizedType) {
            ParameterizedType that = (ParameterizedType) o;

            Type thatOwner = that.getOwnerType();
            Type thatRawType = that.getRawType();

            ret = (ownerType == null ? thatOwner == null : ownerType.equals(thatOwner)) &&
                  (rawType == null ? thatRawType == null : rawType.equals(thatRawType)) &&
                  Arrays.equals(actualTypeArguments, that.getActualTypeArguments());
        }
        return ret;
    }

    @Override
    public Type[] getActualTypeArguments() {
        return this.actualTypeArguments.clone();
    }

    @Override
    public Type getRawType() {
        return this.rawType;
    }

    @Override
    public Type getOwnerType() {
        return this.ownerType;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(actualTypeArguments) ^
               (ownerType == null ? 0 : ownerType.hashCode()) ^
               (rawType == null ? 0 : rawType.hashCode());
    }

}
