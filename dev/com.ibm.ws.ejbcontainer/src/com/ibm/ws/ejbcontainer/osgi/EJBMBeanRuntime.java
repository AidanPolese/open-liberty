/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.ejbcontainer.osgi;

import java.util.List;

import org.osgi.framework.ServiceRegistration;

import com.ibm.ws.ejbcontainer.EJBComponentMetaData;
import com.ibm.ws.ejbcontainer.EJBType;
import com.ibm.wsspi.adaptable.module.Container;

public interface EJBMBeanRuntime {
    ServiceRegistration<?> registerModuleMBean(String appName, String moduleName, Container container, String ddPath, List<EJBComponentMetaData> ejbs);

    ServiceRegistration<?> registerEJBMBean(String appName, String moduleName, String beanName, EJBType type);
}
