package com.ibm.tx.jta;
//
// COMPONENT_NAME: WAS.transactions
//
// ORIGINS: 27
//
// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2007
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// %Z% %I% %W% %G% %U% [%H% %T%]
//
// DESCRIPTION:
//
// Change History:
//
// Date      Programmer    Defect   Description
// --------  ----------    ------   -----------
// 07/08/14  johawkes      451213   Creation

import javax.transaction.xa.XAResource;

public interface OnePhaseXAResource extends XAResource
{
    //
    //  Return the name of the OnePhaseXAResource
    //
    public String getResourceName();
}