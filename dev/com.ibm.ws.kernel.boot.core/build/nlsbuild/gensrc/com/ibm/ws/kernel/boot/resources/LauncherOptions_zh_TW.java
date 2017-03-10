/**
 * WARNING!  THIS FILE IS AUTOMATICALLY GENERATED!  DO NOT MODIFY IT!
 * Generated on Wed Mar 08 13:02:26 EST 2017
 */

package com.ibm.ws.kernel.boot.resources;

public class LauncherOptions_zh_TW extends java.util.ListResourceBundle
{
   public Object[][] getContents() {
       return resources;
   }
   private final static Object[][] resources= {
      { "action-desc.create", "\t\u5982\u679c\u6307\u5b9a\u7684\u4f3a\u670d\u5668\u4e0d\u5b58\u5728\uff0c\u5247\u5efa\u7acb\u65b0\u4f3a\u670d\u5668\u3002\n\t--template \u9078\u9805\u53ef\u7528\u4f86\u6307\u5b9a\u5efa\u7acb\u65b0\u4f3a\u670d\u5668\u6642\n\t\u8981\u4f7f\u7528\u7684\u7bc4\u672c\u3002" },
      { "action-desc.debug", "\t\u5728\u9664\u932f\u5668\u9023\u63a5\u81f3\u9664\u932f\u57e0\uff08\u9810\u8a2d\u57e0\uff1a7777\uff09\u4e4b\u5f8c\uff0c\n\t\u5728\u4e3b\u63a7\u53f0\u524d\u666f\u4e2d\u57f7\u884c\u6307\u540d\u7684\u4f3a\u670d\u5668\u3002" },
      { "action-desc.dump", "\t\u5c07\u4f86\u81ea\u4f3a\u670d\u5668\u7684\u8a3a\u65b7\u8cc7\u8a0a\u50be\u51fa\u81f3\u4fdd\u5b58\u6a94\u3002\u53ef\u4ee5\u4f7f\u7528\n\t--archive \u9078\u9805\u3002--include \u9078\u9805\u53ef\u4ee5\n\t\u8207 \"heap\"\u3001\"system\" \u53ca \"thread\" \u503c\u642d\u914d\u4f7f\u7528\u3002" },
      { "action-desc.help", "\t\u5217\u5370\u8aaa\u660e\u8cc7\u8a0a\u3002" },
      { "action-desc.javadump", "\t\u5f9e\u4f3a\u670d\u5668 JVM \u50be\u51fa\u8a3a\u65b7\u8cc7\u8a0a\u3002--include\n\t\u9078\u9805\u53ef\u4ee5\u8207 \"heap\" \u53ca \"system\" \u503c\u642d\u914d\u4f7f\u7528\u3002" },
      { "action-desc.list", "\t\u5217\u51fa\u6240\u5b9a\u7fa9\u7684 Liberty \u8a2d\u5b9a\u6a94\u61c9\u7528\u7a0b\u5f0f\u4f3a\u670d\u5668\u3002" },
      { "action-desc.package", "\t\u5c07\u4f3a\u670d\u5668\u5305\u88dd\u6210\u4fdd\u5b58\u6a94\u3002\u53ef\u4ee5\u4f7f\u7528 --archive \u9078\u9805\u3002   \n\t--include \u9078\u9805\u7684\u503c\u53ef\u4ee5\u662f \"all\"\u3001\"usr\"\u3001\"minify\"\u3001\n\t\"wlp\"\u3001\"runnable\"\u3001\"all,runnable\" \u548c \"minify,runnable\"\u3002\n\t\"runnable\" \u548c \"all,runnable\" \u9019\u5169\u500b\u503c\u76f8\u7b49\u3002\"runnable\" \u503c\n\t\u53ea\u80fd\u8207 \"jar\" \u985e\u578b\u7684\u4fdd\u5b58\u6a94\u642d\u914d\u904b\u4f5c\u3002" },
      { "action-desc.run", "\t\u5728\u4e3b\u63a7\u53f0\u524d\u666f\u4e2d\u57f7\u884c\u6307\u540d\u7684\u4f3a\u670d\u5668\u3002" },
      { "action-desc.start", "\t\u555f\u52d5\u6307\u540d\u7684\u4f3a\u670d\u5668\u3002" },
      { "action-desc.status", "\t\u6aa2\u67e5\u6307\u540d\u4f3a\u670d\u5668\u7684\u72c0\u614b\u3002" },
      { "action-desc.stop", "\t\u505c\u6b62\u57f7\u884c\u4e2d\u7684\u6307\u5b9a\u4f3a\u670d\u5668\u5be6\u4f8b\u3002" },
      { "action-desc.version", "\t\u986f\u793a\u4f3a\u670d\u5668\u7248\u672c\u8cc7\u8a0a\u4e26\u7d50\u675f\u3002" },
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
      { "briefUsage", "\u7528\u6cd5\uff1ajava [JVM \u9078\u9805] -javaagent:bin/tools/ws-javaagent.jar \\        \n\t-jar bin/tools/ws-server.jar serverName [\u52d5\u4f5c] [\u9078\u9805]  " },
      { "javaAgent.desc", "\t\u9019\u662f JVM \u9078\u9805\uff0c\u7528\u4f86\u6307\u5b9a\u8a2d\u5099\u6e2c\u8a66\u7684\u4ee3\u7406\u7a0b\u5f0f\u3002  \n\t\u57f7\u884c\u6642\u671f\u4f7f\u7528\u8a2d\u5099\u6e2c\u8a66\u4f86\u6536\u96c6\u8ffd\u8e64\u548c\u5176\u4ed6\u9664\u932f\n\t\u8cc7\u8a0a\u3002bootstrap-agent.jar \u8207\u7528\u4f86\u555f\u52d5\u57f7\u884c\u6642\u671f\u7684 JAR\n\t\u4f4d\u65bc\u76f8\u540c\u76ee\u9304\u4e2d\u3002" },
      { "javaAgent.key", "    -javaagent:bin/tools/ws-javaagent.jar" },
      { "option-desc.archive", "\t\u6307\u5b9a\u8981\u7531\u5305\u88dd\u6216\u50be\u51fa\u52d5\u4f5c\u7522\u751f\u7684\u4fdd\u5b58\u6a94\u76ee\u6a19\u3002\n\t\u60a8\u53ef\u4ee5\u7528\u7d55\u5c0d\u8def\u5f91\u6216\u76f8\u5c0d\u8def\u5f91\u4f86\u6307\u5b9a\u6b64\u76ee\u6a19\u3002\n\t\u5982\u679c\u7701\u7565\u6b64\u9078\u9805\uff0c\u6703\u5728\u4f3a\u670d\u5668\u8f38\u51fa\u76ee\u9304\u4e2d\u5efa\u7acb\n\t\u4fdd\u5b58\u6a94\u3002\u76ee\u6a19\u6a94\u526f\u6a94\u540d\u53ef\u80fd\u6703\u5f71\u97ff\u6240\u7522\u751f\n\t\u4fdd\u5b58\u6a94\u7684\u683c\u5f0f\u3002        \n\t\u5728 z/OS \u4e2d\uff0c\u5305\u88dd\u52d5\u4f5c\u7684\u9810\u8a2d\u4fdd\u5b58\u6a94\u683c\u5f0f\u70ba \"pax\"\uff0c\n\t\u800c\u5728\u6240\u6709\u5176\u4ed6\u5e73\u53f0\u4e0a\u5247\u70ba \"zip\"\u3002\n\t\u4fdd\u5b58\u6a94\u683c\u5f0f \"jar\" \u6703\u7522\u751f\u985e\u4f3c\u65bc\u539f\u59cb\u5b89\u88dd\u7a0b\u5f0f\u4fdd\u5b58\u6a94\u7684\n\t\u81ea\u884c\u89e3\u58d3\u7e2e JAR\u3002                                     \n\t\u7576\u4fdd\u5b58\u6a94\u683c\u5f0f \"jar\" \u8207 --include \u9078\u9805\u4e0a\u7684 \"runnable\" \u5408\u4f75\u4f7f\u7528\u6642\uff0c\n\t\u6703\u7522\u751f\u4e00\u500b\u53ef\u57f7\u884c\u7684 JAR \u6a94\uff0c\u800c\u53ef\u4ee5\u5229\u7528 java -jar\uff0c\n\t\u5f9e JAR \u6a94\u4f86\u57f7\u884c Liberty \u4f3a\u670d\u5668\u3002" },
      { "option-desc.clean", "\t\u6e05\u9664\u8207\u6b64\u4f3a\u670d\u5668\u5be6\u4f8b\u76f8\u95dc\u7684\u6240\u6709\u5feb\u53d6\u8cc7\u8a0a\u3002" },
      { "option-desc.include", "\t\u4ee5\u9017\u9ede\u5340\u9694\u7684\u503c\u6e05\u55ae\u3002\u6709\u6548\u503c\u96a8\n\t\u52d5\u4f5c\u800c\u7570\u3002" },
      { "option-desc.os", "\t\u6307\u5b9a\u60a8\u5e0c\u671b\u5957\u88dd\u4f3a\u670d\u5668\u652f\u63f4\u7684\u4f5c\u696d\u7cfb\u7d71\u3002\n\t\u8acb\u63d0\u4f9b\u4ee5\u9017\u9ede\u5340\u9694\u7684\u6e05\u55ae\u3002\u9810\u8a2d\u503c\u662f any\uff0c\n\t\u8868\u793a\u4f3a\u670d\u5668\u53ef\u90e8\u7f72\u81f3\u4f86\u6e90\u6240\u652f\u63f4\u7684\n\t\u4efb\u4f55\u4f5c\u696d\u7cfb\u7d71\u3002                                     \n\t\u5982\u679c\u8981\u6307\u5b9a\u4e0d\u652f\u63f4\u67d0\u4f5c\u696d\u7cfb\u7d71\uff0c\u8acb\u5728\u8a72\u4f5c\u696d\u7cfb\u7d71\u524d\u9762\u9644\u52a0\n\t\u6e1b\u865f (\"-\") \u4f5c\u70ba\u5b57\u9996\u3002\u5982\u9700\u4f5c\u696d\u7cfb\u7d71\u503c\u6e05\u55ae\uff0c\n\t\u8acb\u53c3\u95b1 OSGi Alliance \u7db2\u7ad9\uff0c\u5b83\u4f4d\u65bc\u4e0b\u5217 URL\uff1a\n\thttp://www.osgi.org/Specifications/Reference#os                      \n\t\u9019\u500b\u9078\u9805\u53ea\u6703\u5957\u7528\u81f3\u5305\u88dd\u4f5c\u696d\uff0c\u4e26\u4e14\u53ea\u80fd\u8207\n\t--include=minify \u9078\u9805\u642d\u914d\u4f7f\u7528\u3002\u5982\u679c\u60a8\u6392\u9664\u67d0\u4f5c\u696d\u7cfb\u7d71\uff0c\n\t\u4e4b\u5f8c\u5982\u679c\u60a8\u5c0d\u4fdd\u5b58\u6a94\u91cd\u8907\u57f7\u884c\u7e2e\u88fd\u4f5c\u696d\uff0c\n\t\u5c31\u7121\u6cd5\u5305\u542b\u8a72\u4f5c\u696d\u7cfb\u7d71\u3002" },
      { "option-desc.template", "\t\u6307\u5b9a\u5efa\u7acb\u65b0\u4f3a\u670d\u5668\u6642\u8981\u4f7f\u7528\u7684\u7bc4\u672c\u540d\u7a31\u3002" },
      { "option-key.archive", "    --archive=\"\u76ee\u6a19\u4fdd\u5b58\u6a94\u7684\u8def\u5f91\"" },
      { "option-key.clean", "    --clean" },
      { "option-key.include", "    --include=value,value,..." },
      { "option-key.os", "    --os=value,value,..." },
      { "option-key.template", "    --template=\"templateName\"" },
      { "processName.desc", "\t\u4f3a\u670d\u5668\u7684\u672c\u7aef\u552f\u4e00\u540d\u7a31\uff1b\u540d\u7a31\u53ef\u4ee5\u4f7f\u7528\n\tUnicode \u82f1\u6578\u5b57\u5143\uff08\u4f8b\u5982\uff0cA-Z\u3001a-z\u30010-9\uff09\u3001\u5e95\u7dda (_)\u3001\n\t\u6a6b\u7dda (-)\u3001\u52a0\u865f (+) \u548c\u53e5\u9ede (.) \u4f86\u5efa\u69cb\u3002\u4f3a\u670d\u5668\u540d\u7a31     \n\t\u7684\u958b\u982d\u4e0d\u80fd\u662f\u6a6b\u7dda (-) \u6216\u53e5\u9ede (.)\u3002" },
      { "processName.key", "    serverName" },
      { "scriptUsage", "\u7528\u6cd5\uff1a{0} action serverName [\u9078\u9805]" },
      { "use.actions", "\u52d5\u4f5c\uff1a" },
      { "use.jvmarg", "JVM \u9078\u9805\uff1a" },
      { "use.options", "\u9078\u9805\uff1a" }
   };
}
