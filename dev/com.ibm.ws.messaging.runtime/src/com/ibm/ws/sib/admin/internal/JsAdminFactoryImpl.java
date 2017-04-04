/*
 * 
 * 
 * =============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * Copyright IBM Corp. 2012
 * 
 * The source code for this program is not published or otherwise divested of
 * its trade secrets, irrespective of what has been deposited with the U.S.
 * Copyright Office.
 * =============================================================================
 * 
 *
 * Change activity:
 *
 * Reason          Date   Origin   Description
 * --------------- ------ -------- ---------------------------------------------
 * 186445.1        140103 philip   Original implementation
 * 186445.2        180103 philip   Enhance using new WCCM model
 * 186445.8        270104 philip   Extra implementation
 * 191793.2        090304 philip   Initial ForeignBus related interfaces
 * 175637.5.2      150304 philip   Support alias/foreign destinations
 * 192832.26       260404 philip   Add createMediationLocalizationDefinition()
 * 187806.1        060504 philip   Package refactor + temp create methods
 * 193585.12       250504 philip   Remove old "toleration" methods
 * 196675.1.7      300504 philip   Add new creators
 * 234931          141004 philip   Remove deprecated methods
 * SIB0002.adm.4   220605 tpm      PEV destination support
 * SIB0002.adm.4.1 180705 tevans   Initial changes for PEV Runtime control
 * SIB0002.adm.4.4 180805 geraint  Added new methods for PEV:
 *                                 - createMQLocalizationDefinition()
 *                                 - createMQMediationLocalizationDefinition()
 * SIB0002.adm.4.4 230805 geraint  Added a new method for PEV:
 *                                 - createMediationExecutionPointDefinition()
 * SIB0125.adm.2   121206 leonarda RCS
 * SIB0211.adm.2   220207 leonarda MQLinkDefinition creation
 * SIB0125.adm.4   120307 leonarda RCS mediation execution point def construction
 * =============================================================================
 */

package com.ibm.ws.sib.admin.internal;

import com.ibm.ws.sib.admin.BaseDestinationDefinition;
import com.ibm.ws.sib.admin.DestinationAliasDefinition;
import com.ibm.ws.sib.admin.DestinationDefinition;
import com.ibm.ws.sib.admin.internal.JsAdminFactory;
import com.ibm.ws.sib.admin.LWMConfig;
import com.ibm.ws.sib.admin.LocalizationDefinition;
import com.ibm.ws.sib.admin.MQLinkDefinition;
import com.ibm.ws.sib.admin.MQLocalizationDefinition;
import com.ibm.wsspi.sib.core.DestinationType;

public final class JsAdminFactoryImpl extends JsAdminFactory {

    //----------------------------------------------------------------------------
    // BaseDestinationDefinition
    //----------------------------------------------------------------------------

    @Override
    public BaseDestinationDefinition createBaseDestinationDefinition(DestinationType type, String name) {
        return new BaseDestinationDefinitionImpl(type, name);
    }

    @Override
    public BaseDestinationDefinition createBaseDestinationDefinition(LWMConfig d) {
        return new BaseDestinationDefinitionImpl(d);
    }

    //----------------------------------------------------------------------------
    // DestinationDefinition
    //----------------------------------------------------------------------------

    @Override
    public DestinationDefinition createDestinationDefinition(DestinationType type, String name) {
        return new DestinationDefinitionImpl(type, name);
    }

    @Override
    public DestinationDefinition createDestinationDefinition(LWMConfig d) {
        return new DestinationDefinitionImpl(d);
    }

    //----------------------------------------------------------------------------
    // DestinationAliasDefinition
    //----------------------------------------------------------------------------

    @Override
    public DestinationAliasDefinition createDestinationAliasDefinition(DestinationType type, String name) {
        return new DestinationAliasDefinitionImpl(type, name);
    }

    @Override
    public DestinationAliasDefinition createDestinationAliasDefinition(LWMConfig d) {
        return new DestinationAliasDefinitionImpl(d);
    }

    //----------------------------------------------------------------------------
    // DestinationForeignDefinition
    //----------------------------------------------------------------------------

    @Override
//    public DestinationForeignDefinition createDestinationForeignDefinition(DestinationType type, String name) {
//        return new DestinationForeignDefinitionImpl(type, name);
//    }
//
//    public DestinationForeignDefinition createDestinationForeignDefinition(ConfigObject d) {
//        return new DestinationForeignDefinitionImpl(d);
//    }
    //----------------------------------------------------------------------------
    // LocalizationDefinition
    //----------------------------------------------------------------------------
    public LocalizationDefinition createLocalizationDefinition(String name) {
        return new LocalizationDefinitionImpl(name);
    }

    @Override
    public LocalizationDefinition createLocalizationDefinition(LWMConfig lp) {
        return new LocalizationDefinitionImpl(lp);
    }

    /** {@inheritDoc} */
    @Override
    public MQLinkDefinition createMQLinkDefinition(String uuid) {
        // TODO Auto-generated method stub
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public MQLocalizationDefinition createMQLocalizationDefinition(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public MQLocalizationDefinition createMQLocalizationDefinition(LWMConfig mqs, LWMConfig bm, LWMConfig lpp) {
        // TODO Auto-generated method stub
        return null;
    }

}