/************** Begin Copyright - Do not add comments here **************
 *
 *  
 * IBM Confidential OCO Source Material
 * 5724-H88, 5724-J08, 5724-I63, 5655-W65, 5724-H89, 5722-WE2   Copyright IBM Corp., 2012, 2013
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U. S. Copyright Office.
 * 
 * Change History:
 * 
 * Tag       Person      Defect/Feature      Comments
 * -------   ------      --------------      --------------------------------------------------
 */
package com.ibm.ws.security.wim.adapter.ldap;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;

import com.ibm.ws.ffdc.annotation.FFDCIgnore;

public class CachedNamingEnumeration implements NamingEnumeration<SearchResult>, Cloneable {

    /**
     * IBM Copyright string.
     */
    static final String COPYRIGHT_NOTICE = com.ibm.websphere.security.wim.copyright.IBMCopyright.COPYRIGHT_NOTICE_SHORT_2012;

    private List<SearchResult> iList = null;
    transient private Iterator<SearchResult> m_Enum = null;

    public void close() throws NamingException {}

    CachedNamingEnumeration() {}

    CachedNamingEnumeration(List<SearchResult> list)
    {
        iList = list;
    }

	@FFDCIgnore (CloneNotSupportedException.class)
    public Object clone() {
		try {
			super.clone();
		} catch (CloneNotSupportedException e) {
		}

		List<SearchResult> newList = null;
		if (iList != null) {
			newList = new Vector<SearchResult>(iList.size());
			for (SearchResult element : iList) {
				SearchResult result = (SearchResult) element;
				Attributes attrs = (Attributes) result.getAttributes().clone();
				SearchResult clonedResult = new SearchResult(result.getName(), null, null, attrs);
				newList.add(clonedResult);
			}
		}
		return new CachedNamingEnumeration(newList);
	}

    public boolean hasMore() throws NamingException
    {
        if (iList == null) {
            iList = new Vector<SearchResult>(0);
        }
        if (m_Enum == null) {
            m_Enum = iList.iterator();
        }
        return m_Enum.hasNext();
    }

    public SearchResult next() throws NamingException
    {
        if (iList == null) {
            iList = new Vector<SearchResult>(0);
        }
        if (m_Enum == null) {
            m_Enum = iList.iterator();
        }
        return m_Enum.next();
    }

    @FFDCIgnore (NamingException.class)
    public boolean hasMoreElements()
    {
        try {
            return hasMore();
        }
        catch (NamingException e) {
        	e.getMessage();
            return false;
        }
    }

    @FFDCIgnore (NamingException.class)
    public SearchResult nextElement()
    {
        try {
            return next();
        }
        catch (NamingException e) {
        	e.getMessage();
            return null;
        }
    }

    public void add(SearchResult elem)
    {
        if (iList == null) {
            iList = new Vector<SearchResult>(0);
        }
        iList.add(elem);
    }

    public String toString()
    {
        if (iList != null) {
            return iList.toString();
        }
        else {
            return "";
        }

    }
}
