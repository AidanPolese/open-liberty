When adding or removing a feature from the beta packaging, you must update
the following files:

beta_features.xml
 - this file contains the list of all the beta features
   and is used to package the zip

templates/servers/defaultServer/server.xml
 - this file is created when a server is created from the beta installation
   and should contain each beta feature (and any meaningful configuration)
   listed in beta_features.xml

When adding or removing a feature, check with the release architect or
Liberty runtime architect for approval.
