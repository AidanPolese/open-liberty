/*
*
* OCO Source Materials
*
* WLP Copyright IBM Corp. 2016
*
* The source code for this program is not published or otherwise divested
* of its trade secrets, irrespective of what has been deposited with the
* U.S. Copyright Office.
*/
package com.ibm.ws.anno.test.data.sub;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// @formatter:off
/**
 * Self referential annotation definition. Modeled after the 81538,L6Q,000
 * problem class 'GwtCompatible':
 *
 * public annotation com.google.common.annotations.GwtCompatible
 * extends java.lang.Object
 * implements java.lang.annotation.Annotation
 * Version [ 50 ] ( 0x32 ) ( J2SE 6.0 )
 *
 * @java.lang.annotation.Retention
 *                                 [ value ] [ CLASS ] (enum)
 * @java.lang.annotation.Target
 *                              [ value ] [ 2 elements ] (array)
 *                              [ 0 ] [ TYPE ] (enum)
 *                              [ 1 ] [ METHOD ] (enum)
 * @java.lang.annotation.Documented
 * @com.google.common.annotations.GwtCompatible
 *                                              Is Visible: [ false ]
 *                                              [M] serializable : [ ()Z ] ( boolean )
 *                                              Default: [ false ] (primitive)
 *                                              [M] emulated : [ ()Z ] ( boolean )
 *                                              Default: [ false ] (primitive)
 */
@Retention(RetentionPolicy.CLASS)
@Target({ ElementType.TYPE, ElementType.METHOD })
@SelfAnno(false)
public @interface SelfAnno {
    boolean value();
}
