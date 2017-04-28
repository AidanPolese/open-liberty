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

import java.util.HashMap;
import java.util.Iterator;

import com.ibm.websphere.ras.annotation.Trivial;

@Trivial
public class LogicalNode implements XPathLogicalNode {

    private String operator = null;
    private Object leftChild = null;
    private Object rightChild = null;
    boolean inRepos = true;

    @Override
    public void setOperator(String operator) {
        this.operator = operator;
    }

    @Override
    public String getOperator() {
        return operator;
    }

    @Override
    public void setLeftChild(Object leftChild) {
        this.leftChild = leftChild;
    }

    @Override
    public Object getLeftChild() {
        return leftChild;
    }

    @Override
    public void setRightChild(Object rightChild) {
        this.rightChild = rightChild;
    }

    @Override
    public Object getRightChild() {
        return rightChild;
    }

    @Override
    public short getNodeType() {
        return XPathNode.NODE_LOGICAL;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Iterator getPropertyNodes(HashMap attNodeMap) {
        HashMap nodeMap = attNodeMap;

        if (nodeMap == null) {
            nodeMap = new HashMap();
        }
        ((XPathNode) leftChild).getPropertyNodes(nodeMap);
        return ((XPathNode) rightChild).getPropertyNodes(nodeMap);
    }

    @Override
    public void setPropertyLocation(boolean inRepos) {
        this.inRepos = inRepos;
    }

    @Override
    public boolean isPropertyInRepository() {
        return inRepos;
    }

    @Override
    public String toString() {
        StringBuffer s = new StringBuffer();
        s = s.append(leftChild.toString() + " " + operator + " " + rightChild.toString());
        return s.toString();
    }
}
