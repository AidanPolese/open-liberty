/**
 * WARNING!  THIS FILE IS AUTOMATICALLY GENERATED!  DO NOT MODIFY IT!
 * Generated on Wed Mar 08 13:02:45 EST 2017
 */

package com.ibm.ws.product.utility.resources;

public class UtilityOptions_it extends java.util.ListResourceBundle
{
   public Object[][] getContents() {
       return resources;
   }
   private final static Object[][] resources= {
      { "compare.desc", "\tConfrontare le iFix applicate all'attuale installazione con un nuovo\n\tlivello di fixpack ed elencare eventuali iFix non nel fixpack o confrontare con \n\tun elenco fornito di iFix ed elencare se sono incluse nella      \n\t versione corrente." },
      { "compare.option-desc.apars", "\tLo strumento di confronto controlla l'attuale installazione in questo  \n\telenco separato da virgole di ID APAR per vedere se li contiene e quindi elenca\n\teventuali APAR non inclusi." },
      { "compare.option-desc.output", "\tUn percorso a un file che conterr\u00e0 l'output da questo comando. Questa opzione\n\tnon \u00e8 obbligatoria. Il valore predefinito \u00e8 STDOUT." },
      { "compare.option-desc.target", "\tSpecificare il file di destinazione per il confronto con l'attuale installazione. La     \n\tdestinazione pu\u00f2 essere una directory o un file di archivio ma deve essere un'ubicazione \n\tdi installazione di WebSphere Application Server Liberty Profile valida." },
      { "compare.option-desc.verbose", "\tVisualizzare i messaggi di errore dettagliati quando si verifica un errore." },
      { "compare.option-key.apars", "    --apars=\"un elenco separato da virgole di ID APAR\"" },
      { "compare.option-key.output", "    --output=\"percorso a un file di output\"" },
      { "compare.option-key.target", "    --target=\"percorso alla directory o al file di archivio\"" },
      { "compare.option-key.verbose", "    --verbose" },
      { "compare.option.addon", "\u00c8 necessario fornire o --target o --apars." },
      { "compare.usage.options", "\t{0} compare [opzioni]" },
      { "featureInfo.desc", "\tElencare tutte le funzioni installate." },
      { "featureInfo.option-desc.output", "\tUn percorso a un file che contien l'output da questo comando. Questa    \n\topzione non \u00e8 obbligatoria. Il valore predefinito \u00e8 STDOUT." },
      { "featureInfo.option-key.output", "    --output=\"percorso a un file di output\"" },
      { "featureInfo.usage.options", "\t{0} featureInfo [opzioni]" },
      { "global.description", "Descrizione:" },
      { "global.options", "Opzioni:" },
      { "global.options.statement", "\tUtilizzare il comando help [nomeAzione] per informazioni dettagliate sulle opzioni di ciascuna azione." },
      { "global.usage", "Uso:" },
      { "help.desc", "\tStampare le informazioni della guida per l'azione specificata." },
      { "help.usage.options", "\t{0} help [nomeAzione]" },
      { "validate.desc", "\tConvalidare un'installazione di produzione con il file checksum del prodotto." },
      { "validate.option-desc.checksumfile", "\tSpecificare il file contenente il checksum dei file *.mf e *.blst \n\tin fase di installazione. Questa opzione non \u00e8 obbligatoria. Il valore predefinito \u00e8 il file    \n\tlib/version/productChecksums.cs" },
      { "validate.option-desc.output", "\tUn percorso a un file che contiene l'output da questo comando. Questa    \n\topzione non \u00e8 obbligatoria. Il valore predefinito \u00e8 STDOUT." },
      { "validate.option-key.checksumfile", "    --checksumfile=\"percorso al file checksum\"" },
      { "validate.option-key.output", "    --output=\"percorso a un file di output\"" },
      { "validate.usage.options", "\t{0} validate [opzioni]" },
      { "version.desc", "\tStampare le informazioni sul prodotto, quali il nome e la versione." },
      { "version.option-desc.ifixes", "\tQuando fornita specifica di inserie nell'outpu anche l'elenco di iFix installate." },
      { "version.option-desc.output", "\tUn percorso a un file che contiene l'output da questo comando. Questa    \n\topzione non \u00e8 obbligatoria. Il valore predefinito \u00e8 STDOUT." },
      { "version.option-desc.verbose", "\tVisualizzare tutto il contenuto di ogni file propriet\u00e0." },
      { "version.option-key.ifixes", "    --ifixes" },
      { "version.option-key.output", "    --output=\"percorso a un file di output\"" },
      { "version.option-key.verbose", "    --verbose" },
      { "version.usage.options", "\t{0} version [opzioni]" },
      { "viewLicenseAgreement.desc", "\tVisualizza l'accordo di licenza per l'edizione di Liberty Profile installata." },
      { "viewLicenseAgreement.usage.options", "\t{0} viewLicenseAgreement" },
      { "viewLicenseInfo.desc", "\tVisualizza le informazioni di licenza per l'edizione di Liberty Profile installata." },
      { "viewLicenseInfo.usage.options", "\t{0} viewLicenseInfo" }
   };
}
