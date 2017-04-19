// /I/ /W/ /G/ /U/   <-- CMVC Keywords, replace / with %
// %I% %W% %G% %U%
//
// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2014
//
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Module  :  JPABVComponentImpl.java
//
// Change Activity:
//
// Reason    Version   Date     Userid    Change Description
// --------- --------- -------- --------- -----------------------------------------
// 
// --------- --------- -------- --------- -----------------------------------------

package com.ibm.ws.jpa.container.beanvalidation;

import java.util.Map;

import javax.validation.ValidationException;
import javax.validation.ValidatorFactory;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;

import com.ibm.ws.beanvalidation.service.BeanValidation;
import com.ibm.ws.jpa.management.JPAEMFPropertyProvider;
import com.ibm.ws.runtime.metadata.ComponentMetaData;
import com.ibm.ws.threadContext.ComponentMetaDataAccessorImpl;

/*
 * This class is factored out of AbstractJPAComponent and exists
 * in order to pass a ValidatorFactory to the JPA provider.
 */
@Component(service={JPAEMFPropertyProvider.class})
public class JPABVComponentImpl implements ValidatorFactoryLocator, JPAEMFPropertyProvider{
    
    private BeanValidation bvalService;

    @Override
    public ValidatorFactory getValidatorFactory() // d727932
    {
        if (bvalService == null) {
            throw new ValidationException("bean validation provider is not available");
        }
        ComponentMetaData cmd = ComponentMetaDataAccessorImpl.getComponentMetaDataAccessor().getComponentMetaData();
        return bvalService.getValidatorFactoryOrDefault(cmd);
    }

    
    @Reference(cardinality=ReferenceCardinality.MANDATORY)
    protected void setBeanValidationService(BeanValidation bv) {
        bvalService = bv;
    }
    
    protected void unsetBeanValidationService(BeanValidation bv) {
        bvalService = null;
    }


    @Override
    public void updateProperties(Map<String, Object> props) {
        props.put("javax.persistence.validation.factory", new JPAValidatorFactory(this));
    }
}
