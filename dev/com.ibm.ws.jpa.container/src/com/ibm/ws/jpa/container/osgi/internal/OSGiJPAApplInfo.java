/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jpa.container.osgi.internal;

import com.ibm.ws.container.service.app.deploy.ApplicationInfo;
import com.ibm.ws.jpa.JPAPuId;
import com.ibm.ws.jpa.management.AbstractJPAComponent;
import com.ibm.ws.jpa.management.JPAApplInfo;
import com.ibm.ws.jpa.management.JPAPUnitInfo;
import com.ibm.ws.jpa.management.JPAPXml;
import com.ibm.ws.jpa.management.JPAScopeInfo;
import com.ibm.wsspi.adaptable.module.Container;

public class OSGiJPAApplInfo extends JPAApplInfo {
    private final ApplicationInfo appInfo;

    OSGiJPAApplInfo(AbstractJPAComponent jpaComponent, String name, ApplicationInfo appInfo) {
        super(jpaComponent, name);
        this.appInfo = appInfo;
    }

    @Override
    protected JPAPUnitInfo createJPAPUnitInfo(JPAPuId puId, JPAPXml pxml, JPAScopeInfo scopeInfo) {
        return new OSGiJPAPUnitInfo(this, puId, pxml.getClassLoader(), scopeInfo);
    }

    Container getContainer() {
        return appInfo.getContainer();
    }
}
