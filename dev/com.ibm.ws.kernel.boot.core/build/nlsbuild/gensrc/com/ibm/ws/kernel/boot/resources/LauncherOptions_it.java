/**
 * WARNING!  THIS FILE IS AUTOMATICALLY GENERATED!  DO NOT MODIFY IT!
 * Generated on Wed Mar 08 13:02:26 EST 2017
 */

package com.ibm.ws.kernel.boot.resources;

public class LauncherOptions_it extends java.util.ListResourceBundle
{
   public Object[][] getContents() {
       return resources;
   }
   private final static Object[][] resources= {
      { "action-desc.create", "\tCrea un nuovo server se il server specificato non esiste. L'opzione      \n\t--template pu\u00f2 essere utilizzata per specificare un template da utilizzare quando      \n\tsi crea un nuovo server." },
      { "action-desc.debug", "\tEsegue il server indicato nella console in primo piano dopo la connessione di un\n\tprogramma di debug alla porta di debug (valore predefinito: 7777)." },
      { "action-desc.dump", "\tEsegue il dump delle informazioni di diagnostica dal server in un archivio. \u00c8\n\tpossibile utilizzare l'opzione --archive.  L'opzione --include pu\u00f2 essere utilizzata con\n\ti valori \"heap\", \"system\" e \"thread\"." },
      { "action-desc.help", "\tStampa le informazioni della guida." },
      { "action-desc.javadump", "\tEsegue il dump delle informazioni di diagnostica dalla JVM del server. L'opzione --include\n\tpu\u00f2 essere utilizzata con i valori \"heap\" e \"system\"." },
      { "action-desc.list", "\tElenca i server delle applicazioni Liberty Profile che sono definiti." },
      { "action-desc.package", "\tCrea il package di un server in un archivio. L'opzione --archive pu\u00f2 essere utilizzata.    \n\tL'opzione --include pu\u00f2 essere utilizzata con i valori \"all\", \"usr\", \"minify\", \n\t\"wlp\", \"runnable\", \"all,runnable\" e \"minify,runnable\". I valori \n\t\"runnable\" e \"all,runnable\" sono equivalenti. il valore \"runnable\"  \n\tfunziona solo con il tipo di archivio \"jar\"." },
      { "action-desc.run", "\tEsegue il server indicato nella console in primo piano." },
      { "action-desc.start", "\tAvvia il server indicato." },
      { "action-desc.status", "\tControlla lo stato del server indicato." },
      { "action-desc.stop", "\tArresta l'istanza del server indicato in esecuzione." },
      { "action-desc.version", "\tVisualizza le informazioni relative alla versione del server e chiude." },
      { "action-key.create", "    --create" },
      { "action-key.debug", "    --debug" },
      { "action-key.dump", "    --dump" },
      { "action-key.help", "    --help" },
      { "action-key.javadump", "    --javadump" },
      { "action-key.list", "    --list" },
      { "action-key.package", "    --package" },
      { "action-key.run", "    --run" },
      { "action-key.start", "    --start" },
      { "action-key.status", "    --status" },
      { "action-key.stop", "    --stop" },
      { "action-key.version", "    --version" },
      { "briefUsage", "Utilizzo: java [opzioni JVM] -javaagent:bin/tools/ws-javaagent.jar \\        \n\t-jar bin/tools/ws-server.jar serverName [azioni] [opzioni]  " },
      { "javaAgent.desc", "\tOpzione JVM utilizzata per specificare un agent per la strumentazione.   \n\tIl runtime utilizza la strumentazione per raccogliere informazioni \n\tsulla traccia ed altre informazioni di debug. bootstrap-agent.jar \n\tdeve essere nella stessa directory del jar utilizzato per avviare il runtime." },
      { "javaAgent.key", "    -javaagent:bin/tools/ws-javaagent.jar" },
      { "option-desc.archive", "\tSpecificare la destinazione di archivio che deve essere generata dall'azione di package \n\to di dump. La destinazione pu\u00f2 essere specificata come un percorso assoluto o come \n\tun percorso relativo. Se questa opzione viene omessa, il file di archivio verr\u00e0\n\tcreato nella directory di output del server. L'estensione \n\tdel nome file di destinazione pu\u00f2 influire sul formato dell'archivio generato.       \n\tIl formato dell'archivio predefinito per l'azione di package \u00e8 \"pax\" su z/OS   \n\te \"zip\" su tutte le altre piattaforme.                                    \n\tIl formato archivio \"jar\" produce un jar ad estrazione automatica simile\n\tall'archivio del programma di installazione originale.                                      \n\tIl formato archivio \"jar\" combinato con \"runnable\" nell'opzione --include\n\tproduce un file jar eseguibile che pu\u00f2 eseguire il server Liberty dal \n\tfile jar utilizzando java -jar." },
      { "option-desc.clean", "\tElimina tutte le informazioni memorizzate nella cache relative a questa istanza del server." },
      { "option-desc.include", "\tUn elenco di valori delimitati da virgole. I valori validi variano in base\n\tall'azione." },
      { "option-desc.os", "\tSpecifica i sistemi operativi che si desidera che il server in package\n\tsupporti. Fornire un elenco di valori separati da virgole. Il valore predefinito \u00e8 any,    \n \tche indica che il server deve essere distribuibile a qualsiasi sistema operativo\n\tsupportato dall'origine.                                      \n\tPer specificare che un sistema operativo non deve essere supportato anteporre al nome\n\tun segno meno (\"-\"). Per un elenco di valori di sistemi operativi, fare riferimento\n \tal sito web OSGi Alliance al seguente URL:                  \n\thttp://www.osgi.org/Specifications/Reference#os                      \n\tQuesta opzione si applica solo alle operazioni di package e pu\u00f2 essere utilizzata\n\tcon l'opzione --include=minify. Se si esclude un sistema operativo\n \tnon \u00e8 possibile includerlo successivamente se si ripete l'operazione minify\n\tsull'archivio. " },
      { "option-desc.template", "\tSpecifica il nome del modello da utilizzare quando si crea un nuovo server. " },
      { "option-key.archive", "    --archive=\"percorso del file di archivio di destinazione\"" },
      { "option-key.clean", "    --clean" },
      { "option-key.include", "    --include=value,value,..." },
      { "option-key.os", "    --os=value,value,..." },
      { "option-key.template", "    --template=\"templateName\"" },
      { "processName.desc", "\tUn nome localmente univoco per il server; il nome pu\u00f2 essere creato    \n\tutilizzando caratteri alfanumerici Unicode (ad esempio, A-Za-z0-9), \n\tcarattere di sottolineatura (_), trattino (-), segno pi\u00f9 (+) e punto (.). Un nome server     \n\tnon pu\u00f2 iniziare con un trattino (-) o un punto (.)." },
      { "processName.key", "    serverName" },
      { "scriptUsage", "Utilizzo: {0} azione nome server [opzioni]" },
      { "use.actions", "Azioni:" },
      { "use.jvmarg", "Opzioni JVM:" },
      { "use.options", "Opzioni:" }
   };
}
