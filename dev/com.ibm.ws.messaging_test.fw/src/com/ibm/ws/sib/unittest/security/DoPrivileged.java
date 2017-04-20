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
 * <p>This annotation is set on a method call and read at runtime. If it is 
 *   applied to a method then the security manager treats that method as if
 *   the content of the method were wrapped in a doPriviledged call. In short
 *   it stop searching down the stack.</p>
 *
 * <p>SIB build component: sib.unittest.security</p>
 *
 * @author nottinga
 * @version 1.1
 * @since 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DoPrivileged
{
}