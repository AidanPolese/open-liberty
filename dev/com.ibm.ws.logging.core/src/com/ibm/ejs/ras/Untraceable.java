// @(#) 1.3 SERV1/ws/code/ras/src/com/ibm/ejs/ras/Untraceable.java, WAS.ras, WASX.SERV1, uu0827.36 4/17/06 11:20:50 [7/9/08 14:47:27]
/*
 * COMPONENT_NAME: WAS.ras
 *
 * ORIGINS: 27         (used for IBM originated files)
 *
 * IBM Confidential OCO Source Material
 * 5724-I63, 5724-H88, 5655-N01, 5733-W61 (C) COPYRIGHT International Business Machines Corp. 2000,2006
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * DESCRIPTION:
 *
 * Change History:
 *
 * Reason    Version  Date        Userid    Description
 * ----------------------------------------------------------------------------
 * d136906     5.0    06-26-2002  stopyro   Part Created.
 * d360116     6.1    04-05-2006  tomasz    Added ibm api tag to the javadoc
 * d360116.2   6.1    04-17-2006  tomasz    Removed ibm api tag from the javadoc
 */

package com.ibm.ejs.ras;

/**
 * When an object is passed to Ras as a parameter to be traced or to substitute
 * into a message, the default behavior is to call toString() on the object. For
 * the cases where a non-default behavior is preferred, the Traceable interface
 * (and the toTraceString() method) can be implemented by the Object.
 * 
 * However, there are some objects for which we cannot call toString and the
 * objects are not allowed to add new non-J2EE defined methods to the Object. To
 * handle such cases, the Untraceable interface is defined.
 * 
 * A class should implement this interface to inform Ras not to call toString.
 * When an object that implements this interface is passed to Ras, Ras will
 * simply insert the objects classname into the stream.
 */
public interface Untraceable {

}
