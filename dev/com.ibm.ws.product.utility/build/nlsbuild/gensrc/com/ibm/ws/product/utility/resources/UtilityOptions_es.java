/**
 * WARNING!  THIS FILE IS AUTOMATICALLY GENERATED!  DO NOT MODIFY IT!
 * Generated on Wed Mar 08 13:02:45 EST 2017
 */

package com.ibm.ws.product.utility.resources;

public class UtilityOptions_es extends java.util.ListResourceBundle
{
   public Object[][] getContents() {
       return resources;
   }
   private final static Object[][] resources= {
      { "compare.desc", "\tCompara los iFixes aplicados a la instalaci\u00f3n actual con un nuevo \n\tnivel de paquete de arreglo y lista los iFixes que no est\u00e1n en el paquete de arreglo o compara con \n\tuna lista proporcionada de iFixes y lista si est\u00e1n incluidos en la versi\u00f3n      \n\t actual." },
      { "compare.option-desc.apars", "\tLa herramienta de comparaci\u00f3n comprueba la instalaci\u00f3n actual con esta lista \n\tseparada por comas de los ID de APAR para ver si los contiene y luego lista los \n\tAPAR que no est\u00e9n incluidos." },
      { "compare.option-desc.output", "\tV\u00eda de acceso a un archivo que contiene la salida de este mandato. Esta opci\u00f3n\n\tno es obligatoria. El valor por omisi\u00f3n es STDOUT." },
      { "compare.option-desc.target", "\tEspecifique el archivo de destino con el que se va a comparar la instalaci\u00f3n actual. El \n\tdestino puede ser un directorio o un archivo de archivado, pero debe ser una ubicaci\u00f3n de instalaci\u00f3n v\u00e1lida de \n\tWebSphere Application Server Liberty Profile." },
      { "compare.option-desc.verbose", "\tMuestra los mensajes de error detallados cuando se produce un error." },
      { "compare.option-key.apars", "    --apars=\"lista separada por comas de los ID de APAR\"" },
      { "compare.option-key.output", "    --output=\"v\u00eda de acceso al archivo de salida\"" },
      { "compare.option-key.target", "    --target=\"v\u00eda de acceso al directorio o archivo de archivado\"" },
      { "compare.option-key.verbose", "    --verbose" },
      { "compare.option.addon", "Se debe proporcionar --target o --apars." },
      { "compare.usage.options", "\t{0} compare [opciones]" },
      { "featureInfo.desc", "\tLista todas las caracter\u00edsticas instaladas." },
      { "featureInfo.option-desc.output", "\tV\u00eda de acceso a un archivo que contiene la salida de este mandato. Esta  \n\topci\u00f3n no es obligatoria. El valor por omisi\u00f3n es STDOUT." },
      { "featureInfo.option-key.output", "    --output=\"v\u00eda de acceso al archivo de salida\"" },
      { "featureInfo.usage.options", "\t{0} featureInfo [opciones]" },
      { "global.description", "Descripci\u00f3n:" },
      { "global.options", "Opciones:" },
      { "global.options.statement", "\tUtilice help [nombreAcci\u00f3n] para obtener informaci\u00f3n de opciones detalladas de cada acci\u00f3n." },
      { "global.usage", "Uso:" },
      { "help.desc", "\tImprime informaci\u00f3n de ayuda para la acci\u00f3n especificada." },
      { "help.usage.options", "\t{0} help [nombreAcci\u00f3n]" },
      { "validate.desc", "\tValida una instalaci\u00f3n de producci\u00f3n contra un archivo de suma de comprobaci\u00f3n del producto." },
      { "validate.option-desc.checksumfile", "\tEspecifica el archivo que contiene la suma de comprobaci\u00f3n de los archivos *.mf y *.blst\n\tque se est\u00e1n instalando. Esta opci\u00f3n no es obligatoria. El valor por omisi\u00f3n es el archivo    \n\tlib/version/productChecksums.cs" },
      { "validate.option-desc.output", "\tV\u00eda de acceso a un archivo que contiene la salida de este mandato. Esta  \n\topci\u00f3n no es obligatoria. El valor por omisi\u00f3n es STDOUT." },
      { "validate.option-key.checksumfile", "    --checksumfile=\"v\u00eda de acceso al archivo de suma de comprobaci\u00f3n\"" },
      { "validate.option-key.output", "    --output=\"v\u00eda de acceso al archivo de salida\"" },
      { "validate.usage.options", "\t{0} validate [opciones]" },
      { "version.desc", "\tImprime informaci\u00f3n del producto como el nombre y la versi\u00f3n." },
      { "version.option-desc.ifixes", "\tCuando se proporciona, especifica que tambi\u00e9n se da salida a la lista de iFixes instalados." },
      { "version.option-desc.output", "\tV\u00eda de acceso a un archivo que contiene la salida de este mandato. Esta  \n\topci\u00f3n no es obligatoria. El valor por omisi\u00f3n es STDOUT." },
      { "version.option-desc.verbose", "\tMuestra todo el contenido de cada archivo de propiedades." },
      { "version.option-key.ifixes", "    --ifixes" },
      { "version.option-key.output", "    --output=\"v\u00eda de acceso al archivo de salida\"" },
      { "version.option-key.verbose", "    --verbose" },
      { "version.usage.options", "\t{0} version [opciones]" },
      { "viewLicenseAgreement.desc", "\tMuestra el acuerdo de licencia para la edici\u00f3n de perfil Liberty que se ha instalado." },
      { "viewLicenseAgreement.usage.options", "\t{0} viewLicenseAgreement" },
      { "viewLicenseInfo.desc", "\tMuestra la informaci\u00f3n de licencia para la edici\u00f3n de perfil Liberty que se ha instalado." },
      { "viewLicenseInfo.usage.options", "\t{0} viewLicenseInfo" }
   };
}
