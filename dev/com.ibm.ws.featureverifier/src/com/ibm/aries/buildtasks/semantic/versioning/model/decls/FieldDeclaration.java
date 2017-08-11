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
 * 18925       03-Aug-2011 emijiang@uk Created
 * 25215       07-Oct-2011 emijiang@uk Minor tidy up
 * 26949       03-Nov-2011 emijiang@uk Changed the field equal criteria due to field hidden fact
 * 26539       07-Nov-2011 emijiang@uk Changing serial version uid should be a breaking change for a serializable
 */
package com.ibm.aries.buildtasks.semantic.versioning.model.decls;

public class FieldDeclaration extends GenericDeclaration
{
    private final String desc;
    private final Object value;

    public FieldDeclaration(int access, String name, String desc, String signature, Object value) {
        super(access, name, signature);
        this.desc = desc;
        this.value = value;
        // System.out.println("   - Field decl of "+name+" access "+access+" signature "+signature);
    }

    public String toXML() {
        StringBuilder sb = new StringBuilder();
        sb.append("         <fielddecl>\n");
        sb.append("           <name>" + escapeXML(getName()) + "</name>\n");
        sb.append("           <access>" + getRawAccess() + "</access>\n");
        sb.append("           <desc>" + escapeXML(getDesc()) + "</desc>\n");
        if (getSignature() != null) {
            sb.append("           <signature>" + escapeXML(getSignature()) + "</signature>\n");
        }
        //value.. ouch.. we'll encode the string value.. currently the getter is only 
        //        used to obtain the serialUUID (long), so we can rebuild that from string.
        if (getValue() != null) {
            //value can contain some real nasty chars.. so we'll render it as hex ;p 

            byte[] a = getValue().toString().getBytes();
            StringBuilder sb2 = new StringBuilder(a.length * 2);
            for (byte b : a)
                sb2.append(String.format("%02x", b & 0xff));

            sb.append("           <value>" + escapeXML(sb2.toString()) + "</value>\n");
        }
        sb.append("         </fielddecl>\n");
        return sb.toString();
    }

    public String getDesc()
    {
        return desc;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        if (getClass() != obj.getClass())
            return false;
        FieldDeclaration other = (FieldDeclaration) obj;
        if (getName() == null) {
            if (other.getName() != null)
                return false;
        } else if (!getName().equals(other.getName()))
            return false;
        return true;
    }

}
