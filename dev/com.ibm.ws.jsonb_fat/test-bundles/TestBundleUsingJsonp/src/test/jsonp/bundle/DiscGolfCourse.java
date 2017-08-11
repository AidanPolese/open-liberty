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
package test.jsonp.bundle;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple Java object for an OSGi service component to marshall/unmarshall to/from JSON via JSON-B.
 */
public class DiscGolfCourse {
    public String name;
    public String street;
    public String city;
    public String state;
    public Integer zipCode;
    public List<Basket> baskets = new ArrayList<Basket>();

    @Override
    public String toString() {
        return name + " @ " + street + ' ' + city + ", " + state + ' ' + zipCode + ' ' + baskets;
    }
}
