/**
 * WARNING!  THIS FILE IS AUTOMATICALLY GENERATED!  DO NOT MODIFY IT!
 * Generated on Wed Mar 08 13:02:39 EST 2017
 */

package com.ibm.ws.config.internal.resources;

public class ConfigMessages_fr extends java.util.ListResourceBundle
{
   public Object[][] getContents() {
       return resources;
   }
   private final static Object[][] resources= {
      { "audit.dropin.being.processed", "CWWKG0093A: Traitement de la ressource de suppression de configuration : {0}" },
      { "audit.include.being.processed", "CWWKG0028A: Traitement de la ressource de configuration incluse : {0}" },
      { "config.validator.activeValue", "La propri\u00e9t\u00e9 {0} sera d\u00e9finie sur {1}." },
      { "config.validator.activeValueNull", "La propri\u00e9t\u00e9 {0} ne sera d\u00e9finie sur aucune valeur." },
      { "config.validator.activeValueSecure", "La propri\u00e9t\u00e9 {0} sera d\u00e9finie sur la valeur d\u00e9finie dans {1}." },
      { "config.validator.attributeConflict", "La propri\u00e9t\u00e9 {0} comporte des valeurs en conflit :" },
      { "config.validator.foundConflictInstance", "Des param\u00e8tres en conflit ont \u00e9t\u00e9 d\u00e9tect\u00e9s pour l''instance {1} de la configuration de {0}" },
      { "config.validator.foundConflictSingleton", "Conflit de param\u00e8tres d\u00e9tect\u00e9 pour la configuration de {0}" },
      { "config.validator.valueConflict", "La valeur {0} a \u00e9t\u00e9 d\u00e9finie dans {1}." },
      { "config.validator.valueConflictNull", "Aucune valeur n''est d\u00e9finie dans {0}." },
      { "config.validator.valueConflictSecure", "Valeur s\u00e9curis\u00e9e d\u00e9finie dans {0}." },
      { "copyright", "\nEl\u00e9ments sous licence - Propri\u00e9t\u00e9 d'IBM\n(C) COPYRIGHT International Business Machines Corp. 2010 - All Rights Reserved.\nUS Government Users Restricted Rights - Use, duplication or disclosure\nrestricted by GSA ADP Schedule Contract with IBM Corp." },
      { "error.ExtendsAliasMustExtend", "CWWKG0097E: L''identit\u00e9 persistante {0} indique un attribut ibm:extendsAlias {1} et doit par cons\u00e9quent indiquer l''attribut ibm:extends." },
      { "error.alias.collision", "CWWKG0026E: Au moins deux d\u00e9finitions de m\u00e9tatype partagent le m\u00eame PID (identit\u00e9 persistante) ou le m\u00eame alias. Le PID ou l''alias {0} est partag\u00e9 par les d\u00e9finitions de classe d''objets {1}." },
      { "error.attribute.validation.exception", "CWWKG0075E: La valeur {2} n''est pas valide pour l''attribut {1} de l''\u00e9l\u00e9ment de configuration {0}. Le message de validation \u00e9tait : {3}." },
      { "error.breaking.include.conflict", "CWWKG0088E: L''\u00e9l\u00e9ment de configuration {1} est sp\u00e9cifi\u00e9 dans deux ressources de configuration diff\u00e9rentes : {0} et {2}." },
      { "error.cannot.read.location", "CWWKG0090E: La ressource de configuration {0} n''existe pas ou ne peut pas \u00eatre lue. " },
      { "error.config.update.disk", "CWWKG0024E: La configuration de serveur {0} n''a pas \u00e9t\u00e9 mise \u00e0 jour sur le disque. Erreur : {1}" },
      { "error.config.update.event", "CWWKG0025E: Les \u00e9v\u00e9nements de mise \u00e0 jour de configuration de serveur n''ont pas \u00e9t\u00e9 \u00e9mis pour {0}. Erreur : {1}" },
      { "error.config.update.exception", "CWWKG0074E: Impossible de mettre \u00e0 jour la configuration pour {0} avec l''identificateur unique {2} en raison de l''exception : {1}." },
      { "error.config.update.init", "CWWKG0015E: Le syst\u00e8me n''a pas pu mettre \u00e0 jour une ou plusieurs configurations. Erreur : {0}" },
      { "error.configValidator.error", "CWWKG0047E: Une erreur s''est produite lors de la tentative de v\u00e9rification d''un document de configuration : {0}, {1}." },
      { "error.configValidator.keyInfoMissing", "CWWKG0049E: Aucun \u00e9l\u00e9ment KeyInfo n''a \u00e9t\u00e9 trouv\u00e9 dans la signature contenue dans un document de configuration : {0}." },
      { "error.configValidator.parseFailed", "CWWKG0045E: Impossible d''analyser le document de configuration : {0}, {1}." },
      { "error.configValidator.protectedSectionModified", "CWWKG0053E: Une section du document de configuration qui est prot\u00e9g\u00e9e par une signature a \u00e9t\u00e9 modifi\u00e9e : {0}." },
      { "error.configValidator.signatureMissing", "CWWKG0048E: Un document de configuration ne contient pas de signature : {0}." },
      { "error.configValidator.signatureNotValid", "CWWKG0054E: La signature contenue dans un document de configuration n''est pas valide : {0}." },
      { "error.configValidator.signerNotAuthorized", "CWWKG0050E: Le document de configuration a \u00e9t\u00e9 sign\u00e9 par une entit\u00e9 non autoris\u00e9e : {0}." },
      { "error.configValidator.unmarshalFailed", "CWWKG0046E: Impossible de d\u00e9sordonnancer la signature contenue dans un document de configuration : {0}, {1}." },
      { "error.configValidator.x509CertificateMissing", "CWWKG0052E: Aucun \u00e9l\u00e9ment X509Certificate n''a \u00e9t\u00e9 trouv\u00e9 dans la signature contenue dans un document de configuration : {0}." },
      { "error.configValidator.x509DataMissing", "CWWKG0051E: Aucun \u00e9l\u00e9ment X509Data n''a \u00e9t\u00e9 trouv\u00e9 dans la signature contenue dans un document de configuration : {0}." },
      { "error.conflicting.rename.attribute", "CWWKG0068E: Impossible de renommer l''attribut {0} en {1} dans l''identit\u00e9 persistante {2} car cet attribut a d\u00e9j\u00e0 \u00e9t\u00e9 renomm\u00e9 par un m\u00e9tatype \u00e9tendu." },
      { "error.dsExists", "CWWKG0039E: L''\u00e9l\u00e9ment d\u00e9sign\u00e9 avec {0} est d\u00e9j\u00e0 enregistr\u00e9." },
      { "error.extendsAlias.collision", "CWWKG0100E: L''attribut {2} ibm:extendsAlias est en double dans une hi\u00e9rarchie extends unique. Les PID sont {0} et {1}. Rendez chaque attribut ibm:extendsAlias unique au sein de la hi\u00e9rarchie extends." },
      { "error.factoryOnly", "CWWKG0061E: L''identit\u00e9 persistante {0} n''est pas une identit\u00e9 persistante d''usine et ne peut donc pas \u00e9tendre l''identit\u00e9 persistante {1}." },
      { "error.factoryOnly.extendsAlias", "CWWKG0096E: L''identit\u00e9 persistante {0} n''est pas une identit\u00e9 persistante d''usine et ne peut donc pas avoir l''attribut ibm:extendsAlias {1}. Effectuez une nouvelle configuration d''identit\u00e9 persistante afin d''utiliser une identit\u00e9 persistante d''usine ou retirez l''attribut ibm:extendsAlias de l''identit\u00e9 persistante." },
      { "error.fileNotFound", "CWWKG0040E: Le fichier {0} est introuvable." },
      { "error.final.override", "CWWKG0060E: Il n''est pas possible de remplacer ou de changer le nom de l''attribut {0} pour l''identit\u00e9 persistante {1} car il est d\u00e9clar\u00e9 comme \u00e9tant final par l''identit\u00e9 persistante {2}." },
      { "error.include.location.not.specified", "CWWKG0089E: L''attribut d''emplacement doit \u00eatre sp\u00e9cifi\u00e9 sur l''\u00e9l\u00e9ment d''inclusion de configuration sp\u00e9cifi\u00e9 \u00e0 la ligne {0} de la ressource {1}" },
      { "error.invalid.boolean.attribute", "CWWKG0081E: La valeur {0} de l''attribut bool\u00e9en {1} n''est pas valide. Les valeurs valides sont \"true\" et \"false\". La valeur par d\u00e9faut {2} sera utilis\u00e9e." },
      { "error.invalidArgument", "CWWKG0041E: Argument non valide : {0}. La valeur doit \u00eatre sp\u00e9cifi\u00e9e." },
      { "error.invalidOCDRef", "ERREUR : Le PID [{0}] du m\u00e9tatype sp\u00e9cifie un ID de d\u00e9finition de classe d''objets qui n''existe pas [{1}]" },
      { "error.missing.required.attribute", "CWWKG0058E: L''attribut requis {1} manque dans l''\u00e9l\u00e9ment {0} avec l''identificateur unique {2}." },
      { "error.missing.required.attribute.singleton", "CWWKG0095E: L''attribut requis {1} manque dans l''\u00e9l\u00e9ment {0}." },
      { "error.missingSuper", "CWWKG0059E: L''identit\u00e9 persistante {0} n''a pas pu \u00eatre trait\u00e9e car elle \u00e9tend une identit\u00e9 persistante non disponible {1}." },
      { "error.ocdExists", "CWWKG0038E: La classe d''objets avec {0} est d\u00e9j\u00e0 enregistr\u00e9e." },
      { "error.parentpid.and.childalias", "CWWKG0098E: L''identit\u00e9 persistant {0} indique l''attribut {1} de {2} et doit par cons\u00e9quent indiquer l''attribut {3}." },
      { "error.parse.bundle", "CWWKG0002E: L''analyseur de configuration a d\u00e9tect\u00e9 une erreur lors de du traitement du bundle, de sa version ou de son PID (identit\u00e9 persistante). Erreur : {0} Erreur : {1} Raison : {2}" },
      { "error.parse.server", "CWWKG0001E: L''analyseur de configuration a d\u00e9tect\u00e9 une erreur lors de l''analyse syntaxique de la racine de la configuration et des documents de configuration r\u00e9f\u00e9renc\u00e9s. Erreur : {0}" },
      { "error.prod.ext.features.not.found", "CWWKG0078E: L''extension de produit {0} ne contient aucune fonction." },
      { "error.prod.ext.not.defined", "CWWKG0080E: L''extension de produit avec le nom {0} n''existe pas." },
      { "error.prod.ext.not.found", "CWWKG0079E: L''extension de produit {0} est introuvable \u00e0 l''emplacement {1}." },
      { "error.rename.attribute.missing", "CWWKG0067E: Impossible de renommer la d\u00e9finition d''attribut {1} sp\u00e9cifi\u00e9e par l''attribut ibm:rename {2} dans l''identit\u00e9 persistante {0}." },
      { "error.schemaGenException", "CWWKG0036E: Erreur lors de la g\u00e9n\u00e9ration du sch\u00e9ma : {0}" },
      { "error.schemaGenInvalidJarLocation", "CWWKG0037E: Emplacement JAR non valide" },
      { "error.specify.parentpid", "CWWKG0077E: La d\u00e9finition de m\u00e9tatype pour {0} d\u00e9finit un alias enfant mais ne d\u00e9finit pas de parent." },
      { "error.superFactoryOnly", "CWWKG0062E: L''identit\u00e9 persistante {0} n''est pas une identit\u00e9 persistante d''usine et ne peut donc pas \u00eatre \u00e9tendue par l''identit\u00e9 persistante {1}." },
      { "error.syntax.parse.server", "CWWKG0014E: L''analyseur de configuration a d\u00e9tect\u00e9 une erreur de syntaxe XML lors de l''analyse syntaxique de la racine de la configuration et des documents de configuration r\u00e9f\u00e9renc\u00e9s. Erreur : {0} Fichier : {1} Ligne : {2} Colonne : {3}" },
      { "error.targetRequired", "CWWKG0034E: Le nom cible doit \u00eatre sp\u00e9cifi\u00e9" },
      { "error.unique.value.conflict", "CWWKG0031E: La valeur {1} sp\u00e9cifi\u00e9e pour l''attribut unique {0} est d\u00e9j\u00e0 utilis\u00e9e." },
      { "error.unknownArgument", "CWWKG0035E: Option inconnue : {0}" },
      { "error.variable.name.missing", "CWWKG0091E: Un attribut de nom doit \u00eatre sp\u00e9cifi\u00e9 pour la variable sur la ligne {0} de la ressource {1}" },
      { "error.variable.value.missing", "CWWKG0092E: Un attribut de valeur doit \u00eatre sp\u00e9cifi\u00e9 pour la variable sur la ligne {0} de la ressource {1}" },
      { "fatal.configValidator.documentNotValid", "CWWKG0044E: Arr\u00eat du serveur car un document de configuration ne contient pas de signature valide : {0}." },
      { "fatal.configValidator.dropinsEnabled", "CWWKG0056E: Arr\u00eat du serveur car les suppressions sont activ\u00e9es." },
      { "frameworkShutdown", "CWWKG0010I: Le serveur {0} s''arr\u00eate en raison d''une erreur d''initialisation pr\u00e9c\u00e9dente." },
      { "info.config.refresh.nochanges", "CWWKG0018I: La configuration du serveur n'a pas \u00e9t\u00e9 mise \u00e0 jour. Aucun changement fonctionnel n'a \u00e9t\u00e9 d\u00e9tect\u00e9." },
      { "info.config.refresh.start", "CWWKG0016I: D\u00e9marrage de la mise \u00e0 jour de la configuration du serveur." },
      { "info.config.refresh.stop", "CWWKG0017I: La mise \u00e0 jour de la configuration du serveur a abouti en {0} secondes." },
      { "info.config.refresh.timeout", "CWWKG0027W: D\u00e9passement du d\u00e9lai imparti lors de la mise \u00e0 jour de la configuration du serveur." },
      { "info.configValidator.documentValid", "CWWKG0055I: Un document de configuration contient une signature valide : {0}." },
      { "info.configValidator.validator", "CWWKG0043I: Classe de valideur de configuration en cours d''utilisation : {0}." },
      { "info.ignore.invalid.optional.include", "CWWKG0006I: Ignor\u00e9 : inclusion (@include?) d''une ressource ({0}), qui n''est pas valide.  Ligne : {1}. {2}" },
      { "info.ignore.unresolved.optional.include", "CWWKG0005I: La ressource {0} ({1}) optionnelle n''est pas r\u00e9solue et donc ignor\u00e9e. Ligne : {2}, {3}" },
      { "info.prop.ignored", "CWWKG0003I: Un op\u00e9rateur n''a pas \u00e9t\u00e9 sp\u00e9cifi\u00e9, ou la valeur sp\u00e9cifi\u00e9e est Null ou vide. La propri\u00e9t\u00e9 est ignor\u00e9e. Propri\u00e9t\u00e9 : {0} Fichier : {1}" },
      { "info.unsupported.api", "CWWKG0004I: L''API {0} n''est pas prise en charge." },
      { "missing.metatype.file", "CWWKG0073W: Les fichiers de localisation de m\u00e9tatype sont introuvables dans le bundle {0}." },
      { "schemagen.alias.required", "CWWKG0022E: Un alias de configuration est obligatoire pour la configuration imbriqu\u00e9e {0}." },
      { "schemagen.bad.reference.extension", "CWWKG0029E: L''attribut {0} n''a pas l''extension ibm:reference ou l''extension ne sp\u00e9cifie pas de PID." },
      { "schemagen.bad.reference.pid", "CWWKG0030E: La r\u00e9f\u00e9rence de PID {0} r\u00e9pertori\u00e9e dans l''extension ibm:reference n''existe pas." },
      { "schemagen.duplicate.pid", "CWWKG0021E: Le m\u00eame PID (identit\u00e9 persistante) de configuration, {0}, est d\u00e9fini dans plusieurs fichiers metatype.xml." },
      { "schemagen.invalid.child", "CWWKG0023E: La configuration enfant {0} doit \u00eatre une configuration de fabrique (factory)." },
      { "schemagen.invalid.extension.pid", "CWWKG0066E: L''identit\u00e9 persistante de m\u00e9tatype {0} tente d''\u00e9tendre une identit\u00e9 persistante qui n''existe pas {1}" },
      { "schemagen.invalid.parent", "CWWKG0020E: La configuration parent {0} sp\u00e9cifi\u00e9e dans {1} n''est pas valide." },
      { "schemagen.invalid.type.override", "CWWKG0064E: Remplacement non valide du type d''attribut par l''attribut {0} dans le m\u00e9tatype {1}. Le type d''origine {2} sera utilis\u00e9 \u00e0 la place." },
      { "schemagen.no.attrib.desc", "CWWKG0071W: L''attribut {0} de la d\u00e9finition de classe d''objets {1} dans le bundle {2} ne comporte pas de description d''attribut." },
      { "schemagen.no.attrib.name", "CWWKG0072W: L''attribut {0} de la d\u00e9finition de classe d''objets {1} dans le bundle {2} ne comporte pas de nom d''attribut." },
      { "schemagen.noextensions", "CWWKG0019E: La configuration parent {0} sp\u00e9cifi\u00e9e dans {1} n''admet pas les extensions." },
      { "schemagen.non.factorypid.extension", "CWWKG0065E: Une identit\u00e9 persistante {0} qui n''est pas d''usine tente d''\u00e9tendre un autre m\u00e9tatype." },
      { "schemagen.rename.attribute.missing", "CWWKG0063E: Impossible de renommer la d\u00e9finition d''attribut {1} telle qu''elle est sp\u00e9cifi\u00e9e par l''attribut ibm:rename {2} dans l''identit\u00e9 persistante {0}." },
      { "schemagen.unresolved.attrib.desc", "CWWKG0069W: L''attribut {0} de la d\u00e9finition de classe d''objets {1} dans le bundle {2} comporte une description d''attribut non r\u00e9solue." },
      { "schemagen.unresolved.attrib.name", "CWWKG0070W: L''attribut {0} de la d\u00e9finition de classe d''objets {1} dans le bundle {2} comporte un nom d''attribut non r\u00e9solu." },
      { "warn.bad.reference.filter", "CWWKG0086W: Le filtre de r\u00e9f\u00e9rence {0} n''est pas valide." },
      { "warn.bundle.factory.noinstance", "CWWKG0009W: La configuration {0} dans le bundle {1} sp\u00e9cifie une configuration de fabrique (classe factory) sans ID." },
      { "warn.cannot.resolve.optional.include", "CWWKG0084W: Le fichier de configuration d''inclusion en option ne peut pas \u00eatre r\u00e9solu : {0}" },
      { "warn.config.delete.failed", "CWWKG0012W: Le syst\u00e8me n''a pas pu supprimer la configuration {0}." },
      { "warn.config.delete.failed.multiple", "CWWKG0013W: Le syst\u00e8me n''a pas supprim\u00e9 la configuration {0}. Plusieurs configurations concordantes ont \u00e9t\u00e9 trouv\u00e9es." },
      { "warn.config.invalid.using.default.value", "CWWKG0083W: Une erreur de validation s''est produite lors du traitement de la propri\u00e9t\u00e9 [{0}], valeur = [{1}]. Valeur par d\u00e9faut utilis\u00e9e : {2}. " },
      { "warn.config.invalid.value", "CWWKG0032W: Valeur inattendue sp\u00e9cifi\u00e9e pour la propri\u00e9t\u00e9 [{0}], valeur = [{1}]. La valeur ou les valeurs attendues sont : {2}." },
      { "warn.config.validate.failed", "CWWKG0011W: La configuration a \u00e9chou\u00e9 \u00e0 la validation. {0}" },
      { "warn.configValidator.refreshFailed", "CWWKG0057W: La nouvelle configuration n'a pas \u00e9t\u00e9 charg\u00e9e car une signature n'\u00e9tait pas valide." },
      { "warn.file.delete.failed", "CWWKG0007W: Le syst\u00e8me n''a pas pu supprimer {0}" },
      { "warn.file.mkdirs.failed", "CWWKG0008W: Le syst\u00e8me n''a pas pu cr\u00e9er les r\u00e9pertoires pour {0}." },
      { "warn.parse.circular.include", "CWWKG0042W: Les ressources de configuration incluses forment une d\u00e9pendance circulaire : {0}." },
      { "warning.invalid.boolean.attribute", "CWWKG0082W: La valeur {0} de l''attribut bool\u00e9en {1} sera interpr\u00e9t\u00e9e comme \u00e9tant \"false\"." },
      { "warning.multiple.matches", "CWWKG0087W: La valeur [{1}] sp\u00e9cifi\u00e9e pour l''attribut de r\u00e9f\u00e9rence [{0}] n''est pas valide car elle correspond \u00e0 plusieurs configurations." },
      { "warning.old.config.still.in.use", "CWWKG0076W: La configuration pr\u00e9c\u00e9dente pour {0} avec l''ID {1} est toujours utilis\u00e9e." },
      { "warning.pid.not.found", "CWWKG0033W: La valeur [{1}] sp\u00e9cifi\u00e9e pour l''attribut de r\u00e9f\u00e9rence [{0}] n''a pas \u00e9t\u00e9 trouv\u00e9e dans la configuration." },
      { "warning.unexpected.server.element", "CWWKG0085W: Le document de configuration du serveur comporte un \u00e9l\u00e9ment server imbriqu\u00e9. La configuration imbriqu\u00e9e est ignor\u00e9e." },
      { "warning.unrecognized.merge.behavior", "CWWKG0094W: La valeur {0} qui est sp\u00e9cifi\u00e9e pour le comportement de fusion sur l''\u00e9l\u00e9ment d''inclusion de configuration n''est pas valide. Le syst\u00e8me utilisera la valeur par d\u00e9faut de la fusion." }
   };
}
