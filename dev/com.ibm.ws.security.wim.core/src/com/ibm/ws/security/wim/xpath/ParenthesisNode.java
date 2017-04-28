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
package com.ibm.ws.security.wim.xpath;

import java.util.HashMap;
import java.util.Iterator;

import com.ibm.websphere.ras.annotation.Trivial;
import com.ibm.ws.security.wim.xpath.mapping.datatype.XPathNode;
import com.ibm.ws.security.wim.xpath.mapping.datatype.XPathParenthesisNode;

@Trivial
public class ParenthesisNode implements XPathParenthesisNode {

    private Object child = null;
    boolean inRepos = true;

    @Override
    public void setChild(Object arg0) {
        child = arg0;
    }

    @Override
    public Object getChild() {
        return child;
    }

    @Override
    public short getNodeType() {
        return XPathNode.NODE_PARENTHESIS;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Iterator getPropertyNodes(HashMap propNodeMap) {
        HashMap nodeMap = propNodeMap;

        if (nodeMap == null) {
            nodeMap = new HashMap();
        }
        return ((XPathNode) child).getPropertyNodes(nodeMap);
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
        s = s.append("(" + child.toString() + ")");
        return s.toString();
    }
}
