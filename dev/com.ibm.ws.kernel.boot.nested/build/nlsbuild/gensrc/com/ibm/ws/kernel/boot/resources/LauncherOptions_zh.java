/**
 * WARNING!  THIS FILE IS AUTOMATICALLY GENERATED!  DO NOT MODIFY IT!
 * Generated on Wed Mar 08 13:02:27 EST 2017
 */

package com.ibm.ws.kernel.boot.resources;

public class LauncherOptions_zh extends java.util.ListResourceBundle
{
   public Object[][] getContents() {
       return resources;
   }
   private final static Object[][] resources= {
      { "action-desc.create", "\t\u5f53\u6307\u5b9a\u7684\u670d\u52a1\u5668\u4e0d\u5b58\u5728\u65f6\uff0c\u521b\u5efa\u65b0\u670d\u52a1\u5668\u3002\n\t--template \u9009\u9879\u53ef\u7528\u4e8e\u6307\u5b9a\u521b\u5efa\u65b0\u670d\u52a1\u5668\u65f6\u8981\u4f7f\u7528\u7684\u6a21\u677f\u3002" },
      { "action-desc.debug", "\t\u5728\u8c03\u8bd5\u5668\u8fde\u63a5\u81f3\u8c03\u8bd5\u7aef\u53e3\uff08\u7f3a\u7701\u503c\uff1a7777\uff09\u4e4b\u540e\uff0c\n\t\u5728\u63a7\u5236\u53f0\u524d\u666f\u4e2d\u8fd0\u884c\u6307\u5b9a\u7684\u670d\u52a1\u5668\u3002" },
      { "action-desc.dump", "\t\u5c06\u8bca\u65ad\u4fe1\u606f\u4ece\u670d\u52a1\u5668\u8f6c\u50a8\u5230\u5f52\u6863\u4e2d\u3002\n\t\u53ef\u4ee5\u4f7f\u7528 --archive \u9009\u9879\u3002--include \u9009\u9879\u53ef\u4ee5\u4e0e\n\t\u201cheap\u201d\u3001\u201csystem\u201d\u548c\u201cthread\u201d\u503c\u914d\u5408\u4f7f\u7528\u3002" },
      { "action-desc.help", "\t\u663e\u793a\u5e2e\u52a9\u4fe1\u606f\u3002" },
      { "action-desc.javadump", "\t\u8f6c\u50a8\u6765\u81ea\u670d\u52a1\u5668 JVM \u7684\u8bca\u65ad\u4fe1\u606f\u3002--include\n\t\u9009\u9879\u53ef\u4ee5\u4e0e\u201cheap\u201d\u548c\u201cthread\u201d\u503c\u914d\u5408\u4f7f\u7528\u3002" },
      { "action-desc.list", "\t\u5217\u793a\u5df2\u5b9a\u4e49\u7684 Liberty \u6982\u8981\u6587\u4ef6\u5e94\u7528\u7a0b\u5e8f\u670d\u52a1\u5668\u3002" },
      { "action-desc.package", "\t\u5c06\u670d\u52a1\u5668\u5c01\u88c5\u5230\u5f52\u6863\u4e2d\u3002\u53ef\u4f7f\u7528 --archive \u9009\u9879\u3002   \n\t --include \u9009\u9879\u7684\u503c\u53ef\u4e3a\u201call\u201d\u3001\u201cusr\u201d\u3001\u201cminify\u201d\u3001\n\t\u201cwlp\u201d\u3001\u201crunnable\u201d\u3001\u201call,runnable\u201d\u548c\u201cminify,runnable\u201d\u3002\n\t\u201crunnable\u201d\u548c\u201call,runnable\u201d\u8fd9\u4e24\u4e2a\u503c\u7b49\u4ef7\u3002\n\t\u503c\u201crunnable\u201d\u4ec5\u9002\u7528\u4e8e\u201cjar\u201d\u7c7b\u578b\u7684\u5f52\u6863\u3002" },
      { "action-desc.run", "\t\u5728\u63a7\u5236\u53f0\u524d\u666f\u4e2d\u8fd0\u884c\u6307\u5b9a\u7684\u670d\u52a1\u5668\u3002" },
      { "action-desc.start", "\t\u542f\u52a8\u6307\u5b9a\u7684\u670d\u52a1\u5668\u3002" },
      { "action-desc.status", "\t\u68c0\u67e5\u6307\u5b9a\u670d\u52a1\u5668\u7684\u72b6\u6001\u3002" },
      { "action-desc.stop", "\t\u505c\u6b62\u6307\u5b9a\u670d\u52a1\u5668\u7684\u6b63\u5728\u8fd0\u884c\u7684\u5b9e\u4f8b\u3002" },
      { "action-desc.version", "\t\u663e\u793a\u670d\u52a1\u5668\u7248\u672c\u4fe1\u606f\u5e76\u9000\u51fa\u3002" },
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
      { "briefUsage", "\u7528\u6cd5\uff1ajava [JVM options] -javaagent:bin/tools/ws-javaagent.jar \\        \n\t-jar bin/tools/ws-server.jar serverName [actions] [options]  " },
      { "javaAgent.desc", "\t\u8fd9\u662f\u7528\u4e8e\u6307\u5b9a\u4ee3\u7406\u7a0b\u5e8f\u4ee5\u8fdb\u884c\u68c0\u6d4b\u7684 JVM \u9009\u9879\u3002\n\t\u8fd0\u884c\u65f6\u4f7f\u7528\u68c0\u6d4b\u6765\u6536\u96c6\u8ddf\u8e2a\u548c\u5176\u4ed6\u8c03\u8bd5\u4fe1\u606f\u3002\n\tbootstrap-agent.jar \u4e0e\u7528\u4e8e\u542f\u52a8\u8fd0\u884c\u65f6\u7684\n\tJAR \u4f4d\u4e8e\u540c\u4e00\u76ee\u5f55\u4e2d\u3002" },
      { "javaAgent.key", "    -javaagent:bin/tools/ws-javaagent.jar" },
      { "option-desc.archive", "\t\u6307\u5b9a\u8981\u7531\u6253\u5305\u64cd\u4f5c\u6216\u8f6c\u50a8\u64cd\u4f5c\u751f\u6210\u7684\u5f52\u6863\n\t\u76ee\u6807\u3002\u53ef\u91c7\u7528\u7edd\u5bf9\u8def\u5f84\u6216\u76f8\u5bf9\u8def\u5f84\u7684\u5f62\u5f0f\u6307\u5b9a\u8be5\n\t\u76ee\u6807\u3002\u5982\u679c\u7701\u7565\u6b64\u9009\u9879\uff0c\u90a3\u4e48\u5c06\u5728\n\t\u670d\u52a1\u5668\u8f93\u51fa\u76ee\u5f55\u4e2d\u521b\u5efa\u8be5\u5f52\u6863\u6587\u4ef6\u3002\u76ee\u6807\u6587\u4ef6\u540d\n\t\u7684\u6269\u5c55\u540d\u53ef\u80fd\u5f71\u54cd\u6240\u751f\u6210\u5f52\u6863\u6587\u4ef6\u7684\u683c\u5f0f\u3002        \n\t\u6253\u5305\u64cd\u4f5c\u7684\u7f3a\u7701\u5f52\u6863\u683c\u5f0f\u4e3a\u201cpax\u201d\uff08\u5728 z/OS \u4e0a\uff09\n\t\u548c\u201czip\u201d\uff08\u5728\u6240\u6709\u5176\u4ed6\u5e73\u53f0\u4e0a\uff09\u3002\n\t\u5f52\u6863\u683c\u5f0f\u201cjar\u201d\u5c06\u751f\u6210\u4e0e\u539f\u59cb\u5b89\u88c5\u7a0b\u5e8f\n\t\u5f52\u6863\u76f8\u4f3c\u7684\u81ea\u62bd\u53d6 JAR\u3002\n\t\u5c06\u5f52\u6863\u683c\u5f0f\u201cjar\u201d\u4e0e --include \u9009\u9879\u4e0a\u7684\u201crunnable\u201d\u7ec4\u5408\u4f7f\u7528\n\t\u5c06\u751f\u6210\u53ef\u8fd0\u884c\u7684 JAR \u6587\u4ef6\uff0c\u53ef\u4ee5\u4ece\u8be5 JAR \u6587\u4ef6\n\t\u4f7f\u7528 java -jar \u6765\u8fd0\u884c Liberty \u670d\u52a1\u5668\u3002" },
      { "option-desc.clean", "\t\u6e05\u9664\u4e0e\u6b64\u670d\u52a1\u5668\u5b9e\u4f8b\u76f8\u5173\u7684\u6240\u6709\u9ad8\u901f\u7f13\u5b58\u4fe1\u606f\u3002" },
      { "option-desc.include", "\t\u9017\u53f7\u5206\u9694\u7684\u503c\u5217\u8868\u3002\u6709\u6548\u503c\u89c6\u64cd\u4f5c\n\t\u7684\u4e0d\u540c\u800c\u6709\u6240\u4e0d\u540c\u3002" },
      { "option-desc.os", "\t\u6307\u5b9a\u5e0c\u671b\u6253\u5305\u670d\u52a1\u5668\u652f\u6301\u7684\n\t\u64cd\u4f5c\u7cfb\u7edf\u3002\u63d0\u4f9b\u9017\u53f7\u5206\u9694\u7684\u5217\u8868\u3002\u7f3a\u7701\u503c\u4e3a any\uff0c\n\t\u6307\u793a\u670d\u52a1\u5668\u53ef\u90e8\u7f72\u5230\u6e90\u652f\u6301\u7684\n\t\u4efb\u4f55\u64cd\u4f5c\u7cfb\u7edf\u3002                                     \n\t\u8981\u6307\u5b9a\u4e0d\u652f\u6301\u64cd\u4f5c\u7cfb\u7edf\uff0c\u8bf7\u5bf9\u5176\u6dfb\u52a0\n\t\u51cf\u53f7\uff08\u201c-\u201d\uff09\u524d\u7f00\u3002\u6709\u5173\u64cd\u4f5c\u7cfb\u7edf\u503c\u7684\u5217\u8868\uff0c\u8bf7\u53c2\u9605\n\t\u4f4d\u4e8e\u4ee5\u4e0b URL \u7684 OSGi Alliance Web \u7ad9\u70b9\uff1a\n\thttp://www.osgi.org/Specifications/Reference#os                      \n\t\u6b64\u9009\u9879\u4ec5\u9002\u7528\u4e8e\u8f6f\u4ef6\u5305\u64cd\u4f5c\uff0c\u4ec5\u53ef\u7528\u4e8e\n\to--include=minify \u9009\u9879\u3002\u5982\u679c\u6392\u9664\u64cd\u4f5c\n\t\u7cfb\u7edf\uff0c\u90a3\u4e48\u5728\u4ee5\u540e\u9488\u5bf9\u5f52\u6863\u91cd\u590d\u5408\u5e76\u538b\u7f29\u64cd\u4f5c\u7684\u60c5\u51b5\u4e0b\uff0c\u65e0\u6cd5\u5305\u542b\n\t\u6b64\u64cd\u4f5c\u7cfb\u7edf\u3002" },
      { "option-desc.template", "\t\u6307\u5b9a\u521b\u5efa\u65b0\u670d\u52a1\u5668\u65f6\u8981\u4f7f\u7528\u7684\u6a21\u677f\u7684\u540d\u79f0\u3002" },
      { "option-key.archive", "    --archive=\"\u6307\u5411\u76ee\u6807\u5f52\u6863\u6587\u4ef6\u7684\u8def\u5f84\"" },
      { "option-key.clean", "    --clean" },
      { "option-key.include", "    --include=value,value,..." },
      { "option-key.os", "    --os=value,value,..." },
      { "option-key.template", "    --template=\"templateName\"" },
      { "processName.desc", "\t\u670d\u52a1\u5668\u7684\u672c\u5730\u552f\u4e00\u540d\u79f0\uff1b\u53ef\u4ee5\n\t\u4f7f\u7528 Unicode \u5b57\u6bcd\u6570\u5b57\u5b57\u7b26\uff08\u4f8b\u5982\uff0cA-Z\u3001a-z\u30010-9\uff09\u3001\u4e0b\u5212\u7ebf (_)\u3001\n\t\u77ed\u5212\u7ebf (-)\u3001\u52a0\u53f7 (+) \u548c\u53e5\u70b9 (.) \u6784\u9020\u540d\u79f0\u3002\u670d\u52a1\u5668\u540d\u79f0\u4e0d\u80fd\u4ee5\u77ed\u5212\u7ebf (-) \u6216\u53e5\u70b9 (.) \u5f00\u5934\u3002" },
      { "processName.key", "    serverName" },
      { "scriptUsage", "\u7528\u6cd5\uff1a{0} \u64cd\u4f5c serverName [\u9009\u9879]" },
      { "use.actions", "\u64cd\u4f5c\uff1a" },
      { "use.jvmarg", "JVM \u9009\u9879\uff1a" },
      { "use.options", "\u9009\u9879\uff1a" }
   };
}
