#!/bin/sh
set -e

# Railway dynamically assigns $PORT
PORT="${PORT:-8080}"
echo "Configuring Tomcat to listen on port: $PORT"

# Replace standard port 8080 with dynamic $PORT in server.xml
sed -i "s/port=\"8080\"/port=\"$PORT\"/g" /usr/local/tomcat/conf/server.xml

echo "Starting Apache Tomcat 10..."
exec catalina.sh run
