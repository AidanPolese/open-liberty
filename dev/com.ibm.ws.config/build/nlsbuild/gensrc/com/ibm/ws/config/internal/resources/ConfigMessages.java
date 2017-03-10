/**
 * WARNING!  THIS FILE IS AUTOMATICALLY GENERATED!  DO NOT MODIFY IT!
 * Generated on Wed Mar 08 13:02:39 EST 2017
 */

package com.ibm.ws.config.internal.resources;

public class ConfigMessages extends java.util.ListResourceBundle
{
   public Object[][] getContents() {
       return resources;
   }
   private final static Object[][] resources= {
      { "audit.dropin.being.processed", "CWWKG0093A: Processing configuration drop-ins resource: {0}" },
      { "audit.include.being.processed", "CWWKG0028A: Processing included configuration resource: {0}" },
      { "config.validator.activeValue", "Property {0} will be set to {1}." },
      { "config.validator.activeValueNull", "Property {0} will be set to no value." },
      { "config.validator.activeValueSecure", "Property {0} will be set to the value defined in {1}." },
      { "config.validator.attributeConflict", "Property {0} has conflicting values:" },
      { "config.validator.foundConflictInstance", "Found conflicting settings for {1} instance of {0} configuration." },
      { "config.validator.foundConflictSingleton", "Found conflicting settings for {0} configuration." },
      { "config.validator.valueConflict", "Value {0} is set in {1}." },
      { "config.validator.valueConflictNull", "No value is set in {0}." },
      { "config.validator.valueConflictSecure", "Secure value is set in {0}." },
      { "copyright", "\nLicensed Material - Property of IBM\n(C) COPYRIGHT International Business Machines Corp. 2010 - All Rights Reserved.\nUS Government Users Restricted Rights - Use, duplication or disclosure\nrestricted by GSA ADP Schedule Contract with IBM Corp." },
      { "error.ExtendsAliasMustExtend", "CWWKG0097E: The {0} persisted identity specifies an ibm:extendsAlias attribute of {1} and must therefore specify the ibm:extends attribute." },
      { "error.alias.collision", "CWWKG0026E: Two or more metatype definitions share the same persisted identity (PID) or alias. The PID or alias of {0} is shared by {1} object class definitions." },
      { "error.attribute.validation.exception", "CWWKG0075E: The value {2} is not valid for attribute {1} of configuration element {0}. The validation message was: {3}." },
      { "error.breaking.include.conflict", "CWWKG0088E: The {1} configuration element is specified in two different configuration resources: {0} and {2}." },
      { "error.cannot.read.location", "CWWKG0090E: The {0} configuration resource does not exist or cannot be read. " },
      { "error.config.update.disk", "CWWKG0024E: The server configuration {0} was not updated on disk. Error: {1}" },
      { "error.config.update.event", "CWWKG0025E: The server configuration update events were not emitted for {0}. Error: {1}" },
      { "error.config.update.exception", "CWWKG0074E: Unable to update the configuration for {0} with the unique identifier {2} because of the exception: {1}." },
      { "error.config.update.init", "CWWKG0015E: The system could not update one or more configurations. Error: {0}" },
      { "error.configValidator.error", "CWWKG0047E: An error occurred while attempting to verify a configuration document: {0}, {1}." },
      { "error.configValidator.keyInfoMissing", "CWWKG0049E: A KeyInfo element was not found within the signature contained within a configuration document: {0}." },
      { "error.configValidator.parseFailed", "CWWKG0045E: Unable to parse a configuration document: {0}, {1}." },
      { "error.configValidator.protectedSectionModified", "CWWKG0053E: A section within a configuration document that is protected by a signature has been modified: {0}." },
      { "error.configValidator.signatureMissing", "CWWKG0048E: Configuration document does not contain a signature: {0}." },
      { "error.configValidator.signatureNotValid", "CWWKG0054E: The signature contained within a configuration document is not valid: {0}." },
      { "error.configValidator.signerNotAuthorized", "CWWKG0050E: Configuration document has been signed by an unauthorized entity: {0}." },
      { "error.configValidator.unmarshalFailed", "CWWKG0046E: Unable to unmarshal the signature contained within a configuration document: {0}, {1}." },
      { "error.configValidator.x509CertificateMissing", "CWWKG0052E: The X509Certificate element was not found within the signature contained within a configuration document: {0}." },
      { "error.configValidator.x509DataMissing", "CWWKG0051E: The X509Data element was not found within the signature contained within a configuration document: {0}." },
      { "error.conflicting.rename.attribute", "CWWKG0068E: Unable to rename attribute {0} to {1} in persisted identity {2} because this attribute has already been renamed by an extended metatype." },
      { "error.dsExists", "CWWKG0039E: Designate with {0} is already registered." },
      { "error.extendsAlias.collision", "CWWKG0100E: The {2} ibm:extendsAlias attribute is duplicated in a single extends hierarchy. The PIDs are {0} and {1}. Make each ibm:extendsAlias attribute unique within the extends hierarchy." },
      { "error.factoryOnly", "CWWKG0061E: The persisted identity {0} is not a factory persisted identity and so cannot be used to extend the persisted identity {1}." },
      { "error.factoryOnly.extendsAlias", "CWWKG0096E: The {0} persisted identity is not a factory persisted identity and so cannot have an ibm:extendsAlias attribute of {1}. Reconfigure the persisted identity to use a factory persisted identity or remove the ibm:extendsAlias attribute from the persisted identity." },
      { "error.fileNotFound", "CWWKG0040E: The file {0} was not found." },
      { "error.final.override", "CWWKG0060E: It is not valid to override or rename attribute {0} for persisted identity {1} because it is declared final by the persisted identity {2}." },
      { "error.include.location.not.specified", "CWWKG0089E: The location attribute must be specified on the include configuration element specified on line {0} of resource {1}" },
      { "error.invalid.boolean.attribute", "CWWKG0081E: The value {0} for boolean attribute {1} is invalid. Valid values are \"true\" and \"false\". The default value of {2} will be used." },
      { "error.invalidArgument", "CWWKG0041E: Invalid argument {0}. The value must be specified." },
      { "error.invalidOCDRef", "ERROR: Metatype PID [{0}] specifies non-existent object class definition ID [{1}]" },
      { "error.missing.required.attribute", "CWWKG0058E: The element {0} with the unique identifier {2} is missing the required attribute {1}." },
      { "error.missing.required.attribute.singleton", "CWWKG0095E: The element {0} is missing the required attribute {1}." },
      { "error.missingSuper", "CWWKG0059E: The persisted identity {0} could not be processed because it extends an unavailable persisted identity {1}." },
      { "error.ocdExists", "CWWKG0038E: Object class with {0} is already registered." },
      { "error.parentpid.and.childalias", "CWWKG0098E: The {0} persisted identity specifies the {1} attribute of {2} and must therefore specify the {3} attribute." },
      { "error.parse.bundle", "CWWKG0002E: The configuration parser detected an error while processing the bundle, version or persisted identity (PID). Error: {0} Error: {1} Reason: {2}" },
      { "error.parse.server", "CWWKG0001E: The configuration parser detected an error while parsing the root of the configuration and the referenced configuration documents. Error: {0}" },
      { "error.prod.ext.features.not.found", "CWWKG0078E: The product extension {0} does not contain any features." },
      { "error.prod.ext.not.defined", "CWWKG0080E: The product extension with the name of {0} does not exist." },
      { "error.prod.ext.not.found", "CWWKG0079E: The product extension {0} cannot be found at location {1}." },
      { "error.rename.attribute.missing", "CWWKG0067E: Unable to rename attribute definition {1} specified by the ibm:rename attribute {2} in persisted identity {0}." },
      { "error.schemaGenException", "CWWKG0036E: Error generating schema: {0}" },
      { "error.schemaGenInvalidJarLocation", "CWWKG0037E: Invalid jar location." },
      { "error.specify.parentpid", "CWWKG0077E: The metatype definition for {0} defines a child alias but does not define a parent." },
      { "error.superFactoryOnly", "CWWKG0062E: The persisted identity {0} is not a factory persisted identity so cannot be extended by the persisted identity {1}." },
      { "error.syntax.parse.server", "CWWKG0014E: The configuration parser detected an XML syntax error while parsing the root of the configuration and the referenced configuration documents. Error: {0} File: {1} Line: {2} Column: {3}" },
      { "error.targetRequired", "CWWKG0034E: Target file must be specified" },
      { "error.unique.value.conflict", "CWWKG0031E: The value {1} specified for unique attribute {0} is already in use." },
      { "error.unknownArgument", "CWWKG0035E: Unknown option: {0}" },
      { "error.variable.name.missing", "CWWKG0091E: A name attribute must be specified for the variable on line {0} of resource {1}" },
      { "error.variable.value.missing", "CWWKG0092E: A value attribute must be specified for the variable on line {0} of resource {1}" },
      { "fatal.configValidator.documentNotValid", "CWWKG0044E: Server shutdown because a configuration document does not contain a valid signature: {0}." },
      { "fatal.configValidator.dropinsEnabled", "CWWKG0056E: Server shutdown because drop-ins are enabled." },
      { "frameworkShutdown", "CWWKG0010I: The server {0} is shutting down because of a previous initialization error." },
      { "info.config.refresh.nochanges", "CWWKG0018I: The server configuration was not updated. No functional changes were detected." },
      { "info.config.refresh.start", "CWWKG0016I: Starting server configuration update." },
      { "info.config.refresh.stop", "CWWKG0017I: The server configuration was successfully updated in {0} seconds." },
      { "info.config.refresh.timeout", "CWWKG0027W: Timeout while updating server configuration." },
      { "info.configValidator.documentValid", "CWWKG0055I: Configuration document contains a valid signature: {0}." },
      { "info.configValidator.validator", "CWWKG0043I: Configuration validator class in use: {0}." },
      { "info.ignore.invalid.optional.include", "CWWKG0006I: Ignoring @include? resource({0}), which is not valid.  Line: {1}. {2}" },
      { "info.ignore.unresolved.optional.include", "CWWKG0005I: Ignoring unresolved optional {0} resource({1}). Line: {2}, {3}" },
      { "info.prop.ignored", "CWWKG0003I: An operator is not specified, or the specified value is null or empty. The property is ignored. Property: {0} File: {1}" },
      { "info.unsupported.api", "CWWKG0004I: This API is unsupported: {0}" },
      { "missing.metatype.file", "CWWKG0073W: Unable to find the metatype localization files in bundle {0}." },
      { "schemagen.alias.required", "CWWKG0022E: A configuration alias is required for {0} nested configuration." },
      { "schemagen.bad.reference.extension", "CWWKG0029E: Attribute {0} has does not have ibm:reference extension or the extension does not specify a pid." },
      { "schemagen.bad.reference.pid", "CWWKG0030E: Pid reference {0} listed on ibm:reference extension does not exist." },
      { "schemagen.duplicate.pid", "CWWKG0021E: The same configuration persisted identity (PID) {0} is defined in multiple metatype.xml files." },
      { "schemagen.invalid.child", "CWWKG0023E: Child configuration {0} must be a factory configuration." },
      { "schemagen.invalid.extension.pid", "CWWKG0066E: Metatype persisted identity {0} is attempting to extend a non-existent persisted identity {1}" },
      { "schemagen.invalid.parent", "CWWKG0020E: Parent configuration {0} specified in {1} is not valid." },
      { "schemagen.invalid.type.override", "CWWKG0064E: Invalid override of attribute type by attribute {0} in metatype {1}. The original type {2} will be used, instead." },
      { "schemagen.no.attrib.desc", "CWWKG0071W: The {0} attribute of the object class definition {1} in bundle {2} has no attribute description." },
      { "schemagen.no.attrib.name", "CWWKG0072W: The {0} attribute of the object class definition {1} in bundle {2} has no attribute name." },
      { "schemagen.noextensions", "CWWKG0019E: Parent configuration {0} specified in {1} does not support extensions." },
      { "schemagen.non.factorypid.extension", "CWWKG0065E: A non-factory persisted identity {0} is attempting to extend another metatype." },
      { "schemagen.rename.attribute.missing", "CWWKG0063E: Unable to rename attribute definition {1} as specified by the ibm:rename attribute {2} in persisted identity {0}." },
      { "schemagen.unresolved.attrib.desc", "CWWKG0069W: The {0} attribute of the object class definition {1} in bundle {2} has an unresolved attribute description." },
      { "schemagen.unresolved.attrib.name", "CWWKG0070W: The {0} attribute of the object class definition {1} in bundle {2} has an unresolved attribute name." },
      { "warn.bad.reference.filter", "CWWKG0086W: The reference filter {0} is not valid." },
      { "warn.bundle.factory.noinstance", "CWWKG0009W: Configuration {0} in bundle {1} specifies factory configuration without an ID." },
      { "warn.cannot.resolve.optional.include", "CWWKG0084W: The optional include configuration file cannot be resolved: {0}" },
      { "warn.config.delete.failed", "CWWKG0012W: The system could not delete {0} configuration." },
      { "warn.config.delete.failed.multiple", "CWWKG0013W: The system did not delete {0} configuration. Multiple matching configurations were found." },
      { "warn.config.invalid.using.default.value", "CWWKG0083W: A validation failure occurred while processing the [{0}] property, value = [{1}]. Default value in use: {2}. " },
      { "warn.config.invalid.value", "CWWKG0032W: Unexpected value specified for property [{0}], value = [{1}]. Expected value(s) are: {2}." },
      { "warn.config.validate.failed", "CWWKG0011W: The configuration validation did not succeed. {0}" },
      { "warn.configValidator.refreshFailed", "CWWKG0057W: New configuration not loaded because of invalid signature." },
      { "warn.file.delete.failed", "CWWKG0007W: The system could not delete {0}" },
      { "warn.file.mkdirs.failed", "CWWKG0008W: The system could not create directories for {0}." },
      { "warn.parse.circular.include", "CWWKG0042W: Included configuration resources form a circular dependency: {0}." },
      { "warning.invalid.boolean.attribute", "CWWKG0082W: The value {0} for boolean attribute {1} will be interpreted as \"false\"." },
      { "warning.multiple.matches", "CWWKG0087W: The value [{1}] specified for the reference attribute [{0}] is not valid because it matches multiple configurations." },
      { "warning.old.config.still.in.use", "CWWKG0076W: The previous configuration for {0} with id {1} is still in use." },
      { "warning.pid.not.found", "CWWKG0033W: The value [{1}] specified for the reference attribute [{0}] was not found in the configuration." },
      { "warning.unexpected.server.element", "CWWKG0085W: The server configuration document contains a nested server element. The nested configuration is ignored." },
      { "warning.unrecognized.merge.behavior", "CWWKG0094W: The {0} value that is specified for the merge behavior on the include configuration element is not valid. The system will use the default value of merge." }
   };
}
