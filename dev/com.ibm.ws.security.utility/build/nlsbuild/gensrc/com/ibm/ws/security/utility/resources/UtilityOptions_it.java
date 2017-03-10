/**
 * WARNING!  THIS FILE IS AUTOMATICALLY GENERATED!  DO NOT MODIFY IT!
 * Generated on Wed Mar 08 13:02:37 EST 2017
 */

package com.ibm.ws.security.utility.resources;

public class UtilityOptions_it extends java.util.ListResourceBundle
{
   public Object[][] getContents() {
       return resources;
   }
   private final static Object[][] resources= {
      { "createLTPAKeys.desc", "\tCreare una serie di chiavi LTPA per l''uso da parte del server o che possono essere condivise\n\tcon pi\u00f9 server. Se non si specifica un server o un file, viene creato un file ltpa.keys\n\tnella directory di lavoro corrente." },
      { "createLTPAKeys.option-desc.file", "\tIl file in cui scrivere le chiavi LTPA.\n\tQuesto argomento non pu\u00f2 essere utilizzato se \u00e8 utilizzato l''argomento --server." },
      { "createLTPAKeys.option-desc.password.encoding", "\tSpecifica come codificare la password delle chiavi LTPA in server.xml.\n\tLe codifiche supportate sono xor e aes. La codifica predefinita \u00e8 xor.\n\tUtilizzare il comando securityUtility encode --listCustom per vedere se sono supportate\n \tulteriori codifiche personalizzate." },
      { "createLTPAKeys.option-desc.password.key", "\tSpecifica una chiave da utilizzare durante la codifica della password delle chiavi LTPA utilizzando\n\tAES. Di questa stringa verr\u00e0 eseguito l''hash per produrre una chiave di codifica che\n\tverr\u00e0 utilizzata per codificare e decodificare la password. La chiave pu\u00f2 essere\n\tfornita al server definendo la variabile \n\twlp.password.encryption.key, il cui valore \u00e8 la chiave. Se questa opzione non\n\tviene fornita, verr\u00e0 utilizzata una chiave predefinita." },
      { "createLTPAKeys.option-desc.server", "\tIl server per cui creare le chiavi LTPA.\n\tQuesto argomento non pu\u00f2 essere utilizzato se \u00e8 utilizzato l''argomento --file." },
      { "createLTPAKeys.option-key.file", "    --file=name" },
      { "createLTPAKeys.option-key.password.encoding", "    --passwordEncoding=[xor|aes]" },
      { "createLTPAKeys.option-key.password.key", "    --passwordKey=[key]" },
      { "createLTPAKeys.option-key.server", "    --server=nome" },
      { "createLTPAKeys.required-desc.password", "\tPassword chiavi LTPA. Se non \u00e8 definito alcun valore, verr\u00e0 richiesto di specificarlo." },
      { "createLTPAKeys.required-key.password", "    --password[=pwd]" },
      { "createLTPAKeys.usage.options", "\t{0} createLTPAKeys --password[=pwd] [opzioni]" },
      { "encode.desc", "\tCodificare il testo fornito." },
      { "encode.option-desc.encoding", "\tSpecifica come codificare la password. Le codifiche supportate sono xor, aes,\n\te hash. La codifica predefinita \u00e8 xor. {2}" },
      { "encode.option-desc.key", "\tSpecifica una chiave da utilizzare quando si esegue la codifica mediante AES. Di questa stringa verr\u00e0 \n\teseguito l''hash per produrre una chiave di codifica che verr\u00e0 utilizzata per codificare e\n\tdecodificare la password. La chiave pu\u00f2 essere fornita al server\n\tdefinendo la variabile wlp.password.encryption.key il cui valore \u00e8 la\n\tchiave. Se questa opzione non viene fornita, verr\u00e0 utilizzata una chiave predefinita." },
      { "encode.option-desc.listCustom", "\tVisualizza le informazioni della codifica password personalizzata\n\tin formato JSON (JavaScript Object Notation).\n\tLe informazioni sono:\n\tname : il nome dell''algoritmo di codifica della password personalizzata\n\tfeaturename : il nome della funzione\n\tdescription : la descrizione della codifica password personalizzata" },
      { "encode.option-desc.notrim", "\tSpecifica se i caratteri spazio sono rimossi dall''inizio e\n\tdalla fine del testo specificato. Se questa opzione viene specificata, il testo\n\tfornito verr\u00e0 codificato cos\u00ec com''\u00e8.\n\tSe questa opzione non viene specificata, i caratteri spazio dall''inizio\n\te dalla fine del testo specificato verranno rimossi. " },
      { "encode.option-desc.text", "\tSe non viene specificato alcun argomento, lo strumento passer\u00e0\n\t alla modalit\u00e0 interattiva; altrimenti, il testo fornito sar\u00e0 codificato.\n\tIl testo contenente degli spazi dovr\u00e0 essere racchiuso tra virgolette se viene specificato come un argomento." },
      { "encode.option-key.encoding", "    --encoding=[xor|aes|hash{1}]" },
      { "encode.option-key.key", "    --key=[key]" },
      { "encode.option-key.listCustom", "    --listCustom" },
      { "encode.option-key.notrim", "    --notrim" },
      { "encode.option-key.text", "    textToEncode" },
      { "encode.usage.options", "\t{0} encode [opzioni]" },
      { "global.actions", "Azioni:" },
      { "global.description", "Descrizione:" },
      { "global.options", "Opzioni:" },
      { "global.options.statement", "\tUtilizzare il comando help [nomeAzione] per informazioni dettagliate sulle opzioni di ciascuna azione." },
      { "global.required", "Richiesta:" },
      { "global.usage", "Uso:" },
      { "help.desc", "\tStampare le informazioni della guida per l''azione specificata." },
      { "help.usage.options", "\t{0} help [nomeAzione]" },
      { "sslCert.desc", "\tCreare un certificato SSL predefinito per l''uso da parte della configurazione server o\n\tclient. " },
      { "sslCert.option-desc.createConfigFile", "\tFacoltativo. Il frammento di codice verr\u00e0 scritto nel file specificato\n\tinvece che nella schermata della console. Quindi, il file pu\u00f2 essere incluso nella \n\tconfigurazione di server.xml utilizzando il frammento di codice fornito." },
      { "sslCert.option-desc.keySize", "\tLa dimensione della chiave del certificato.  La dimensione chiave predefinita \u00e8 {7}." },
      { "sslCert.option-desc.password.encoding", "\tSpecifica come codificare la password del keystore. Le codifiche supportate sono\n\txor e aes. La codifica predefinita \u00e8 xor.\n\tUtilizzare il comando securityUtility encode --listCustom per vedere se sono supportate\n \tulteriori codifiche personalizzate." },
      { "sslCert.option-desc.password.key", "\tSpecifica una chiave da utilizzare durante la codifica della password del keystore utilizzando\n\tAES. Di questa stringa verr\u00e0 eseguito l''hash per produrre una chiave di codifica che\n\tverr\u00e0 utilizzata per codificare e decodificare la password. La chiave pu\u00f2 essere\n\tfornita al server definendo la variabile \n\twlp.password.encryption.key, il cui valore \u00e8 la chiave. Se questa opzione non\n\tviene fornita, verr\u00e0 utilizzata una chiave predefinita." },
      { "sslCert.option-desc.sigAlg", "\tL''algoritmo di firma del certificato.\n\tL''algoritmo di firma predefinito \u00e8 {8}." },
      { "sslCert.option-desc.subject", "\tIl DN per il soggetto e l''emittente certificato. Il DN predefinito \u00e8 basato \n\tsul nome host." },
      { "sslCert.option-desc.validity", "\tNumero di giorni di validit\u00e0 del certificato. Il periodo di validit\u00e0 predefinito \u00e8 \n\t{2}. Il periodo di validit\u00e0 minimo \u00e8 {3}." },
      { "sslCert.option-key.createConfigFile", "    --createConfigFile=file" },
      { "sslCert.option-key.keySize", "    --keySize=dimensione" },
      { "sslCert.option-key.password.encoding", "    --passwordEncoding=[xor|aes]" },
      { "sslCert.option-key.password.key", "    --passwordKey=[key]" },
      { "sslCert.option-key.sigAlg", "    --sigAlg=algoritmo di firma" },
      { "sslCert.option-key.subject", "    --subject=DN" },
      { "sslCert.option-key.validity", "    --validity=giorni" },
      { "sslCert.option.addon", "Il certificato verr\u00e0 creato con alias {4}.\nL''algoritmo chiave \u00e8 {5} e quello di firma \u00e8 {6}.\nPer ulteriore controllo sulla creazione del certificato, utilizzare direttamente keytool." },
      { "sslCert.required-desc.client", "\tIl client per cui creare il certificato.  Questo argomento non pu\u00f2 essere\n\tutilizzato se \u00e8 utilizzato l''argomento --server." },
      { "sslCert.required-desc.password", "\tLa password di keystore, minimo {1} caratteri.\n\tSe non \u00e8 definito alcun valore, verr\u00e0 richiesto di specificarlo." },
      { "sslCert.required-desc.server", "\tIl server per cui creare il certificato.  Questo argomento non pu\u00f2 essere\n\tutilizzato se \u00e8 utilizzato l''argomento --client." },
      { "sslCert.required-key.client", "    --client=nome" },
      { "sslCert.required-key.password", "    --password[=pwd]" },
      { "sslCert.required-key.server", "    --server=nome" },
      { "sslCert.usage.options", "\t{0} createSSLCertificate '{--server nomeserver|--client nomeclient'}  \n\t--password password [opzioni]" }
   };
}
