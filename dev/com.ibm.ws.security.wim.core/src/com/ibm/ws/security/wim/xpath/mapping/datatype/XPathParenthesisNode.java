/************** Begin Copyright - Do not add comments here **************
 *
 *
 * IBM Confidential OCO Source Material
 * Virtual Member Manager (C) COPYRIGHT International Business Machines Corp. 2012
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 */
package com.ibm.ws.security.wim.xpath.mapping.datatype;

public interface XPathParenthesisNode extends XPathNode {

    void setChild(Object value);

    Object getChild();

    void setPropertyLocation(boolean isInRepos);

    boolean isPropertyInRepository();
}
