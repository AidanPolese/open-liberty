/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014, 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jaxrs20.beanvalidation.component;

import java.util.List;

import org.apache.cxf.jaxrs.validation.JAXRSBeanValidationFeature;
import org.apache.cxf.jaxrs.validation.ValidationExceptionMapper;
import org.apache.cxf.validation.BeanValidationProvider;
import org.osgi.service.component.annotations.Component;

import com.ibm.ws.jaxrs20.api.JaxRsBeanValidationService;

@Component(name = "com.ibm.ws.jaxrs20.JaxRsBeanValidationServiceImpl", property = { "service.vendor=IBM" })
public class JaxRsBeanValidationServiceImpl implements JaxRsBeanValidationService {

    @Override
    public boolean enableBeanValidationProviders(List<Object> providers) {

        providers.add(new JAXRSBeanValidationFeature());
        providers.add(new ValidationExceptionMapper());

        return true;

    }

    @Override
    public Class<?> getBeanValidationProviderClass() {// throws Exception {
        BeanValidationProvider provider = new BeanValidationProvider();
        Class<?> clazz = provider.getClass();

        return clazz;
    }

}
