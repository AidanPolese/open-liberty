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

import java.util.Date;

import javax.json.bind.annotation.JsonbTypeDeserializer;

/**
 * Application class that can be marshalled/unmarshalled to/from JSON.
 */
public class Scrum {
    public String squadName;
    public Date start;
    @JsonbTypeDeserializer(LocationDeserializer.class) // JsonbDeserializer disambiguates which Location subclass to use
    public Location location;

    @Override
    public String toString() {
        return "Scrum for " + squadName + ' ' + start + " @" + location;
    }
}
