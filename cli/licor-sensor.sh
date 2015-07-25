#!/bin/bash

CLASSPATH="../lib/*"

if [ -n "${JAVA_HOME}" -a -x "${JAVA_HOME}/bin/java" ]; then
 java="${JAVA_HOME}/bin/java"
else
 java=java
fi

exec "${java}" -client -classpath "${CLASSPATH}" -Djava.util.logging.config.file=logging.properties fi.uef.envi.emrooz.io.licor.GHGSensorObservationReader "$@"
