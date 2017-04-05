package com.ibm.tx.jta.impl;
/* ***************************************************************************************************** */
/* COMPONENT_NAME: WAS.transactions                                                                      */
/*                                                                                                       */
/*  ORIGINS: 27                                                                                          */
/*                                                                                                       */
/* IBM Confidential OCO Source Material                                                                  */
/* 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2002, 2009 */
/* The source code for this program is not published or otherwise divested                               */
/* of its trade secrets, irrespective of what has been deposited with the                                */
/* U.S. Copyright Office.                                                                                */
/*                                                                                                       */
/* %Z% %I% %W% %G% %U% [%H% %T%]                                                                         */
/*                                                                                                       */
/*  DESCRIPTION:                                                                                         */
/*                                                                                                       */
/*  Change History:                                                                                      */
/*                                                                                                       */
/*  Date      Programmer    Defect   Description                                                         */
/*  --------  ----------    ------   -----------                                                         */
/*  27/11/03  johawkes      178502   Start an RA during XA recovery                                      */
/*  05/12/03  johawkes      184903   Refactor PartnerLogTable                                            */
/*  13/04/04  beavenj       LIDB1578.1 Initial supprort for ha-recovery                                  */
/*  14/06/04  johawkes      209345   Remove unused code                                                  */
/*  26/09/04  hursdlg       234516   Pass FailureScopeController to PartnerLogData                       */
/*  14/06/05  hursdlg       283253   Componentization changes for recovery                               */
/*  06/06/05  johawkes      443467   Repackaging                                                         */
/*  06/08/07  johawkes      451213.1 Moved into JTM                                                      */
/*  02/06/09  mallam        596067   package move                                                        */
/* ***************************************************************************************************** */

//
// JCARecoveryData is a specialization of PartnerLogData
//
// The log data object is an JCARecoveryWrapper and this class provides
// methods to support the use of this particular data type.
//
public final class JCARecoveryData extends PartnerLogData
{
    //
    // Ctor when called from registration of a JCA provider
    //
    public JCARecoveryData(FailureScopeController failureScopeController,JCARecoveryWrapper logData)
    {
        super(logData,failureScopeController);
        
        _serializedLogData = logData.serialize();
        _sectionId = TransactionImpl.JCAPROVIDER_SECTION;
    }

    //
    // Ctor when called from recovery of an JCA provider from the log
    //
    public JCARecoveryData(RecoveryManager recoveryManager,byte[] serializedLogData, long id)
    {
        super(serializedLogData, new JCARecoveryWrapper(serializedLogData), id, recoveryManager.getFailureScopeController().getPartnerLog());
        _recovered = true;
    }

    public JCARecoveryWrapper getWrapper()
    {
        return (JCARecoveryWrapper)_logData;
    }
}