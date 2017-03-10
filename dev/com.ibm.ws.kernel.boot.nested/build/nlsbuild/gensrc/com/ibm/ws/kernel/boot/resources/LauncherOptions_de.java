/**
 * WARNING!  THIS FILE IS AUTOMATICALLY GENERATED!  DO NOT MODIFY IT!
 * Generated on Wed Mar 08 13:02:27 EST 2017
 */

package com.ibm.ws.kernel.boot.resources;

public class LauncherOptions_de extends java.util.ListResourceBundle
{
   public Object[][] getContents() {
       return resources;
   }
   private final static Object[][] resources= {
      { "action-desc.create", "\tNeue Server erstellen, wenn der angegebene Server nicht vorhanden ist. Die Option \n\t--template kann verwendet werden, um eine Vorlage anzugeben, die zum Erstellen eines \n\tneuen Servers verwendet werden soll." },
      { "action-desc.debug", "\tF\u00fchrt den benannten Server im Vordergrund der Konsole aus, nachdem\n\tein Debugger eine Verbindung zum Debug-Port (standardm\u00e4\u00dfig 7777) hergestellt hat." },
      { "action-desc.dump", "\tSpeichert Diagnoseinformationen des Servers in einem Archiv. Die Option \n\t--archive kann verwendet werden. Die Option --include kann mit den Werten\n\t\"heap\", \"system\" und \"thread\" verwendet werden." },
      { "action-desc.help", "\tGibt die Hilfeinformationen aus." },
      { "action-desc.javadump", "\tErstellt einen Speicherauszug der Diagnoseinformationen aus der Server-JVM. Die \n\tOption --include kann mit den Werten \"heap\" und \"system\" verwendet werden." },
      { "action-desc.list", "\tListet die definierten Anwendungsserver des Liberty-Profils auf." },
      { "action-desc.package", "\tServer in ein Archiv packen. Die Option --archive kann verwendet werden.    \n\tDie Option --include kann zusammen mit den Werten \"all\", \"usr\", \n\t\"minify\", \"wlp\", \"runnable\", \"all,runnable\" und \"minify,runnable\"\n\tverwendet werden. Die Werte \"runnable\" und \"all,runnable\" sind \n\t\u00e4quivalent. Der Wert \"runnable\" funktioniert nur f\u00fcr Archive\n\tdes Typs \"jar\"." },
      { "action-desc.run", "\tF\u00fchrt den angegebenen Server im Vordergrund der Konsole aus." },
      { "action-desc.start", "\tStartet den angegebenen Server." },
      { "action-desc.status", "\tPr\u00fcft den Status des angegebenen Servers." },
      { "action-desc.stop", "\tAktive Instanz des angegebenen Servers stoppen." },
      { "action-desc.version", "\tVersionsinformationen des Servers anzeigen und beenden." },
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
      { "briefUsage", "Syntax: java [JVM-Optionen] -javaagent:bin/tools/ws-javaagent.jar \\        \n\t-jar bin/tools/ws-server.jar Servername [Aktionen] [Optionen]  " },
      { "javaAgent.desc", "\tMit dieser JVM-Option wird ein Agent f\u00fcr die Instrumentierung angegeben.   \n\tDie Laufzeitumgebung verwendet Instrumentierung f\u00fcr die Erfassung von Trace- und anderen\n\tDebuginformationen. Die Datei bootstrap-agent.jar befindet sich in demselben Verzeichnis wie\n\tdie JAR-Datei, die zum Starten der Laufzeitumgebung verwendet wird." },
      { "javaAgent.key", "    -javaagent:bin/tools/ws-javaagent.jar" },
      { "option-desc.archive", "\tGeben Sie das durch das Paket oder Speicherauszugsaktion zu generierende \n\tArchivierungsziel an. Das Ziel kann als absoluter Pfad oder als relativer Pfad \n\tangegeben werden. Wird diese Option ausgelassen, wird die Archivdatei im\n\tAusgabeverzeichnis des Servers erstellt. Die Erweiterung des Zieldateinamens \n\tkann Einfluss auf das Format des generierten Archivs haben.         \n\tDas Standardarchivformat f\u00fcr die Paketaktion ist \"pax\" unter z/OS   \n\tund \"zip\" auf allen anderen Plattformen. \n\tDas Archivformat \"jar\" erzeugt eine selbst extrahierende JAR-Datei, die dem \n\turspr\u00fcnglichen Installationsarchiv gleicht. \n\tDas Archivformat \"jar\" erzeugt in Kombination mit \"runnable\" \n\tin der Option --include eine ausf\u00fchrbare JAR-Datei, mit der \n\tder Liberty-Server unter Verwendung von java -jar ausgef\u00fchrt\n\twerden kann." },
      { "option-desc.clean", "\tAlle zwischengespeicherten Informationen bereinigen, \n\tdie sich auf diese Serverinstanz beziehen." },
      { "option-desc.include", "\tEine durch Kommas begrenzte Liste mit Werten. Die g\u00fcltigen Werte variieren je\n\tnach Aktion." },
      { "option-desc.os", "\tGibt die Betriebssysteme an, die der gepackte Server unterst\u00fctzen\n\tsoll. Geben Sie eine durch Kommas getrennte Liste an. Der \n\tStandardwert ist any, d. h., der Server muss unter jedem von der \n\tQuelle unterst\u00fctzten Betriebssystem implementierbar sein. Um\n\tanzugeben, dass ein Betriebssystem nicht unterst\u00fctzt wird, stellen\n\tSie diesem Betriebssystem ein Minuszeichen (\"-\") voran. Eine \n\tListe der Betriebssystemwerte finden Sie auf der OSGi Alliance-Website\n\tunter der folgenden URL: \n\thttp://www.osgi.org/Specifications/Reference#os                      \n\tDiese Option gilt nur f\u00fcr die Paketoperation und kann nur mit der \n\tOption --include=minify verwendet werden. Wenn Sie ein Betriebssystem\n\tausschlie\u00dfen, ist es nicht m\u00f6glich, es sp\u00e4ter durch Wiederholung einer \n\tminify-Operation wieder in das Archiv einzuschlie\u00dfen. " },
      { "option-desc.template", "\tGibt den Namen der Schablone an, die beim Erstellen eines neuen Servers verwendet werden soll." },
      { "option-key.archive", "    --archive=\"Pfad zur Zielarchivdatei\"" },
      { "option-key.clean", "    --clean" },
      { "option-key.include", "    --include=Wert,Wert,..." },
      { "option-key.os", "    --os=Wert,Wert,..." },
      { "option-key.template", "    --template=\"Schablonenname\"" },
      { "processName.desc", "\tEin lokal eindeutiger Name f\u00fcr den Server. Der Name kann aus \n\talphanumerischen Unicode-Zeichen (z. B. A-Za-z0-9), Unterstreichungszeichen (_), \n\tMinuszeichen (-), Pluszeichen (+) und Punkten (.) gebildet werden. Ein Servername \n\tdarf nicht mit einem Minuszeichen (-) oder mit einem Punkt (.) beginnen." },
      { "processName.key", "    Servername" },
      { "scriptUsage", "Syntax: {0} Aktion Servername [Optionen]" },
      { "use.actions", "Aktionen:" },
      { "use.jvmarg", "JVM-Optionen:" },
      { "use.options", "Optionen:" }
   };
}
