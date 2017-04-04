package com.ibm.ws.objectManager.utils;

/*
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * 5724-H88, 5724-J08, 5724-I63, 5655-W65, 5724-H89, 5722-WE2   Copyright IBM Corp., 2013
 * 
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 *
 *
 * Change activity:
 *
 *  Reason           Date    Origin     Description
 * --------------- -------- ---------- ----------------------------------------
 *   251161        07/04/05   gareth    Add ObjectManager code to CMVC
 * ============================================================================
 */

// default (English language, United States)
public class TraceTemplates
                extends java.util.ResourceBundle {
    final String templateValues[][] = {
                                       { "com.ibm.ws.objectManager.ObjectManagerState.<init>.entry", "logFileName={0}\nobjectManager={1}\nlogFileType={2}" },
                                       { "com.ibm.ws.objectManager.ObjectManagerState.performColdStart.entry", "hello logFileName={0}\nobjectManager={1}\nlogFileType={2}" },
    };
    java.util.HashMap templates = new java.util.HashMap();

    {
        for (int i = 0; i < templateValues.length; i++) {
            templates.put(templateValues[i][0], templateValues[i][1]);
        }
    }

    public Object handleGetObject(String key) {
        return templates.get(key);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.ResourceBundle#getKeys()
     */
    public java.util.Enumeration getKeys()
    {
        // TODO Auto-generated method stub
        // return templates.keySet().iterator();
        return null;
    }
}
