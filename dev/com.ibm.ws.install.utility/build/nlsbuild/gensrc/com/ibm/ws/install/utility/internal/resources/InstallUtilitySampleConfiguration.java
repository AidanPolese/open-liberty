/**
 * WARNING!  THIS FILE IS AUTOMATICALLY GENERATED!  DO NOT MODIFY IT!
 * Generated on Wed Mar 08 13:02:48 EST 2017
 */

package com.ibm.ws.install.utility.internal.resources;

public class InstallUtilitySampleConfiguration extends java.util.ListResourceBundle
{
   public Object[][] getContents() {
       return resources;
   }
   private final static Object[][] resources= {
      { "SAMPLE_CONFIG", "######################################################################\n## ## Using the online IBM WebSphere Liberty Repository ##\n## Set the useDefaultRepository property to false to prevent\n## installUtility from accessing the internet to connect to the\n## IBM WebSphere Liberty Repository repository. By default, access\n## is enabled.\n## -------------------------------------------------------------------\n#useDefaultRepository=false\n\n######################################################################\n## ## Using custom repositories ##\n## installUtility can install assets from compressed or uncompressed\n## directory-based repositories and hosted instances of the Liberty\n## Asset Repository Service. Provide a repository name and the\n## directory path, the file path, or the URL to each custom repository\n## containing Liberty assets.\n## The repositories are accessed in the order that they are specified.\n\n## Specify the name and directory path, archive file path, or URL\n## to directory-based repositories.\n## -------------------------------------------------------------------\n#localRepositoryName1.url={0}\n#localRepositoryName2.url={1}\n#localRepositoryName3.url={2}\n\n## Specify the name and URL to hosted repositories.\n## -------------------------------------------------------------------\n#hostedRepositoryName1.url=http://w3.mycompany.com/repository\n#hostedRepositoryName2.url=https://w3.mycompany.com/secure/repository\n\n## Specify the credentials of each repository, if required.\n## For enhanced security, encode the value of the .password\n## property by using the securityUtility encode action.\n## If you do not set the user and password, you receive a prompt\n## to provide them.\n## -------------------------------------------------------------------\n#hostedRepositoryName2.user=username\n#hostedRepositoryName2.password=myPassword\n\n######################################################################\n## ## Using a proxy server (optional) ##\n## If you use a proxy server to access the internet,\n## specify values for the proxy settings properties.\n## For enhanced security, encode the value of the proxyPassword\n## property by using the securityUtility encode action.\n## If you do not set the proxyUser and proxyPassword, you receive\n## a prompt to provide them.\n## -------------------------------------------------------------------\n#proxyHost=hostName\n#proxyPort=3128\n#proxyUser=proxyUsername\n#proxyPassword=myProxyPassword" }
   };
}
