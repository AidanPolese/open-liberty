Last update: 2013/08/13

This project (com.ibm.websphere.management.repository) is intended to
encapsulate all of the public API and SPI related to the collective repository
(the collective controller and collective member). This does contain some
non-public interfaces. This was done to provide simple classpath dependency for
class visibility of these internal packages. These internal packages should
have been moved but were not prior to GA. Since this code is now in service,
please leave them where they are, however...

Do not add ADDITIONAL non-public interfaces to this project.

