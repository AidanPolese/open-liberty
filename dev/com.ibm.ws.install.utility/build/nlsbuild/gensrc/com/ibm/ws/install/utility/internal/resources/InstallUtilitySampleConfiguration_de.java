/**
 * WARNING!  THIS FILE IS AUTOMATICALLY GENERATED!  DO NOT MODIFY IT!
 * Generated on Wed Mar 08 13:02:48 EST 2017
 */

package com.ibm.ws.install.utility.internal.resources;

public class InstallUtilitySampleConfiguration_de extends java.util.ListResourceBundle
{
   public Object[][] getContents() {
       return resources;
   }
   private final static Object[][] resources= {
      { "SAMPLE_CONFIG", "######################################################################\n## ## Online verf\u00fcgbares IBM WebSphere Liberty Repository verwenden ##\n## Setzen Sie die Eigenschaft useDefaultRepository auf false, um zu \n## verhindern, dass installUtility \u00fcber das Internet eine Verbindung\n## zum Repository IBM WebSphere Liberty Repository herstellt. Der \n## Internetzugriff ist standardm\u00e4\u00dfig aktiviert.\n## -------------------------------------------------------------------\n#useDefaultRepository=false\n\n######################################################################\n## ## Angepasste Repositorys verwenden ##\n## installUtility kann Assets aus komprimierten und nicht komprimierten\n## verzeichnisbasierten Repositorys und gehosteten Instanzen des Liberty\n## Asset Repository Service installieren. Geben Sie einen Repository-Namen\n## und den Verzeichnispfad, den Dateipfad oder die URL jedes angepassten\n## Repositorys an, das Liberty-Assets enth\u00e4lt.\n## Der Zugriff auf die Repositorys erfolgt in der Reihenfolge ihrer Angabe.\n\n## Geben Sie den Namen und den Verzeichnispfad, den Archivdateipfad oder\n## die URL zu verzeichnisbasierten Repositorys an.\n## -------------------------------------------------------------------\n#localRepositoryName1.url={0}\n#localRepositoryName2.url={1}\n#localRepositoryName3.url={2}\n\n## Geben Sie den Namen und die URL f\u00fcr gehostete Repositorys an.\n## -------------------------------------------------------------------\n#hostedRepositoryName1.url=http://w3.mycompany.com/repository\n#hostedRepositoryName2.url=https://w3.mycompany.com/secure/repository\n\n## Geben Sie, sofern erforderlich, die Berechtigungsnachweise jedes Repositorys an.\n## F\u00fcr eine h\u00f6here Sicherheit codieren Sie den Wert der Eigenschaft .password\n## mit der Aktion encode von securityUtility. \n## Wenn Sie den Benutzernamen und das Kennwort nicht festlegen,\n## werden Sie aufgefordert, sie anzugeben.\n## -------------------------------------------------------------------\n#hostedRepositoryName2.user=Benutzername\n#hostedRepositoryName2.password=meinKennwort\n\n######################################################################\n## ## Proxy-Server verwenden (optional) ##\n## Wenn Sie einen Proxy-Server f\u00fcr den Zugriff auf das Internet \n## verwenden, geben Sie Werte f\u00fcr die Proxy-Einstellungseigenschaften an.\n## F\u00fcr eine h\u00f6here Sicherheit codieren Sie den Wert der Eigenschaft\n## proxyPassword mit der Aktion encode von securityUtility. \n## Wenn Sie proxyUser und proxyPassword nicht definieren,\n## werden Sie aufgefordert, entsprechende Werte anzugeben.\n## -------------------------------------------------------------------\n#proxyHost=Hostname\n#proxyPort=3128\n#proxyUser=Name_des_Proxy-Benutzers\n#proxyPassword=meinProxyKennwort" }
   };
}
