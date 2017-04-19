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
package com.ibm.ws.beanvalidation.v11.cdi.internal;

import javax.enterprise.inject.spi.BeanManager;
import javax.validation.ConstraintValidatorFactory;

import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

import com.ibm.ws.beanvalidation.service.ValidationReleasable;
import com.ibm.ws.beanvalidation.service.ValidationReleasableFactory;
import com.ibm.ws.cdi.CDIService;
import com.ibm.ws.managedobject.ManagedObject;
import com.ibm.ws.managedobject.ManagedObjectException;
import com.ibm.ws.managedobject.ManagedObjectFactory;
import com.ibm.ws.managedobject.ManagedObjectService;
import com.ibm.ws.runtime.metadata.ModuleMetaData;
import com.ibm.ws.threadContext.ComponentMetaDataAccessorImpl;
import com.ibm.wsspi.kernel.service.utils.AtomicServiceReference;

/**
 * An implementation that is CDI aware.
 */
@Component(service = ValidationReleasableFactory.class)
public class ValidationReleasableFactoryImpl implements ValidationReleasableFactory {

    private static final String REFERENCE_CDI_SERVICE = "cdiService";
    private static final String REFERENCE_MANAGED_OBJECT_SERVICE = "managedObjectService";

    private final AtomicServiceReference<CDIService> cdiService =
                    new AtomicServiceReference<CDIService>(REFERENCE_CDI_SERVICE);
    private final AtomicServiceReference<ManagedObjectService> managedObjectServiceRef = new AtomicServiceReference<ManagedObjectService>(REFERENCE_MANAGED_OBJECT_SERVICE);

    @Override
    public <T> ManagedObject<T> createValidationReleasable(Class<T> clazz) {
        BeanManager beanManager = getCurrentBeanManager();

        // If the bean manger isn't null, this indicates that the module that is
        // invoking this code path has CDI enabled.
        if (beanManager != null) {

            // The mof handles calling produce, inject, and postConstruct.
            ManagedObjectFactory<T> mof = getManagedBeanManagedObjectFactory(clazz);
            try {
                return mof.createManagedObject();
            } catch (Exception e) {
                // ffdc
            }
        }
        return null;
    }

    @Override
    public ValidationReleasable<ConstraintValidatorFactory> createConstraintValidatorFactory() {
        BeanManager beanManager = getCurrentBeanManager();
        if (beanManager != null) {
            return new ReleasableConstraintValidatorFactory(this);
        }
        return null;
    }

    private BeanManager getCurrentBeanManager() {
        return cdiService.getServiceWithException().getCurrentBeanManager();
    }

    private <T> ManagedObjectFactory<T> getManagedBeanManagedObjectFactory(Class<T> clazz) {
        ModuleMetaData mmd = ComponentMetaDataAccessorImpl.getComponentMetaDataAccessor().getComponentMetaData().getModuleMetaData();
        ManagedObjectService managedObjectService = managedObjectServiceRef.getService();
        if (managedObjectService != null) {
            try {
                ManagedObjectFactory<T> factory = managedObjectService.createManagedObjectFactory(mmd, clazz, true);
                if (factory.isManaged()) {
                    return factory;
                }
            } catch (ManagedObjectException e) {
                // ffdc
            }
        }
        return null;
    }

    @Activate
    protected void activate(ComponentContext cc) {
        cdiService.activate(cc);
        managedObjectServiceRef.activate(cc);
    }

    @Deactivate
    protected void deactivate(ComponentContext cc) {
        cdiService.deactivate(cc);
        managedObjectServiceRef.deactivate(cc);
    }

    @Reference(name = REFERENCE_CDI_SERVICE, service = CDIService.class)
    protected void setCdiService(ServiceReference<CDIService> ref) {
        cdiService.setReference(ref);
    }

    protected void unsetCdiService(ServiceReference<CDIService> ref) {
        cdiService.unsetReference(ref);
    }

    @Reference(name = REFERENCE_MANAGED_OBJECT_SERVICE,
               service = ManagedObjectService.class,
               policy = ReferencePolicy.DYNAMIC,
               policyOption = ReferencePolicyOption.GREEDY)
    protected void setManagedObjectService(ServiceReference<ManagedObjectService> ref) {
        managedObjectServiceRef.setReference(ref);
    }

    protected void unsetManagedObjectService(ServiceReference<ManagedObjectService> ref) {
        managedObjectServiceRef.unsetReference(ref);
    }
}
