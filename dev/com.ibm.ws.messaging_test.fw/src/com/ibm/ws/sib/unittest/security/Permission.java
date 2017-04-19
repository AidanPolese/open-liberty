/*
 * COMPONENT_NAME: sib.unittest.security
 *
 *  ORIGINS: 27
 *
 * 
 * 
 * ===========================================================================
 * IBM Confidential OCO Source Material
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ===========================================================================
 * 
 *
 * 
 *
 * Change activity:
 *
 * Reason          Date        Origin   Description
 * --------------- ----------- -------- ----------------------------------------
 * d461380         22-Aug-2007 nottinga Initial Code Drop
 */
package com.ibm.ws.sib.unittest.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>This annotation is used to specify the permissions associated with a 
 *   method or a class. It is used at runtime and can be applied to classes or
 *   methods.
 * </p>
 *
 * <p>SIB build component: sib.unittest.security</p>
 *
 * @author nottinga
 * @version 1.1
 * @since 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Permission
{
  /**
   * A String array of permission specifications. Each String takes the format 
   * from the java.policy file which is:
   * 
   *   <Java Permission class name> [<resource>] [<action>];
   * 
   * If resource or action contains spaces then they must be enclosed in ".
   */
  public String[] value();
}