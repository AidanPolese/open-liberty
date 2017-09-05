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
package com.ibm.ws.security.jsr375.cdi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.security.enterprise.SecurityContext;

/**
 * TODO: Determine if this bean can be PassivationCapable.
 */
public class SecurityContextBean implements Bean<SecurityContext> {

    /*
     * (non-Javadoc)
     *
     * @see javax.enterprise.context.spi.Contextual#create(javax.enterprise.context.spi.CreationalContext)
     */
    @Override
    public SecurityContext create(CreationalContext<SecurityContext> arg0) {
        // TODO Return the actual SecurityContext impl
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.enterprise.context.spi.Contextual#destroy(java.lang.Object, javax.enterprise.context.spi.CreationalContext)
     */
    @Override
    public void destroy(SecurityContext arg0, CreationalContext<SecurityContext> arg1) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     *
     * @see javax.enterprise.inject.spi.BeanAttributes#getName()
     */
    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.enterprise.inject.spi.BeanAttributes#getQualifiers()
     */
    @Override
    public Set<Annotation> getQualifiers() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.enterprise.inject.spi.BeanAttributes#getScope()
     */
    @Override
    public Class<? extends Annotation> getScope() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.enterprise.inject.spi.BeanAttributes#getStereotypes()
     */
    @Override
    public Set<Class<? extends Annotation>> getStereotypes() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.enterprise.inject.spi.BeanAttributes#getTypes()
     */
    @Override
    public Set<Type> getTypes() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.enterprise.inject.spi.BeanAttributes#isAlternative()
     */
    @Override
    public boolean isAlternative() {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.enterprise.inject.spi.Bean#getBeanClass()
     */
    @Override
    public Class<?> getBeanClass() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.enterprise.inject.spi.Bean#getInjectionPoints()
     */
    @Override
    public Set<InjectionPoint> getInjectionPoints() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.enterprise.inject.spi.Bean#isNullable()
     */
    @Override
    public boolean isNullable() {
        // TODO Auto-generated method stub
        return false;
    }

}
