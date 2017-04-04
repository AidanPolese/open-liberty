/************** Begin Copyright - Do not add comments here **************
 *
 *  
 * IBM Confidential OCO Source Material
 * Virtual Member Manager (C) COPYRIGHT International Business Machines Corp. 2012
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * 
 * Change History:
 * 
 * Tag          Person   Defect/Feature      Comments
 * ----------   ------   --------------      --------------------------------------------------
 */
package com.ibm.ws.security.wim.xpath.mapping.datatype;

import java.util.HashMap;
import java.util.Iterator;

import com.ibm.websphere.ras.annotation.Trivial;

@Trivial
public class LogicalNode implements XPathLogicalNode {

    static final String COPYRIGHT_NOTICE = com.ibm.websphere.security.wim.copyright.IBMCopyright.COPYRIGHT_NOTICE_LONG_2012;

    private String operator = null;
    private Object leftChild = null;
    private Object rightChild = null;
    boolean inRepos = true;

    public void setOperator(String operator)
    {
        this.operator = operator;
    }

    public String getOperator()
    {
        return operator;
    }

    public void setLeftChild(Object leftChild)
    {
        this.leftChild = leftChild;
    }

    public Object getLeftChild()
    {
        return leftChild;
    }

    public void setRightChild(Object rightChild)
    {
        this.rightChild = rightChild;
    }

    public Object getRightChild()
    {
        return rightChild;
    }

    public short getNodeType()
    {
        return XPathNode.NODE_LOGICAL;
    }

    @SuppressWarnings("unchecked")
    public Iterator getPropertyNodes(HashMap attNodeMap)
    {
        HashMap nodeMap = attNodeMap;

        if (nodeMap == null) {
            nodeMap = new HashMap();
        }
        ((XPathNode) leftChild).getPropertyNodes(nodeMap);
        return ((XPathNode) rightChild).getPropertyNodes(nodeMap);
    }

    public void setPropertyLocation(boolean inRepos)
    {
        this.inRepos = inRepos;
    }

    public boolean isPropertyInRepository()
    {
        return inRepos;
    }

    public String toString()
    {
        StringBuffer s = new StringBuffer();
        s = s.append(leftChild.toString() + " " + operator + " " + rightChild.toString());
        return s.toString();
    }
}
