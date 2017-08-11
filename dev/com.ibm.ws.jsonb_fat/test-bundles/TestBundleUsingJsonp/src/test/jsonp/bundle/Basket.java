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

/**
 * Simple Java object for an OSGi service component to marshall/unmarshall to/from JSON via JSON-B.
 */
public class Basket {
    public static enum Direction {
        N, NE, E, SE, S, SW, W, NW
    }

    public static enum Tee {
        CONCRETE, DIRT
    }

    public Tee tee;
    public int par;
    public int length;
    public Direction direction;
    public String terrain;

    public Basket() {}

    public Basket(Tee tee, int par, int length, Direction direction, String terrain) {
        this.tee = tee;
        this.par = par;
        this.length = length;
        this.direction = direction;
        this.terrain = terrain;
    }

    @Override
    public String toString() {
        return tee + " par " + par + ' ' + length + "ft " + direction + ' ' + terrain;
    }
}
