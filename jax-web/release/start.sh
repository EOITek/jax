#!/bin/sh

DIR=$(cd "$(dirname "$0")" && pwd)

cd $DIR

if [ -x "$JAVA_HOME/bin/java" ]; then
    JAVA="$JAVA_HOME/bin/java"
else
    echo "JAVA_HOME is not set and java could not be found."
    exit 1
fi

export JAX_HOME=$DIR

ProgramJar=${JAX_HOME}/jax/jax-web-*.jar
LoggingConfig=${JAX_HOME}/jax/logback-spring.xml
ConfigLocation=classpath:/application.yml,file://${JAX_HOME}/jax/application.yml
LibDir=${JAX_HOME}/jax/lib

JAX_JAVA_OPTS="-Xmx1024m -Xms1024m"

# Set Debug options if enabled
if [ "x$JAX_WEB_DEBUG_PORT" != "x" ]; then
    # Use the defaults if JAVA_DEBUG_OPTS was not set
    DEFAULT_JAVA_DEBUG_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=$JAX_WEB_DEBUG_PORT"
    if [ -z "$JAVA_DEBUG_OPTS" ]; then
        JAVA_DEBUG_OPTS="$DEFAULT_JAVA_DEBUG_OPTS"
    fi
	echo "Enabling Java debug options: $JAVA_DEBUG_OPTS"
    JAX_JAVA_OPTS="$JAVA_DEBUG_OPTS $JAX_JAVA_OPTS"
fi

exec "$JAVA" -Dloader.path=${LibDir} -jar $JAX_JAVA_OPTS -Dspring.config.location=${ConfigLocation} -Dlogging.config=${LoggingConfig} ${ProgramJar} "$@" <&- &
