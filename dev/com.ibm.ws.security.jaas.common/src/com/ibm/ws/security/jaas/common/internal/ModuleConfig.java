/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.security.jaas.common.internal;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.osgi.service.metatype.annotations.Option;

import com.ibm.ws.bnd.metatype.annotation.Ext;

@ObjectClassDefinition(factoryPid = "com.ibm.ws.security.authentication.internal.jaas.jaasLoginModuleConfig",
                name = "%jaasLoginModule",
                description = "%jaasLoginModule.desc",
                localization = "OSGI-INF/l10n/metatype")
@Ext.Alias("jaasLoginModule")
public @interface ModuleConfig {

    @AttributeDefinition(name = "%className", description = "%className.desc")
    String className();

    @AttributeDefinition(name = "%controlFlag", description = "%controlFlag.desc", defaultValue = "REQUIRED",
                    options = { @Option(value = "REQUIRED", label = "%controlFlag.REQUIRED"),
                               @Option(value = "REQUISITE", label = "%controlFlag.REQUISITE"),
                               @Option(value = "SUFFICIENT", label = "%controlFlag.SUFFICIENT"),
                               @Option(value = "OPTIONAL", label = "%controlFlag.OPTIONAL") })
    String controlFlag();

    @AttributeDefinition(name = "%libraryRef", description = "%libraryRef.desc")
    @Ext.ReferencePid("com.ibm.ws.classloading.sharedlibrary")
    String libraryRef();

    @AttributeDefinition(name = "internal", description = "internal use only", defaultValue = "(service.pid=${libraryRef})")
    String SharedLib_target();

    @AttributeDefinition(name = "%optionsRef", description = "%optionsRef.desc", required = false)
    @Ext.FlatReferencePid("com.ibm.ws.security.authentication.internal.jaas.jaasLoginModuleConfig.options")
    String options();

    @AttributeDefinition(name = "internal", description = "internal use only")
    String id();

}
