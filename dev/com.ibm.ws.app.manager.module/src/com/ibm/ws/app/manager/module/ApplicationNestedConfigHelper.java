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
package com.ibm.ws.app.manager.module;

import com.ibm.ws.container.service.app.deploy.NestedConfigHelper;
import com.ibm.wsspi.application.handler.ApplicationInformation;

/**
 * Implementation of {@link NestedConfigHelper} that gets it's properties from a {@link ApplicationInformation} instance.
 */
public class ApplicationNestedConfigHelper implements NestedConfigHelper {

    private final ApplicationInformation<?> appInfo;

    /**
     * @param appInfo
     */
    public ApplicationNestedConfigHelper(ApplicationInformation<?> appInfo) {
        super();
        this.appInfo = appInfo;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ws.container.service.app.deploy.NestedConfigHelper#get(java.lang.String)
     */
    @Override
    public Object get(String propName) {
        return appInfo.getConfigProperty(propName);
    }

}
