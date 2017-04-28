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
package com.ibm.wsspi.rest.handler.helper;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

import com.ibm.wsspi.rest.handler.RESTRequest;
import com.ibm.wsspi.rest.handler.RESTResponse;

/**
 * This helper service performs the default authorization on the given user.
 * 
 * @ibm-spi
 */
@Component(service = { DefaultAuthorizationHelper.class }, configurationPolicy = ConfigurationPolicy.IGNORE, immediate = true, property = { "service.vendor=IBM" })
public class DefaultAuthorizationHelper {

    public boolean checkAdministratorRole(RESTRequest request, RESTResponse response) throws IOException {
        if (request.isUserInRole("Administrator")) {
            return true;
        }

        //Not in admin role, so built error msg
        //TODO: Translate msg
        response.sendError(403, "Administrator role needed.");

        return false;
    }
}
