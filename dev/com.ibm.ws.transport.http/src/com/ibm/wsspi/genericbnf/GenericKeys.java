// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70. (C) COPYRIGHT International Business Machines Corp. 2004, 2009
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.wsspi.genericbnf;

import java.io.UnsupportedEncodingException;

/**
 * Generic abstract class for the various enumerated classes in the
 * Channel. Each basic key has a String name and an int ordinal to
 * match. The extended classes are responsible for maintaining the
 * static list of all the keys in the enum, along with anything extra
 * they may require.
 * 
 * @ibm-private-in-use
 */
public abstract class GenericKeys implements Comparable<GenericKeys> {

    /** String version of the key's name */
    protected String name = null;
    /** byte[] version of the key's name */
    protected byte[] byteArray = null;
    /** Ordinal associated with the key */
    protected int ordinal = -1;
    /** Hashcode for this object */
    protected int hashcode;

    /**
     * Constructor is limited to the subclasses.
     * 
     * @param inputName
     * @param inputOrdinal
     */
    protected GenericKeys(String inputName, int inputOrdinal) {
        this.name = inputName;
        try {
            this.byteArray = inputName.getBytes(HeaderStorage.ENGLISH_CHARSET);
        } catch (UnsupportedEncodingException uee) {
            // no FFDC required
            // Invalid key name
            throw new IllegalArgumentException("Unsupported non-English name: " + inputName);
        }
        this.ordinal = inputOrdinal;
        this.hashcode = inputOrdinal + inputName.hashCode();
    }

    /**
     * Query the name of this key as a byte[].
     * 
     * @return byte[]
     */
    final public byte[] getByteArray() {
        return this.byteArray;
    }

    /**
     * Query the ordinal number for this header.
     * 
     * @return int
     */
    final public int getOrdinal() {
        return this.ordinal;
    }

    /**
     * Query the name for this key as a String.
     * 
     * @return String
     */
    public String getName() {
        if (null == this.name && null != this.byteArray) {
            try {
                this.name = new String(this.byteArray, HeaderStorage.ENGLISH_CHARSET);
            } catch (UnsupportedEncodingException uee) {
                // no FFDC required
                // Invalid key name
                throw new IllegalArgumentException("Unsupported non-English name: " + new String(this.byteArray));
            }
        }
        return this.name;
    }

    /**
     * For debugging purposes, convert this object to a String.
     * 
     * @return String
     */
    public String toString() {
        return "Key: " + getName() + " Ordinal: " + getOrdinal();
    }

    /**
     * Compare this key against the given value. Returns a negative integer,
     * zero, or a positive integer as this object is less than, equal to, or
     * greater than the specified GenericKeys object.
     * 
     * @param inKey
     * @return int
     */
    public int compareTo(GenericKeys inKey) {
        return (null == inKey) ? -1 : (getOrdinal() - inKey.getOrdinal());
    }

    /**
     * Check whether this object equals another.
     * 
     * @param val
     * @return boolean
     */
    public boolean equals(Object val) {

        if (this == val) {
            return true;
        }
        // instanceof handles class types and null input
        if (!(val instanceof GenericKeys)) {
            return false;
        }
        return (hashCode() == ((GenericKeys) val).hashCode());
    }

    /**
     * Allow an equality check against another enum object.
     * 
     * @param val
     * @return boolean (true if ordinals match)
     */
    public boolean equals(GenericKeys val) {
        return (null == val) ? false : (hashCode() == val.hashCode());
    }

    /**
     * Hash code of this object.
     * 
     * @return int
     */
    public int hashCode() {
        return this.hashcode;
    }

}
