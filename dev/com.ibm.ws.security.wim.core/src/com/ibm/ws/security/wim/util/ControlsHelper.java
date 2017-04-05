/************** Begin Copyright - Do not add comments here **************
 *  
 * IBM Confidential OCO Source Material
 * Virtual Member Manager (C) COPYRIGHT International Business Machines Corp. 2012
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * 
 * Change History:
 * 
 * Tag          Person   Defect/Feature      Comments
 * ----------   ------   --------------      --------------------------------------------------
 */
package com.ibm.ws.security.wim.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ibm.websphere.ras.annotation.Trivial;
import com.ibm.wsspi.security.wim.model.Control;
import com.ibm.wsspi.security.wim.model.Root;

/**
 * @author Ankit Jain
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
@Trivial
public class ControlsHelper
{
    /**
     * IBM Copyright string
     */
    static final String COPYRIGHT_NOTICE = com.ibm.websphere.security.wim.copyright.IBMCopyright.COPYRIGHT_NOTICE_SHORT_2012;

    public static Map <String, Control>getControlMap(Root root)
    {
        Map ctrlMap = new HashMap();
        List controls = root.getControls();
        if (controls != null) {
            for (int i = 0; i < controls.size(); i++) {
                Control control = (Control) controls.get(i);
                String type = control.getTypeName();
                if (ctrlMap.get(type) == null) {
                    ctrlMap.put(type, control);
                }
            }
        }
        return ctrlMap;
    }
 
}
