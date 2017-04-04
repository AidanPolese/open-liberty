//IBM Confidential OCO Source Material
//5724-I63, 5724-H88, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997-2010
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.
//

//Code added as part of LIDB 2283-4
//
//  CHANGE HISTORY
//  Defect         Date         Modified By         Description
//--------------------------------------------------------------------------------------
//  PK17266    	  01/04/2006  	mmolden         	Access to HashMap Synchronized for operations that alter the state of Map.
//  PK79550       03/27/2008    jebergma            CLAUSETABLE CREATED with scalable=false
//  PM06111       02/08/2010    mmulholl            Add methods for a scalable ClauseTable to support use of String keys 
package com.ibm.ws.webcontainer.util;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClauseTable {
    private final Map<String, ClauseNode> hashTable = new ConcurrentHashMap<String, ClauseNode>(16, .90f, 16);; //PK17266 changed variable name for clarity

    public ClauseTable() {}

    // PM06111 Start: Add methods to work with String kets 
    public ClauseNode get(String key) {
        return hashTable.get(key);
    }

    public void remove(String key) {
        hashTable.remove(key);
    }

    public void add(String key, ClauseNode item) {
        hashTable.put(key, item);
    }

    // PM06111 Start: Add methods to work with String kets 

    public ArrayList<ClauseNode> getList() {
        return new ArrayList<ClauseNode>(hashTable.values());
    }

    public int size() {
        return hashTable.size();
    }

}