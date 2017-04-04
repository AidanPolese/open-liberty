Last update: 2014/12/1

This project (com.ibm.ws.webserver.plugin.utility) implements the bin/pluginUtility
command-line utility. It it based on the com.ibm.ws.collective.utility project.
Currently the exposed tasks are: generate merge DynamicRouting help


*NOTE* Only extend this functionality judiciously. To add new task see below.

-------------------------------------------------------------------------------

To add a new task to the script:

1. Create a class in: com.ibm.ws.webserver.plugin.utility.tasks
2. Register it in the WebServerPluginUtility main() method.
   Note the order in which it is registered will dictate the order it is
   listed in help.
