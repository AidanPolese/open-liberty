#
# Dockerfile to create a Node.js image
#
# This Dockerfile makes the following assumptions:
# - the collectives enabling scripts (joinMember, removeMember) will be placed in /opt/ibm/docker in the Docker container
# - Node.js servers are created at /root/wlpn
# - the Node.js server will be "defaultServer" (I.e. /root/wlpn/defaultServer)
# - the application to install will already have its dependencies resolved by "npm install"
# - the Docker container will be executed using --net=host, so that no ports are explicitly exported in this file

FROM node:4-slim

#
# Auto-start the Node.js server with the container
#
ENTRYPOINT ["/usr/local/bin/wlpn-server", "run", "defaultServer"]

#
# Add collectives enabling scripts
#
ADD joinMember /opt/ibm/docker/
RUN chmod +x /opt/ibm/docker/joinMember
ADD removeMember /opt/ibm/docker/
RUN chmod +x /opt/ibm/docker/removeMember

#
# Update the OS and JRE
#
RUN apt-get update

#
# Install APIConnect collective member
#
RUN npm install -g apiconnect-collective-member


#
# Use the below to install an application for use onto the Docker image
#
COPY express-example-app-1.0.2.tgz /root/wlpn/
RUN wlpn-server unpack defaultServer /root/wlpn/express-example-app-1.0.2.tgz


