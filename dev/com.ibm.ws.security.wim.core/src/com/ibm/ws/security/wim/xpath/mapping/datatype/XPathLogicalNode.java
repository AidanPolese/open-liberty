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

public interface XPathLogicalNode extends XPathNode {

    static final String OP_AND = "and";
    static final String OP_OR = "or";

    void setOperator(String operator);

    String getOperator();

    void setLeftChild(Object leftChild);

    Object getLeftChild();

    void setRightChild(Object rightChild);

    Object getRightChild();

    void setPropertyLocation(boolean isInRepos);

    boolean isPropertyInRepository();
}
