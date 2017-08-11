/*
 * @start_prolog@
 * ============================================================================
 * IBM Confidential OCO Source Materials
 *
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 * @end_prolog@
 * 
 * Change activity:
 * 
 * Issue       Date        Name        Description
 * ----------- ----------- --------    ------------------------------------
 * 22101       16-Aug-2011 emijiang@uk Initial version
 * 26155       18-Oct-2011 emijiang@uk List all binary incompatibility changes
 */
package com.ibm.aries.buildtasks.semantic.versioning;

public class BinaryCompatibilityStatus
{
    private final boolean compatible;
    private final String reason;

    public BinaryCompatibilityStatus(boolean compatible, String reason) {
        this.compatible = compatible;
        this.reason = reason;
    }

    public boolean isCompatible()
    {
        return compatible;
    }

    public String getReason()
    {
        return reason;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (compatible ? 1231 : 1237);
        result = prime * result + ((reason == null) ? 0 : reason.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BinaryCompatibilityStatus other = (BinaryCompatibilityStatus) obj;
        if (compatible != other.compatible)
            return false;
        if (reason == null) {
            if (other.reason != null)
                return false;
        } else if (!reason.equals(other.reason))
            return false;
        return true;
    }

}
