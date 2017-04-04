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
package com.ibm.ws.security.jaas.common;

import com.ibm.ws.security.notifications.BaseSecurityChangeNotifier;
import com.ibm.ws.security.notifications.SecurityChangeNotifier;

/**
 * Service to send notifications to registered SecurityChangeListener objects
 * when the JAAS configuration is modified.
 */
public class JAASChangeNotifier extends BaseSecurityChangeNotifier implements SecurityChangeNotifier {

}
