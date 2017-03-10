/**
 * WARNING!  THIS FILE IS AUTOMATICALLY GENERATED!  DO NOT MODIFY IT!
 * Generated on Wed Mar 08 13:02:45 EST 2017
 */

package com.ibm.ws.product.utility.resources;

public class UtilityOptions_fr extends java.util.ListResourceBundle
{
   public Object[][] getContents() {
       return resources;
   }
   private final static Object[][] resources= {
      { "compare.desc", "\tCompare les correctifs provisoires appliqu\u00e9s \u00e0 l'installation en cours \n\tau nouveau niveau de correctif et affiche les correctifs qui ne figurent pas dans le groupe de correctifs \n\tou compare \u00e0 une liste de correctifs provisoires fournie et indique s'ils figurent dans la \n\t version en cours." },
      { "compare.option-desc.apars", "\tL'outil de comparaison v\u00e9rifie l'installation en cours par \n\trapport \u00e0 cette liste d'ID APAR s\u00e9par\u00e9e par des virgules pour voir si elles y figurant, puis il affiche\n\tles APAR qui n'y figurent pas." },
      { "compare.option-desc.output", "\tChemin d'acc\u00e8s au fichier contenant la sortie de cette commande. Cette \n\toption n'est pas obligatoire. Par d\u00e9faut : STDOUT." },
      { "compare.option-desc.target", "\tIndiquez le fichier cible auquel comparer l'installation en cours. Ce     \n\tfichier peut \u00eatre un r\u00e9pertoire ou un fichier archive mais il doit \u00eatre un emplacement \n\td'installation de profil Liberty WebSphere Application Server valide." },
      { "compare.option-desc.verbose", "\tAffiche des messages d'erreur d\u00e9taill\u00e9s lorsqu'une erreur se produit." },
      { "compare.option-key.apars", "    --apars=\"liste s\u00e9par\u00e9e par des virgules d'ID APAR\"" },
      { "compare.option-key.output", "    --output=\"chemin d'acc\u00e8s \u00e0 un fichier de sortie\"" },
      { "compare.option-key.target", "    --target=\"chemin vers r\u00e9pertoire ou fichier archive\"" },
      { "compare.option-key.verbose", "    --verbose" },
      { "compare.option.addon", "--target ou --apars doit \u00eatre indiqu\u00e9." },
      { "compare.usage.options", "\t{0} compare [options]" },
      { "featureInfo.desc", "\tAfficher toutes les fonctions install\u00e9es." },
      { "featureInfo.option-desc.output", "\tChemin d'acc\u00e8s au fichier contenant la sortie de cette commande. Cette   \n\toption n'est pas obligatoire. Par d\u00e9faut : STDOUT." },
      { "featureInfo.option-key.output", "    --output=\"chemin d'acc\u00e8s \u00e0 un fichier de sortie\"" },
      { "featureInfo.usage.options", "\t{0} featureInfo [options]" },
      { "global.description", "Description :" },
      { "global.options", "Options :" },
      { "global.options.statement", "\tUtilisez help [actionName] pour obtenir des informations d\u00e9taill\u00e9es sur les options de chaque action." },
      { "global.usage", "Syntaxe :" },
      { "help.desc", "\tImprimer les informations d'aide pour l'action indiqu\u00e9e." },
      { "help.usage.options", "\t{0} help [actionName]" },
      { "validate.desc", "\tValider une installation de production par rapport \u00e0 un fichier de total de contr\u00f4le produit." },
      { "validate.option-desc.checksumfile", "\tIndiquez le fichier contenant le total de contr\u00f4le des fichiers *.mf et *.blst \n \ten cours d'installation. Cette option n'est pas obligatoire. Par d\u00e9faut, il s'agit du fichier \n\tlib/version/productChecksums.cs" },
      { "validate.option-desc.output", "\tChemin d'acc\u00e8s au fichier contenant la sortie de cette commande. Cette   \n\toption n'est pas obligatoire. Par d\u00e9faut : STDOUT." },
      { "validate.option-key.checksumfile", "    --checksumfile=\"chemin vers fichier de total de contr\u00f4le\"" },
      { "validate.option-key.output", "    --output=\"chemin d'acc\u00e8s \u00e0 un fichier de sortie\"" },
      { "validate.usage.options", "\t{0} validate [options]" },
      { "version.desc", "\tImprimer des infos produit, par exemple le nom et la version du produit." },
      { "version.option-desc.ifixes", "\tLorsqu'elle est fournie cette option indique que la liste des correctifs temporaires install\u00e9s est \u00e9galement g\u00e9n\u00e9r\u00e9e en sortie." },
      { "version.option-desc.output", "\tChemin d'acc\u00e8s au fichier contenant la sortie de cette commande. Cette   \n\toption n'est pas obligatoire. Par d\u00e9faut : STDOUT." },
      { "version.option-desc.verbose", "\tAfficher le contenu int\u00e9gral de chaque fichier de propri\u00e9t\u00e9s." },
      { "version.option-key.ifixes", "    --ifixes" },
      { "version.option-key.output", "    --output=\"chemin d'acc\u00e8s \u00e0 un fichier de sortie\"" },
      { "version.option-key.verbose", "    --verbose" },
      { "version.usage.options", "\t{0} version [options]" },
      { "viewLicenseAgreement.desc", "\tAffiche le contrat de licence pour l'\u00e9dition de profil Liberty install\u00e9e." },
      { "viewLicenseAgreement.usage.options", "\t{0} viewLicenseAgreement" },
      { "viewLicenseInfo.desc", "\tAffiche les informations de licence pour l'\u00e9dition de profil Liberty install\u00e9e." },
      { "viewLicenseInfo.usage.options", "\t{0} viewLicenseInfo" }
   };
}
