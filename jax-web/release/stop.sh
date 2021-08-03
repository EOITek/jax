#!/bin/sh

DIR=$(cd "$(dirname "$0")" && pwd)

cd $DIR

PID_FILE=jax.pid.lock

if [ -f $PID_FILE ]; then
  TARGET_ID="$(cat jax.pid.lock)"
  PID="$(ps -ef | grep $TARGET_ID | grep java | grep jax-web | awk '{print $2}')"
  if [ "x$PID" = "x" ]; then
    echo "can not find pid $TARGET_ID to stop"
  else
    echo "stopping $TARGET_ID"
    kill "$TARGET_ID"
    for i in {1..300}
    do
      pinfo=$(jps | grep "$TARGET_ID")
      if [ -n "${pinfo}" ]; then
        echo "waiting for stop ${pinfo}"
        sleep 2
      else
        break
      fi
    done
    echo "stopped $TARGET_ID"
  fi
else
  echo "can not find pid file $PID_FILE to stop"
fi
