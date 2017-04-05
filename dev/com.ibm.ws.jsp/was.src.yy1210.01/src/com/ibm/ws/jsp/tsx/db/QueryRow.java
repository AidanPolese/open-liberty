// IBM Confidential OCO Source Material
// 5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997, 2002
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.ws.jsp.tsx.db;

import java.util.Hashtable;

import com.ibm.ws.jsp.JspCoreException;

public class QueryRow {
    Hashtable row = null;
    /**
     * This method was created in VisualAge.
     */
    public QueryRow() {}
    /**
     * This method was created in VisualAge.
     * @param colCount int
     */
    protected QueryRow(int colCount) {
        row = new Hashtable(colCount);
    }
    /**
     * This method was created in VisualAge.
     * @return java.lang.String
     * @param propertyName java.lang.String
     */
    public String getValue(String propertyName) throws JspCoreException {
        // use current row 
        // get the value from hashtable and give it. Return null if property
        // not found ?? should we throw an error here ??
        String val = (String) row.get(propertyName);
        /*      if(val == null)
              {// invalid attribute
                 throw new JasperException((JspConstants.InvalidAttrName)+propertyName);
              }
              else
              {*/
        return val;
        //}
    }
    /**
     * This method was created in VisualAge.
     * @param name java.lang.String
     * @param val java.lang.String
     */
    protected void put(String name, String val) {
        if (val == null) {
            row.put(name, "");
        }
        else {
            row.put(name, val);
        }
    }
}
