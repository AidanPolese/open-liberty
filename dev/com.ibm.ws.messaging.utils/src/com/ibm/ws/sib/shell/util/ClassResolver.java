/*
 
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * Copyright IBM Corp. 2012
 * 
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 
 * Change activity:
 *
 * Reason          Date        Origin   Description
 * --------------- ----------- -------- ----------------------------------------
 * 
 * ============================================================================
 */
package com.ibm.ws.sib.shell.util;

import java.net.URL;

/**
 * <p>This interface defines a Class Resolver. A Class Resolver is responsible
 *   for trying to find a class.
 * </p>
 *
 * <p>SIB build component: sib.shell</p>
 *
 * @author nottinga
 * @version 1.5
 * @since 1.0
 */
public interface ClassResolver
{
  /* ------------------------------------------------------------------------ */
  /* findClass method                                    
  /* ------------------------------------------------------------------------ */
  /**
   * This method is invoked to find a class using this resolver.
   * 
   * @param className the name of the class to find.
   * @return the class, or if none can be found null.
   */
  public Class findClass(String className);

  /* ------------------------------------------------------------------------ */
  /* findClass method                                    
  /* ------------------------------------------------------------------------ */
  /**
   * This method is invoked to find a class using this resolver.
   * 
   * @param className the name of the class to find.
   * &param suppressFFDC true if ClassNotFoundExceptions should not be written to FFDC
   * @return the class, or if none can be found null.
   */
  public Class findClass(String className, boolean suppressFFDC);


  /* ------------------------------------------------------------------------ */
  /* findResource method                                    
  /* ------------------------------------------------------------------------ */
  /**
   * This method is invoked to find a resource using this resolver.
   * 
   * @param resourceName The name of the resource to find.
   * @return a URL that can be used to read the resource.
   */
  public URL findResource(String resourceName);
  /* ------------------------------------------------------------------------ */
  /* getName method                                    
  /* ------------------------------------------------------------------------ */
  /**
   * This method should return a unique name that can be used in trace to 
   * indicate the identity of this resolver.
   * 
   * @return The name of the resolver.
   */
  public String getName();
}
