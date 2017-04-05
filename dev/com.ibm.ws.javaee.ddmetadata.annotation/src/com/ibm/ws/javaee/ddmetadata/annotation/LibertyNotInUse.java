/*
* IBM Confidential
*
* OCO Source Materials
*
* WLP Copyright IBM Corp. 2016
*
* The source code for this program is not published or otherwise divested
* of its trade secrets, irrespective of what has been deposited with the
* U.S. Copyright Office.
*/
package com.ibm.ws.javaee.ddmetadata.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Target;

@Target({ TYPE, METHOD, FIELD })
/**
 * Indicates that a type or method is not used by the Liberty runtime.
 *
 * Note that in some cases code will reference these types or methods, but the values
 * make no difference to the runtime so they should not be configurable.
 */
public @interface LibertyNotInUse {

}
