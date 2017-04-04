/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012, 2014
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.javaee.dd.managedbean;

import java.util.List;

import com.ibm.ws.javaee.dd.DeploymentDescriptor;
import com.ibm.ws.javaee.dd.commonbnd.Interceptor;
import com.ibm.ws.javaee.ddmetadata.annotation.DDAttribute;
import com.ibm.ws.javaee.ddmetadata.annotation.DDAttributeType;
import com.ibm.ws.javaee.ddmetadata.annotation.DDConstants;
import com.ibm.ws.javaee.ddmetadata.annotation.DDElement;
import com.ibm.ws.javaee.ddmetadata.annotation.DDIdAttribute;
import com.ibm.ws.javaee.ddmetadata.annotation.DDRootElement;
import com.ibm.ws.javaee.ddmetadata.annotation.DDVersion;
import com.ibm.ws.javaee.ddmetadata.annotation.LibertyModule;
import com.ibm.ws.javaee.ddmetadata.annotation.LibertyNotInUse;

@DDRootElement(name = "managed-bean-bnd",
               versions = {
                            @DDVersion(versionString = "1.0", version = 10, namespace = DDConstants.WEBSPHERE_EE_NS_URI),
                            @DDVersion(versionString = "1.1", version = 11, namespace = DDConstants.WEBSPHERE_EE_NS_URI)
               })
@DDIdAttribute
@LibertyModule
public interface ManagedBeanBnd extends DeploymentDescriptor {

    /**
     * @return version="..." attribute value
     */
    @LibertyNotInUse
    @DDAttribute(name = "version", type = DDAttributeType.String)
    String getVersion();

    /**
     * @return &lt;interceptor> as a read-only list
     */
    @DDElement(name = "interceptor")
    List<Interceptor> getInterceptors();

    /**
     * @return &lt;managed-bean> as a read-only list
     */
    @DDElement(name = "managed-bean")
    List<ManagedBean> getManagedBeans();
}
