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
package org.apache.bval.jsr303.resolver;

/**
 * This is a no-op extender so that we maintain compatibility with bean validation
 * 1.0 apps that explicitly listed this apache traversable resolver class
 * in the <traversable-resolver> element in validation.xml.
 * 
 * <p>
 * Apache Bean Validation changed the package name in their 1.1 implementation,
 * so we provide this class with the original package name to allow 1.0 applications
 * to move up without needing to change their application.
 */
public class SimpleTraversableResolver extends org.apache.bval.jsr.resolver.SimpleTraversableResolver {

}
