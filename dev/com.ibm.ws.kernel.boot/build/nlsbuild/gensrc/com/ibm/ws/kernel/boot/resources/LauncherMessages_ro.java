/**
 * WARNING!  THIS FILE IS AUTOMATICALLY GENERATED!  DO NOT MODIFY IT!
 * Generated on Wed Mar 08 13:02:28 EST 2017
 */

package com.ibm.ws.kernel.boot.resources;

public class LauncherMessages_ro extends java.util.ListResourceBundle
{
   public Object[][] getContents() {
       return resources;
   }
   private final static Object[][] resources= {
      { "audit.jvm.shutdown", "CWWKE0085I: Serverul {0} se opre\u015fte pentru c\u0103 JVM iese." },
      { "audit.kernelStartTime", "CWWKE0002I: Kernel-ul a pornit dup\u0103 {0}" },
      { "audit.kernelUpTime", "CWWKE0036I: Serverul {0} s-a oprit dup\u0103 {1}." },
      { "audit.kernelUpTime.client", "CWWKE0908I: Clientul {0} s-a oprit dup\u0103 {1}." },
      { "audit.launchTime", "CWWKE0001I: Serverul {0} a fost lansat." },
      { "audit.launchTime.client", "CWWKE0907I: Clientul {0} a fost lansat \u00een execu\u0163ie." },
      { "audit.licenseRestriction.base_ilan.ilan", "CWWKE0100I: Acest produs este licen\u0163iat pentru dezvoltare \u015fi utilizare \u00een produc\u0163ie limitat\u0103. Termenii de licen\u0163\u0103 pot fi vizualiza\u0163i aici: {0}" },
      { "audit.licenseRestriction.developers.ilan", "CWWKE0101I: Acest produs este licen\u0163iat pentru utilizare la dezvoltare. Termenii de licen\u0163\u0103 pot fi vizualiza\u0163i aici: {0}" },
      { "audit.licenseRestriction.early_access.ilar", "CWWKE0104I: Acest produs este un produs beta \u015fi nu poate fi utilizat \u00een produc\u0163ie. Termenii de licen\u0163\u0103 pot fi vizualiza\u0163i aici: {0}" },
      { "audit.system.exit", "CWWKE0084I: Serverul {0} se opre\u015fte pentru c\u0103 firul de execu\u0163ie {1} ({2}) a apelat metoda {3}: {4}" },
      { "error.archiveTarget", "CWWKE0011E: Parametrul --archive necesit\u0103 un argument: --archive=\"<fi\u015fier de arhivat>\"" },
      { "error.badBitmode", "CWWKE0017E: Procesul a detectat un 31-bit JVM care nu este suportat. Mediul de rulare necesit\u0103 64-bit Java 6 sau ulterior." },
      { "error.badConfigRoot", "CWWKE0010E: Fi\u015fierul server.xml necesar trebuie s\u0103 existe \u015fi s\u0103 poat\u0103 fi citit. Cale: {0} Motiv: {1}" },
      { "error.badLocation", "CWWKE0004E: Sistemul nu a putut rezolva loca\u0163iile pentru instalarea serverului." },
      { "error.badVersion", "CWWKE0042E: Nu ruleaz\u0103 versiunea Java corect\u0103. Mediul runtime necesit\u0103 Java 6 sau ulterior." },
      { "error.blst.failed.to.resolve", "CWWKE0073E: A e\u015fuat rezolvarea fi\u015fierului BLST pentru {0}." },
      { "error.blst.spec.invalid", "CWWKE0072E: Este invalid \u015firul de specifica\u0163ie BLST {0}. Specifica\u0163ia trebuie s\u0103 fie  de forma: <nume-simbolic>;versiune=\"<interval-versiune>\"." },
      { "error.bootPropsStream", "CWWKE0014E: Sistemul nu a putut deschide sau citi propriet\u0103\u0163ile bootstrap specificate. Cale: {0} Motiv: {1}" },
      { "error.bootPropsURI", "CWWKE0008E: URI-ul propriet\u0103\u0163ilor boostrap este formatat gre\u015fit. uri={0}, motiv={1}" },
      { "error.bundleInstallException", "CWWKE0032E: Au ap\u0103rut una sau mai multe excep\u0163ii la instalarea bundle-urilor de platforme." },
      { "error.cannotConvertEbcdicToAscii", "CWWKE0007E: Sistemul nu a reu\u015fit convertirea cu succes a urm\u0103torului fi\u015fier de la EBCDIC la ASCII: {0}" },
      { "error.client.runner", "CWWKE0917E: Aplica\u0163ia client a raportat o eroare." },
      { "error.client.runner.missing", "CWWKE0916E: CWWKE0916E: Caracteristica de client nu a fost activat\u0103. Examina\u0163i mesajele de eroare." },
      { "error.clientDirExists", "CWWKE0904E: Nu s-a putut crea clientul numit {0} pentru c\u0103 directorul de client {1} exist\u0103 deja." },
      { "error.clientNameCharacter", "CWWKE0900E: Numele de client specificat con\u0163ine un caracter care nu este valid (name={0}). Caracterele valide sunt: alfanumerice Unicode (de ex. 0-9, a-z, A-Z), liniu\u0163\u0103 de subliniere (_), liniu\u0163\u0103 (-), plus (+) \u015fi punct (.). Un nume de client nu poate \u00eencepe cu o liniu\u0163\u0103 (-) sau un punct (.)." },
      { "error.create.java8serverenv", "CWWKE0103E: Nu se poate crea un fi\u015fier server.env file \u00een urm\u0103toarea loca\u0163ie: {0}" },
      { "error.create.unknownJavaLevel", "CWWKE0102E: Sistemul nu poate determina nivelul specifica\u0163iei Java din propriet\u0103\u0163ile sistemului.  Valoarea nu este specificat\u0103 sau nu este specificat\u0103 corect.  Propriet\u0103\u0163ile de sistem con\u0163in valoarea urm\u0103toare pentru java.specification.level: {0}" },
      { "error.creatingNewClient", "CWWKE0902E: A ap\u0103rut o excep\u0163ie la crearea clientului {0}. clientPath: {1} Motiv: {2}" },
      { "error.creatingNewClientExists", "CWWKE0905E: A ap\u0103rut o excep\u0163ie la crearea clientului {0}. S-a detectat o activitate extern\u0103 la calea de client ({1}), a\u015fadar s-a oprit crearea clientului." },
      { "error.creatingNewClientMkDirFail", "CWWKE0906E: A ap\u0103rut o excep\u0163ie la crearea clientului {0} la loca\u0163ia {1}." },
      { "error.creatingNewServer", "CWWKE0030E: A avut loc o excep\u0163ie \u00een timpul cre\u0103rii serverului {0}. serverPath: {1} Motiv: {2}" },
      { "error.creatingNewServerExists", "CWWKE0065E: A avut loc o excep\u0163ie \u00een timpul cre\u0103rii serverului {0}. A fost detectat\u0103 activitate extern\u0103 la calea de server ({1}), prin urmare a fost oprit\u0103 crearea serverului." },
      { "error.creatingNewServerMkDirFail", "CWWKE0066E: Nu se poate crea un director pentru serverul {0} \u00een urm\u0103toarea loca\u0163ie: {1}." },
      { "error.enableIIOPTransport", "CWWKE0006E: V\u0103 rug\u0103m s\u0103 activa\u0163i caracteristica orb pentru suport ORB." },
      { "error.fileNotFound", "CWWKE0054E: Nu se poate descide fi\u015fierul {0}. " },
      { "error.frameworkDef", "CWWKE0019E: Nu s-a specificat defini\u0163ia cadrului de lucru necesar." },
      { "error.frameworkDefFile", "CWWKE0020E: Nu exist\u0103 defini\u0163ia cadrului de lucru specificat ({0})." },
      { "error.frameworkJarFile", "CWWKE0021E: Bundle-ul cadrului de lucru specificat ({0}) nu exist\u0103." },
      { "error.frameworkRestart", "CWWKE0040E: Bundle-urile de platforme nu au putut fi rezolvate fa\u0163\u0103 de cache. Reporni\u0163i serverul cu o pornire curat\u0103." },
      { "error.initLog", "CWWKE0035E: A fost o problem\u0103 cu fi\u015fierul istoric ini\u0163ial specificat. logPath={0}" },
      { "error.invalid.directory", "CWWKE0050E: Nu s-a putut ini\u0163ializa ascult\u0103torul comenzii de server. Directorul de server {0} nu este valid." },
      { "error.java.security.exception.codebase", "CWWKE0913E: O excep\u0163ie nea\u015fteptat\u0103 este prins\u0103 la \u00eencercarea de a determina loca\u0163ia de baz\u0103 de cod.  Excep\u0163ie: {0}.  " },
      { "error.kernelDef", "CWWKE0022E: Nu s-a specificat defini\u0163ia de kernel necesar\u0103." },
      { "error.kernelDefFile", "CWWKE0023E: Nu exist\u0103 defini\u0163ia de kernel specificat\u0103 ({0})." },
      { "error.loadNativeLibrary", "CWWKE0064E: Nu se poate \u00eenc\u0103rca biblioteca nativ\u0103 z/OS {0}." },
      { "error.mbean.operation.failure", "CWWKE0918E:  Nu se poate realiza opera\u0163ia cerut\u0103. Pentru detalii, consulta\u0163i istoricele serverului de profil Liberty {0} pe gazda {1} \u00een loca\u0163ia {2} pentru cererea {3}." },
      { "error.minify.missing.manifest", "CWWKE0079E: \u00cempachetarea serverului {0} a e\u015fuat din cauza unui manifest de server lips\u0103." },
      { "error.minify.unable.to.determine.features", "CWWKE0078E: Nu se pot determina caracteristicile de re\u0163inut pentru serverul {0}." },
      { "error.minify.unable.to.start.server", "CWWKE0076E: Nu se poate interoga serverul {0} pentru a determina caracteristicile de re\u0163inut." },
      { "error.minify.unable.to.stop.server", "CWWKE0077E: Nu se poate opri serverul {0}, s-a \u00eenceput determinarea caracteristicilor de re\u0163inut." },
      { "error.missingBundleException", "CWWKE0033E: N-au putut fi g\u0103site bundle-urile de platforme." },
      { "error.missingDumpFile", "CWWKE0009E: Sistemul nu poate g\u0103si urm\u0103torul fi\u015fier \u015fi acest fi\u015fier nu va fi inclus \u00een arhiva dump de server: {0}" },
      { "error.noAttachAPI", "CWWKE0046E: Nu s-a putut g\u0103si o implementare a API-ului Java Attach." },
      { "error.noExistingClient", "CWWKE0903E: Clientul specificat {0} nu exist\u0103; folosi\u0163i op\u0163iunea --create pentru a crea un nou client. clientPath: {1}" },
      { "error.noExistingServer", "CWWKE0031E: Serverul specificat {0} nu exist\u0103; utiliza\u0163i op\u0163iunea --create pentru a crea un nou server. serverPath: {1}" },
      { "error.noStartedBundles", "CWWKE0034E: Nu s-a instalat nici un bundle, v\u0103 rug\u0103m verifica\u0163i imaginea de instalare." },
      { "error.os.without.include", "CWWKE0083E: Parametrul --os poate fi folosit numai cu --include=minify." },
      { "error.package.missingLibExtractDir", "CWWKE0922E: Comanda de pachet nu se poate finaliza deoarece instalarea nu are directorul lib/extract." },
      { "error.packageServerError", "CWWKE0051E: Serverul {0} nu a putut fi \u00eempachetat." },
      { "error.pause.request.failed", "CWWKE0927E: A fost primit\u0103 o cerere de trecere \u00een pauz\u0103, dar infrastructura pentru trecerea \u00een pauz\u0103 a componentelor nu este disponibil\u0103. Cererea nu a putut fi procesat\u0103. " },
      { "error.platform.dir.not.found", "CWWKE0074E: A e\u015fuat localizarea directorului platform\u0103." },
      { "error.platformBundleException", "CWWKE0015E: Au ap\u0103rut erori interne la pornirea serverului. " },
      { "error.rasProvider", "CWWKE0038E: Nu s-a specificat furnizorul de istoric necesar." },
      { "error.rasProviderResolve", "CWWKE0039E: Nu exist\u0103 fi\u015fierul JAR al furnizorului de istorice specificat sau bundle-ul ({0})." },
      { "error.resume.request.failed", "CWWKE0928E: A fost primit\u0103 o cerere de reluare, dar infrastructura pentru trecerea \u00een pauz\u0103 a componentelor nu este disponibil\u0103. Cererea nu a putut fi procesat\u0103. " },
      { "error.secPermission", "CWWKE0016E: Fi\u015fierul JAR bootstrap necesit\u0103 configura\u0163ia de securitate AllPermission pentru a lansa mediul runtime \u015fi cadrul de lucru OSGi ({0})." },
      { "error.server.pause.failed", "CWWKE0929E: O cerere de trecere \u00een pauz\u0103 s-a finalizat, dar urm\u0103toarele componente nu au putut fi trecute \u00een pauz\u0103: {0}" },
      { "error.server.resume.failed", "CWWKE0930E: O cerere de reluare s-a finalizat, dar urm\u0103toarele componente nu s-au putut relua: {0}" },
      { "error.serverAlreadyRunning", "CWWKE0029E: O instan\u0163\u0103 a serverului {0} ruleaz\u0103 deja." },
      { "error.serverCommand.init", "CWWKE0052E: A e\u015fuat ini\u0163ializarea ascult\u0103torului de comenzi de server din cauza unei excep\u0163ii I/E {0}" },
      { "error.serverDirExists", "CWWKE0045E: Nu s-a putut crea serverul numit {0} pentru c\u0103 directorul de server {1} exist\u0103 deja." },
      { "error.serverDirPermission", "CWWKE0044E: Nu exist\u0103 nici o permisiune de scriere pentru directorul de server {0}" },
      { "error.serverJavaDumpCommandPortDisabled", "CWWKE0091E: Dump-ul de server {0} nu a putut fi oprit deoarece portul de comand\u0103 server este dezactivat." },
      { "error.serverNameCharacter", "CWWKE0012E: Numele de server specificat con\u0163ine un caracter care nu este valid (name={0}). Caracterele valide sunt: alfanumerice Unicode (de ex. 0-9, a-z, A-Z), liniu\u0163\u0103 de subliniere (_), liniu\u0163\u0103 (-), plus (+) \u015fi punct (.). Un nume de erver nu poate \u00eencepe cu liniu\u0163\u0103 (-) sau punct (.)." },
      { "error.serverStopCommandPortDisabled", "CWWKE0089E: Serverul {0} nu a putut fi oprit deoarece portul de comand\u0103 server este dezactivat." },
      { "error.set.securitymanager", "CWWKE0910E: E\u015fuare la setarea Security Manager implicit datorit\u0103 excep\u0163iei: {0}  Acest lucru survine dac\u0103 managerul de securitate a fost deja setat \u015fi metoda sa checkPermission nu-i permite s\u0103 fie \u00eenlocuit." },
      { "error.set.trace.securitymanager", "CWWKE0911E: E\u015fuare la setarea NoRethrow Security Manager datorit\u0103 excep\u0163iei: {0}.  Acest lucru survine dac\u0103 managerul de securitate a fost deja setat \u015fi metoda sa checkPermission nu-i permite s\u0103 fie \u00eenlocuit." },
      { "error.shutdownClientException", "CWWKE0047E: Agentul de control al proceselor s-a oprit cu o excep\u0163ie nea\u015fteptat\u0103 {0}" },
      { "error.specifiedLocation", "CWWKE0025E: Valoarea {0} trebuie s\u0103 fac\u0103 referire la un director. Valoarea specificat\u0103 face referire la o resurs\u0103 de fi\u015fier existent\u0103. Valoare: {1}" },
      { "error.stopServerError", "CWWKE0049E: Serverul {0} nu a putut fi oprit. " },
      { "error.unable.load.property", "CWWKE0080E: Nu se poate \u00eenc\u0103rca proprietatea {0} din fi\u015fierul {1}." },
      { "error.unable.to.package", "CWWKE0082E: Nu se poate \u00eempacheta serverul {0} din cauza unei excep\u0163ii I/E {1}." },
      { "error.unableToLaunch", "CWWKE0005E: Mediul runtime nu a putut fi lansat." },
      { "error.unableZipDir", "CWWKE0056E: Nu se poate arhiva directorul datorit\u0103 unei excep\u0163ii IO {0}. " },
      { "error.unknown.console.protocol", "CWWKE0037E: {0} nu este un protocol de consol\u0103 suportat. Protocolul telnet va fi utilizat \u00een schimb. " },
      { "error.unknown.kernel.version", "CWWKE0075E: A e\u015fuat citirea intervalului de versiuni kernel din manifestul de bootstrap." },
      { "error.unknownArgument", "CWWKE0013E: Op\u0163iune necunoscut\u0103: {0}" },
      { "error.unknownException", "CWWKE0018E: A ap\u0103rut o excep\u0163ie \u00een timp ce se lansa mediul runtime: {0}" },
      { "error.unsupportedLaunch", "CWWKE0043E: Loca\u0163ia de lansare nu este un fi\u015fier local. ({0})" },
      { "error.zosProcStart.create.pidfile", "CWWKE0105E: S-ar putea ca pornirea serverului {0} utiliz\u00e2nd procedura z/OS STC {1} s\u0103 fi e\u015fuat \u00eenainte de a fi fost creat fi\u015fierul PID. Verifica\u0163i ie\u015firea STC." },
      { "error.zosProcStart.mvs.start", "CWWKE0106E: A e\u015fuat pornirea procedurii z/OS STC {0}. Pornirea a returnat codul MGCRE {1}." },
      { "error.zosProcStart.start.length", "CWWKE0107E: Nu a putut fi pornit\u0103 procedura z/OS STC {0}. A fost dep\u0103\u015fit\u0103 lungimea maxim\u0103 a comenzii de pornire." },
      { "info.LibInventoryGenerationException", "Nu poate genera inventarul bibliotecii \u00een timpul dumpului {0} serverului." },
      { "info.addProductExtension", "CWWKE0108I: Extensia de produs {0} a fost activat\u0103 programatic. Identificatorul de produs al extensiei de produs este {1}. Loca\u0163ia de instalare a produsului pentru extensia de produs este {2}." },
      { "info.bootProp", "Propriet\u0103\u0163i boot: {0}" },
      { "info.bootstrapChange", "CWWKE0003I: Loca\u0163iile bootstrap s-au modificat, a\u015fadar serverul va fi pornit curat." },
      { "info.clientIsRunning", "Clientul {0} ruleaz\u0103." },
      { "info.clientNotExist", "Clientul {0} nu exist\u0103." },
      { "info.clientPackageComplete", "Pachetul de client {0} complet \u00een {1}." },
      { "info.clientPackageException", "A e\u015fuat pachetul de client {0}. Verifica\u0163i istoricele de client pentru detalii." },
      { "info.clientPackageUnreachable", "A e\u015fuat pachetul de client {0}. Trebuie s\u0103 opri\u0163i clientul \u00eenainte de a putea fi \u00eempachetat." },
      { "info.clientPackaging", "\u00cempachetarea clientului {0}." },
      { "info.cmdArgs", "Parametri: {0}" },
      { "info.communicate.server", "A ap\u0103rut o eroare de comunica\u0163ii \u00eentre comanda {0} \u015fi serverul {1}." },
      { "info.configNotExist", "Nu s-a specfificat server.xml" },
      { "info.configRoot", "Document configurare: {0}" },
      { "info.consolePort", "Se ascult\u0103 pe portul {0} ... " },
      { "info.days", "{0} zile" },
      { "info.defaultClient", "Clientul nu a fost specificat. Se folose\u015fte valoarea implicit\u0103: {0}" },
      { "info.defaultServer", "Nu s-a specificat serverul. Se folose\u015fte valoarea implicit\u0103: {0}" },
      { "info.frameworkRestart", "CWWKE0041I: Cache-ul platformei este nesincornizat. Se reporne\u015fte cadrul de lucru." },
      { "info.hours", "{0} ore" },
      { "info.initlogs", "Se redirec\u0163ioneaz\u0103 stdout \u015fi stderr la fi\u015fierul {0} " },
      { "info.introspect.request.received", "CWWKE0057I: Cerere introspect\u0103 primit\u0103. Serverul scap\u0103 de status." },
      { "info.java2security.started", "CWWKE0909I: Serverul {0} a pornit cu Java 2 Security activat\u0103" },
      { "info.javadump.created", "CWWKE0068I: S-a creat un dump Java: {0}" },
      { "info.javadump.request.received", "CWWKE0067I: A fost primit\u0103  o cerere de dump Java." },
      { "info.javadump.zos.system.created", "CWWKE0092I: Este finalizat\u0103 cererea Java System Transaction Dump (SYSTDUMP)." },
      { "info.list.of.defined.servers", "Urm\u0103toarele servere sunt definite relativ la directorul de utilizatori {0}." },
      { "info.minutes", "{0} minute" },
      { "info.newClientCreated", "S-a creat clientul {0}." },
      { "info.newServerCreated", "S-a creat serverul {0}." },
      { "info.no.servers.defined", "Nu este definit niciun server \u00een directorul de utilizatori {0}." },
      { "info.pauseFailedException", "Trecerea \u00een pauz\u0103 a serverului {0} a e\u015fuat. Pentru detalii verifica\u0163i \u00een istoricele de server." },
      { "info.pauseFailedException.target", "Trecerea \u00een pauz\u0103 a componentelor \u0163int\u0103 specificate ale serverului {0} a e\u015fuat. Pentru detalii verifica\u0163i \u00een istoricele de server." },
      { "info.pausedListeners", "Trecerea \u00een pauz\u0103 a serverului {0} s-a finalizat." },
      { "info.pausedListeners.target", "Trecerea \u00een pauz\u0103 a componentelor \u0163int\u0103 specificate ale serverului {0} s-a finalizat." },
      { "info.pausingListeners", "Se trece \u00een pauz\u0103 serverul {0}." },
      { "info.pausingListeners.target", "Se trec \u00een pauz\u0103 componentele \u0163int\u0103 specificate ale serverului {0}." },
      { "info.resumeFailedException", "Reluarea serverului {0} a e\u015fuat. Pentru detalii verifica\u0163i \u00een istoricele de server." },
      { "info.resumeFailedException.target", "Reluarea componentelor \u0163int\u0103 specificate ale serverului {0} a e\u015fuat. Pentru detalii verifica\u0163i \u00een istoricele de server." },
      { "info.resumedListeners", "Reluarea serverului {0} s-a finalizat." },
      { "info.resumedListeners.target", "Reluarea componentelor \u0163int\u0103 specificate ale serverului {0} s-a finalizat." },
      { "info.resumingListeners", "Se reia serverul {0}." },
      { "info.resumingListeners.target", "Se reiau componentele \u0163int\u0103 specificate ale serverului {0}." },
      { "info.runtimePackageComplete", "Pachetul runtime se finalizeaz\u0103 \u00een {0}." },
      { "info.runtimePackageException", "Pachetul runtime a e\u015fuat. Verifica\u0163i istoricele serverului pentru detalii." },
      { "info.runtimePackaging", "Se \u00eempacheteaz\u0103 runtime-ul liberty." },
      { "info.seconds", "{0} secunde" },
      { "info.server.pause.all.request.received", "CWWKE0923I: A fost primit\u0103 o cerere de a trece \u00een pauz\u0103 toate componentele capabile de pauz\u0103 din server." },
      { "info.server.pause.request.completed", "CWWKE0938I: O cerere de trecere \u00een pauz\u0103 s-a finalizat." },
      { "info.server.pause.request.received", "CWWKE0924I: A fost primit\u0103 o cerere de a trece \u00een pauz\u0103 urm\u0103toarele componente din server: {0}" },
      { "info.server.resume.all.request.received", "CWWKE0925I: A fost primit\u0103 o cerere de a relua toate componentele trecute \u00een pauz\u0103 din server." },
      { "info.server.resume.request.completed", "CWWKE0939I: O cerere de reluare s-a finalizat." },
      { "info.server.resume.request.received", "CWWKE0926I: A fost primit\u0103 o cerere de a relua urm\u0103toarele componente din server: {0}" },
      { "info.serverDumpComplete", "Dump-ul de server {0} complet \u00een {1}." },
      { "info.serverDumpCompleteZos", "Este finalizat\u0103 cererea Server {0} System Transaction Dump (SYSTDUMP)." },
      { "info.serverDumpException", "Dump server {0} e\u015fuat. Verifica\u0163i istoricele serverului pentru detalii." },
      { "info.serverDumpOptionUnsupported", "Serverul {0} nu suport\u0103 tipul de dump {1}." },
      { "info.serverDumping", "Se creeaz\u0103 un dump al serverului {0}." },
      { "info.serverIsAlreadyRunning", "Serverul {0} ruleaz\u0103 deja." },
      { "info.serverIsAlreadyRunningWithPID", "Serverul {0} ruleaz\u0103 deja cu ID-ul de proces {1}." },
      { "info.serverIsRunning", "Serverul {0} ruleaz\u0103." },
      { "info.serverIsRunningWithPID", "Serverul {0} ruleaz\u0103 cu ID de proces {1}." },
      { "info.serverLaunch", "Se lanseaz\u0103 {3} ({0}) pe {1}, versiunea {2}" },
      { "info.serverNotExist", "Serverul {0} nu exist\u0103." },
      { "info.serverNotRunning", "Serverul {0} nu ruleaz\u0103." },
      { "info.serverPackageComplete", "Pachetul de server {0} complet \u00een {1}." },
      { "info.serverPackageException", "Pachet server {0} e\u015fuat. Verifica\u0163i istoricele serverului pentru detalii." },
      { "info.serverPackageUnreachable", "Pachet server {0} e\u015fuat. Trebuie oprit \u00eenainte de a putea fi \u00eempachetat." },
      { "info.serverPackaging", "Se \u00eempacheteaz\u0103 serverul {0}." },
      { "info.serverPackagingBuildingArchive", "Se construie\u015fte arhiva pentru serverul {0}." },
      { "info.serverPackagingCollectingInformation", "Se interogheaz\u0103 serverul {0} pentru con\u0163inut." },
      { "info.serverStartException", "A e\u015fuat pornirea serverului {0}. Verifica\u0163i istoricele serverului pentru detalii." },
      { "info.serverStartUnreachable", "Starea serverului {0} nu a putut fi determinat\u0103. \u00cenl\u0103tura\u0163i fi\u015fierul {1} dac\u0103 ID  de proces {2} nu este procesul serverului." },
      { "info.serverStarted", "Serverul {0} a pornit." },
      { "info.serverStartedWithPID", "Serverul {0} a pornit cu ID de proces {1}." },
      { "info.serverStarting", "Se porne\u015fte serverul {0}." },
      { "info.serverStatusException", "Starea serverului {0} nu a putut fi determinat\u0103. Verifica\u0163i istoricele serverului pentru detalii." },
      { "info.serverStopException", "A e\u015fuat oprirea serverului {0}. Verifica\u0163i istoricele serverului pentru detalii." },
      { "info.serverStopped", "Serverul {0} s-a oprit." },
      { "info.serverStopping", "Se opre\u015fte serverul {0}." },
      { "info.serverVersion", "{0} pe {1}, versiunea {2}" },
      { "info.stop.request.received", "CWWKE0055I: Oprirea serverului a fost cerut\u0103 pe {0,date,full} la {0,time,short}. Serverul {1} se opre\u015fte." },
      { "info.syslogs", "Ie\u015fire redirec\u0163ionat\u0103 la SystemOut.log \u015fi SystemErr.log \u00een {0}" },
      { "info.unableZipFile", "Nu se poate deschide fi\u015fierul {0} din cauza {1}." },
      { "java.security.permdenied.class.info", "CWWKE0919I: Clasa care \u00eencalc\u0103 regula este: {0}" },
      { "java.security.permdenied.codebaseloc.info", "CWWKE0920I: La loca\u0163ia de baz\u0103 de codului: {0}" },
      { "unable.to.package.missing.file", "CWWKE0081E: Nu se poate \u00eempacheta serverul {0} cu filtrul de sistem de operare cerut {1} din cauza resursei lips\u0103 {2}." },
      { "warn.fileEncodingNotFound", "CWWKE0061W: Identificatorul setului de caractere codate (ccsid) z/OS pentru codarea {0} nu exist\u0103. Fi\u015fierelor text nu li se va ad\u0103uga tagul. " },
      { "warn.fingerprintUnableToMkDirs", "CWWKE0093W: Serverul nu a putut crea loca\u0163ia {0} la \u00eencercarea de a scrie fi\u015fierul {1}. " },
      { "warn.ifix.ignored", "CWWKE0060W: Se ignor\u0103 jar iFix {0} deoarece nu exist\u0103 jar {1} de baz\u0103." },
      { "warn.ifix.resource.ignored", "CWWKE0071W: Ignorarea corec\u0163iei provizorii {0} deoarece versiunea de baz\u0103 {1} nu exist\u0103." },
      { "warn.javadump.unsupported", "CWWKE0069W: Nu este suportat tipul de dump Java {0}." },
      { "warn.package.invalid.looseFile", "CWWKE0070W: Fi\u015fierul spa\u0163iat {0} este invalid." },
      { "warn.packageRuntime.include.unknownOption", "CWWKE0099W: Nu se poate folosi op\u0163iunea --include={0}, se va folosi \u00een schimb --include=wlp." },
      { "warn.packageServer.include.unknownOption", "CWWKE0058W: Nu se poate utiliza op\u0163iunea --include={0}, se va utiliza \u00een loc --include=all." },
      { "warn.registerNative", "CWWKE0063W: Nu s-a putut \u00eenregistra metoda nativ\u0103 cu numele de descriptor {0}. " },
      { "warn.unableTagFile", "CWWKE0062W: Fi\u015fierelor text nu li se va ad\u0103uga tagul deoarece serviciul __chattr a e\u015fuat cu errno {0}. " },
      { "warn.unableWriteFile", "CWWKE0059W: Nu se poate scrie fi\u015fierul {0} datorit\u0103 unei excep\u0163ii IO {1}. " },
      { "warning.java.security.permdenied", "CWWKE0912W: Politica actual\u0103 Java 2 Security a raportat o poten\u0163ial\u0103 \u00eenc\u0103lcare a permisiunii Java 2 Security. {0}Permisiune:{1}Cod:{2}{3}Urm\u0103 stiv\u0103:{4}Loca\u0163ie baz\u0103 de cod:{5}" },
      { "warning.java.security.permdenied.quickmsg", "CWWKE0921W: Politica actual\u0103 Java 2 Security a raportat o poten\u0163ial\u0103 \u00eenc\u0103lcare a permisiunii Java 2 Security. {0}" },
      { "warning.javase6.endofservice", "CWWKE0109W: Suportul pentru Java SE 6 cu WebSphere Application Server Liberty se termin\u0103 \u00een septembrie 2017. Pentru a r\u0103m\u00e2ne actual \u015fi pentru a reduce riscurile de securitate, actualiza\u0163i la Java SE 8." },
      { "warning.noPlatformCache", "CWWKE0026W: Sistemul nu a putut scrie fi\u015fierul cache platform\u0103 ({0}). Excep\u0163ie: {1}" },
      { "warning.server.pause.invalid.targets", "CWWKE0931W: A fost primit\u0103 o cerere de trecere \u00een pauz\u0103 anumite componente, dar lista de componente pe op\u0163iunea pentru \u0163int\u0103 a fost goal\u0103. Nu a fost realizat\u0103 nicio ac\u0163iune." },
      { "warning.server.pause.missing.targets", "CWWKE0935W: A fost primit\u0103 o cerere de trecere \u00een pauz\u0103, dar urm\u0103toarele componente nu au fost g\u0103site: {0}" },
      { "warning.server.pause.no.targets", "CWWKE0933W: A fost primit\u0103 o cerere de trecere \u00een pauz\u0103, dar nu a fost g\u0103sit\u0103 pe server nicio component\u0103 capabil\u0103 de pauz\u0103. Nu a fost realizat\u0103 nicio ac\u0163iune." },
      { "warning.server.resume.invalid.targets", "CWWKE0932W: A fost primit\u0103 o cerere de reluare anumite componente, dar lista de componente pe op\u0163iunea pentru \u0163int\u0103 a fost goal\u0103. Nu a fost realizat\u0103 nicio ac\u0163iune." },
      { "warning.server.resume.missing.targets", "CWWKE0936W: A fost primit\u0103 o cerere de reluare, dar urm\u0103toarele componente nu au fost g\u0103site: {0}" },
      { "warning.server.resume.no.targets", "CWWKE0934W: A fost primit\u0103 o cerere de reluare, dar nu a fost g\u0103sit\u0103 pe server nicio component\u0103 capabil\u0103 de pauz\u0103. Nu a fost realizat\u0103 nicio ac\u0163iune." },
      { "warning.server.status.missing.targets", "CWWKE0937W: A fost primit\u0103 o cerere de stare, dar urm\u0103toarele componente nu au fost g\u0103site: {0}" },
      { "warning.serverDumpCompleteCommandPortDisabled", "CWWKE0090W: Dump-ul de server {0} complet \u00een {1}.  Unele informa\u0163ii nu au putut fi ob\u0163inute deoarece portul de comand\u0103 server este dezactivat, \u00eempiedic\u00e2nd comunica\u0163ia direct\u0103 cu serverul care ruleaz\u0103." },
      { "warning.serverNotFound", "CWWKE0048W: Nu s-a putut g\u0103si o instan\u0163\u0103 ce ruleaz\u0103 a serverului cu numele {0} \u015fi directorul de server {1}." },
      { "warning.serverStartedWithPIDCommandPortDisabled", "CWWKE0088W: Pornirea serverului a fost ini\u0163ializat\u0103 pentru serverul {0} cu ID proces {1}, dar nu  este posibil s\u0103 se spun\u0103 dac\u0103 pornirea a fost finalizat\u0103 deoarece portul de comand\u0103 este dezactivat." },
      { "warning.singleClient", "CWWKE0901W: Un singur client poate fi specificat \u00een linia de comand\u0103; numele urm\u0103toare vor fi ignorate (client={0}, ignored={1})." },
      { "warning.singleServer", "CWWKE0027W: Un singur server poate fi specificat \u00een linia de comand\u0103; numele urm\u0103toare vor fi ignorate (server={0}, ignored={1})." },
      { "warning.unableToPackageLooseConfigFileCannotResolveLocSymbol", "CWWKE0094W: Serverul nu poate \u00eempacheta fi\u015fierele de aplica\u0163ii deoarece unul sau mai multe simboluri de loca\u0163ie din fi\u015fierul {0} sunt necunoscute." },
      { "warning.unableToPackageLooseConfigFileMissingPath", "CWWKE0095W: Serverul nu poate \u00eempacheta fi\u015fierele de aplica\u0163ii specificate \u00een fi\u015fierul XML de aplica\u0163ii de configura\u0163ii flexibile {0} deoarece nu le poate g\u0103si." },
      { "warning.unrecognized.command", "CWWKE0053W: Comand\u0103 nerecunoscut\u0103 {0}" },
      { "warning.zOS.java.security.permdenied1", "CWWKE0914W: Politica curent\u0103 Java 2 Security a reportat o violare poten\u0163ial\u0103 a Java 2 Security Permission. Referi\u0163i-v\u0103 la Knowledge Center pentru mai multe informa\u0163ii.{0}Permisiune:{1}Cod:{2}Loca\u0163ie de baz\u0103 cod:{3}" },
      { "warning.zOS.java.security.permdenied2", "CWWKE0915W: Politica curent\u0103 Java 2 Security a reportat o violare poten\u0163ial\u0103 a Java 2 Security Permission.  Urm\u0103rirea stiv\u0103:{0} " }
   };
}
