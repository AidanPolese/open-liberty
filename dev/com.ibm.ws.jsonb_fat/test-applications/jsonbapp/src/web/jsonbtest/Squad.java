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

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

/**
 * Application class that can be marshalled/unmarshalled to/from JSON.
 */
public class Squad {
    private String name;
    private Pod pod;
    private byte size;
    private float storyPointsPerIteration;

    @JsonbCreator
    public Squad(@JsonbProperty("name") String n,
                 @JsonbProperty("size") byte s,
                 @JsonbProperty("pod") Pod p,
                 @JsonbProperty("velocity") float v) {
        name = n;
        size = s;
        pod = p;
        storyPointsPerIteration = v;
    }

    @Override
    public String toString() {
        return name + " [" + size + "] velocity " + storyPointsPerIteration + " @ " + pod;
    }

    public String getName() {
        return name;
    }

    public Pod getPod() {
        return pod;
    }

    public byte getSize() {
        return size;
    }

    public float getVelocity() {
        return storyPointsPerIteration;
    }
}
