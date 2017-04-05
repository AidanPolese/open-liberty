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
package org.apache.bval.jsr303;

/**
 * This is a no-op extender so that we maintain compatibility with bean validation
 * 1.0 apps that explicitly listed the apache provider class in the <default-provider>
 * element in validation.xml.
 * 
 * <p>
 * Apache Bean Validation changed the package name in their 1.1 implementation,
 * so we provide this provider with the original package name to allow 1.0 applications
 * to move up without needing to change their application.
 */
public class ApacheValidationProvider extends org.apache.bval.jsr.ApacheValidationProvider {

}
