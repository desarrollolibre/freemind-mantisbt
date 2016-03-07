#!/bin/bash

FREEMIND_HOME="/usr/share/freemind"
FREEMIND_PLUGINS_FOLDER="$FREEMIND_HOME/plugins"
FREEMIND_MANTISBT="freemind-mantisbt"
PLUGIN_JAR="freemind-mantisbt-0.0.1.jar"

if [[ ! -e "$FREEMIND_PLUGINS_FOLDER/$FREEMIND_MANTISBT" ]]; then
	sudo mkdir $FREEMIND_PLUGINS_FOLDER/$FREEMIND_MANTISBT
fi

echo "Packaging..."
# take the compiled code and package it in its distributable format
mvn package

sudo cp target/$PLUGIN_JAR $FREEMIND_PLUGINS_FOLDER/$FREEMIND_MANTISBT/freemind-mantisbt.jar
echo "target/$PLUGIN_JAR copied to $FREEMIND_PLUGINS_FOLDER/$FREEMIND_MANTISBT/freemind-mantisbt.jar"

sudo cp src/main/resources/freemind-mantisbt.xml $FREEMIND_PLUGINS_FOLDER
echo "src/main/resources/freemind-mantisbt.xml copied to $FREEMIND_PLUGINS_FOLDER"

sudo cp ~/.m2/repository/axis/axis/1.3/axis-1.3.jar $FREEMIND_PLUGINS_FOLDER/$FREEMIND_MANTISBT/
echo "~/.m2/repository/axis/axis/1.3/axis-1.3.jar copied to $FREEMIND_PLUGINS_FOLDER/$FREEMIND_MANTISBT/"

sudo cp ~/.m2/repository/axis/axis-jaxrpc/1.3/axis-jaxrpc-1.3.jar $FREEMIND_PLUGINS_FOLDER/$FREEMIND_MANTISBT/
echo "~/.m2/repository/axis/axis-jaxrpc/1.3/axis-jaxrpc-1.3.jar copied to $FREEMIND_PLUGINS_FOLDER/$FREEMIND_MANTISBT/"

sudo cp ~/.m2/repository/axis/axis-saaj/1.3/axis-saaj-1.3.jar $FREEMIND_PLUGINS_FOLDER/$FREEMIND_MANTISBT/
echo "~/.m2/repository/axis/axis-saaj/1.3/axis-saaj-1.3.jar copied to $FREEMIND_PLUGINS_FOLDER/$FREEMIND_MANTISBT/"

sudo cp ~/.m2/repository/biz/futureware/mantis/mantis-axis-soap-client/1.2.6/mantis-axis-soap-client-1.2.6.jar $FREEMIND_PLUGINS_FOLDER/$FREEMIND_MANTISBT/
echo "~/.m2/repository/biz/futureware/mantis/mantis-axis-soap-client/1.2.6/mantis-axis-soap-client-1.2.6.jar copied to $FREEMIND_PLUGINS_FOLDER/$FREEMIND_MANTISBT/"

sudo cp ~/.m2/repository/commons-discovery/commons-discovery/0.2/commons-discovery-0.2.jar $FREEMIND_PLUGINS_FOLDER/$FREEMIND_MANTISBT/
echo "~/.m2/repository/commons-discovery/commons-discovery/0.2/commons-discovery-0.2.jar copied to $FREEMIND_PLUGINS_FOLDER/$FREEMIND_MANTISBT/"

sudo cp ~/.m2/repository/commons-logging/commons-logging/1.0.4/commons-logging-1.0.4.jar $FREEMIND_PLUGINS_FOLDER/$FREEMIND_MANTISBT/
echo "~/.m2/repository/commons-logging/commons-logging/1.0.4/commons-logging-1.0.4.jar copied to $FREEMIND_PLUGINS_FOLDER/$FREEMIND_MANTISBT/"

sudo cp ~/.m2/repository/wsdl4j/wsdl4j/1.5.1/wsdl4j-1.5.1.jar $FREEMIND_PLUGINS_FOLDER/$FREEMIND_MANTISBT/
echo "~/.m2/repository/wsdl4j/wsdl4j/1.5.1/wsdl4j-1.5.1.jar copied to $FREEMIND_PLUGINS_FOLDER/$FREEMIND_MANTISBT/"


echo "freemind-mantisbt plugin installed successfully."
