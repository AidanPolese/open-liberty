#!/bin/bash
set -e
export ANT_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5005"
# now execute ant
exec ant "$@"
