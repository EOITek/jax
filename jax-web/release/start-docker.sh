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
LoggingConfig=${JAX_HOME}/jax/logback-spring-docker.xml
ConfigLocation=classpath:/application.yml,file://${JAX_HOME}/jax/application.yml
LibDir=${JAX_HOME}/jax/lib

JAX_JAVA_OPTS="-Xmx1024m -Xms1024m"

"$JAVA" -Dloader.path=${LibDir} -jar $JAX_JAVA_OPTS -Dspring.config.location=${ConfigLocation} -Dlogging.config=${LoggingConfig} ${ProgramJar} "$@" <&-
