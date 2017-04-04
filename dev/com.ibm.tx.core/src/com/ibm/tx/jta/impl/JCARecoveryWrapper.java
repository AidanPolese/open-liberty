package com.ibm.tx.jta.impl;
/* ********************************************************************************* */
/* COMPONENT_NAME: WAS.transactions                                                  */
/*                                                                                   */
/*  ORIGINS: 27                                                                      */
/*                                                                                   */
/* IBM Confidential OCO Source Material                                              */
/* 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2002, 2009 */
/* The source code for this program is not published or otherwise divested           */
/* of its trade secrets, irrespective of what has been deposited with the            */
/* U.S. Copyright Office.                                                            */
/*                                                                                   */
/* %Z% %I% %W% %G% %U% [%H% %T%]                                                     */
/*                                                                                   */
/*  DESCRIPTION:                                                                     */
/*                                                                                   */
/*  Change History:                                                                  */
/*                                                                                   */
/*  Date      Programmer    Defect   Description                                     */
/*  --------  ----------    ------   -----------                                     */
/*  27/11/03  johawkes      178502    Start an RA during XA recovery                 */
/*  05/12/03  johawkes      184903    Refactor PartnerLogTable                       */
/*  13/04/04  beavenj       LIDB1578.1 Initial supprort for ha-recovery              */     
/*  14/06/04  johawkes      209345    Remove unused code                             */
/*  06/06/07  johawkes      443467    Repackaging                                    */
/*  06/08/07  johawkes      451213.1  Moved into JTM                                 */
/*  02/06/09  mallam        596067    package move                                   */
/* ********************************************************************************* */

import com.ibm.tx.jta.impl.PartnerLogData;
import com.ibm.tx.jta.impl.RecoveryWrapper;
import com.ibm.tx.jta.impl.FailureScopeController;

public final class JCARecoveryWrapper implements RecoveryWrapper
{
    private final String _providerId;
    
    public JCARecoveryWrapper(String providerId)
    {
        _providerId = providerId;
    }

    // recovery constructor
    JCARecoveryWrapper(byte[] logData)
    {
        _providerId = new String(logData);
    }

    public String getProviderId()
    {
        return _providerId;
    }

    public String toString()
    {
        return _providerId;
    }

    public boolean isSameAs(RecoveryWrapper rw)
    {
        if (rw instanceof JCARecoveryWrapper)
        {
            if (_providerId != null)
            {
                return _providerId.equals(((JCARecoveryWrapper)rw).getProviderId());
            }

            return ((JCARecoveryWrapper)rw).getProviderId() == null;
        }

        return false;
    }

    public int hashCode()
    {
        if(_providerId == null)
        {
            return 0;
        }

        return _providerId.hashCode();
    }

    /**
     * @return
     */
    public byte[] serialize()
    {
        if(_providerId == null)
        {
            return new byte[0];
        }

        return _providerId.getBytes();
    }
    
    public PartnerLogData container(FailureScopeController failureScopeController)
    {
        return new JCARecoveryData(failureScopeController,this);
    }
}