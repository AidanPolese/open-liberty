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
package com.ibm.ws.ejbcontainer.jpa.internal;

import javax.ejb.EJBException;

import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;

import com.ibm.ws.ejbcontainer.osgi.EJBContainer;
import com.ibm.ws.jpa.JPAExPcBindingContext;
import com.ibm.ws.jpa.JPAExPcBindingContextAccessor;
import com.ibm.wsspi.kernel.service.utils.AtomicServiceReference;

public class JPAContextAccessor 
   implements JPAExPcBindingContextAccessor
{

   private AtomicServiceReference<EJBContainer> ejbContainerSR = new AtomicServiceReference<EJBContainer>("ejbContainer");
   
   @Override
   public JPAExPcBindingContext getExPcBindingContext()
   {
      return (JPAExPcBindingContext) ejbContainerSR.getServiceWithException().getExPcBindingContext();
   }
   
   public void activate(ComponentContext cc) {
       ejbContainerSR.activate(cc);
   }

   public void deactivate(ComponentContext cc) {
       ejbContainerSR.deactivate(cc);
   }
   
   public void setEJBContainer(ServiceReference<EJBContainer> reference) {
       ejbContainerSR.setReference(reference);
   }

   public void unsetEJBContainer(ServiceReference<EJBContainer> reference) {
       ejbContainerSR.unsetReference(reference);
   }

   @Override
   public RuntimeException newEJBException(String msg) {
	   return new EJBException(msg);
   }
  
}