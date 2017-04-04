// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.

package com.ibm.ws.webcontainer.util;

import java.util.Enumeration;

/**
 * Singleton empty enumeration.
 */
@SuppressWarnings("unchecked")
public class EmptyEnumeration implements java.util.Enumeration, java.io.Serializable{
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 3546642126931963953L;
	private static Enumeration _instance;
/**
 * EmptyEnumeration constructor comment.
 */
private EmptyEnumeration() {
}
/**
 * This method was created in VisualAge.
 * @return com.ibm.servlet.prototype.util.EmptyEnumeration
 */
private synchronized static void createInstance() {
	if(_instance == null){
		_instance = new EmptyEnumeration();
	};
}
/**
 * hasMoreElements method comment.
 */
public boolean hasMoreElements() {
	return false;
}
/**
 * This method was created in VisualAge.
 * @return Enumeration
 */
public static Enumeration instance() {
	if(_instance == null){
		createInstance();
	}
	return _instance;
}
/**
 * nextElement method comment.
 */
public Object nextElement() {
	return null;
}
}
