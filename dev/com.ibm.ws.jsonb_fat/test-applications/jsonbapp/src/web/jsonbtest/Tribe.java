/*
* IBM Confidential
*
* OCO Source Materials
*
* WLP Copyright IBM Corp. 2017
*
* The source code for this program is not published or otherwise divested
* of its trade secrets, irrespective of what has been deposited with the
* U.S. Copyright Office.
*/
package web.jsonbtest;

import java.util.ArrayList;
import java.util.List;

/**
 * Application class that can be marshalled/unmarshalled to/from JSON.
 */
public class Tribe {
    public String name;
    public List<Squad> squads = new ArrayList<Squad>();

    @Override
    public String toString() {
        return name + ' ' + squads;
    }
}
