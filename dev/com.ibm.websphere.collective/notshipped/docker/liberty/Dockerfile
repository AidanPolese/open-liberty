#
# Dockerfile to create a Liberty ND image
#
# This Dockerfile makes the following assumptions:
# - the collectives enabling scripts (joinMember, removeMember) will be placed in /opt/ibm/docker in the Docker container
# - WebSphere Liberty will be installed at /opt/ibm/wlp
# - the WebSphere Liberty server will be "defaultServer" (I.e. /opt/ibm/wlp/usr/servers/defaultServer)
# - the Docker container will be executed using --net=host, so that no ports are explicitly exported in this file

FROM websphere-liberty:beta

#
# Auto-start the Liberty server with the container
#
ENTRYPOINT ["/opt/ibm/wlp/bin/server", "run", "defaultServer"]

#
# Add collectives enabling scripts
#
ADD joinMember /opt/ibm/docker/
RUN chmod +x /opt/ibm/docker/joinMember
ADD removeMember /opt/ibm/docker/
RUN chmod +x /opt/ibm/docker/removeMember

#
# Update the OS
#
RUN apt-get update

#
# Add the additional required features
#
RUN installUtility install --acceptLicense scalingMember-1.0 clusterMember-1.0 collectiveMember-1.0

ADD server.xml /opt/ibm/wlp/usr/servers/defaultServer/

#
# Use the below to copy an application for use onto the Docker image
#
# ADD ShowMyIpServlet.war /opt/ibm/wlp/usr/servers/defaultServer/dropins/
