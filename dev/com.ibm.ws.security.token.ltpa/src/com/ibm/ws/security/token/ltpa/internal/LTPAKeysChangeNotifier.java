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
package com.ibm.ws.security.token.ltpa.internal;

import com.ibm.ws.security.notifications.BaseSecurityChangeNotifier;
import com.ibm.ws.security.notifications.SecurityChangeNotifier;

/**
 * Service to send notifications to registered SecurityChangeListener objects
 * when the LTPA keys are changed.
 */
public class LTPAKeysChangeNotifier extends BaseSecurityChangeNotifier implements SecurityChangeNotifier {

}
